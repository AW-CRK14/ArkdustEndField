package com.landis.arkdust.operator;

import com.landis.arkdust.operator.value.StatsPanel;
import com.landis.arkdust.operator.model.BaseOperatorModel;
import com.landis.arkdust.operator.skill.AbstractOperatorSkill;
import com.landis.arkdust.operator.value.base.StatsValueGroup;
import net.minecraft.resources.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

public abstract class AbstractOperator {
    public static final Logger LOGGER = LogManager.getLogger("BREA:Operator:System");
    public final ResourceLocation name;
    public final StatsPanel stats;
    private List<AbstractOperatorSkill> skills;
    private int currentSkillIndex = 0; // 当前激活的技能索引
    public final BaseOperatorModel baseOperatorModel = this.initModel();

    public AbstractOperator(ResourceLocation name, StatsPanel stats){
        this.name = name;
        this.stats = stats;
    }

    // 添加技能到干员
    abstract List<AbstractOperatorSkill> initSkills();

    abstract BaseOperatorModel initModel();

    public List<AbstractOperatorSkill> getOrCreateSkills(){
        if(skills == null){
            skills = this.initSkills();
        }
        return skills;
    }

    public void activateCurrentSkill() {
        if (!getOrCreateSkills().isEmpty() && currentSkillIndex < skills.size()) {
            AbstractOperatorSkill skill = skills.get(currentSkillIndex);
            skill.activateSkill();
        }
    }

    public int getSkillCount(){
        return this.getOrCreateSkills().size();
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
        if (!getOrCreateSkills().isEmpty()) {
            return this.skills.get(this.currentSkillIndex);
        }else return null;
    }


    public double getNaturalRegenRate() {
        return this.stats.getBaseValueFromType(StatsValueGroup.ValueType.NATURAL_REGEN_RATE).getValue();
    }
}
