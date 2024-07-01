package com.landis.breakdowncore.system.storage.element;

import com.landis.breakdowncore.BreakdownCore;
import com.landis.breakdowncore.system.animation.Animation;
import com.landis.breakdowncore.system.material.Material;
import com.landis.breakdowncore.system.material.Registry$Material;
import com.mojang.serialization.Lifecycle;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.RegistryBuilder;

public class ElementsRegistry {
    public static final ResourceKey<Registry<MagicElement>> ELEMENTS_KEY = ResourceKey.createRegistryKey(new ResourceLocation(BreakdownCore.MODID,"elements"));
    public static final Registry<MagicElement> REGISTRY_ELEMENTS = new RegistryBuilder<>(ELEMENTS_KEY).sync(true).create();
    public static final DeferredRegister<MagicElement> elements = DeferredRegister.create(REGISTRY_ELEMENTS, BreakdownCore.MODID);
    public static final MagicElement FIRE = register("fire", new MagicElement("fire", new ResourceLocation(BreakdownCore.MODID, "textures/elements/fire.png")));

    private static MagicElement register(String name, MagicElement element) {
        elements.register(name, () -> element);
        return element;
    }

    public static void registerElements() {
    }
}
