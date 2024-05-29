package com.landis.breakdowncore.operator.skill;

import com.landis.breakdowncore.operator.AbstractOperator;
import com.landis.breakdowncore.operator.entity.AbstractOperatorEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.LivingEntity;

public abstract class AbstractOperatorSkill{
    public final TriggerType TYPE;
    public final AbstractOperator owner;
    private boolean isActive;
    public final boolean isInfinite;
    private int duration;
    private int skillPoint;
    private float sp;
    private int chargeCount = 1;
    private int totalSP;

    public AbstractOperatorSkill(TriggerType type, AbstractOperator owner, int duration, int sp){
        this.TYPE = type;
        this.owner = owner;
        this.isInfinite = false;
        this.duration = duration;
        this.skillPoint = sp;
        this.totalSP = this.skillPoint * this.chargeCount;
    }

    public AbstractOperatorSkill(TriggerType type, AbstractOperator owner, boolean isInfinite, int duration, int sp){
        this.TYPE = type;
        this.owner = owner;
        this.isInfinite = isInfinite;
        this.duration = duration;
        this.skillPoint = sp;
        this.totalSP = this.skillPoint * this.chargeCount;
    }
    public AbstractOperatorSkill(TriggerType type, AbstractOperator owner, boolean isInfinite, int duration, int sp,int chargeCount){
        this.TYPE = type;
        this.owner = owner;
        this.isInfinite = isInfinite;
        this.duration = duration;
        this.skillPoint = sp;
        this.chargeCount = chargeCount;
        this.totalSP = this.skillPoint * this.chargeCount;
    }

    public void addSPOne() {
        this.sp += 1;
        if (this.sp > this.totalSP) {
            this.sp = this.totalSP;
        }
    }

    // 每个游戏刻度调用的tick方法
    public void tick(int tickCount) {
        if(this.TYPE == TriggerType.AUTO && !this.isFullSP()){
            if(tickCount % 20 == 0){
                this.sp += owner.getNaturalRegenRate();
            }
            if (this.sp > this.totalSP) {
                this.sp = this.totalSP;
            }
        }
    }

    public void setChargeCount(int count){
        this.chargeCount = count;
    }

    public boolean isFullSP(){
        return this.sp == (float) this.totalSP;
    }

    public boolean isReady(){
        return this.sp >= this.skillPoint;
    }

    public final boolean isActive(){
        return this.isActive;
    }

    public final void activateSkill(){
        if(this.isReady()){
            this.sp -= this.skillPoint;
            this.isActive = true;
            this.effectSkill();
        }
    };

    public abstract void effectSkill();
    public abstract void endSkill();

}
