package com.landis.breakdowncore.operator.value;

import com.landis.breakdowncore.operator.AbstractOperator;
import com.landis.breakdowncore.operator.value.base.AddonValue;
import com.landis.breakdowncore.operator.value.base.BaseValue;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StatsPanel {
    private final BaseValue health;
    private final BaseValue defense;
    private final BaseValue damage;
    private final BaseValue magicDefense;
    private final BaseValue attackInterval;
    private final BaseValue respawnTime;
    private final BaseValue cost;
    private final BaseValue naturalRegenRate;
    private final RareLevel rareLevel;
    private final List<AddonValue> addonValues = new ArrayList<>();
    private boolean isLoadDone = false;

    /**
     * 构造一个新的StatsPanel实例。
     *
     * @param rareLevel 稀有等级
     * @param health 生命值
     * @param defense 防御力
     * @param damage 攻击力
     * @param magicDefense 魔法抗性
     * @param attackInterval 攻击间隔时间
     * @param respawnTime 复活时间类型
     * @param cost 花费
     */
    public StatsPanel(RareLevel rareLevel,int health,int defense,int damage,int magicDefense,int attackInterval,BaseValue.RespawnTimeType respawnTime,int cost) {
        this.health = new BaseValue(BaseValue.ValueType.Health, health);
        this.defense = new BaseValue(BaseValue.ValueType.DEFENSE, defense);
        this.damage = new BaseValue(BaseValue.ValueType.DAMAGE, damage);
        this.magicDefense = new BaseValue(BaseValue.ValueType.MAGIC_DEFENSE, magicDefense);
        this.attackInterval = new BaseValue(BaseValue.ValueType.ATTACK_INTERVAL, attackInterval);
        this.respawnTime = new BaseValue(BaseValue.ValueType.RESPAWN_TIME, respawnTime.getRespawnTime());
        this.cost = new BaseValue(BaseValue.ValueType.COST, cost);
        this.naturalRegenRate = new BaseValue(BaseValue.ValueType.NATURAL_REGEN_RATE, 1.0);
        this.rareLevel = rareLevel;
    }

    /**
     * 构造一个新的StatsPanel实例。
     *
     * @param rareLevel 稀有等级
     * @param health 生命值
     * @param defense 防御力
     * @param damage 攻击力
     * @param cost 花费
     */
    public StatsPanel(RareLevel rareLevel,int health,int defense,int damage,int cost) {
        this.health = new BaseValue(BaseValue.ValueType.Health, health);
        this.defense = new BaseValue(BaseValue.ValueType.DEFENSE, defense);
        this.damage = new BaseValue(BaseValue.ValueType.DAMAGE, damage);
        this.magicDefense = new BaseValue(BaseValue.ValueType.MAGIC_DEFENSE, 0.0);
        this.attackInterval = new BaseValue(BaseValue.ValueType.ATTACK_INTERVAL, 5.0);
        this.respawnTime = new BaseValue(BaseValue.ValueType.RESPAWN_TIME, BaseValue.RespawnTimeType.COMMON.getRespawnTime());
        this.cost = new BaseValue(BaseValue.ValueType.COST, cost);
        this.naturalRegenRate = new BaseValue(BaseValue.ValueType.NATURAL_REGEN_RATE, 1.0);
        this.rareLevel = rareLevel;
    }

    /**
     * 向附加值映射表中添加附加值。
     * 这个附加值会直接应用在BaseValue上。
     * @param addonValue 要添加的附加值对象。
     */
    public void putAddonValueToList(AddonValue addonValue){
        this.addonValues.add(addonValue);
        updateStatePanel();
    }

    public void updateStatePanel(){
        List<AddonValue> skipped_final_add_calculate = new ArrayList<>();
        List<AddonValue> skipped_final_multiply_calculate = new ArrayList<>();
        List<AddonValue> skipped_multiply_calculate = new ArrayList<>();
        for (AddonValue addonValue : this.addonValues) {
            if(addonValue.IS_FINAL_CALCULATE){
                if(addonValue.IS_MULTIPLY){
                    skipped_final_multiply_calculate.add(addonValue);
                }else{
                    skipped_final_add_calculate.add(addonValue);
                }
            }else{
                if(addonValue.IS_MULTIPLY){
                    skipped_multiply_calculate.add(addonValue);
                }else{
                    this.applyAddonValue(addonValue);
                }
            }
        }

        skipped_multiply_calculate.forEach((this::applyAddonValue));
        skipped_final_add_calculate.forEach((this::applyAddonValue));
        skipped_final_multiply_calculate.forEach((this::applyAddonValue));
    }

    public void applyAddonValue(AddonValue addonValue){
        BaseValue bv = this.getBaseValueFromType(addonValue.getTYPE());
        double addedValue = addonValue.addonToValue(bv.getValue());
        bv.setValue(addedValue);
    }

    /**
     * 从附加值映射表中移除附加值。
     * 这个BaseValue会直接移除这个附加值。
     * @param addonValue 要移除的附加值对象。
     */
    public void removeAddonValueFromList(AddonValue addonValue) {
        if(this.addonValues.removeIf(av -> av.getTYPE() == addonValue.getTYPE() && av.getAddonName().equals(addonValue.getAddonName()))){
            updateStatePanel();
        };
    }

    /**
     * 根据属性类型获取基础属性值。
     *
     * @param type 属性类型
     * @return 对应类型的BaseValue对象。
     */
    public BaseValue getBaseValueFromType(BaseValue.ValueType type) {
        return switch (type) {
            case Health -> health;
            case DEFENSE -> defense;
            case DAMAGE -> damage;
            case MAGIC_DEFENSE -> magicDefense;
            case ATTACK_INTERVAL -> attackInterval;
            case RESPAWN_TIME -> respawnTime;
            case NATURAL_REGEN_RATE -> naturalRegenRate;
            case COST -> cost;
        };
    }

    public void readNbt(CompoundTag compoundTag) {
        this.rareLevel.getLevelValue().readNbt(compoundTag);
        health.setValue(compoundTag.getDouble(BaseValue.ValueType.Health.NBT_NAMESPACE));
        defense.setValue(compoundTag.getDouble(BaseValue.ValueType.DEFENSE.NBT_NAMESPACE));
        damage.setValue(compoundTag.getDouble(BaseValue.ValueType.DAMAGE.NBT_NAMESPACE));
        magicDefense.setValue(compoundTag.getDouble(BaseValue.ValueType.MAGIC_DEFENSE.NBT_NAMESPACE));
        attackInterval.setValue(compoundTag.getDouble(BaseValue.ValueType.ATTACK_INTERVAL.NBT_NAMESPACE));
        respawnTime.setValue(compoundTag.getDouble(BaseValue.ValueType.RESPAWN_TIME.NBT_NAMESPACE));
        cost.setValue(compoundTag.getDouble(BaseValue.ValueType.COST.NBT_NAMESPACE));
        naturalRegenRate.setValue(compoundTag.getDouble(BaseValue.ValueType.NATURAL_REGEN_RATE.NBT_NAMESPACE));
        this.readAddonValuesFromNbt(compoundTag);
    }

    public CompoundTag writeNbt(CompoundTag compoundTag) {
        compoundTag.putDouble(BaseValue.ValueType.Health.NBT_NAMESPACE, health.getValue());
        compoundTag.putDouble(BaseValue.ValueType.DEFENSE.NBT_NAMESPACE, defense.getValue());
        compoundTag.putDouble(BaseValue.ValueType.DAMAGE.NBT_NAMESPACE, damage.getValue());
        compoundTag.putDouble(BaseValue.ValueType.MAGIC_DEFENSE.NBT_NAMESPACE, magicDefense.getValue());
        compoundTag.putDouble(BaseValue.ValueType.ATTACK_INTERVAL.NBT_NAMESPACE, attackInterval.getValue());
        compoundTag.putDouble(BaseValue.ValueType.RESPAWN_TIME.NBT_NAMESPACE, respawnTime.getValue());
        compoundTag.putDouble(BaseValue.ValueType.COST.NBT_NAMESPACE, cost.getValue());
        compoundTag.putDouble(BaseValue.ValueType.NATURAL_REGEN_RATE.NBT_NAMESPACE, naturalRegenRate.getValue());
        CompoundTag tag = writeAddonValuesToNBT(compoundTag);
        return this.rareLevel.getLevelValue().writeNbt(tag);
    }

    public CompoundTag writeAddonValuesToNBT(CompoundTag compoundTag) {
        ListTag addonValueList = new ListTag();

        for (AddonValue addonValue : this.addonValues) {
            CompoundTag addonValueCompoundTag = addonValue.asNBT();
            // 将CompoundTag添加到ListTag中
            addonValueList.add(addonValueCompoundTag);
        }
        // 将ListTag添加到CompoundTag中
        compoundTag.put("operator_base_addon_values", addonValueList);
        return compoundTag;
    }

    private void readAddonValuesFromNbt(CompoundTag compoundTag) {
        // 清除现有的附加值
        addonValues.clear();

        // 从NBT获取AddonValues列表
        ListTag addonValueList = compoundTag.getList("operator_base_addon_values", 10); // 10 是 CompoundTag 类型的ID

        for (int i = 0; i < addonValueList.size(); ++i) {
            CompoundTag addonValueCompoundTag = addonValueList.getCompound(i);
            // 从CompoundTag中读取AddonValue的数据
            String addonName = addonValueCompoundTag.getString("AddonName");
            double addonValue = addonValueCompoundTag.getDouble("AddonValue");
            String typeString = addonValueCompoundTag.getString("ValueType");
            boolean isMultiply = addonValueCompoundTag.getBoolean("IsMultiply");
            boolean isFinalCalculate = addonValueCompoundTag.getBoolean("IsFinalCalculate");

            // 将字符串类型的TYPE转换回ValueType枚举
            BaseValue.ValueType type = null;
            for (BaseValue.ValueType valueType : BaseValue.ValueType.values()) {
                if (valueType.name().equals(typeString)) {
                    type = valueType;
                    break;
                }
            }

            if (type == null) {
                // 如果没有找到对应的ValueType，可以记录日志或者抛出异常
                AbstractOperator.LOGGER.error("Unknown ValueType in NBT: " + typeString);
            }else{
                // 创建新的AddonValue实例并添加到映射中
                AddonValue addonValue_obj = new AddonValue(type, addonName, addonValue, isMultiply, isFinalCalculate);
                addonValues.add(addonValue_obj);
            }
        }
    }

    // 玩家升级时更新属性
    private void updateStats() {
    }


    public LevelValue getLevelValue() {
        return this.rareLevel.getLevelValue();
    }

    // 以下是获取各种属性的方法
    public BaseValue getHealth() {
        return health;
    }

    public BaseValue getDefense() {
        return defense;
    }

    public BaseValue getDamage() {
        return damage;
    }

    public BaseValue getMagicDefense() {
        return magicDefense;
    }

    public BaseValue getAttackInterval() {
        return attackInterval;
    }

    public BaseValue getRespawnTime() {
        return respawnTime;
    }

    public BaseValue getCost() {
        return this.cost;
    }

    public BaseValue getNaturalRegenRate() {
        return naturalRegenRate;
    }

    public RareLevel getRareLevel() {
        return rareLevel;
    }


}
