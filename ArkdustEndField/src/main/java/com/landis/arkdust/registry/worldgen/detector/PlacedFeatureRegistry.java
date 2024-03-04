package com.landis.arkdust.registry.worldgen.detector;

import com.landis.arkdust.Arkdust;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.placement.*;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.List;

public class PlacedFeatureRegistry {
    public static void registry(IEventBus bus){
        OverWorld.REGISTER.register(bus);
    }
    public static void bootstrap(BootstapContext<ConfiguredFeature<?,?>> context) {
    }

    public static class OverWorld{

        public static final DeferredRegister<PlacedFeature> REGISTER = DeferredRegister.create(Registries.PLACED_FEATURE, Arkdust.MODID);

//        public static final DeferredHolder<PlacedFeature,PlacedFeature> SINGLE_SKELETON_HEAD =
//                REGISTER.register("single_skeleton_head",()->create(ConfiguredFeatureRegistry.OverWorld.SINGLE_SKELETON_HEAD,
//                        new TemperaturePlacement(1.9F,true),RarityFilter.onAverageOnceEvery(256), InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP_TOP_SOLID));
//
//        public static final DeferredHolder<PlacedFeature,PlacedFeature> SINGLE_UNAC_SPIRIT_STONE =
//                REGISTER.register("single_unac_spirit_stone",()->create(ConfiguredFeatureRegistry.OverWorld.SINGLE_UNAC_SPIRIT_STONE,
//                        new TemperaturePlacement(0.1F,false),RarityFilter.onAverageOnceEvery(256), InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP_TOP_SOLID));

        public static void bootstrap(BootstapContext<ConfiguredFeature<?,?>> context){

        }

        private static PlacedFeature create(Holder<ConfiguredFeature<?, ?>> feature, PlacementModifier... placement){
            return new PlacedFeature(feature,List.of(placement));
        }

    }
}
