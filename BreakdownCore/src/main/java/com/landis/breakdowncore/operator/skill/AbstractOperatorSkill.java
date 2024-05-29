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
    private float lastTickTime;


    public AbstractOperatorSkill(TriggerType type, AbstractOperator owner, boolean isInfinite, int duration, int sp){
        this.TYPE = type;
        this.owner = owner;
        this.isInfinite = isInfinite;
        this.duration = duration;
        this.skillPoint = sp;
        this.totalSP = this.skillPoint * this.chargeCount;
    }

    public void addSPOne() {
        // 受到攻击时增加SP
        this.sp += 1;
        // 确保SP不会超过最大值
        if (this.sp > this.totalSP) {
            this.sp = this.totalSP;
        }
    }

    // 每个游戏刻度调用的tick方法
    public void tick(AbstractOperatorEntity operatorEntity) {
        if(operatorEntity.getServer() != null && this.TYPE == TriggerType.AUTO && !this.isFullSP()){
            float currentTickTime = operatorEntity.getServer().getCurrentSmoothedTickTime();
            float tickDelta = currentTickTime - this.lastTickTime;
            if(tickDelta >= 20.0f){
                this.sp += operatorEntity.OPERATOR.getNatural_regen_rate();
                this.lastTickTime = currentTickTime;
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
            this.effectSkill();
        }
    };

    public abstract void effectSkill();
    public abstract void endSkill();

}
