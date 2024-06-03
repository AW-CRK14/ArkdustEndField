package com.landis.arkdust.operator.value.base;

import net.minecraft.nbt.CompoundTag;

public class AddonValue {
    private final String addonName;
    private final double addonValue;
    private final BaseValue.ValueType TYPE;
    public final boolean isMultiply;
    public final boolean isFinalCalculate;

    public AddonValue(BaseValue.ValueType type, String addonName, double addonValue , boolean isMultiply, boolean isFinalCalculate){
        this.TYPE = type;
        this.addonName = addonName;
        this.addonValue = addonValue;
        this.isMultiply = isMultiply;
        this.isFinalCalculate = isFinalCalculate;
    }

    public double addonToValue(double originValue){
        if(!this.isMultiply){
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
        tag.putBoolean("IsMultiply", isMultiply);
        tag.putBoolean("IsFinalCalculate", isFinalCalculate);
        return tag;
    }

    public double removeToValue(double originValue){
        if(!this.isMultiply){
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




}
