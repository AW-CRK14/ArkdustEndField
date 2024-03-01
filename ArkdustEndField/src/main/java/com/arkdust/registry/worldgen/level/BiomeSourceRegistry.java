package com.arkdust.registry.worldgen.level;

import com.arkdust.Arkdust;
import com.arkdust.worldgen.dimension.SarconDimension;
import com.mojang.serialization.Codec;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.biome.BiomeSource;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class BiomeSourceRegistry {
    public static final DeferredRegister<Codec<? extends BiomeSource>> REGISTER = DeferredRegister.create(Registries.BIOME_SOURCE, Arkdust.MODID);

    public static final DeferredHolder<Codec<? extends BiomeSource>,Codec<SarconDimension.Source>> SARCON = REGISTER.register("sarcon",()->SarconDimension.Source.CODEC);
}
