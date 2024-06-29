package com.landis.breakdowncore.n;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;

public record RangeChecker(ResourceLocation id, int numA, int numB) {
    public boolean check(int number) {
        return numA == numB ? number == numA : number >= Math.min(numA, numB) && number <= Math.max(numA, numB);
    }

    public static final Codec<RangeChecker> CODEC = RecordCodecBuilder.create(i -> i.group(
            ResourceLocation.CODEC.fieldOf("rl").forGetter(RangeChecker::id),
            Codec.INT.fieldOf("num").fieldOf("a").forGetter(RangeChecker::numA),
            Codec.INT.fieldOf("num").fieldOf("b").forGetter(RangeChecker::numB)
    ).apply(i,RangeChecker::new));
}