package com.landis.arkdust.worldgen.dimension;

import com.landis.arkdust.Arkdust;
import com.landis.arkdust.registry.worldgen.level.BiomeRegistry;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.KeyDispatchDataCodec;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.biome.Climate;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import net.minecraft.world.level.levelgen.SurfaceRules;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public class SarconDimension {
    public static final ResourceKey<LevelStem> STEM = ResourceKey.create(Registries.LEVEL_STEM, new ResourceLocation(Arkdust.MODID, "sarcon"));
    public static final ResourceKey<Level> LEVEL = ResourceKey.create(Registries.DIMENSION, new ResourceLocation(Arkdust.MODID, "sarcon"));
    public static final ResourceKey<DimensionType> TYPE = ResourceKey.create(Registries.DIMENSION_TYPE, new ResourceLocation(Arkdust.MODID, "sarcon_type"));
    public static final ResourceKey<NoiseGeneratorSettings> SETTING = ResourceKey.create(Registries.NOISE_SETTINGS, new ResourceLocation(Arkdust.MODID, "sarcon"));

    public static class Source extends BiomeSource {
        public static final Logger LOGGER = LogManager.getLogger(Arkdust.getLogName("worldgen.dimension.biome.sarcon"));

        public static final Codec<Source> CODEC = RecordCodecBuilder.create(obj -> obj.group(
                Biome.CODEC.listOf().fieldOf("biomes").forGetter(ins -> ins.BIOMES)
        ).apply(obj, obj.stable(Source::new)));

        private final List<Holder<Biome>> BIOMES;
        private final Map<ResourceKey<Biome>, Holder<Biome>> BIOME_MAP;

        public Source(HolderGetter<Biome> getter) {
            this.BIOME_MAP = new HashMap<>();
            BiomeRegistry.Sarcon.BIOMES.forEach(obj -> BIOME_MAP.put(obj, getter.getOrThrow(obj)));
            this.BIOMES = BIOME_MAP.values().stream().toList();
        }

        public Source(List<Holder<Biome>> biomes) {
            BIOMES = biomes;
            BIOME_MAP = new HashMap<>();
            for (Holder<Biome> holder : biomes) {
                if (holder.unwrapKey().isEmpty()) LOGGER.warn("Biome holder({})'s element is not exist", holder);
                BIOME_MAP.put(holder.unwrapKey().get(), holder);
            }
        }

        @Override
        protected Codec<? extends BiomeSource> codec() {
            return CODEC;
        }

        @Override
        protected Stream<Holder<Biome>> collectPossibleBiomes() {
            return BIOMES.stream();
        }

        @Override
        public Holder<Biome> getNoiseBiome(int pX, int pY, int pZ, Climate.Sampler pSampler) {//TODO

            return BIOME_MAP.get(BiomeRegistry.Sarcon.PLATEAU);
        }

        /* 0 - 20    : 沙漠
         * 20 - 27.5 : 退化草原
         * 30 - 50   : 山崖
         * 50 - 60   : 高山草甸
         * 60 - 75   : 稀疏雨林
         * 75 - 100  : 雨林*/
        public static float getBiomeTypeMark(int pX, int pY) {
            final int k = -1200;
            long y = (long) pY << 2 + 2;
            long x = (long) pX << 2 + 2;
            float distance = (y + 2 * x - k) * 0.447F;
            double origin = Math.sqrt(x * x + y * y);

            if (distance < 0) {
                if (distance > -40) return 50 + distance * 0.5F;

                int desertPoint = (int) (Math.min(1200 + origin / 20, 2400) * -1);
                if (distance <= -8000) {
                    return 0;
                } else if (distance <= desertPoint) {
                    return 20 - (desertPoint - distance) / 450F;
                } else {
                    return 27.5F - 7.5F * distance / desertPoint;
                }
            } else {
                if (distance > 13000) return 100;
                int forestPoint = (int) Math.min(1800 + origin / 40, 2000);
                if (distance < forestPoint) return 50 + distance / forestPoint * 10;
                int rainforestPoint = (int) Math.min(4000 + origin / 30, 5000);
                if (distance < rainforestPoint)
                    return 60 + 15 * (distance - forestPoint) / (rainforestPoint - forestPoint);
                else return 75 + (distance - rainforestPoint) / (520 - (float) rainforestPoint / 25);
            }
        }
    }

//    public record DensityFunc


    public static class Generator extends DimensionPre.YRelativeDimensionGenerator {

        public static final Codec<Generator> CODEC = RecordCodecBuilder.create(
                p_255585_ -> p_255585_.group(
                                BiomeSource.CODEC.fieldOf("biome_source").forGetter(ChunkGenerator::getBiomeSource),
                                NoiseGeneratorSettings.CODEC.fieldOf("settings").forGetter(NoiseBasedChunkGenerator::generatorSettings)
                        )
                        .apply(p_255585_, p_255585_.stable(Generator::new))
        );

        public Generator(BiomeSource pBiomeSource, Holder<NoiseGeneratorSettings> settings) {
            super(pBiomeSource, settings, 0.3F);
        }

        @Override
        protected Codec<? extends ChunkGenerator> codec() {
            return CODEC;
        }

        public static int getHeight(int x, int y) {
            // 定义一个常量k，表示直线y = -2x + k的截距
            final int k = -1200;
            // 定义一个变量height，用于存储高度值
            float height;
            // 判断坐标(x, y)是否在直线y = -2x + k上
            if (y == -2 * x + k) {
                // 如果在直线上，高度值为344
                height = 343;
            } else {
                // 计算坐标(x, y)到直线y = -2x + k的距离
                float distance = Math.abs(y + 2 * x - k) / 2.236F;
                // 计算坐标(x, y)到原点(0, 0)的距离
                double origin = Math.sqrt(x * x + y * y);
                if (y < -2 * x + k) {//沙漠半区高度计算
                    double m = Math.min(1200 + origin / 20, 2400);
                    if (distance < 40) {//断崖区高度计算
                        height = 344 - distance * 3.2F;
                    } else if (distance < m) {//退化草原区高度计算
                        height = (float) (216 - (distance / m) * 24);
                    } else {//沙漠区高度计算
                        height = 192;
                    }
                } else {//雨林半区高度计算
                    // 计算n的值，高山草甸边界
                    double n = Math.min(1800 + origin / 40, 2000);
                    // 计算p的值，稀疏雨林边界
                    double p = Math.min(4000 + origin / 30, 5000);
                    // 判断距离是否小于n
                    if (distance < n) {//高山草甸高度计算
                        height = (float) (344 - distance / n * 30);
                    } else if (distance < p) {//稀疏雨林高度计算
                        height = (float) (314 - 50 * (distance - n) / p - n);
                    } else {//密林高度计算
                        height = (float) Math.max(264 - (distance - p) / 100, 228);
                    }
                }
            }
            // 返回高度值
            return (int) (height - 160);
        }

        public int offsetYAt(int x, int z) {
            return getHeight(x, z);
        }

        public int extremeYOffset(boolean max) {
            return max ? 343 - 160 : 192 - 160;
        }

        @Override
        public int getMinY() {
            return 0;
        }

        @Override
        public int getGenDepth() {
            return (int) (generatorSettings().value().noiseSettings().height() * routerScaleFactor + extremeYOffset(true));
        }
    }

    public static class SurfaceSource implements SurfaceRules.RuleSource {
        public static final Codec<SurfaceSource> CODEC = Codec.unit(new SurfaceSource());

        @Override
        public KeyDispatchDataCodec<? extends SurfaceRules.RuleSource> codec() {
            return new KeyDispatchDataCodec<>(CODEC);
        }

        @Override
        public SurfaceRules.SurfaceRule apply(SurfaceRules.Context context) {
            return new SurfaceRule(context);
        }
    }

    public record SurfaceRule(SurfaceRules.Context context) implements SurfaceRules.SurfaceRule {
        @Nullable
        @Override
        public BlockState tryApply(int pX, int pY, int pZ) {
//            Holder<Biome> biome = context.biome.get();
            return Blocks.STONE.defaultBlockState();
        }
    }
}
