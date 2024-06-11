package com.landis.breakdowncore.system.material;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.landis.breakdowncore.BreakdownCore;
import com.landis.breakdowncore.Registries;
import com.landis.breakdowncore.event.render.SpriteBeforeStitchEvent;
import com.landis.breakdowncore.helper.SpriteHelper;
import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.SpriteContents;
import net.minecraft.client.resources.metadata.animation.FrameSize;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceMetadata;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.neoforged.neoforge.client.event.ModelEvent;
import net.neoforged.neoforge.client.event.TextureAtlasStitchedEvent;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.registries.RegisterEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static net.minecraft.world.inventory.InventoryMenu.BLOCK_ATLAS;

/**
 * 本类用于处理在注册与注册前阶段的各种额外处理需求
 */
@Mod.EventBusSubscriber(modid = BreakdownCore.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class EventConsumer$Material {
    private static final boolean[] REG_FLAG = new boolean[]{false, false, false, false};
    private static boolean preRegFlag = false;

    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void preReg(RegisterEvent event) {
        if (!preRegFlag) {
            System$Material.gatherData();
            preRegFlag = true;
        }
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public static void extraReg(RegisterEvent event) {
        ResourceKey<? extends Registry<?>> key = event.getRegistryKey();

        if (key.equals(Registry$Material.Keys.MATERIAL)) {
            REG_FLAG[0] = true;
        } else if (key.equals(Registry$Material.Keys.MATERIAL_FEATURE)) {
            REG_FLAG[1] = true;
        } else if (key.equals(Registry$Material.Keys.MATERIAL_ITEM_TYPE)) {
            REG_FLAG[2] = true;
        }

        if (REG_FLAG[0] && REG_FLAG[1] && REG_FLAG[2] && !REG_FLAG[3]) {
            for (MaterialItemType type : Registry$Material.MATERIAL_ITEM_TYPE) {
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
    public static void setup(FMLLoadCompleteEvent event) {
        System$Material.init();
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void mapTexture(TextureAtlasStitchedEvent event) {
        if (event.getAtlas().location().equals(BLOCK_ATLAS)) {
            System$Material.initTexture(event.getAtlas());
        }
    }


    @SubscribeEvent
    public static void attachToCreativeModeTab(BuildCreativeModeTabContentsEvent event) {
        if (event.getTab().equals(Registries.BREA_TAB.get())) {
            for (MaterialItemType mit : Registry$Material.MATERIAL_ITEM_TYPE) {
                mit.attachToCreativeTab(event);
            }
        }
    }

//    @SubscribeEvent
//    public static void extraItemModelAttach(ModelEvent.RegisterAdditional event) {
//        List<ResourceLocation> list = new ArrayList<>();
//        for(MaterialItemType type : Registry$Material.MATERIAL_ITEM_TYPE){
//            list.addAll(type.attachToItemModelReg());
//        }
//        for(ResourceLocation location : list){
//            if(BreakdownCore.checkResource(BreakdownCore.covertToModelID(location.withPrefix("item/")))){
//                event.register(System$Material.trans2ModelLocation(location));
//            }
//        }
//    }

    @SubscribeEvent
    public static void onModelBake(ModelEvent.ModifyBakingResult event) {
        for (MaterialItemType type : Registry$Material.MATERIAL_ITEM_TYPE) {
            type.consumeModelReg(event);
        }
    }

    @Mod.EventBusSubscriber(modid = BreakdownCore.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class Client {
        public static final ResourceLocation MISSING_MATERIAL_SPRITE = new ResourceLocation(BreakdownCore.MODID, "textures/brea/material/material/missing.png");
        public static final Logger MATERIAL_LOGGER = LogManager.getLogger("BREA:Material:AtlasManager");

        @SubscribeEvent
        public static void stitchToAtlas(SpriteBeforeStitchEvent event) {
            if (event.atlasLocation.equals(new ResourceLocation("minecraft", "blocks"))) {
                ResourceManager rm = Minecraft.getInstance().getResourceManager();

                //虽然不是这么用的，但是比较方便
                Map<MaterialItemType, SpriteContents> alphaCache = new HashMap<>();
                Map<MaterialItemType, SpriteContents> coverCache = new HashMap<>();
                Multimap<Integer, MaterialItemType> idpCache = HashMultimap.create();

                for (MaterialItemType type : Registry$Material.MATERIAL_ITEM_TYPE) {
                    //创建物品类型alpha通道缓存
                    ResourceLocation location = type.id.withPath(s -> "textures/brea/material/mit/" + s + ".png");
                    rm.getResource(location).ifPresentOrElse(r -> alphaCache.put(type, event.loader.loadSprite(type.id, r)), () -> {
                        MATERIAL_LOGGER.warn("Can't find texture at ResourceLocation={} for MaterialItemType={id:{}}.", location, type.id);
                        alphaCache.put(type, null);
                    });

                    //创建物品覆盖层缓存
                    coverCache.put(type, rm.getResource(type.id.withPath(s -> "textures/brea/material/mit_cover/" + s + ".png"))
                            .map(r -> event.loader.loadSprite(type.id, r)).orElse(null));
                }

                for (Material material : Registry$Material.MATERIAL) {
                    if (material.intermediateProduct) {
                        if (!idpCache.containsKey(material.x16color)) {
                            for (MaterialItemType type : material.getOrCreateTypes()) {
                                handleSprite(event, alphaCache.get(type), SpriteHelper.pack(SpriteHelper.withColor(16, 16, SpriteHelper.Color.invert(material.x16color))), coverCache.get(type), System$Material.idpForAtlasID(material.x16color, type));
                            }
                            idpCache.putAll(material.x16color, material.getOrCreateTypes());
                        } else {
                            for (MaterialItemType type : material.getOrCreateTypes()) {
                                if (!idpCache.containsEntry(material.x16color, type)) {
                                    handleSprite(event, alphaCache.get(type), SpriteHelper.pack(SpriteHelper.withColor(16, 16, SpriteHelper.Color.invert(material.x16color))), coverCache.get(type), System$Material.idpForAtlasID(material.x16color, type));
                                    idpCache.put(material.x16color, type);
                                }
                            }
                        }
                    } else {
                        //加载材料纹理
                        ResourceLocation locationMat = material.id.withPath(s -> "textures/brea/material/material/" + s + ".png");
                        SpriteContents mat = event.loader.loadSprite(material.id, rm.getResource(locationMat).orElseGet(() -> {
                            MATERIAL_LOGGER.warn("Can't find texture at ResourceLocation={} for Material={id:{}}. Use MISSING.", locationMat, material.id);
                            return rm.getResource(MISSING_MATERIAL_SPRITE).get();
                        }));
                        event.register(mat);

                        for (MaterialItemType type : material.getOrCreateTypes()) {
                            ResourceLocation l = System$Material.combineForAtlasID(material, type);
                            //加载可选的替换材质
                            rm.getResource(material.id.withPath(s -> "textures/brea/material/override/" + s + "/" + type.id.getNamespace() + "_" + type.id.getPath() + ".png")).ifPresentOrElse(
                                    r -> event.register(event.loader.loadSprite(l, r)),
                                    () -> {//以及……强制组合  我不想写动态解析了
                                        //TODO 动态材质的处理与组合
                                        SpriteContents alpha = alphaCache.get(type);
                                        SpriteContents cover = coverCache.get(type);

                                        handleSprite(event, alpha, mat, cover, l);
                                    }
                            );
                        }
                    }
                }

            }

        }

        private static void handleSprite(SpriteBeforeStitchEvent event, SpriteContents alpha, SpriteContents mat, SpriteContents cover, ResourceLocation l) {
            if (alpha == null) {
            } else if (mat.animatedTexture == null && alpha.animatedTexture == null) {
                //全静态材质叠合
                List<NativeImage> images = new ArrayList<>();
                images.add(SpriteHelper.firstFrame(mat));
                images.add(SpriteHelper.firstFrame(alpha));
                if (cover != null) {
                    images.add(SpriteHelper.firstFrame(cover));
                }
                images = SpriteHelper.scaleToSame(images, true);

                NativeImage f = SpriteHelper.alphaFilter(images.get(0), images.get(1));
                if (images.size() == 3) {
                    f = SpriteHelper.blend(f, images.get(2));
                }

                event.register(new SpriteContents(l, new FrameSize(f.getWidth(), f.getHeight()), f, ResourceMetadata.EMPTY));
            }
        }
    }
}
