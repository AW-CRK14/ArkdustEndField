package com.landis.arkdust.registry.worldgen.detector;

import com.landis.arkdust.Arkdust;
import com.landis.arkdust.worldgen.plmodifier.TemperaturePlacement;
import com.mojang.serialization.Codec;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.levelgen.placement.PlacementModifier;
import net.minecraft.world.level.levelgen.placement.PlacementModifierType;

public class PlacementModifierRegistry {
    public static void bootstrap(){
    }

    public static final PlacementModifierType<TemperaturePlacement> TEMPERATURE = registry("temperature",TemperaturePlacement.CODEC);

    public static <P extends PlacementModifier> PlacementModifierType<P> registry(String name, Codec<P> codec){
        return Registry.register(BuiltInRegistries.PLACEMENT_MODIFIER_TYPE, Arkdust.MODID + ":" + name, () -> codec);
    }
}
