package com.landis.breakdowncore;

import com.landis.breakdowncore.system.macmodule.Registry$MacModule;
import com.landis.breakdowncore.system.material.*;
import com.landis.breakdowncore.system.material.datagen.MaterialReflectDataGatherEvent;
import com.landis.breakdowncore.system.weather.Registry$Weather;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Items;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.registries.NewRegistryEvent;
import net.neoforged.neoforge.registries.RegisterEvent;

import java.util.HashMap;
import java.util.Map;

@Mod.EventBusSubscriber(modid = BreakdownCore.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModBusConsumer {
    public static final Map<ResourceKey<? extends Registry<?>>, Registry<?>> REGS_MAP = new HashMap<>();


    @SubscribeEvent
    public static void newRegistry(NewRegistryEvent event) {
        event.register(Registry$Material.MATERIAL_ITEM_TYPE);
        event.register(Registry$Material.MATERIAL_FEATURE);
        event.register(Registry$Material.MATERIAL);

        event.register(Registry$MacModule.MODULE_TYPES);

        event.register(Registry$Weather.CLIMATE_PARAMETER);
        event.register(Registry$Weather.WEATHER);
        event.register(Registry$Weather.WEATHER_PROVIDER);
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void reg(RegisterEvent event) {
        REGS_MAP.put(event.getRegistryKey(), event.getRegistry());
    }

    @SubscribeEvent
    public static void attachMaterialData(MaterialReflectDataGatherEvent event) {
        event.handler.registryReflectItemMaterialInfo(new Holder.Direct<>(Items.COAL), BreaRegistries.MaterialReg.LIGNITE.getId(), BreaRegistries.MaterialReg.COMBUSTIBLE_TYPE.getId());
    }

//    @Mod.EventBusSubscriber(modid = BreakdownCore.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
//    public static class Client {
////        @SubscribeEvent
////        public static void resourceReload(RegisterClientReloadListenersEvent event){
////            event.registerReloadListener(MaterialAtlasManager.init());
////        }
////
////        @SubscribeEvent
////        public static void renderTypesRegistry(RegisterNamedRenderTypesEvent event){
////            event.register(new ResourceLocation(BreakdownCore.MODID,"material"), RenderType.translucent(), MaterialAtlasManager.RENDER_TYPE);
////        }
//    }
}
