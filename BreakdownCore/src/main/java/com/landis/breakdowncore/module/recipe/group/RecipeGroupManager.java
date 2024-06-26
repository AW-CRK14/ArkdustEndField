package com.landis.breakdowncore.module.recipe.group;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.resources.ResourceLocation;

public class RecipeGroupManager {
    private static Multimap<ResourceLocation,ResourceLocation> recipeGroupAttachment = HashMultimap.create();
}
