package com.landis.breakdowncore.operator.value.base;

public class LevelValue {
    public static final int[][] EXP_DATA = {
            {100, 117, 134, 151, 168, 185, 202, 219, 236, 253, 270, 287, 304, 321, 338, 355, 372, 389, 406, 423, 440, 457, 474, 491, 508, 525, 542, 559, 574, 589, 605, 621, 637, 653, 669, 685, 701, 716, 724, 739, 749, 759, 770, 783, 804, 820, 836, 852, 888},
            {120, 172, 224, 276, 328, 380, 432, 484, 536, 588, 640, 692, 744, 796, 848, 900, 952, 1004, 1056, 1108, 1160, 1212, 1264, 1316, 1368, 1420, 1472, 1524, 1576, 1628, 1706, 1784, 1862, 1940, 2018, 2096, 2174, 2252, 2330, 2408, 2584, 2760, 2936, 3112, 3288, 3464, 3640, 3816, 3992, 4168, 4344, 4520, 4696, 4890, 5326, 6019, 6312, 6505, 6838, 7391, 7657, 7823, 8089, 8355, 8621, 8887, 9153, 9419, 9605, 9951, 10448, 10945, 11442, 11939, 12436, 12933, 13430, 13927, 14549},
            {191, 303, 415, 527, 639, 751, 863, 975, 1087, 1199, 1311, 1423, 1535, 1647, 1759, 1871, 1983, 2095, 2207, 2319, 2431, 2543, 2655, 2767, 2879, 2991, 3103, 3215, 3327, 3439, 3602, 3765, 3928, 4091, 4254, 4417, 4580, 4743, 4906, 5069, 5232, 5395, 5558, 5721, 5884, 6047, 6210, 6373, 6536, 6699, 6902, 7105, 7308, 7511, 7714, 7917, 8120, 8323, 8526, 8729, 9163, 9597, 10031, 10465, 10899, 11333, 11767, 12201, 12729, 13069, 13747, 14425, 15103, 15781, 16459, 17137, 17815, 18493, 19171, 19849, 21105, 22361, 23617, 24873, 26129, 27385, 28641, 29897, 31143}
    };
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

    public int getNextLevelExp() {
        // 检查是否已经达到最大等级
        if (level >= MAX_LEVEL_TWO && eliteStage == 2) {
            return -1; // 已经达到最大等级，无需再获取经验值
        } else if (level >= MAX_LEVEL_ONE && eliteStage == 1) {
            return -1; // 已经达到最大等级，无需再获取经验值
        } else if (level > BASE_MAX_LEVEL && eliteStage == 0) {
            return -1; // 已经达到最大等级，无需再获取经验值
        }

        int nextExp = 0;
        if (level <= BASE_MAX_LEVEL && eliteStage == 0) {
            // 等级在第一个阶段
            if (experience >= EXP_DATA[0][level - 1]) {
                // 已经达到当前等级的最大经验值，需要升级到下一个等级
                nextExp = EXP_DATA[0][level] - EXP_DATA[0][level - 1];
            } else {
                // 当前等级尚未达到最大经验值
                nextExp = EXP_DATA[0][level - 1] - experience;
            }
        } else if (level <= MAX_LEVEL_ONE && eliteStage == 1) {
            // 等级在第二个阶段
            if (experience >= EXP_DATA[1][level - 1]) {
                // 已经达到当前等级的最大经验值，需要升级到下一个等级
                nextExp = EXP_DATA[1][level] - EXP_DATA[1][level - 1];
            } else {
                // 当前等级尚未达到最大经验值
                nextExp = EXP_DATA[1][level - 1] - experience;
            }
        } else {
            // 等级在第三个阶段
            int index = level - MAX_LEVEL_TWO;
            if (experience >= EXP_DATA[2][level - 1]) {
                // 已经达到当前等级的最大经验值，需要升级到下一个等级
                nextExp = EXP_DATA[2][level] - EXP_DATA[2][level - 1];
            } else {
                // 当前等级尚未达到最大经验值
                nextExp = EXP_DATA[2][level - 1] - experience;
            }
        }
        return nextExp;
    }
}
