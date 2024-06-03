package com.landis.arkdust.operator.feature;

import com.landis.arkdust.operator.AbstractOperator;

public class OperatorFeature {
    private String name; // 特性名称
    private int effectValue; // 特性效果值，例如护甲穿透数值
    private final boolean AFFECTS_ALLY; // 是否影响队友
    private final boolean AFFECTS_SELF; // 是否影响自身

    public OperatorFeature(String name, int effectValue, boolean affectsAlly, boolean affectsSelf) {
        this.name = name;
        this.effectValue = effectValue;
        this.AFFECTS_ALLY = affectsAlly;
        this.AFFECTS_SELF = affectsSelf;
    }

    // 获取特性名称
    public String getName() {
        return name;
    }

    // 获取特性效果值
    public int getEffectValue() {
        return effectValue;
    }

    // 是否影响队友
    public boolean isAffectsAlly() {
        return AFFECTS_ALLY;
    }

    // 是否影响自身
    public boolean isAffectsSelf() {
        return AFFECTS_SELF;
    }

    // 应用特性效果
    public void applyEffect(AbstractOperator operator) {
        if (isAffectsSelf()) {
            this.applyAffectSelf(operator);
        }
        if (isAffectsAlly()) {
            this.applyAffectAlly(operator);
        }
    }

    public void applyAffectAlly(AbstractOperator operator){}

    public void applyAffectSelf(AbstractOperator operator){}
}