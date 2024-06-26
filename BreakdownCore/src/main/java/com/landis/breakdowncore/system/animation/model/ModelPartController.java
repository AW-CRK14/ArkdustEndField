package com.landis.breakdowncore.system.animation.model;

import com.landis.breakdowncore.system.animation.model.data.ModelPoseData;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelPart;

import javax.annotation.Nullable;

public interface ModelPartController{
    @Nullable
    ModelPart getModelPartByNum(int num);
    @Nullable
    Iterable<ModelPart> ModelParts();

}