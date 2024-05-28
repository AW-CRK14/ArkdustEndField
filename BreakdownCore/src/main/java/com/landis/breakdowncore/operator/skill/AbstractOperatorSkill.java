package com.landis.breakdowncore.operator.skill;

import com.landis.breakdowncore.operator.AbstractOperator;

public abstract class AbstractOperatorSkill{
    public final TriggerType TYPE;
    public final AbstractOperator owner;
    public final boolean isInfinite;
    private int duration;
    private int sp;


    public AbstractOperatorSkill(TriggerType type, AbstractOperator owner, boolean isInfinite, int duration, int sp){
        this.TYPE = type;
        this.owner = owner;
        this.isInfinite = isInfinite;
        this.duration = duration;
        this.sp = sp;
    }

    public final void tick(){
        if (TYPE == TriggerType.ATTACK){

        }else if (TYPE == TriggerType.BLOCKED){

        }else{

        }
    }
    public abstract void activateSkill(AbstractOperator operator);
    public abstract void endSkill(AbstractOperator operator);

}
