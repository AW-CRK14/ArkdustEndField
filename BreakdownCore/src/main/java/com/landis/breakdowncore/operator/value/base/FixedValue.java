package com.landis.breakdowncore.operator.value.base;

public class FixedValue extends BaseValue{
    private final double trustAddableValue;
    private final double potentialAddableValue;

    public FixedValue(int baseValue, int maxValue, int trustAddableValue, int potentialAddableValue) {
        super(baseValue,maxValue);
        this.trustAddableValue = trustAddableValue;
        this.potentialAddableValue = potentialAddableValue;
    }

    public FixedValue(int baseValue, int maxValue) {
        super(baseValue,maxValue);
        this.trustAddableValue = 0;
        this.potentialAddableValue = 0;
    }

    public double getValue() {
        return value;
    }

    public double addTrustValue(){
        return value += trustAddableValue;
    }

    public double addPotentialValue(){
        return value += potentialAddableValue;
    }

    public double addMaxValueValue(){
        return value += MAX_VALUE;
    }
}
