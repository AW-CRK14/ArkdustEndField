package com.arkdust.helper;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Block;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class TagHelper {
    public static Block getRandomBlockElement(TagKey<Block> tag, Random random){
        List<Holder<Block>> list = new ArrayList<>();
        BuiltInRegistries.BLOCK.getTagOrEmpty(tag).forEach(list::add);
        return list.get(random.nextInt(list.size())).value();
    }

    public static Block getRandomBlockElement(TagKey<Block> tag, RandomSource random){
        List<Holder<Block>> list = new ArrayList<>();
        BuiltInRegistries.BLOCK.getTagOrEmpty(tag).forEach(list::add);
        return list.get(random.nextInt(list.size())).value();
    }
}
