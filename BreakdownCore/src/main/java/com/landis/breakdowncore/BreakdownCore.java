package com.landis.breakdowncore;

import com.mojang.logging.LogUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ItemModelGenerator;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.loading.FMLLoader;
import net.neoforged.fml.loading.moddiscovery.MinecraftLocator;
import net.neoforged.fml.loading.targets.CommonLaunchHandler;
import net.neoforged.fml.loading.targets.FMLClientLaunchHandler;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.common.NeoForgeMod;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.slf4j.Logger;

@Mod(BreakdownCore.MODID)
public class BreakdownCore {
    public static final String MODID = "brea";
    private static final Logger LOGGER = LogUtils.getLogger();

    public BreakdownCore(IEventBus modEventBus) {

        Registries.ITEM.register(modEventBus);
        Registries.TAB.register(modEventBus);
        Registries.MaterialReg.MATERIAL.register(modEventBus);
        Registries.MaterialReg.FEATURE.register(modEventBus);
        Registries.MaterialReg.TYPE.register(modEventBus);

    }

    private static ItemModelGenerator ITEM_MODELGEN;
    public static ItemModelGenerator getItemModelgen(){
        if(ITEM_MODELGEN == null){
            ITEM_MODELGEN = new ItemModelGenerator();
        }
        return ITEM_MODELGEN;
    }

    public static boolean checkResource(ResourceLocation location){
        return Minecraft.getInstance().getResourceManager().getResource(location).isPresent();
    }

    public static ResourceLocation covertToModelID(ResourceLocation location){
        return location.withPath(s -> "models/" + s + ".json");
    }
}
