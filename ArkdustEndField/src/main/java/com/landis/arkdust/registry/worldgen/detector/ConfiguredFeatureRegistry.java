package com.landis.arkdust.registry.worldgen.detector;

import com.landis.arkdust.Arkdust;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ConfiguredFeatureRegistry {

    public static void registry(IEventBus bus){
        OverWorld.REGISTER.register(bus);
    }
    public static void bootstrap(BootstapContext<ConfiguredFeature<?,?>> context) {
    }

    public static class OverWorld{

        public static final DeferredRegister<ConfiguredFeature<?,?>> REGISTER = DeferredRegister.create(Registries.CONFIGURED_FEATURE, Arkdust.MODID);

//        public static final DeferredHolder<ConfiguredFeature<?, ?>, ConfiguredFeature<SingleBlockFeature.BlocksConf, SingleBlockFeature>> SINGLE_SKELETON_HEAD =
//                REGISTER.register("single_skeleton_head",()->new ConfiguredFeature<>(FeatureRegistry.SINGLE_BLOCK.get(),
//                new SingleBlockFeature.BlocksConf(Blocks.SKELETON_SKULL.defaultBlockState())));
//
//        public static final DeferredHolder<ConfiguredFeature<?, ?>, ConfiguredFeature<SingleBlockFeature.BlocksConf, SingleBlockFeature>> SINGLE_UNAC_SPIRIT_STONE =
//                REGISTER.register("single_unac_spirit_stone",()->new ConfiguredFeature<>(FeatureRegistry.SINGLE_BLOCK.get(),
//                        new SingleBlockFeature.BlocksConf(BlockRegistry.SPIRIT_STONE_UNACTIVATED.get().defaultBlockState())));

        public static void bootstrap(BootstapContext<ConfiguredFeature<?,?>> context){

        }
    }
}
