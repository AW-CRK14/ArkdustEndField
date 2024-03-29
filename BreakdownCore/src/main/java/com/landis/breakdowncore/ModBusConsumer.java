package com.landis.breakdowncore;

import com.landis.breakdowncore.system.material.Registry$Material;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.registries.NewRegistryEvent;
import net.neoforged.neoforge.registries.RegisterEvent;

import java.util.HashMap;
import java.util.Map;

@Mod.EventBusSubscriber(modid = BreakdownCore.MODID,bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModBusConsumer {
    public static final Map<ResourceKey<? extends Registry<?>>,Registry<?>> REGS_MAP = new HashMap<>();


    @SubscribeEvent
    public static void newRegistry(NewRegistryEvent event){
        event.register(Registry$Material.MATERIAL_ITEM_TYPE);
        event.register(Registry$Material.MATERIAL_FEATURE);
        event.register(Registry$Material.MATERIAL);
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void reg(RegisterEvent event){
        REGS_MAP.put(event.getRegistryKey(),event.getRegistry());
    }
}
