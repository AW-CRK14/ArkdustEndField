package com.landis.arkdust.worldgen.noiserouter;

import com.landis.arkdust.Arkdust;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.levelgen.*;
import net.minecraft.world.level.levelgen.synth.NormalNoise;

import java.util.stream.Stream;

import static net.minecraft.world.level.levelgen.NoiseRouterData.getFunction;

public class Router {
    public static NoiseRouter sarconRouter(HolderGetter<DensityFunction> pDensityFunctions, HolderGetter<NormalNoise.NoiseParameters> pNoiseParameters) {

        //气候数据部分。只取湿度，奇异度，深度，侵蚀；温度，大陆性使用常数列。
        DensityFunction humidityCover = pDensityFunctions.getOrThrow(ResourceKey.create(Registries.DENSITY_FUNCTION, new ResourceLocation(Arkdust.MODID, "sarcon/surface_humidity"))).value();
        DensityFunction river = pDensityFunctions.getOrThrow(ResourceKey.create(Registries.DENSITY_FUNCTION, new ResourceLocation(Arkdust.MODID, "sarcon/surface_river"))).value();

        DensityFunction humidity = DensityFunctions.min(DensityFunctions.constant(0.12), DensityFunctions.add(humidityCover, river));//取和，再钳制最大值  预估区间(-6,1.2)
        DensityFunction depth = DensityFunctions.add(DensityFunctions.constant(1.8), DensityFunctions.mul(humidity, DensityFunctions.constant(-0.8F)));//预估区间(-3,3)

        DensityFunction surfaceRoot = pDensityFunctions.getOrThrow(ResourceKey.create(Registries.DENSITY_FUNCTION, new ResourceLocation(Arkdust.MODID, "sarcon/surface_root_test"))).value();
        DensityFunction surface = NoiseRouterData.postProcess(
                slide(
                        DensityFunctions.add(
                                DensityFunctions.add(DensityFunctions.cache2d(surfaceRoot), depth),
                                DensityFunctions.add(DensityFunctions.constant(3), DensityFunctions.mul(DensityFunctions.constant(-1), river))
                        ),
                        0, 304, 36, 24, -0.08, 0, 24, 0.18)
        );

        DensityFunction withCave = surface;//TODO 添加洞穴

        return new NoiseRouter(
                DensityFunctions.zero(),
                DensityFunctions.zero(),
                DensityFunctions.zero(),
                DensityFunctions.zero(),
                DensityFunctions.zero(),
                humidity,
                DensityFunctions.zero(),
                NoiseRouterData.getFunction(pDensityFunctions, NoiseRouterData.EROSION),
                depth,
                NoiseRouterData.getFunction(pDensityFunctions, NoiseRouterData.RIDGES_FOLDED),
                surface,
                withCave,
                DensityFunctions.zero(),
                DensityFunctions.zero(),
                DensityFunctions.zero()
        );

    }

    public static DensityFunction slide(
            DensityFunction pDensityFunction, int pMinY, int pMaxY, int gradientSOffsetA, int gradientEOffsetA, double lerpFacA, int gradientSOffsetB, int gradientEOffsetB, double lerpFacB
    ) {
        DensityFunction densityFunction1 = DensityFunctions.yClampedGradient(pMinY + pMaxY - gradientSOffsetA, pMinY + pMaxY - gradientEOffsetA, 1.0, 0.0);
        DensityFunction lerped = DensityFunctions.lerp(densityFunction1, lerpFacA, pDensityFunction);
        DensityFunction densityFunction2 = DensityFunctions.yClampedGradient(pMinY + gradientSOffsetB, pMinY + gradientEOffsetB, 0.0, 1.0);
        return DensityFunctions.lerp(densityFunction2, lerpFacB, lerped);
    }
}
