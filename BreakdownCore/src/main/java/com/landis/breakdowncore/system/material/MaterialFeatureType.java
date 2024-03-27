package com.landis.breakdowncore.system.material;

import net.minecraft.resources.ResourceLocation;

public record MaterialFeatureType<I extends IMaterialFeature<I>>(ResourceLocation id, Class<I> clazz){ }