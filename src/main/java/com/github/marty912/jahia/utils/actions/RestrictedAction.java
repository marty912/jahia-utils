package com.github.marty912.jahia.utils.actions;

import org.jahia.bin.Action;
import org.jahia.bin.ActionResult;
import org.jahia.services.content.JCRSessionWrapper;
import org.jahia.services.render.RenderContext;
import org.jahia.services.render.Resource;
import org.jahia.services.render.URLResolver;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

@SuppressWarnings("unused")
public abstract class RestrictedAction extends Action implements RestrictedActionInterface {
    private String requiredNodeTypes;

    @Override
    public ActionResult doExecute(HttpServletRequest req, RenderContext renderContext, Resource resource, JCRSessionWrapper session, Map<String, List<String>> parameters, URLResolver urlResolver) throws Exception {
        RestrictedActionUtils.checkRequiredNodeTypes(resource, this.requiredNodeTypes);
        return doExecuteRestricted(req, renderContext, session, resource, parameters, urlResolver);
    }

    public abstract ActionResult doExecuteRestricted(HttpServletRequest req, RenderContext renderContext, JCRSessionWrapper systemSession, Resource resource, Map<String, List<String>> parameters, URLResolver urlResolver) throws Exception;

    public void setRequiredNodeTypes(String requiredNodeTypes) {
        this.requiredNodeTypes = requiredNodeTypes;
    }
}
