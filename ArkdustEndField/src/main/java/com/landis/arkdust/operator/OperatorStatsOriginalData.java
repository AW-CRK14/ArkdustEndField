package com.landis.arkdust.operator;

import com.landis.arkdust.operator.value.base.StatsValueGroup;

public class OperatorStatsOriginalData {
    public final double HEALTH;
    public final double DEFENSE;
    public final double DAMAGE;
    public final double CRITICAL_CHANCE;
    public final double CRITICAL_STRIKE_BOUNDS;
    public final double MAGIC_DEFENSE;
    public final double ATTACK_INTERVAL;
    public final double RESPAWN_TIME;
    public final double NATURAL_REGEN_RATE;
    public final double COST;

    public OperatorStatsOriginalData(double health, double defense, double damage, double criticalChance, double criticalStrikeBounds, double magicDefense, double attackInterval, double respawnTime, double naturalRegenRate, double cost) {
        HEALTH = health;
        DEFENSE = defense;
        DAMAGE = damage;
        CRITICAL_CHANCE = criticalChance;
        CRITICAL_STRIKE_BOUNDS = criticalStrikeBounds;
        MAGIC_DEFENSE = magicDefense;
        ATTACK_INTERVAL = attackInterval;
        RESPAWN_TIME = respawnTime;
        NATURAL_REGEN_RATE = naturalRegenRate;
        COST = cost;
    }

    public OperatorStatsOriginalData(double health, double defense, double damage, double cost) {
        this(health, defense, damage, 0.05, 0.3, 0, 5, StatsValueGroup.RespawnTimeType.COMMON, 1, cost);
    }
}
