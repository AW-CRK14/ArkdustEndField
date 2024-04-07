package com.landis.breakdowncore.operator;

import com.landis.breakdowncore.operator.value.RareLevel;
import com.landis.breakdowncore.operator.value.base.BaseValuePanel;
import com.landis.breakdowncore.operator.value.LevelValue;

public class BaseOperator  {
    public final String NAME;
    public final RareLevel RARE;
    protected LevelValue levelValue;
    public final BaseValuePanel valuePanel;
    public final BaseOperatorModel baseOperatorModel;
    public BaseOperator(String name, RareLevel rareLevel, BaseValuePanel valuePanel){
        this.NAME = name;
        this.RARE = rareLevel;
        this.valuePanel = valuePanel;
        this.baseOperatorModel = new EmptyModel();
        this.levelValue = rareLevel.getLevelValue();
    }

    public BaseOperator(String name, RareLevel rareLevel, BaseValuePanel valuePanel, BaseOperatorModel baseOperatorModel){
        this.NAME = name;
        this.RARE = rareLevel;
        this.valuePanel = valuePanel;
        this.baseOperatorModel = baseOperatorModel;
        this.levelValue = rareLevel.getLevelValue();
    }




}
