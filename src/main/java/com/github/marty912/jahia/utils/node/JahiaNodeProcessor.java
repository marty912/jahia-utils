package com.github.marty912.jahia.utils.node;

import org.apache.commons.collections.CollectionUtils;
import org.apache.jackrabbit.value.BinaryImpl;
import org.jahia.services.content.JCRContentUtils;
import org.jahia.services.content.JCRNodeWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Binary;
import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Value;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@SuppressWarnings("unused")
public class JahiaNodeProcessor {
    private static final Logger LOGGER = LoggerFactory.getLogger(JahiaNodeProcessor.class);

    private JahiaNodeProcessor() {}

    public static void process(JCRNodeWrapper node, Object o, JahiaNodeProcessorSettings settings) throws RepositoryException {
        try {
            if (settings.createChildren) {
                createChildren(node, o, settings);
            }
            if (settings.setProperties) {
                setProperties(node, o);
            }
        } catch (IllegalAccessException | InvocationTargetException | IOException e) {
            LOGGER.error("Error while processing node " + node, e);
        }
    }

    private static void createChildren(JCRNodeWrapper node, Object o, JahiaNodeProcessorSettings settings) throws IllegalAccessException, RepositoryException, InvocationTargetException, IOException {
        for (Field field : o.getClass().getDeclaredFields()) {
            Object fieldValue = field.get(o);

            if (fieldValue != null) {
                createChildNode(node, settings, field, fieldValue);
                createChildrenNodes(node, settings, field, fieldValue);
            } else if (settings.deleteChildrenIfNull) {
                deleteChildNode(node, field);
                deleteChildrenNode(node, field);
            }
        }
    }

    @SuppressWarnings("unchecked")
    private static void createChildrenNodes(JCRNodeWrapper node, JahiaNodeProcessorSettings settings, Field field, Object fieldValue) throws RepositoryException, IllegalAccessException, InvocationTargetException, IOException {
        final JahiaNodes jahiaNodes = field.getAnnotation(JahiaNodes.class);
        if (jahiaNodes != null && fieldValue instanceof Collection) {
            if (settings.deleteChildrenIfNull) {
                deleteAllChildrenNodes(node, (Collection<NamedNode>) fieldValue, jahiaNodes);
            }
            for (NamedNode element : ((Collection<NamedNode>) fieldValue)) {
                String nodeName = element.getSystemName();

                JCRNodeWrapper childNode = node.hasNode(nodeName) ? node.getNode(nodeName) : node.addNode(nodeName, jahiaNodes.nodeType());
                if (settings.setPropertiesOnChildren) {
                    setProperties(childNode, element);
                }

                createChildren(childNode, element, settings);
            }
        }
    }

    private static void createChildNode(JCRNodeWrapper node, JahiaNodeProcessorSettings settings, Field field, Object fieldValue) throws RepositoryException, IllegalAccessException, InvocationTargetException, IOException {
        final JahiaNode annotation = field.getAnnotation(JahiaNode.class);
        if (annotation != null) {
            String nodeName = annotation.systemName();
            final JCRNodeWrapper childNode = node.hasNode(nodeName) ? node.getNode(nodeName) : node.addNode(nodeName, annotation.nodeType());
            if (settings.setPropertiesOnChildren) {
                setProperties(childNode, fieldValue);
            }

            createChildren(childNode, fieldValue, settings);
        }
    }

    private static void setProperties(JCRNodeWrapper node, Object o) throws RepositoryException, IllegalAccessException, InvocationTargetException, IOException {
        final Class<?> oClass = o.getClass();
        for (Field field : oClass.getDeclaredFields()) {
            if (field.isAnnotationPresent(JahiaNodeProperty.class)) {
                final JahiaNodeProperty nodeProperty = field.getAnnotation(JahiaNodeProperty.class);
                if (!field.isAccessible()) {
                    field.setAccessible(true);
                }
                setProperty(node, nodeProperty.value(), field.get(o), field.getType());
            }

            if (field.isAnnotationPresent(JahiaNodeI18nProperty.class)) {
                final JahiaNodeI18nProperty i18nPropertyAnnotation = field.getAnnotation(JahiaNodeI18nProperty.class);
                //noinspection unchecked
                setI18nProperty(node, i18nPropertyAnnotation.value(), (Collection<I18nProperty>) field.get(o));
            }
        }

        for (Method method : oClass.getMethods()) {
            if (method.isAnnotationPresent(JahiaNodeProperty.class)) {
                final JahiaNodeProperty nodePropertyAnnotation = method.getAnnotation(JahiaNodeProperty.class);
                setProperty(node, nodePropertyAnnotation.value(), method.invoke(o), method.getReturnType());
            }

            if (method.isAnnotationPresent(JahiaNodeI18nProperty.class)) {
                final JahiaNodeI18nProperty i18nPropertyAnnotation = method.getAnnotation(JahiaNodeI18nProperty.class);
                //noinspection unchecked
                setI18nProperty(node, i18nPropertyAnnotation.value(), (Collection<I18nProperty>) method.invoke(o));
            }
        }
    }

    private static void deleteChildNode(JCRNodeWrapper node, Field field) throws RepositoryException {
        final JahiaNode annotation = field.getAnnotation(JahiaNode.class);
        if (annotation != null && node.hasNode(annotation.systemName())) {
            node.getNode(annotation.systemName()).remove();
        }
    }

    private static void deleteChildrenNode(JCRNodeWrapper node, Field field) throws RepositoryException {
        final JahiaNodes annotation = field.getAnnotation(JahiaNodes.class);
        if (annotation != null) {
            for (JCRNodeWrapper childNodeToDelete : JCRContentUtils.getChildrenOfType(node, annotation.nodeType())) {
                childNodeToDelete.remove();
            }
        }
    }

    private static void deleteAllChildrenNodes(JCRNodeWrapper node, Collection<NamedNode> fieldValue, JahiaNodes jahiaNodes) throws RepositoryException {
        List<String> systemNames = fieldValue.stream()
                .map(NamedNode::getSystemName)
                .collect(Collectors.toList());

        for (JCRNodeWrapper oldChildNode : JCRContentUtils.getChildrenOfType(node, jahiaNodes.nodeType())) {
            if (!systemNames.contains(oldChildNode.getName())) {
                oldChildNode.remove();
            }
        }
    }

    private static void setI18nProperty(JCRNodeWrapper node, String propertyName, Collection<I18nProperty> value) throws RepositoryException {
        if (CollectionUtils.isNotEmpty(value)) {
            for (I18nProperty i18nProperty : value) {
                final Node i18nNode = node.getOrCreateI18N(i18nProperty.getLocale());
                i18nNode.setProperty(propertyName, i18nProperty.getValue());
            }
        }
    }

    private static void setProperty(JCRNodeWrapper node, String propertyName, Object value, Class<?> valueType) throws RepositoryException, IOException {
        if (value != null) {
            if (valueType.isAssignableFrom(String.class)) {
                node.setProperty(propertyName, (String) value);
            } else if (valueType.isAssignableFrom(Boolean.class) || valueType.isAssignableFrom(boolean.class)) {
                node.setProperty(propertyName, (boolean) value);
            } else if (valueType.isAssignableFrom(Double.class) || valueType.isAssignableFrom(double.class)) {
                node.setProperty(propertyName, (double) value);
            } else if (valueType.isAssignableFrom(Integer.class) || valueType.isAssignableFrom(int.class)) {
                node.setProperty(propertyName, ((Integer) value).longValue());
            } else if (valueType.isAssignableFrom(Long.class) || valueType.isAssignableFrom(long.class)) {
                node.setProperty(propertyName, (long) value);
            } else if (valueType.isAssignableFrom(InputStream.class)) {
                node.setProperty(propertyName, new BinaryImpl((InputStream) value));
            } else if (valueType.isAssignableFrom(String[].class)) {
                node.setProperty(propertyName, (String[]) value);
            } else if (valueType.isAssignableFrom(BigDecimal.class)) {
                node.setProperty(propertyName, (BigDecimal) value);
            } else if (valueType.isAssignableFrom(Date.class)) {
                final Calendar calendar = Calendar.getInstance();
                calendar.setTime((Date) value);
                node.setProperty(propertyName, calendar);
            } else if (valueType.isAssignableFrom(Calendar.class)) {
                node.setProperty(propertyName, (Calendar) value);
            } else if (valueType.isAssignableFrom(Binary.class)) {
                node.setProperty(propertyName, (Binary) value);
            } else if (valueType.isAssignableFrom(Node.class)) {
                node.setProperty(propertyName, (Node) value);
            } else if (valueType.isAssignableFrom(Value.class)) {
                node.setProperty(propertyName, (Value) value);
            } else if (valueType.isAssignableFrom(Value[].class)) {
                node.setProperty(propertyName, (Value[]) value);
            }
        } else {
            node.setProperty(propertyName, (Value) null);
        }
    }

    public static final JahiaNodeProcessorSettings NODE = new JahiaNodeProcessorSettings(true, false, false, false);
    public static final JahiaNodeProcessorSettings CHILDREN = new JahiaNodeProcessorSettings(true, true, false, true);
    public static final JahiaNodeProcessorSettings FULL = new JahiaNodeProcessorSettings(true, true, true, true);

    @SuppressWarnings("WeakerAccess")
    public static class JahiaNodeProcessorSettings {
        final boolean setProperties;
        final boolean createChildren;
        final boolean setPropertiesOnChildren;
        final boolean deleteChildrenIfNull;

        public JahiaNodeProcessorSettings(boolean setProperties, boolean createChildren, boolean setPropertiesOnChildren, boolean deleteChildrenIfNull) {
            this.setProperties = setProperties;
            this.createChildren = createChildren;
            this.setPropertiesOnChildren = setPropertiesOnChildren;
            this.deleteChildrenIfNull = deleteChildrenIfNull;
        }
    }
}
