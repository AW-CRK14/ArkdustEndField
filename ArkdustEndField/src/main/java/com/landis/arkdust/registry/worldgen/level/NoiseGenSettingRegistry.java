package com.landis.arkdust.registry.worldgen.level;

import com.landis.arkdust.worldgen.dimension.SarconDimension;
import com.landis.arkdust.worldgen.noiserouter.Router;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import net.minecraft.world.level.levelgen.NoiseRouterData;
import net.minecraft.world.level.levelgen.NoiseSettings;

import java.util.List;

public class NoiseGenSettingRegistry {
    public static void bootstrap(BootstapContext<NoiseGeneratorSettings> context){
        context.register(SarconDimension.SETTING,new NoiseGeneratorSettings(NoiseSettings.create(0,160,2,2), Blocks.STONE.defaultBlockState(),Blocks.WATER.defaultBlockState(),
//                Router.overworldWithoutCaves(context.lookup(Registries.DENSITY_FUNCTION), context.lookup(Registries.NOISE)),
                Router.sarconRouter(context.lookup(Registries.DENSITY_FUNCTION), context.lookup(Registries.NOISE)),
                new SarconDimension.SurfaceSource(),//TODO SurfaceRule required
                List.of(),1,false,false,false,true));
    }

}
