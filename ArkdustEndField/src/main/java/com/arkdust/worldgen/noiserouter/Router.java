package com.arkdust.worldgen.noiserouter;

import net.minecraft.core.HolderGetter;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.levelgen.*;
import net.minecraft.world.level.levelgen.synth.NormalNoise;

import java.util.stream.Stream;

public class Router {
    public static NoiseRouter overworldWithoutCaves(HolderGetter<DensityFunction> pDensityFunctions, HolderGetter<NormalNoise.NoiseParameters> pNoiseParameters) {
        DensityFunction densityfunction14 = NoiseRouterData.postProcess(NoiseRouterData.slideOverworld(false, NoiseRouterData.getFunction(pDensityFunctions, NoiseRouterData.NOODLE)));
        return NoiseRouterData.noNewCaves(pDensityFunctions,pNoiseParameters,densityfunction14);
    }
}
