package com.landis.breakdowncore.operator.value.base;

public enum RareLevel{

    ONE_STAR(30,0,0),TWO_STAR(0,0),THREE_STAR(55,0),FOUR_STAR(60,70),FIVE_STAR(70,80),SIX_STAR(80,90);
    final LevelValue levelValue;
    RareLevel(int maxLevel1,int maxLevel2){
        this.levelValue = new LevelValue(maxLevel1,maxLevel2);
    }
    RareLevel(int baseMaxLevel,int maxLevel1,int maxLevel2){
        this.levelValue = new LevelValue(baseMaxLevel,maxLevel1,maxLevel2);
    }

    public LevelValue getLevelValue() {
        return levelValue;
    }
}