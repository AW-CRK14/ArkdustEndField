package com.landis.breakdowncore.system.animation.model.data;

import net.minecraft.client.model.geom.PartPose;

public class ModelPoseData{
    private final int modelPartNum;
    private final PartPose partPose;
    private final ScaleData scaleData;
    public ModelPoseData(int modelPartNum, PartPose partPose, ScaleData scaleData) {
        this.modelPartNum = modelPartNum;
        this.partPose = partPose;
        this.scaleData = scaleData;
    }
    public int modelPartNum(){
        return modelPartNum;
    }
    public PartPose partPose(){
        return partPose;
    }
    public ScaleData scaleData(){
        return scaleData;
    }

    public ModelPoseData(int modelPartNum, PartPose partPose) {
        this.modelPartNum = modelPartNum;
        this.partPose = partPose;
        this.scaleData = ScaleData.DEFAULT;
    }

}
