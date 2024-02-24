package com.jms.alertmessaging.entity.student;

import lombok.Getter;

@Getter
public enum Frequency {
    _2M("2m"), _10M("10m"), _1H("1h"), _24H_8("24h-8"), _24H_20("24h-20");

    private final String value;

    Frequency(String value) {
        this.value = value;
    }

    // Optionally, add a static method to convert from String to Enum
    public static Frequency fromValue(String value) {
        for (Frequency frequency : values()) {
            if (frequency.getValue().equals(value)) {
                return frequency;
            }
        }
        throw new IllegalArgumentException("Unknown frequency: " + value);
    }

    public static Frequency defaultValue(){
        return Frequency._2M;
    }
}
