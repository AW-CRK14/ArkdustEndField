package com.arkdust.datagen;

import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeGenerationSettings;
import net.minecraft.world.level.biome.BiomeSpecialEffects;
import net.minecraft.world.level.biome.MobSpawnSettings;

public class BiomeGen {
    public static final BiomeSpecialEffects DEFAULT_EFFECT = new BiomeSpecialEffects.Builder().waterColor(4159204)
            .waterFogColor(329011)
            .fogColor(12638463)
            .skyColor(16776961).build();

    public static Biome testBiomeBuilder(){
        return new Biome.BiomeBuilder().temperatureAdjustment(Biome.TemperatureModifier.NONE).mobSpawnSettings(MobSpawnSettings.EMPTY).generationSettings(BiomeGenerationSettings.EMPTY).specialEffects(DEFAULT_EFFECT).downfall(0.5f).temperature(0.8f).build();
    }
}
