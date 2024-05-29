package com.landis.breakdowncore.operator;

import com.landis.breakdowncore.operator.model.BaseOperatorModel;
import com.landis.breakdowncore.operator.model.EmptyModel;
import com.landis.breakdowncore.operator.skill.AbstractOperatorSkill;
import com.landis.breakdowncore.operator.value.RareLevel;
import com.landis.breakdowncore.operator.value.base.VariableValue;

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
        if(this.getSkillCount() == 3){
            if(this.currentSkillIndex == 2){
                this.currentSkillIndex = 0;
            }else{
                currentSkillIndex += 1;
            }
        }else{

        }
    }


    // 结束当前技能
    public void endCurrentSkill() {
        if (!skills.isEmpty() && currentSkillIndex < skills.size()) {
            AbstractOperatorSkill skill = skills.get(currentSkillIndex);
            skill.endSkill();
        }
    }
    public AbstractOperatorSkill getCurrentSkill(){
        return this.skills.get(this.currentSkillIndex);
    }

    // 检查技能是否就绪
    public boolean isSkillReady() {
        // 实现具体的检查逻辑，例如检查冷却时间
        return this.skills.get(this.currentSkillIndex).isActive();
    }


    public final float getNaturalRegenRate(){
        return this.naturalRegenRate;
    }

}
