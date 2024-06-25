package com.landis.breakdowncore.system.animation;

import com.landis.breakdowncore.system.animation.model.ModelPartController;
import com.landis.breakdowncore.system.animation.model.data.ModelPoseData;
import com.landis.breakdowncore.util.IModelUtil;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.world.entity.LivingEntity;

public class AnimationFrame{
    private final float tick;
    private final ModelPoseData poseData;

    public AnimationFrame(float tick, ModelPoseData poseData) {
        this.tick = tick;
        this.poseData = poseData;
    }

    public float getTick() {
        return tick;
    }

    public ModelPoseData getPoseData() {
        return poseData;
    }
}