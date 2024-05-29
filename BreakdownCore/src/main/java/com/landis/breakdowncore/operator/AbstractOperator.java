package com.landis.breakdowncore.operator;

import com.landis.breakdowncore.operator.model.BaseOperatorModel;
import com.landis.breakdowncore.operator.model.EmptyModel;
import com.landis.breakdowncore.operator.skill.AbstractOperatorSkill;
import com.landis.breakdowncore.operator.value.RareLevel;
import com.landis.breakdowncore.operator.value.base.VariableValue;
import net.minecraft.world.entity.ai.goal.Goal;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractOperator {
    public final String NAME;
    public RareLevel rareLevel;
    public int skillLevel = 1;
    public VariableValue health;
    public VariableValue defense;
    public VariableValue offensiveCapability;
    public VariableValue magicResistance;
    public VariableValue attackInterval;
    public VariableValue respawnTime;
    public VariableValue cost;
    private float naturalRegenRate = 1.0f;
    private final List<AbstractOperatorSkill> skills = this.initSkills();
    private int currentSkillIndex = 0; // 当前激活的技能索引
    public final BaseOperatorModel baseOperatorModel = this.initModel();

    public AbstractOperator(String name){
        this.NAME = name;
    }

    abstract void initValues();

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

    public List<Goal> getOtherOperatorGoal(){
        return new ArrayList<>();
    }

    // 结束当前技能
    public void endCurrentSkill() {
        if (!skills.isEmpty() && currentSkillIndex < skills.size()) {
            AbstractOperatorSkill skill = skills.get(currentSkillIndex);
            skill.endSkill();
        }
    }
    public AbstractOperatorSkill getCurrentSkill() {
        return this.skills.get(this.currentSkillIndex);
    }

    // 检查技能是否就绪
    public boolean isSkillReady() {
        // 实现具体的检查逻辑，例如检查冷却时间
        return this.getCurrentSkill().isActive();
    }


    public float getNaturalRegenRate(){
        return this.naturalRegenRate;
    }

}
