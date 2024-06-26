package com.landis.breakdowncore.system.animation;

import com.landis.breakdowncore.system.animation.model.ModelPartController;
import com.landis.breakdowncore.system.animation.model.data.ModelPoseData;
import com.landis.breakdowncore.util.IModelUtil;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.world.entity.LivingEntity;

import java.util.List;
import java.util.Map;

public class AnimationFrame{
    private final int tick;
    private final List<ModelPoseData> poseData;

    public AnimationFrame(int tick, List<ModelPoseData> poseData) {
        this.tick = tick;
        this.poseData = poseData;
    }

    public int getTick() {
        return tick;
    }

    public List<ModelPoseData> getPoseData() {
        return poseData;
    }
}