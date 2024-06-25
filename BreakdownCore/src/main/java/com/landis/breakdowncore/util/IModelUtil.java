package com.landis.breakdowncore.util;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;

import javax.annotation.Nullable;

public class IModelUtil {
    @Nullable
    public static ModelPart getModelPartByNum(HumanoidModel<?> model, int num){
        return switch (num) {
            case 0 -> model.body;
            case 1 -> model.rightArm;
            case 2 -> model.leftArm;
            case 3 -> model.rightLeg;
            case 4 -> model.leftLeg;
            case 5 -> model.hat;
            default -> null;
        };
    }


}
