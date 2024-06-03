package com.landis.arkdust.operator.value;

import com.google.common.collect.ImmutableMap;
import com.landis.arkdust.operator.OperatorStatsOriginalData;
import com.landis.arkdust.operator.value.base.AddonValue;
import com.landis.arkdust.operator.value.base.StatsValueGroup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.common.util.INBTSerializable;

import javax.annotation.Nonnull;

public class StatsPanel implements INBTSerializable<CompoundTag> {
    private final ImmutableMap<StatsValueGroup.ValueType, StatsValueGroup> VALUES;
    private boolean isLoadDone = false;


    /**
     * 构造一个新的StatsPanel实例。
     *
     * @param health         生命值
     * @param defense        防御力
     * @param damage         攻击力
     * @param magicDefense   魔法抗性
     * @param attackInterval 攻击间隔时间
     * @param respawnTime    复活时间类型
     * @param cost           花费
     */
    public StatsPanel(double health, double defense, double damage, double criticalChance, double criticalStrikeBounds, double magicDefense, double attackInterval, double respawnTime, double cost, double naturalRates) {
        ImmutableMap.Builder<StatsValueGroup.ValueType, StatsValueGroup> valuesBuilder = new ImmutableMap.Builder<>();
        valuesBuilder.put(StatsValueGroup.ValueType.HEALTH, new StatsValueGroup(StatsValueGroup.ValueType.HEALTH, health));
        valuesBuilder.put(StatsValueGroup.ValueType.DEFENSE, new StatsValueGroup(StatsValueGroup.ValueType.DEFENSE, defense));
        valuesBuilder.put(StatsValueGroup.ValueType.DAMAGE, new StatsValueGroup(StatsValueGroup.ValueType.DAMAGE, damage));
        valuesBuilder.put(StatsValueGroup.ValueType.CRITICAL_CHANCE, new StatsValueGroup(StatsValueGroup.ValueType.CRITICAL_CHANCE, criticalChance));
        valuesBuilder.put(StatsValueGroup.ValueType.CRITICAL_STRIKE_BOUNDS, new StatsValueGroup(StatsValueGroup.ValueType.CRITICAL_STRIKE_BOUNDS, criticalStrikeBounds));
        valuesBuilder.put(StatsValueGroup.ValueType.MAGIC_DEFENSE, new StatsValueGroup(StatsValueGroup.ValueType.MAGIC_DEFENSE, magicDefense));
        valuesBuilder.put(StatsValueGroup.ValueType.ATTACK_INTERVAL, new StatsValueGroup(StatsValueGroup.ValueType.ATTACK_INTERVAL, attackInterval));
        valuesBuilder.put(StatsValueGroup.ValueType.RESPAWN_TIME, new StatsValueGroup(StatsValueGroup.ValueType.RESPAWN_TIME, respawnTime));
        valuesBuilder.put(StatsValueGroup.ValueType.COST, new StatsValueGroup(StatsValueGroup.ValueType.COST, cost));
        valuesBuilder.put(StatsValueGroup.ValueType.NATURAL_REGEN_RATE, new StatsValueGroup(StatsValueGroup.ValueType.NATURAL_REGEN_RATE, naturalRates));
        VALUES = valuesBuilder.build();
    }

    public StatsPanel(OperatorStatsOriginalData data) {
        this(data.HEALTH, data.DEFENSE, data.DAMAGE, data.CRITICAL_CHANCE, data.CRITICAL_STRIKE_BOUNDS, data.MAGIC_DEFENSE, data.ATTACK_INTERVAL, data.RESPAWN_TIME, data.COST, data.NATURAL_REGEN_RATE);
    }

    /**
     * 向附加值映射表中添加附加值。
     * 这个附加值会直接应用在BaseValue上。
     *
     * @param addonValue 要添加的附加值对象。
     * @return 被挤出的目标，在没有同名对象时为null。
     */
    public AddonValue applyAddon(@Nonnull AddonValue addonValue) {
        return VALUES.get(addonValue.getValueType()).applyAddon(addonValue);
    }


    /**
     * 从附加值映射表中移除附加值。
     * 这个BaseValue会直接移除这个附加值。
     *
     * @param addonValue 要移除的附加值对象。
     * @return 移除的目标，如果没有移除则返回null。
     */
    public AddonValue removeAddon(@Nonnull AddonValue addonValue) {
        return VALUES.get(addonValue.getValueType()).removeAddon(addonValue);
    }

    /**
     * {@link StatsPanel#removeAddon(AddonValue) removeAddon}的id版本
     */
    public AddonValue removeAddon(StatsValueGroup.ValueType type, ResourceLocation id) {
        return VALUES.get(type).removeAddon(id);
    }

    /**
     * 根据属性类型获取基础属性值。
     *
     * @param type 属性类型
     * @return 对应类型的BaseValue对象。
     */
    public StatsValueGroup getBaseValueFromType(StatsValueGroup.ValueType type) {
        return VALUES.get(type);
    }

    // 玩家升级时更新属性
    private void updateStats() {
    }


    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        for(StatsValueGroup v : VALUES.values()){
            tag.put(v.getType().NBT_KEY_NAME,v.serializeNBT());
        }
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag compoundTag) {
        for (StatsValueGroup.ValueType type : StatsValueGroup.ValueType.values()) {
            VALUES.get(type).deserializeNBT(compoundTag.getCompound(type.NBT_KEY_NAME));
        }
    }
}
