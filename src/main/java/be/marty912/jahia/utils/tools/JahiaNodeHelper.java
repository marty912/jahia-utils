package be.marty912.jahia.utils.tools;

import org.jahia.services.content.JCRContentUtils;
import org.jahia.services.content.JCRNodeWrapper;
import org.springframework.util.Assert;

import javax.jcr.RepositoryException;
import java.util.function.Consumer;

public class JahiaNodeHelper {

    private JahiaNodeHelper() {}

    public static JCRNodeWrapper getOrCreateNode(JCRNodeWrapper parentNode, String nodeName, String nodeType) throws RepositoryException {
        return getOrCreateNode(parentNode, nodeName, nodeType, Boolean.FALSE, null, null);
    }

    public static JCRNodeWrapper getOrCreateNode(JCRNodeWrapper parentNode, String nodeName, String nodeType, Consumer<JCRNodeWrapper> creationCallback, Consumer<JCRNodeWrapper> existingCallback) throws RepositoryException {
        return getOrCreateNode(parentNode, nodeName, nodeType, Boolean.FALSE, creationCallback, existingCallback);
    }

    public static JCRNodeWrapper getOrCreateNode(JCRNodeWrapper parentNode, String nodeName, String nodeType, Boolean save, Consumer<JCRNodeWrapper> creationCallback, Consumer<JCRNodeWrapper> existingCallback) throws RepositoryException {
        Assert.hasText(nodeName, "Node's name is null or empty");
        Assert.hasText(nodeName, "Node's type is null or empty");
        Assert.notNull(parentNode, "Parent's node is null");

        JCRNodeWrapper childNode;
        final String realNodeName = JCRContentUtils.generateNodeName(nodeName);
        if (parentNode.hasNode(realNodeName)) {
            final JCRNodeWrapper existingChildNode = parentNode.getNode(realNodeName);
            if (existingChildNode.isNodeType(nodeType)) {
                childNode = existingChildNode;
            } else {
                throw new IllegalArgumentException("The existing child node (" + existingChildNode + ") is not a " + nodeType);
            }
            if (existingCallback != null) {
                existingCallback.accept(existingChildNode);
            }
        } else {
            childNode = parentNode.addNode(realNodeName, nodeType);
            if (creationCallback != null) {
                creationCallback.accept(childNode);
            }
        }
        if (save) {
            childNode.saveSession();
        }
        return childNode;
    }
}
