package com.landis.breakdowncore.operator.value.base;

public class LevelValue {
    public int experience = 0;
    public int eliteStage = 0;
    public int level = 1;
    public final int BASE_MAX_LEVEL;
    public final int MAX_LEVEL_ONE;
    public final int MAX_LEVEL_TWO;

    public LevelValue(int baseMaxLevel, int maxLevelOne, int maxLevelTwo) {
        BASE_MAX_LEVEL = baseMaxLevel;
        MAX_LEVEL_ONE = maxLevelOne;
        MAX_LEVEL_TWO = maxLevelTwo;
    }
    public LevelValue(int maxLevelOne, int maxLevelTwo) {
        BASE_MAX_LEVEL = 50;
        MAX_LEVEL_ONE = maxLevelOne;
        MAX_LEVEL_TWO = maxLevelTwo;
    }
}
