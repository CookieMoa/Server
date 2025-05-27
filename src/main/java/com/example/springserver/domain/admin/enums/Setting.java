package com.example.springserver.domain.admin.enums;

public enum Setting {
    MODEL_LEARNING(false, Boolean.class),
    MODEL_LEARNING_CYCLE(Cycle.DAILY, Cycle.class),
    KEYWORD_ANALYSIS(false, Boolean.class),
    KEYWORD_ANALYSIS_CYCLE(Cycle.DAILY, Cycle.class),
    BLOCK_REPEATED_ABUSER(false, Boolean.class),
    ABUSE_THRESHOLD(0, Integer.class),
    BLOCK_MALICIOUS_USER(false, Boolean.class),
    MALICIOUS_THRESHOLD(0, Integer.class),
    DETECT_MALICIOUS_REVIEW(false, Boolean.class);

    private final Object defaultValue;
    private final Class<?> valueType;

    Setting(Object defaultValue, Class<?> valueType) {
        this.defaultValue = defaultValue;
        this.valueType = valueType;
    }

    public Object getDefaultValue() {
        return defaultValue;
    }

    public Class<?> getValueType() {
        return valueType;
    }
}

