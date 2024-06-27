package com.landis.breakdowncore.system.animation.model;

import net.minecraft.client.model.geom.ModelPart;

import javax.annotation.Nullable;

public interface ModelPartController{
    @Nullable
    ModelPart getModelPartByNum(int num);
    @Nullable
    Iterable<ModelPart> ModelParts();

}