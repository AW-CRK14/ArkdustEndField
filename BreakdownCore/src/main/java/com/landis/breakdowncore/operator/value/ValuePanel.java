package com.landis.breakdowncore.operator.value;

import com.landis.breakdowncore.operator.value.base.BaseValuePanel;

public class ValuePanel extends BaseValuePanel {
    public void calculateValues(int level, int eliteLevel) {
        double baseProficiency = 0.01 + 0.32 * eliteLevel; // 每个精英阶段1级时的练度
        double maxLevel = 50 + 20 * eliteLevel;
        this.proficiency = baseProficiency + (this.maxProficiency - baseProficiency) * (level - 1) / (maxLevel - 1);
        if (eliteLevel == 2 && level == 90) {
            this.proficiency += 0.01; // 在达到精英二级90级时，练度需要主动+1%
        }
        // 使用练度来计算其他属性值
        // this.health = this.baseHealth + (this.maxHealth - this.baseHealth) * this.proficiency;
        // ... 计算其他属性值
    }
}
