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
public @interface JahiaAction {
    /**
     * The action name. Default value is the bean name
     *
     * @see Action#setName(String)
     */
    String name() default "";

    /**
     * Defines if the action can be executed only by an authenticated user.
     *
     * @see Action#setRequireAuthenticatedUser(boolean)
     */
    boolean requireAuthenticatedUser() default true;

    /**
     * Defines a permission, required to execute this action or <code>null</code> if no particular permission is required.
     *
     * @see Action#setRequiredPermission(String)
     */
    String requiredPermission() default "";

    /**
     * Defines a permission, required to execute this action or <code>null</code> if no particular permission is required.
     *
     * @see Action#setRequiredWorkspace(String)
     */
    String requiredWorkspace() default "";
}
