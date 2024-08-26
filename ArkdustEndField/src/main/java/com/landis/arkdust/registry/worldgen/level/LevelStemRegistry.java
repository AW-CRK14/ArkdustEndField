package com.landis.arkdust.registry.worldgen.level;

import com.landis.arkdust.worldgen.dimension.SarconDimension;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;

public class LevelStemRegistry {
    public static void bootstrap(BootstapContext<LevelStem> bootstrap){
        HolderGetter<Level> levelGetter = bootstrap.lookup(Registries.DIMENSION);
        HolderGetter<DimensionType> levelTypeGetter = bootstrap.lookup(Registries.DIMENSION_TYPE);
        HolderGetter<Biome> biomeGetter = bootstrap.lookup(Registries.BIOME);
        HolderGetter<NoiseGeneratorSettings> noiseGetter = bootstrap.lookup(Registries.NOISE_SETTINGS);
//        bootstrap.register(SarconDimension.STEM,new LevelStem(levelTypeGetter.getOrThrow(SarconDimension.TYPE),new SarconDimension.Generator(new SarconDimension.Source(biomeGetter),noiseGetter.getOrThrow(SarconDimension.SETTING))));
        bootstrap.register(SarconDimension.STEM,new LevelStem(levelTypeGetter.getOrThrow(SarconDimension.TYPE),new NoiseBasedChunkGenerator(new SarconDimension.Source(biomeGetter),noiseGetter.getOrThrow(SarconDimension.SETTING))));
    }
}
