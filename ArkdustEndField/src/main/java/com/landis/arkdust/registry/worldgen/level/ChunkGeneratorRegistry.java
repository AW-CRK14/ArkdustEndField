package com.landis.arkdust.registry.worldgen.level;

import com.landis.arkdust.Arkdust;
import com.landis.arkdust.worldgen.dimension.SarconDimension;
import com.mojang.serialization.Codec;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ChunkGeneratorRegistry {
    public static final DeferredRegister<Codec<? extends ChunkGenerator>> REGISTER = DeferredRegister.create(Registries.CHUNK_GENERATOR, Arkdust.MODID);

    public static final DeferredHolder<Codec<? extends ChunkGenerator>,Codec<SarconDimension.Generator>> SARCON = REGISTER.register("sarcon",()->SarconDimension.Generator.CODEC);
}
