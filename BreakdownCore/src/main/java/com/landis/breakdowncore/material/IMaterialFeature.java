package com.landis.breakdowncore.material;

import net.minecraft.core.Holder;

import java.util.List;

public interface IMaterialFeature {
    List<MaterialAboutRegistry.MaterialItemRegistry.DeferredMIT<?>> getTypes();

    //TODO 依赖模块
}
