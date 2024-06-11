package com.landis.arkdust.operator.value.base;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;

public class AddonValue {
    private final ResourceLocation addonName;
    private final double addonValue;
    private final StatsValueGroup.ValueType valueType;
    public final boolean isMultiply;
    public final boolean isFinalCalculate;

    public AddonValue(StatsValueGroup.ValueType type, ResourceLocation addonName, double addonValue , boolean isMultiply, boolean isFinalCalculate){
        this.valueType = type;
        this.addonName = addonName;
        this.addonValue = addonValue;
        this.isMultiply = isMultiply;
        this.isFinalCalculate = isFinalCalculate;
    }

    public AddonValue(CompoundTag tag, StatsValueGroup.ValueType type){
        this.valueType = type;
        this.addonName = new ResourceLocation(tag.getString("name"));
        this.addonValue = tag.getDouble("value");
        this.isMultiply = tag.getBoolean("is_multiply");
        this.isFinalCalculate = tag.getBoolean("is_final_calculate");
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
        tag.putString("name", addonName.toString());
        tag.putDouble("value", addonValue);
        tag.putBoolean("is_multiply", isMultiply);
        tag.putBoolean("is_final_calculate", isFinalCalculate);
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

    public ResourceLocation getAddonName() {
        return addonName;
    }

    public StatsValueGroup.ValueType getValueType() {
        return valueType;
    }

    public byte getStatsIndex(){
        return (byte) ((isFinalCalculate ? 2 : 0) + (isMultiply ? 1 : 0));
    }

}
