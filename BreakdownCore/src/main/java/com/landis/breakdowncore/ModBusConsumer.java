package com.landis.breakdowncore;

import com.landis.breakdowncore.event.render.SpriteBeforeStitchEvent;
import com.landis.breakdowncore.helper.SpriteHelper;
import com.landis.breakdowncore.system.material.Material;
import com.landis.breakdowncore.system.material.MaterialItemType;
import com.landis.breakdowncore.system.material.Registry$Material;
import com.landis.breakdowncore.system.material.System$Material;
import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.SpriteContents;
import net.minecraft.client.resources.metadata.animation.FrameSize;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceMetadata;
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
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void reg(RegisterEvent event) {
        REGS_MAP.put(event.getRegistryKey(), event.getRegistry());
    }

    @Mod.EventBusSubscriber(modid = BreakdownCore.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class Client {
//        @SubscribeEvent
//        public static void resourceReload(RegisterClientReloadListenersEvent event){
//            event.registerReloadListener(MaterialAtlasManager.init());
//        }
//
//        @SubscribeEvent
//        public static void renderTypesRegistry(RegisterNamedRenderTypesEvent event){
//            event.register(new ResourceLocation(BreakdownCore.MODID,"material"), RenderType.translucent(), MaterialAtlasManager.RENDER_TYPE);
//        }


        public static final ResourceLocation MISSING_MATERIAL_SPRITE = new ResourceLocation(BreakdownCore.MODID, "textures/brea/material/material/missing.png");
        public static final Logger MATERIAL_LOGGER = LogManager.getLogger("BREA:Material:AtlasManager");

        @SubscribeEvent
        public static void stitchToAtlas(SpriteBeforeStitchEvent event) {
            if (event.atlasLocation.equals(new ResourceLocation("minecraft", "blocks"))) {
                ResourceManager rm = Minecraft.getInstance().getResourceManager();

                //虽然不是这么用的，但是比较方便
                Map<MaterialItemType, SpriteContents> alphaCache = new HashMap<>();
                Map<MaterialItemType, SpriteContents> coverCache = new HashMap<>();

                for (Material material : Registry$Material.MATERIAL) {
                    //加载材料纹理
                    SpriteContents mat;
                    {
                        ResourceLocation location = material.id.withPath(s -> "textures/brea/material/material/" + s + ".png");
                        mat = event.loader.loadSprite(material.id, rm.getResource(location).orElseGet(() -> {
                            MATERIAL_LOGGER.warn("Can't find texture at ResourceLocation={} for Material={id:{}}. Use MISSING.", location, material.id);
                            return rm.getResource(MISSING_MATERIAL_SPRITE).get();
                        }));
                    }

                    event.register(mat);

                    for (MaterialItemType type : material.equals(Registries.MaterialReg.MISSING.get()) ? Registry$Material.MATERIAL_ITEM_TYPE : material.getOrCreateTypes()) {
                        //创建物品类型alpha通道缓存
                        if (!alphaCache.containsKey(type)) {
                            ResourceLocation location = type.id.withPath(s -> "textures/brea/material/mit/" + s + ".png");
                            rm.getResource(location).ifPresentOrElse(r -> alphaCache.put(type, event.loader.loadSprite(type.id, r)), () -> {
                                MATERIAL_LOGGER.warn("Can't find texture at ResourceLocation={} for MaterialItemType={id:{}}.", location, type.id);
                                alphaCache.put(type, null);
                            });
                        }

                        //创建物品覆盖层缓存
                        if (!coverCache.containsKey(type)) {
                            coverCache.put(type, rm.getResource(type.id.withPath(s -> "textures/brea/material/mit_cover/" + s + ".png"))
                                    .map(r -> event.loader.loadSprite(type.id, r)).orElse(null));
                        }

                        ResourceLocation l = System$Material.combineForAtlasID(material, type);
                        //加载可选的替换材质
                        rm.getResource(material.id.withPath(s -> "textures/brea/material/override/" + s + "/" + type.id.getNamespace() + "_" + type.id.getPath() + ".png")).ifPresentOrElse(
                                r -> event.register(event.loader.loadSprite(l, r)),
                                () -> {//以及……强制组合  我不想写动态解析了
                                    //TODO 动态材质的处理与组合
                                    SpriteContents alpha = alphaCache.get(type);
                                    SpriteContents cover = coverCache.get(type);

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
                                                f = SpriteHelper.blend(f,images.get(2));
                                        }

                                        event.register(new SpriteContents(l, new FrameSize(f.getWidth(), f.getHeight()), f, ResourceMetadata.EMPTY));
                                    }
                                }
                        );

                    }
                }

            }

        }
    }
}
