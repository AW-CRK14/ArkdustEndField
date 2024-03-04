package com.landis.arkdust.registry.worldgen.level;

import com.landis.arkdust.Arkdust;
import com.landis.arkdust.datagen.BiomeGen;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;

import java.util.List;

public class BiomeRegistry {
    public static void bootstrap(BootstapContext<Biome> context){
        Sarcon.bootstrap(context);
    }

    public static class Sarcon{
        public static void bootstrap(BootstapContext<Biome> context){

            //Sarcon biome builder
            context.register(DESERT, BiomeGen.testBiomeBuilder());
            context.register(DEGRADED_GRASSLAND, BiomeGen.testBiomeBuilder());
            context.register(PLATEAU, BiomeGen.testBiomeBuilder());
            context.register(SPARSE_RAIN_FOREST, BiomeGen.testBiomeBuilder());
            context.register(RAIN_FOREST, BiomeGen.testBiomeBuilder());
            context.register(OASIS, BiomeGen.testBiomeBuilder());
            context.register(GOLDEN_DESERT, BiomeGen.testBiomeBuilder());
            context.register(SALT_DESERT, BiomeGen.testBiomeBuilder());
            context.register(GRASSLAND, BiomeGen.testBiomeBuilder());
            context.register(DEAD_FOREST, BiomeGen.testBiomeBuilder());
            context.register(FRUITFUL_SOIL, BiomeGen.testBiomeBuilder());
            context.register(GEM_FOREST, BiomeGen.testBiomeBuilder());
        }

        public static final ResourceKey<Biome> DESERT = registry("desert");
        public static final ResourceKey<Biome> DEGRADED_GRASSLAND = registry("degraded_grassland");
        public static final ResourceKey<Biome> PLATEAU = registry("plateau");
        public static final ResourceKey<Biome> SPARSE_RAIN_FOREST = registry("sparse_rain_forest");
        public static final ResourceKey<Biome> RAIN_FOREST = registry("rain_forest");

        public static final ResourceKey<Biome> OASIS = registry("oasis");
        public static final ResourceKey<Biome> GOLDEN_DESERT = registry("golden_desert");
        public static final ResourceKey<Biome> SALT_DESERT = registry("hot_land");//源石沙漠

        public static final ResourceKey<Biome> GRASSLAND = registry("grassland");

        public static final ResourceKey<Biome> DEAD_FOREST = registry("dead_forest");
        public static final ResourceKey<Biome> FRUITFUL_SOIL = registry("fruitful_soil");
        public static final ResourceKey<Biome> GEM_FOREST = registry("gem_forest");

//        public static final ResourceKey<Biome> HOT_LAND = registry("hot_land");
//        public static final ResourceKey<Biome> SECONDARY_RAINFOREST = registry("secondary_rainforest");

        public static final List<ResourceKey<Biome>> BIOMES = List.of(DESERT,DEGRADED_GRASSLAND,PLATEAU,SPARSE_RAIN_FOREST,RAIN_FOREST,OASIS,GOLDEN_DESERT,SALT_DESERT,GRASSLAND,DEAD_FOREST,FRUITFUL_SOIL,GEM_FOREST);

        public static ResourceKey<Biome> registry(String name){
            return BiomeRegistry.registry("sarcon",name);
        };
    }

    public static ResourceKey<Biome> registry(String level,String name){
        return ResourceKey.create(Registries.BIOME,new ResourceLocation(Arkdust.MODID,level + "/" + name));
    };
}
