package com.landis.breakdowncore.operator.value.base;

import net.minecraft.nbt.CompoundTag;

public class AddonValue {
    private final String addonName;
    private final double addonValue;
    private final BaseValue.ValueType TYPE;
    public final boolean IS_MULTIPLY;
    private int priorityValue;

    public AddonValue(BaseValue.ValueType type, int priorityValue,String addonName, double addonValue ,boolean isMultiply){
        this.TYPE = type;
        this.addonName = addonName;
        this.addonValue = addonValue;
        this.IS_MULTIPLY = isMultiply;
        this.priorityValue = priorityValue;
    }

    public AddonValue(BaseValue.ValueType type, int priorityValue,String addonName, double addonValue){
        this.TYPE = type;
        this.addonName = addonName;
        this.addonValue = addonValue;
        this.IS_MULTIPLY = false;
        this.priorityValue = priorityValue;
    }

    public void changePriority(int value){
        this.priorityValue = value;
    }

    public double addonToValue(double originValue){
        if(!this.IS_MULTIPLY){
            return originValue + addonValue;
        }else{
            return originValue * addonValue;
        }
    }

    public CompoundTag asNBT() {
        CompoundTag tag = new CompoundTag();
        tag.putString("AddonName", addonName);
        tag.putDouble("AddonValue", addonValue);
        tag.putString("ValueType", TYPE.name());
        tag.putBoolean("IsMultiply", IS_MULTIPLY);
        tag.putInt("PriorityValue", priorityValue);
        return tag;
    }

    public double removeToValue(double originValue){
        if(!this.IS_MULTIPLY){
            return originValue - addonValue;
        }else{
            return originValue / addonValue;
        }
    }

    public double getAddonValue() {
        return addonValue;
    }

    public String getAddonName() {
        return addonName;
    }

    public BaseValue.ValueType getTYPE() {
        return TYPE;
    }

    public int getPriorityValue() {
        return priorityValue;
    }




}
