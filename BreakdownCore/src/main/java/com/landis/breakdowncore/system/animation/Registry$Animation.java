package com.landis.breakdowncore.system.animation;

import com.landis.breakdowncore.BreakdownCore;
import com.mojang.serialization.Codec;
import net.minecraft.client.model.ZombieModel;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.registries.RegistryBuilder;

public class Registry$Animation {
    public static final ResourceKey<Registry<Animation>> ANIMATION_KEY = ResourceKey.createRegistryKey(new ResourceLocation(BreakdownCore.MODID,"animation"));
    public static final Registry<Animation> ANIMATIONS = new RegistryBuilder<>(ANIMATION_KEY).sync(true).create();


}
