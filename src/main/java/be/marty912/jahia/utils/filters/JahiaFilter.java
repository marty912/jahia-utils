package be.marty912.jahia.utils.filters;

import org.springframework.stereotype.Component;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Component
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface JahiaFilter {
    boolean disabled() default false;
    int priority() default 0;
    String description() default "";
    String applyOnNodeTypes() default "";
    String applyOnModes() default "";
    String applyOnConfigurations() default "";
    boolean applyOnEditMode() default false;
    boolean applyOnMainResource() default false;
    String applyOnModules() default "";
    String applyOnSiteTemplateSets() default "";
    String applyOnTemplates() default "";
    String applyOnTemplateTypes() default "";
    boolean skipOnAjaxRequest() default false;
    String skipOnConfigurations() default "";
    boolean skipOnEditMode() default false;
    boolean skipOnMainResource() default false;
    String skipOnModes() default "";
    String skipOnModules() default "";
    String skipOnNodeTypes() default "";
    String skipOnTemplates() default "";
    String skipOnTemplateTypes() default "";
}
