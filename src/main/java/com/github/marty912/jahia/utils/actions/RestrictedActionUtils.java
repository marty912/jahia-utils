package com.github.marty912.jahia.utils.actions;

import org.apache.commons.lang.StringUtils;
import org.jahia.exceptions.JahiaBadRequestException;
import org.jahia.services.content.JCRNodeWrapper;
import org.jahia.services.render.Resource;
import org.jahia.utils.Patterns;

import javax.jcr.RepositoryException;

class RestrictedActionUtils {

    private RestrictedActionUtils() {}

    static void checkRequiredNodeTypes(Resource resource, String requiredNodeTypes) throws RepositoryException {
        if(StringUtils.isNotBlank(requiredNodeTypes)) {
            JCRNodeWrapper resourceNode = resource.getNode();
            for (String nodeType : requiredNodeTypes.contains(",") ? Patterns.COMMA.split(requiredNodeTypes) : new String[] {requiredNodeTypes}) {
                if (resourceNode.isNodeType(nodeType.trim())) {
                    return;
                }
            }
            throw new JahiaBadRequestException("The resource's nodeType is not supported");
        }
    }
}
