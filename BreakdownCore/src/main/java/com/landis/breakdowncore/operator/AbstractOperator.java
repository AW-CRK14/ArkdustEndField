package com.landis.breakdowncore.operator;

import com.landis.breakdowncore.operator.model.BaseOperatorModel;
import com.landis.breakdowncore.operator.skill.AbstractOperatorSkill;
import com.landis.breakdowncore.operator.value.StatsPanel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

public abstract class AbstractOperator {
    public static final Logger LOGGER = LogManager.getLogger("BREA:Operator:System");
    public final String NAME;
    public final StatsPanel STATS;
    private final List<AbstractOperatorSkill> skills = this.initSkills();
    private int currentSkillIndex = 0; // 当前激活的技能索引
    public final BaseOperatorModel baseOperatorModel = this.initModel();

    public AbstractOperator(String name, StatsPanel stats){
        this.NAME = name;
        this.STATS = stats;
    }

    // 添加技能到干员
    abstract List<AbstractOperatorSkill> initSkills();

    abstract BaseOperatorModel initModel();

    public void activateCurrentSkill() {
        if (!skills.isEmpty() && currentSkillIndex < skills.size()) {
            AbstractOperatorSkill skill = skills.get(currentSkillIndex);
            skill.activateSkill();
        }
    }

    public int getSkillCount(){
        return this.skills.size();
    }

    // 切换到下一个技能
    public void switchToNextSkill() {
        // 获取技能列表中的技能数量
        int skillCount = this.getSkillCount();
        // 如果有技能可供切换
        if (skillCount > 1) {
            // 使用模运算来循环切换技能
            this.currentSkillIndex = (this.currentSkillIndex + 1) % skillCount;
        } else if (skillCount == 1) {
            // 如果只有一个技能，不需要切换
            // 可以在这里添加处理逻辑，例如不执行任何操作或重置技能状态
        } else {
            // 如果没有技能，处理错误情况
            // 可以在这里添加错误处理或日志记录
        }
    }
    public AbstractOperatorSkill getCurrentSkill() {
        if (!skills.isEmpty()) {
            return this.skills.get(this.currentSkillIndex);
        }else return null;
    }


    public double getNaturalRegenRate(){
        return this.STATS.getNaturalRegenRate().getValue();
    }

}
