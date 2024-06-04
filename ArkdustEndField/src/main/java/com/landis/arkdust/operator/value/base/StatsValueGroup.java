package com.landis.arkdust.operator.value.base;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Multimap;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.common.util.INBTSerializable;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class StatsValueGroup implements INBTSerializable<CompoundTag> {
    private final ValueType type;
    private double value;
    private double valueAmend;
    private final HashMap<ResourceLocation,AddonValue> ADDONS = new HashMap<>();
    private final Multimap<Byte,AddonValue> WITH_STATS = HashMultimap.create();
    private final double baseValue;


    public StatsValueGroup(ValueType type, double baseValue) {
        this.type = type;
        this.baseValue = baseValue;
        this.value = baseValue;
    }



    public double getValue() {
        return value;
    }

    public double getValueAmend(){
        return valueAmend;
    }

    public double getBaseValue() {
        return baseValue;
    }

    public ValueType getType() {
        return type;
    }



    public void setValue(double value) {
        double d = value - this.value;
        valueAmend += d;
        updateValue();
    }

    public void addonValueApplyToValue(AddonValue addonValue){
        this.setValue(addonValue.addonToValue(this.getValue()));
    }

    public void cleanUp() {
        this.value = this.baseValue;
        this.valueAmend = 0;
        this.ADDONS.clear();
        this.WITH_STATS.clear();
    }

    protected void updateValue() {
        WITH_STATS.get((byte) 0).forEach((this::addonValueApplyToValue));
        WITH_STATS.get((byte) 1).forEach((this::addonValueApplyToValue));
        WITH_STATS.get((byte) 2).forEach((this::addonValueApplyToValue));
        WITH_STATS.get((byte) 3).forEach((this::addonValueApplyToValue));
    }

    public @Nullable AddonValue applyAddon(@Nonnull AddonValue addon){
        AddonValue fallout = ADDONS.put(addon.getAddonName(),addon);
        WITH_STATS.put(addon.getStatsIndex(),addon);
        if(fallout != null){
            WITH_STATS.remove(fallout.getStatsIndex(),fallout);
        }
        updateValue();
        return fallout;
    }

    public @Nullable AddonValue removeAddon(@Nonnull AddonValue addon){
        addon = ADDONS.remove(addon.getAddonName());
        if(addon != null){
            WITH_STATS.remove(addon.getStatsIndex(),addon);
        }
        updateValue();
        return addon;
    }

    public @Nullable AddonValue removeAddon(ResourceLocation location){
        return removeAddon(ADDONS.get(location));
    }


    @Override
    public CompoundTag serializeNBT() {
        CompoundTag nbt = new CompoundTag();
        nbt.putDouble("value",value);
        nbt.putDouble("amend",valueAmend);
        ListTag tag = new ListTag();
        for(AddonValue value : ADDONS.values()){
            tag.add(value.asNBT());
        }
        nbt.put("addon",tag);
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        this.value = nbt.getDouble("value");
        this.valueAmend = nbt.getDouble("amend");
        for(Tag tag : nbt.getList("addon",10)){
            AddonValue value = new AddonValue((CompoundTag) tag,type);
            applyAddon(value);
        }
    }

    public enum ValueType {
        HEALTH("health"),
        DEFENSE("defense"),
        DAMAGE("damage"),
        CRITICAL_CHANCE("critical_chance"),
        CRITICAL_STRIKE_BOUNDS("critical_strike_bounds"),
        MAGIC_DEFENSE("magic_defense"),
        ATTACK_INTERVAL("attack_interval"),
        RESPAWN_TIME("respawn_time"),
        NATURAL_REGEN_RATE("natural_regen_rate"),
        COST("cost");
        public final String NBT_KEY_NAME;

        ValueType(String nbtKeyName) {
            NBT_KEY_NAME = nbtKeyName;
        }

        public static final ValueType[] ENUM = ValueType.values();

        public static final ImmutableMap<String,ValueType> MAP;

        static {
            ImmutableMap.Builder<String,ValueType> builder = new ImmutableMap.Builder<>();
            for(ValueType type : ENUM){
                builder.put(type.NBT_KEY_NAME,type);
            }
            MAP = builder.build();
        }

    }

    public static class RespawnTimeType {
        public static final int VERY_SLOW = 360, SLOW = 120, COMMON = 60, FAST = 30, VERY_FAST = 15;
    }
}
