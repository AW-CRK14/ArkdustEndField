package com.landis.breakdowncore.system.material;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Multimap;
import com.landis.breakdowncore.BreakdownCore;
import com.landis.breakdowncore.event.EventHooks;
import com.mojang.datafixers.util.Pair;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

/**
 * System$Material<br>
 * System$Material是Material系统的额外内容总控中心，用于统一管理额外处理与额外的表生成。
 */
public class System$Material {
    public static final Logger LOGGER = LogManager.getLogger("BREA:Material:SystemController");


    //---[信息收集与初始化 Info gather and initialization]---

    /**
     * 以下内容为flag标志，值为true时表示阶段可用。其具体作用请参考introduction.md文件。
     */
    public static boolean infoBuildStageFlag() {
        return infoB;
    }

    static boolean infoB = true;

    public static boolean releaseFlag() {
        return release;
    }

    static boolean release = false;


    /**
     * 额外处理器({@link Handler$Material})的统一控制区
     */
    static void gatherData() {
        Handler$Material handler = new Handler$Material();
        EventHooks.postMaterialReflectDataGatherEvent(handler);
        handler.setLock();
    }

    static Multimap<ResourceLocation, IMaterialFeature<?>> MF4M_ADDITION = HashMultimap.create();
    static Multimap<ResourceLocation, Holder<MaterialItemType>> MIT4MF_ADDITION = HashMultimap.create();
    static List<Pair<Holder<Item>, Supplier<ITypedMaterialObj>>> I2TMI_PRE = new ArrayList<>();
    static List<Pair<Pair<ResourceLocation, ResourceLocation>, Supplier<ItemStack>>> M_MIT2I_PRE = new ArrayList<>();


    /**
     * 在所有注册任务全部完成后的内容。此时infoB将被设置为false。在这些内容结束后，release将被设置为true
     */
    static ImmutableMap<Class<?>, MaterialFeatureType<?>> MF_CLASS2MFH;
    static ImmutableMap<Item, ITypedMaterialObj> I2TMI;
    static ImmutableMap<Material, ImmutableMap<MaterialItemType, ItemStack>> M_MIT2I;
    static ImmutableMap<Material, TextureAtlasSprite> M2TEXTURE;

    static void init() {
        infoB = false;

        Map<Class<?>, MaterialFeatureType<?>> a = new HashMap<>();
        for (MaterialFeatureType<?> handle : Registry$Material.MATERIAL_FEATURE) {
            a.put(handle.clazz(), handle);
        }
        MF_CLASS2MFH = ImmutableMap.copyOf(a);

        Map<Item, ITypedMaterialObj> b = new HashMap<>();
        for (Pair<Holder<Item>, Supplier<ITypedMaterialObj>> i : I2TMI_PRE) {
            if (!b.containsKey(i.getFirst().value())) {
                b.put(i.getFirst().value(), i.getSecond().get());
            }
        }
        I2TMI = ImmutableMap.copyOf(b);

        Map<Material, Map<MaterialItemType, ItemStack>> c = new HashMap<>();
        for (Pair<Pair<ResourceLocation, ResourceLocation>, Supplier<ItemStack>> i : M_MIT2I_PRE) {
            Material m = Registry$Material.MATERIAL.get(i.getFirst().getFirst());
            MaterialItemType t = Registry$Material.MATERIAL_ITEM_TYPE.get(i.getFirst().getSecond());
            ItemStack s = i.getSecond().get();

            if (m == null || t == null) {
                LOGGER.warn("Unable to get Material or MaterialItemType when creating mark map. At System$Material # init(). This group will be skipped.");
                LOGGER.warn("Details: M_ID:{}, MIT_ID:{}, MaterialInstance:{}, MITInstance:{}", i.getFirst().getFirst(), i.getFirst().getSecond(), m, t);
                continue;
            }

            if (c.containsKey(m)) {
                Map<MaterialItemType, ItemStack> mi = c.get(m);
                if (!mi.containsKey(t)) {
                    mi.put(t, s);
                }
            } else {
                Map<MaterialItemType, ItemStack> mi = new HashMap<>();
                mi.put(t, s);
                c.put(m, mi);
            }
        }
        ImmutableMap.Builder<Material, ImmutableMap<MaterialItemType, ItemStack>> builder = new ImmutableMap.Builder<>();
        c.forEach((m, mit) -> builder.put(m, ImmutableMap.copyOf(mit)));
        M_MIT2I = builder.build();

        MF4M_ADDITION = null;
        MIT4MF_ADDITION = null;
        M_MIT2I_PRE = null;
        I2TMI_PRE = null;

        release = true;
    }

    static void initTexture(TextureAtlas atlas) {
        TextureAtlasSprite missing = atlas.missingSprite;

        Map<Material, TextureAtlasSprite> d = new HashMap<>();
        TextureAtlasSprite sprite;
        for (Material material : Registry$Material.MATERIAL) {
            sprite = atlas.getSprite(material.id.withPath(id -> "brea/material/" + id));
            if (sprite.equals(missing)) {
                sprite = atlas.getSprite(new ResourceLocation(BreakdownCore.MODID, "brea/material/missing"));
            }
            d.put(material, sprite);
        }
        M2TEXTURE = ImmutableMap.copyOf(d);
    }


    //---[模型id获取 Model id provider]---

    public static TextureAtlasSprite getTexture(Material material) {
        if (release) {
            return M2TEXTURE.get(material);
        } else {
            LOGGER.warn("M2TEXTURE map has not present.");
        }
        return null;
    }

    public static ResourceLocation basicModel(ResourceLocation original) {
        return MIT_BASIC_MODEL_LOCATION.apply(original);
    }

    public static ModelResourceLocation trans2ModelLocation(ResourceLocation original) {
        return new ModelResourceLocation(original, "inventory");
    }

    public static final UnaryOperator<ResourceLocation> MIT_BASIC_MODEL_LOCATION = location -> location.withPrefix("mit_basic/");


    public static ResourceLocation combineForAtlasID(Material material, MaterialItemType type) {
        return new ResourceLocation("brea_" + material.id.getNamespace() + "_" + type.id.getNamespace(), material.id.getPath() + "_" + type.id.getPath());
    }

    public static ResourceLocation idpForAtlasID(int x16color, MaterialItemType type) {
        return type.id.withPrefix("breaidp_" + Integer.toHexString(x16color) + "_");
    }

    //---[覆盖信息访问 Overridden info accessor]---

    public static <I extends IMaterialFeature<I>> MaterialFeatureType<I> getMFType(Class<I> c) {
        if (release) {
            return (MaterialFeatureType<I>) MF_CLASS2MFH.get(c);
        }
        LOGGER.error("Can't get MFH for class:{}, the maps hasn't present right now.", c);
        LOGGER.error("If you want to get the correctly MFH, please call this after RegistryEvent(Low priority).");
        throw new IllegalStateException("Map has not present. Flags right now:{infoB=" + infoB + ",release=" + release + "}");
    }

    private static final ImmutableMap<MaterialItemType, ItemStack> EMPTY = ImmutableMap.<MaterialItemType, ItemStack>builder().build();

    public static ItemStack getFromMAndMIT(Material material, MaterialItemType type) {
        ItemStack itemStack = null;
        if (release) {
            itemStack = M_MIT2I.getOrDefault(material, EMPTY).get(type);
        } else {
            LOGGER.warn("M_MIT2I map has not present so the result may lead to some unexpected error.");
        }
        if (itemStack == null) {
            itemStack = material.createItem(type);
            if (itemStack == null) {
                return type.createItem(material);
            }
            return itemStack;
        }
        return itemStack;
    }


    //---[物品堆信息提供 ItemStack material info accessor]---
    public static Material getMaterial(ItemStack stack) {
        ITypedMaterialObj obj = getMaterialInfo(stack.getItem());
        if (obj != null) return obj.getMaterial(stack).get();
        return null;
    }

    public static MaterialItemType getMaterialItemType(ItemStack stack) {
        ITypedMaterialObj obj = getMaterialInfo(stack.getItem());
        return obj == null ? null : obj.getMIType();
    }

    public static void setMaterial(ItemStack stack, Material material) {
        ITypedMaterialObj obj = getMaterialInfo(stack.getItem());
        if (obj != null) obj.setMaterial(stack, material);
    }

    public static @Nullable ITypedMaterialObj getMaterialInfo(Item item) {
        if (!release) {
            LOGGER.warn("I2TMI map has not present so the result may lead to some unexpected error.");
            return item instanceof ITypedMaterialObj i ? i : null;
        }
        return I2TMI.getOrDefault(item, item instanceof ITypedMaterialObj i ? i : null);
    }

}
