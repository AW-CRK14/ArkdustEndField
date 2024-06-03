package com.landis.breakdowncore.operator.value;

public class ResumePanel {
    public final String operatorName;
    public final OperatorOccupation operatorOccupation;

    public ResumePanel(String operatorName, OperatorOccupation operatorOccupation){
        this.operatorName = operatorName;
        this.operatorOccupation = operatorOccupation;
    }
    

    public static class OperatorOccupation{
        public final Occupation occupation;
        public final String occupationName;
        public final StatsPanel defaultState;

        public OperatorOccupation(Occupation occupation, String name, StatsPanel defaultState) {
            this.occupation = occupation;
            this.occupationName = name;
            this.defaultState = defaultState;
        }
    }
    public enum Occupation{
        VANGUARD, // 先锋
        GUARD, // 近卫
        DEFENDER, // 重装
        SNIPER, // 射手
        CASTER, // 术士
        MEDIC, // 医疗
        SUPPORTER, // 辅助
        SPECIALIST, // 特种
    }
}
