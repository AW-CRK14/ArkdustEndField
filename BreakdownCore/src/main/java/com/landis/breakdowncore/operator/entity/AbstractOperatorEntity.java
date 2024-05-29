package com.landis.breakdowncore.operator.entity;

import com.landis.breakdowncore.operator.AbstractOperator;
import com.landis.breakdowncore.operator.skill.AbstractOperatorSkill;
import com.landis.breakdowncore.operator.skill.TriggerType;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.*;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import java.util.function.Predicate;

public abstract class AbstractOperatorEntity extends TamableAnimal implements NeutralMob, RangedAttackMob {

    public final AbstractOperator OPERATOR;
    public static final Predicate<LivingEntity> PREY_SELECTOR = p_308741_ -> {
        EntityType<?> entitytype = p_308741_.getType();
        return entitytype == EntityType.SHEEP || entitytype == EntityType.RABBIT || entitytype == EntityType.FOX;
    };


    protected AbstractOperatorEntity(EntityType<? extends TamableAnimal> pEntityType, AbstractOperator operator,Level pLevel) {
        super(pEntityType, pLevel);
        this.OPERATOR = operator;
    }
    public boolean hurt(DamageSource source, float amount) {
        boolean flag = super.hurt(source, amount);
        AbstractOperatorSkill skill = this.OPERATOR.getCurrentSkill();
        if(skill.TYPE == TriggerType.BLOCKED){
            skill.addSPOne();
        }
        return flag;
    }
    @Override
    public boolean doHurtTarget(Entity pEntity) {
        boolean flag = super.doHurtTarget(pEntity);
        AbstractOperatorSkill skill = this.OPERATOR.getCurrentSkill();
        if(skill.TYPE == TriggerType.ATTACK){
            skill.addSPOne();
        }
        return flag;
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new FloatGoal(this));
        this.goalSelector.addGoal(2, new SitWhenOrderedToGoal(this));
        this.goalSelector.addGoal(4, new LeapAtTargetGoal(this, 0.4F));
        this.goalSelector.addGoal(5, new MeleeAttackGoal(this, 1.0, true));
        this.goalSelector.addGoal(6, new FollowOwnerGoal(this, 1.0, 10.0F, 2.0F, false));
        this.goalSelector.addGoal(10, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(10, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, new OwnerHurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new OwnerHurtTargetGoal(this));
        this.targetSelector.addGoal(3, new HurtByTargetGoal(this).setAlertOthers());
        //this.targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(this, Player.class, 10, true, false, this::isAngryAt));
        this.targetSelector.addGoal(5, new NonTameRandomTargetGoal<>(this, Player.class, false, PREY_SELECTOR));
        //this.targetSelector.addGoal(7, new NearestAttackableTargetGoal<>(this, AbstractSkeleton.class, false));
        this.targetSelector.addGoal(8, new ResetUniversalAngerTargetGoal<>(this, true));
        this.registerCustomGoals();
    }


    protected void registerCustomGoals(){

    }


}
