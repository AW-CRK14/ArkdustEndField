package com.landis.arkdust.operator.value.base;

public class BaseValue {
    private final ValueType TYPE;
    private double value;
    private final double BASE_VALUE;


    public BaseValue(ValueType type, double baseValue) {
        TYPE = type;
        this.value = baseValue;
        this.BASE_VALUE = baseValue;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value){ this.value = value; }

    public void setValueToDefault(){
        this.value = this.BASE_VALUE;
    }

    public double getBASE_VALUE() {
        return BASE_VALUE;
    }

    public ValueType getTYPE() {
        return TYPE;
    }

    public enum ValueType {
        Health("operator_base_health"),
        DEFENSE("operator_base_defense"),
        DAMAGE("operator_base_damage"),
        MAGIC_DEFENSE("operator_base_magic_defense"),
        ATTACK_INTERVAL("operator_base_attack_interval"),
        RESPAWN_TIME("operator_base_respawn_time"),
        NATURAL_REGEN_RATE("operator_base_natural_regen_rate"),
        COST("operator_base_cost");
        public final String NBT_NAMESPACE;

        ValueType(String nbtNamespace) {
            NBT_NAMESPACE = nbtNamespace;
        }

    }

    public enum RespawnTimeType{
        VERY_SLOW(360),SLOW(120),COMMON(60),FAST(30),VERY_FAST(15);

        private final double respawnTime;
        RespawnTimeType(double respawnTime){
            this.respawnTime = respawnTime;
        }

        public double getRespawnTime() {
            return respawnTime;
        }
    }
}
