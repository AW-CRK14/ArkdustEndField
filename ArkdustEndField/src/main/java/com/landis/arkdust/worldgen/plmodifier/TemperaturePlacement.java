package com.landis.arkdust.worldgen.plmodifier;

import com.landis.arkdust.registry.worldgen.detector.PlacementModifierRegistry;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.placement.PlacementContext;
import net.minecraft.world.level.levelgen.placement.PlacementFilter;
import net.minecraft.world.level.levelgen.placement.PlacementModifierType;

public class TemperaturePlacement extends PlacementFilter {
    public static final Codec<TemperaturePlacement> CODEC = RecordCodecBuilder.create(obj->obj.group(
            Codec.FLOAT.fieldOf("temp").forGetter(getter->getter.temp),
            Codec.BOOL.fieldOf("bigger_require").forGetter(getter->getter.biggerRequire)
    ).apply(obj,TemperaturePlacement::new));

    private final boolean biggerRequire;
    private final float temp;
    public TemperaturePlacement(float temp,boolean requireTempBigger){
        this.temp = temp;
        this.biggerRequire = requireTempBigger;
    }

    @Override
    protected boolean shouldPlace(PlacementContext pContext, RandomSource pRandom, BlockPos pPos) {
        return pContext.getLevel().getBiome(pPos).value().getBaseTemperature() > temp == biggerRequire;
    }

    @Override
    public PlacementModifierType<?> type() {
        return PlacementModifierRegistry.TEMPERATURE;
    }
}
