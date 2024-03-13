package com.landis.breakdowncore.material;

import com.google.common.collect.ImmutableMap;
import com.landis.breakdowncore.BreakdownCore;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.registries.RegisterEvent;

/**本类用于处理在注册与注册前阶段的各种额外处理需求*/
@Mod.EventBusSubscriber(modid = BreakdownCore.MODID,bus = Mod.EventBusSubscriber.Bus.MOD)
public class EventConsumer$Material {
    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void preReg(RegisterEvent event){
        System$Material.gatherData();
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public static void extraReg(RegisterEvent event){
        for (MaterialItemType type : Registry$Material.MATERIAL_ITEM_TYPE){
            type.primaryRegister(event);
        }

        for (Material material : Registry$Material.MATERIAL){
            for (MaterialItemType type : material.getOrCreateTypes()){
                type.secondaryRegistry(event,material);
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void setup(FMLCommonSetupEvent event){
        System$Material.init();
    }
}
