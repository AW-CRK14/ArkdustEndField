package com.arkdust.registry.worldgen.detector;

import com.arkdust.Arkdust;
import com.arkdust.worldgen.feature.SingleBlockFeature;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class FeatureRegistry {
        public static final DeferredRegister<Feature<?>> REGISTER = DeferredRegister.create(Registries.FEATURE,Arkdust.MODID);

        public static final DeferredHolder<Feature<?>, SingleBlockFeature> SINGLE_BLOCK = REGISTER.register("single_block",()-> new SingleBlockFeature(SingleBlockFeature.BlocksConf.CODEC));
}
