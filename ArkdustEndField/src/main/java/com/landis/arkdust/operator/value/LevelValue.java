package com.landis.arkdust.operator.value;

import com.landis.arkdust.operator.AbstractOperator;
import net.minecraft.nbt.CompoundTag;

public class LevelValue {
    public static final int BASIC_COMBAT_RECORD = 200;
    public static final int PRIMARY_COMBAT_RECORD = 400;
    public static final int INTERMEDIATE_COMBAT_RECORD = 1000;
    public static final int ADVANCED_COMBAT_RECORD = 2000;
    public static final int[][] EXP_DATA = {
            {100, 117, 134, 151, 168, 185, 202, 219, 236, 253, 270, 287, 304, 321, 338, 355, 372, 389, 406, 423, 440, 457, 474, 491, 508, 525, 542, 559, 574, 589, 605, 621, 637, 653, 669, 685, 701, 716, 724, 739, 749, 759, 770, 783, 804, 820, 836, 852, 888},
            {120, 172, 224, 276, 328, 380, 432, 484, 536, 588, 640, 692, 744, 796, 848, 900, 952, 1004, 1056, 1108, 1160, 1212, 1264, 1316, 1368, 1420, 1472, 1524, 1576, 1628, 1706, 1784, 1862, 1940, 2018, 2096, 2174, 2252, 2330, 2408, 2584, 2760, 2936, 3112, 3288, 3464, 3640, 3816, 3992, 4168, 4344, 4520, 4696, 4890, 5326, 6019, 6312, 6505, 6838, 7391, 7657, 7823, 8089, 8355, 8621, 8887, 9153, 9419, 9605, 9951, 10448, 10945, 11442, 11939, 12436, 12933, 13430, 13927, 14549},
            {191, 303, 415, 527, 639, 751, 863, 975, 1087, 1199, 1311, 1423, 1535, 1647, 1759, 1871, 1983, 2095, 2207, 2319, 2431, 2543, 2655, 2767, 2879, 2991, 3103, 3215, 3327, 3439, 3602, 3765, 3928, 4091, 4254, 4417, 4580, 4743, 4906, 5069, 5232, 5395, 5558, 5721, 5884, 6047, 6210, 6373, 6536, 6699, 6902, 7105, 7308, 7511, 7714, 7917, 8120, 8323, 8526, 8729, 9163, 9597, 10031, 10465, 10899, 11333, 11767, 12201, 12729, 13069, 13747, 14425, 15103, 15781, 16459, 17137, 17815, 18493, 19171, 19849, 21105, 22361, 23617, 24873, 26129, 27385, 28641, 29897, 31143}
    };

    public int experience = 0;
    public int eliteStage = 0;

    public int level = 1;
    public int skillLevel = 1;
    public final int BASE_MAX_LEVEL;
    public final int MAX_LEVEL_ONE;
    public final int MAX_LEVEL_TWO;
    private int targetLevel;
    private int totalExpToLevel;

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

    public void addExperience(int expToAdd) {
        this.experience += expToAdd;
        while (canLevelUp()) {
            levelUp();
        }
    }

    private boolean canLevelUp() {
        if (checkStageAndLevel()) {
            return false;
        }
        this.getExpToNextLevel();
        return this.experience >= this.totalExpToLevel;
    }

    protected void levelUp() {
        if (level < getMaxLevelForStage()) {
            this.level++;
            this.experience -= this.totalExpToLevel;
        }
    }

    public void skillLevelUp() {
        if(this.skillLevel + 1 <= 7){
            this.skillLevel++;
        }
    }

    public void readNbt(CompoundTag compoundTag) {
        experience = compoundTag.getInt("operator_base_experience");
        eliteStage = compoundTag.getInt("operator_base_elite_stage");
        level = compoundTag.getInt("operator_base_level");
        skillLevel = compoundTag.getInt("operator_base_skill_level");
        if (compoundTag.contains("operator_base_target_level")) {
            targetLevel = compoundTag.getInt("operator_base_target_level");
            calculateTotalExpToLevel(targetLevel);
        }else{
            getExpToNextLevel();
        }
    }

    public CompoundTag writeNbt(CompoundTag compoundTag) {
        compoundTag.putInt("operator_base_experience", experience);
        compoundTag.putInt("operator_base_elite_stage", eliteStage);
        compoundTag.putInt("operator_base_level", level);
        compoundTag.putInt("operator_base_skill_level", skillLevel);
        compoundTag.putInt("operator_base_target_level", targetLevel);
        return compoundTag;
    }
    public void EliteStageUpLevel() {
        if (level >= MAX_LEVEL_ONE && eliteStage == 1) {
            this.eliteStage = 2;
            this.level = 1;
            this.experience = 0;
            AbstractOperator.LOGGER.info("Elite stage upgraded to stage 2.");
        } else if (level >= BASE_MAX_LEVEL && eliteStage == 0) {
            this.eliteStage = 1;
            this.level = 1;
            this.experience = 0;
            AbstractOperator.LOGGER.info("Elite stage upgraded to stage 1.");
        } else {
            AbstractOperator.LOGGER.error("Cannot upgrade elite stage at current level and stage.");
        }
    }

    public void setTargetLevel(int targetLevel) {
        // 确保目标等级不超过当前精英阶段的限制
        int maxLevel = getMaxLevelForStage();
        this.targetLevel = Math.min(targetLevel, maxLevel);
        // 计算并设置到达目标等级所需的总经验值
        if(this.targetLevel < this.level){ this.targetLevel = this.level; }
        calculateTotalExpToLevel(this.targetLevel);
    }

    // 获取到下一级所需的经验值
    private void getExpToNextLevel() {
        if (checkStageAndLevel()) return;
        this.totalExpToLevel = EXP_DATA[eliteStage][(this.level < EXP_DATA[eliteStage].length ? this.level : EXP_DATA[eliteStage].length - 1)];
    }


    private void calculateTotalExpToLevel(int targetLevel) {
        if (checkStageAndLevel()) return; // 超出精英等级限制
        if (targetLevel > this.level) { // 确保目标等级高于当前等级
            for (int i = this.level; i < targetLevel; i++) {
                // 根据当前精英阶段和等级计算所需经验值
                this.totalExpToLevel += EXP_DATA[eliteStage][(i < EXP_DATA[eliteStage].length ? i : EXP_DATA[eliteStage].length - 1)];
            }
        }
    }

    private boolean checkStageAndLevel() {
        if (eliteStage == 0 && level == BASE_MAX_LEVEL) {
            return true;
        } else if (eliteStage == 1 && level == MAX_LEVEL_ONE) {
            return true;
        } else return eliteStage == 2 && level == MAX_LEVEL_TWO;
    }


    private int getMaxLevelForStage() {
        return switch (this.eliteStage) {
            case 0 -> BASE_MAX_LEVEL;
            case 1 -> MAX_LEVEL_ONE;
            case 2 -> MAX_LEVEL_TWO;
            default -> throw new IllegalArgumentException("Invalid elite stage: " + this.eliteStage);
        };
    }

    public void setValueFormNbt(CompoundTag compoundTag){

    }

    public int getTotalExpToLevel() {
        return totalExpToLevel;
    }

    public int getSkillLevel() {
        return skillLevel;
    }

    public int getLevel() {
        return level;
    }

    public int getTargetLevel() {
        return targetLevel;
    }

    public int getExperience() {
        return experience;
    }

    public int getEliteStage() {
        return eliteStage;
    }

}
