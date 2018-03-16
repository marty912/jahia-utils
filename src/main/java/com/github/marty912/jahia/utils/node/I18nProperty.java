package com.github.marty912.jahia.utils.node;

import javax.jcr.Value;
import java.util.Locale;

public class I18nProperty {
    private Locale locale;
    private Value value;

    public I18nProperty(Locale locale, Value value) {
        this.locale = locale;
        this.value = value;
    }

    public Locale getLocale() {
        return this.locale;
    }
    public I18nProperty locale(Locale locale) {
        this.locale = locale;
        return this;
    }

    public Value getValue() {
        return value;
    }
    public I18nProperty value(Value value) {
        this.value = value;
        return this;
    }
}
