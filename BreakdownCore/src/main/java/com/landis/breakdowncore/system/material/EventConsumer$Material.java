package com.landis.breakdowncore.system.material;

import com.landis.breakdowncore.BreakdownCore;
import com.landis.breakdowncore.Registries;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.neoforged.neoforge.client.event.ModelEvent;
import net.neoforged.neoforge.client.event.TextureAtlasStitchedEvent;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.registries.RegisterEvent;

import java.util.ArrayList;
import java.util.List;

import static net.minecraft.world.inventory.InventoryMenu.BLOCK_ATLAS;

/**本类用于处理在注册与注册前阶段的各种额外处理需求*/
@Mod.EventBusSubscriber(modid = BreakdownCore.MODID,bus = Mod.EventBusSubscriber.Bus.MOD)
public class EventConsumer$Material {
    private static final boolean[] REG_FLAG = new boolean[]{false,false,false,false};

    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void preReg(RegisterEvent event){
        System$Material.gatherData();
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public static void extraReg(RegisterEvent event){
        ResourceKey<? extends Registry<?>> key = event.getRegistryKey();

        if(key.equals(Registry$Material.Keys.MATERIAL)){
            REG_FLAG[0] = true;
        }else if (key.equals(Registry$Material.Keys.MATERIAL_FEATURE)){
            REG_FLAG[1] = true;
        }else if(key.equals(Registry$Material.Keys.MATERIAL_ITEM_TYPE)){
            REG_FLAG[2] = true;
        }

        if(REG_FLAG[0] && REG_FLAG[1] && REG_FLAG[2] && !REG_FLAG[3]) {
            for (MaterialItemType type : Registry$Material.MATERIAL_ITEM_TYPE){
                type.primaryRegister(event);
            }

            for (Material material : Registry$Material.MATERIAL) {
                for (MaterialItemType type : material.getOrCreateTypes()) {
                    type.secondaryRegistry(event, material);
                }
            }

            REG_FLAG[3] = true;
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


    @SubscribeEvent
    public static void attachToCreativeModeTab(BuildCreativeModeTabContentsEvent event){
        if(event.getTab().equals(Registries.BREA_TAB.get())){
            for(MaterialItemType mit : Registry$Material.MATERIAL_ITEM_TYPE){
                mit.attachToCreativeTab(event);
            }
        }
    }

    @SubscribeEvent
    public static void extraItemModelAttach(ModelEvent.RegisterAdditional event) {
//        List<ResourceLocation> list = new ArrayList<>();
//        for(MaterialItemType type : Registry$Material.MATERIAL_ITEM_TYPE){
//            list.addAll(type.attachToItemModelReg());
//        }
//        for(ResourceLocation location : list){
//            if(BreakdownCore.checkResource(BreakdownCore.covertToModelID(location.withPrefix("item/")))){
//                event.register(System$Material.trans2ModelLocation(location));
//            }
//        }
    }

    @SubscribeEvent
    public static void onModelBake(ModelEvent.ModifyBakingResult event) {
        for(MaterialItemType type : Registry$Material.MATERIAL_ITEM_TYPE){
            type.consumeModelReg(event);
        }
    }
}
