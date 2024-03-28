package com.landis.breakdowncore.system.material;

import com.landis.breakdowncore.BreakdownCore;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.neoforged.neoforge.client.event.ModelEvent;
import net.neoforged.neoforge.client.event.TextureAtlasStitchedEvent;
import net.neoforged.neoforge.registries.RegisterEvent;

import static net.minecraft.world.inventory.InventoryMenu.BLOCK_ATLAS;

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
    public static void setup(FMLLoadCompleteEvent event){
        System$Material.init();
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void mapTexture(TextureAtlasStitchedEvent event){
        if(event.getAtlas().location().equals(BLOCK_ATLAS)) {
            System$Material.initTexture(event.getAtlas());
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void mapModel(ModelEvent.BakingCompleted event){
        System$Material.initModel();
    }
}
