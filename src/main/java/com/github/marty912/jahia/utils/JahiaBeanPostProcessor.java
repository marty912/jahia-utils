package com.github.marty912.jahia.utils;

import com.github.marty912.jahia.utils.actions.JahiaAction;
import com.github.marty912.jahia.utils.actions.RestrictedActionInterface;
import com.github.marty912.jahia.utils.actions.RestrictedJahiaAction;
import com.github.marty912.jahia.utils.filters.JahiaFilter;
import org.apache.commons.lang.StringUtils;
import org.jahia.bin.Action;
import org.jahia.services.render.filter.AbstractFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

import java.util.function.BiConsumer;
import java.util.function.Function;

@Component
public class JahiaBeanPostProcessor implements BeanPostProcessor {
    private static final Logger LOGGER = LoggerFactory.getLogger(JahiaBeanPostProcessor.class);

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) {
        if (bean instanceof Action) {
            this.handleJahiaAction((Action) bean);
        }
        if (bean instanceof AbstractFilter) {
            this.handleJahiaFilter((AbstractFilter) bean);
        }

        return bean;
    }

    private void handleJahiaFilter(AbstractFilter filter) {
        final JahiaFilter annotation = filter.getClass().getAnnotation(JahiaFilter.class);
        if (annotation != null) {
            LOGGER.info(filter.getClass().getSimpleName() + " has a @JahiaFilter");
            filter.setDisabled(annotation.disabled());
            filter.setPriority(annotation.priority());
            filter.setDescription(annotation.description());

            this.setStringFilterProperty(filter, annotation.applyOnNodeTypes(), AbstractFilter::setApplyOnNodeTypes);
            this.setStringFilterProperty(filter, annotation.applyOnModes(), AbstractFilter::setApplyOnModes);
            this.setStringFilterProperty(filter, annotation.applyOnConfigurations(), AbstractFilter::setApplyOnConfigurations);
            this.setFilterProperty(filter, annotation.applyOnEditMode(), AbstractFilter::setApplyOnEditMode);
            this.setFilterProperty(filter, annotation.applyOnMainResource(), AbstractFilter::setApplyOnMainResource);
            this.setStringFilterProperty(filter, annotation.applyOnModules(), AbstractFilter::setApplyOnModules);
            this.setStringFilterProperty(filter, annotation.applyOnSiteTemplateSets(), AbstractFilter::setApplyOnSiteTemplateSets);
            this.setStringFilterProperty(filter, annotation.applyOnTemplates(), AbstractFilter::setApplyOnTemplates);
            this.setStringFilterProperty(filter, annotation.applyOnTemplateTypes(), AbstractFilter::setApplyOnTemplateTypes);
            this.setFilterProperty(filter, annotation.skipOnAjaxRequest(), AbstractFilter::setSkipOnAjaxRequest);
            this.setStringFilterProperty(filter, annotation.skipOnConfigurations(), AbstractFilter::setSkipOnConfigurations);
            this.setFilterProperty(filter, annotation.skipOnEditMode(), AbstractFilter::setSkipOnEditMode);
            this.setFilterProperty(filter, annotation.skipOnMainResource(), AbstractFilter::setSkipOnMainResource);
            this.setStringFilterProperty(filter, annotation.skipOnModes(), AbstractFilter::setSkipOnModes);
            this.setStringFilterProperty(filter, annotation.skipOnModules(), AbstractFilter::setSkipOnModules);
            this.setStringFilterProperty(filter, annotation.skipOnNodeTypes(), AbstractFilter::setSkipOnNodeTypes);
            this.setStringFilterProperty(filter, annotation.skipOnTemplates(), AbstractFilter::setSkipOnTemplates);
            this.setStringFilterProperty(filter, annotation.skipOnTemplateTypes(), AbstractFilter::setSkipOnTemplateTypes);
        }
    }

    private void setStringFilterProperty(AbstractFilter filter, String value, BiConsumer<AbstractFilter, String> setter) {
        this.setFilterProperty(filter, value, setter, StringUtils::isNotBlank);
    }

    private <T> void setFilterProperty(AbstractFilter filter, T value, BiConsumer<AbstractFilter, T> setter, Function<T, Boolean> validator) {
        if (validator.apply(value)) {
            this.setFilterProperty(filter, value, setter);
        }
    }

    private <T> void setFilterProperty(AbstractFilter filter, T value, BiConsumer<AbstractFilter, T> setter) {
        setter.accept(filter, value);
    }

    private void handleJahiaAction(Action action) {
        final JahiaAction jahiaActionAnnotation = action.getClass().getAnnotation(JahiaAction.class);
        if (jahiaActionAnnotation != null) {
            LOGGER.info(action.getClass().getSimpleName() + " has a @JahiaAction");
            final String name = jahiaActionAnnotation.name();
            final String requiredPermission = jahiaActionAnnotation.requiredPermission();
            final String requiredWorkspace = jahiaActionAnnotation.requiredWorkspace();

            action.setName(StringUtils.isNotEmpty(name) ? name : action.getName());
            action.setRequiredPermission(StringUtils.isNotEmpty(requiredPermission) ? requiredPermission : null);
            action.setRequiredWorkspace(StringUtils.isNotEmpty(requiredWorkspace) ? requiredWorkspace : null);
            action.setRequireAuthenticatedUser(jahiaActionAnnotation.requireAuthenticatedUser());
        }

        if (action instanceof RestrictedActionInterface) {
            final RestrictedJahiaAction restrictedJahiaActionAnnotation = action.getClass().getAnnotation(RestrictedJahiaAction.class);
            LOGGER.info(action.getClass().getSimpleName() + " has a @RestrictedJahiaAction");
            if (restrictedJahiaActionAnnotation != null) {
                ((RestrictedActionInterface) action).setRequiredNodeTypes(restrictedJahiaActionAnnotation.requiredNodeTypes());
            }
        }
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) {
        return bean;
    }
}