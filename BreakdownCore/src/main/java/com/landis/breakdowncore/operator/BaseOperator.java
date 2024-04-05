package com.landis.breakdowncore.operator;

import com.landis.breakdowncore.operator.value.ValuePanel;
import com.landis.breakdowncore.operator.value.base.LevelValue;
import com.landis.breakdowncore.operator.value.base.RareLevel;

public class BaseOperator {
    public final String NAME;
    public final RareLevel RARE;
    protected LevelValue levelValue;
    public final ValuePanel valuePanel;
    public final BaseOperatorModel baseOperatorModel;
    public BaseOperator(String name, RareLevel rareLevel, ValuePanel valuePanel){
        this.NAME = name;
        this.RARE = rareLevel;
        this.valuePanel = valuePanel;
        this.baseOperatorModel = new EmptyModel();
        this.levelValue = rareLevel.getLevelValue();
    }

    public BaseOperator(String name, RareLevel rareLevel, ValuePanel valuePanel, BaseOperatorModel baseOperatorModel){
        this.NAME = name;
        this.RARE = rareLevel;
        this.valuePanel = valuePanel;
        this.baseOperatorModel = baseOperatorModel;
        this.levelValue = rareLevel.getLevelValue();
    }


}
