package be.marty912.jahia.utils.actions;

import org.jahia.bin.ActionResult;
import org.jahia.bin.SystemAction;
import org.jahia.services.content.JCRSessionWrapper;
import org.jahia.services.render.RenderContext;
import org.jahia.services.render.Resource;
import org.jahia.services.render.URLResolver;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

public abstract class RestrictedSystemAction extends SystemAction implements RestrictedActionInterface {
    private String requiredNodeTypes;

    @Override
    public ActionResult doExecuteAsSystem(HttpServletRequest req, RenderContext renderContext, JCRSessionWrapper systemSession, Resource resource, Map<String, List<String>> parameters, URLResolver urlResolver) throws Exception {
        RestrictedActionUtils.checkRequiredNodeTypes(resource, this.requiredNodeTypes);
        return doExecuteAsSystemRestricted(req, renderContext, systemSession, resource, parameters, urlResolver);
    }

    public abstract ActionResult doExecuteAsSystemRestricted(HttpServletRequest req, RenderContext renderContext, JCRSessionWrapper systemSession, Resource resource, Map<String, List<String>> parameters, URLResolver urlResolver) throws Exception;

    public void setRequiredNodeTypes(String requiredNodeTypes) {
        this.requiredNodeTypes = requiredNodeTypes;
    }
}
