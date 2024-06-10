package com.landis.breakdowncore;

import com.landis.breakdowncore.event.render.SpriteBeforeStitchEvent;
import com.landis.breakdowncore.helper.SpriteHelper;
import com.landis.breakdowncore.system.macmodule.Registry$MacModule;
import com.landis.breakdowncore.system.material.*;
import com.landis.breakdowncore.unsafe.SkippedRegister;
import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.SpriteContents;
import net.minecraft.client.resources.metadata.animation.FrameSize;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceMetadata;
import net.minecraft.world.item.Items;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.registries.NewRegistryEvent;
import net.neoforged.neoforge.registries.RegisterEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void reg(RegisterEvent event) {
        REGS_MAP.put(event.getRegistryKey(), event.getRegistry());
    }

    @SubscribeEvent
    public static void attachMaterialData(MaterialReflectDataGatherEvent event) {
//        event.handler.registryReflectItemMaterialInfo(new Holder.Direct<>(Items.COAL), );
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
