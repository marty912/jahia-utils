package be.marty912.jahia.utils.actions;

import org.jahia.bin.Action;
import org.springframework.stereotype.Component;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation which allow to create a Jahia Action and to specify its name and its requirements (permission, workspace, authentication)
 */
@Component
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface RestrictedJahiaAction {

    /**
     * Defines the allowed node type(s), required to execute this action or <code>null</code> if no particular node type is required.
     *
     * @see Action#setRequiredWorkspace(String)
     */
    String requiredNodeTypes() default "";
}
