package com.landis.breakdowncore.operator.value.base;

public class BaseValue {
    protected double value;
    protected final double BASE_VALUE;
    protected final double MAX_VALUE;
    public BaseValue(double baseValue, double maxValue) {
        this.value = baseValue;
        this.BASE_VALUE = baseValue;
        this.MAX_VALUE = maxValue;
    }
}
