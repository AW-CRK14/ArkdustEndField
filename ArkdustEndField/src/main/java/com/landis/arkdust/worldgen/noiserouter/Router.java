package com.landis.arkdust.worldgen.noiserouter;

import com.landis.arkdust.Arkdust;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.*;
import net.minecraft.world.level.levelgen.synth.NormalNoise;

public class Router {
    public static NoiseRouter sarconRouter(HolderGetter<DensityFunction> pDensityFunctions, HolderGetter<NormalNoise.NoiseParameters> pNoiseParameters) {

        //气候数据部分。只取湿度，奇异度，深度，侵蚀；温度，大陆性使用常数列。
        DensityFunction humidityCover = DensityFunctions.noise(pNoiseParameters.getOrThrow(Noises.NOODLE_THICKNESS),0.35,0);
        DensityFunction river = getFunction(pDensityFunctions,new ResourceLocation(Arkdust.MODID,"sarcon/river"));

        DensityFunction surfaceRoot = getFunction(pDensityFunctions, new ResourceLocation(Arkdust.MODID, "sarcon/desert_gen"));
        DensityFunction surface = DensityFunctions.mul(DensityFunctions.blendDensity(slide(
                DensityFunctions.add(DensityFunctions.yClampedGradient(32,160,0.31,-0.97),surfaceRoot),
                24, 32, 160, 176, 0.1,-0.1)), DensityFunctions.constant(0.64)
        ).squeeze();
//        DensityFunction surface = NoiseRouterData.postProcess(slide(
//                DensityFunctions.add(DensityFunctions.yClampedGradient(32,160,0.31,-0.97),surfaceRoot),
//                24, 32, 160, 176, 0.1,-0.1));


        DensityFunction withCave = surface;//TODO 添加洞穴

        return new NoiseRouter(
                DensityFunctions.zero(),
                DensityFunctions.zero(),
                DensityFunctions.zero(),
                DensityFunctions.zero(),
                DensityFunctions.zero(),
                humidityCover,
                DensityFunctions.zero(),
                NoiseRouterData.getFunction(pDensityFunctions, NoiseRouterData.EROSION),
                river,
                NoiseRouterData.getFunction(pDensityFunctions, NoiseRouterData.RIDGES_FOLDED),
                surface,
                withCave,
                DensityFunctions.zero(),
                DensityFunctions.zero(),
                DensityFunctions.zero()
        );

    }

    public static DensityFunction add(double value, DensityFunction origin) {
        return DensityFunctions.add(DensityFunctions.constant(value), origin);
    }

    public static DensityFunction mul(double value, DensityFunction origin) {
        return DensityFunctions.mul(DensityFunctions.constant(value), origin);
    }

    public static DensityFunction slide(
            DensityFunction originFunc, int appearStart, int appearEnd, int fadeStart, int fadeEnd, double solid, double air
    ) {
        DensityFunction fadeFunc = DensityFunctions.yClampedGradient(fadeStart, fadeEnd, 1.0, 0.0);
        DensityFunction appearFunc = DensityFunctions.yClampedGradient(appearStart,appearEnd, 0.0, 1.0);
        DensityFunction handler = DensityFunctions.min(appearFunc,fadeFunc);
        return DensityFunctions.add(DensityFunctions.mul(handler,originFunc),DensityFunctions.add(
                DensityFunctions.yClampedGradient(appearStart,appearEnd, solid, 0.0),
                DensityFunctions.yClampedGradient(fadeStart, fadeEnd, 0.0, air)
        ));
    }

    public static DensityFunction getFunction(HolderGetter<DensityFunction> getter, ResourceLocation location) {
        return new DensityFunctions.HolderHolder(getter.getOrThrow(ResourceKey.create(Registries.DENSITY_FUNCTION, location)));
    }
}
