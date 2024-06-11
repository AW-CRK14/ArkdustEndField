package com.landis.breakdowncore.system.material;

import com.mojang.datafixers.util.Pair;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.registries.DeferredHolder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Arrays;
import java.util.function.Supplier;

/**
 * Handler$Material<br><p>
 * 此类是Material系统的拓展功能，用于为不易于修改的，如其它mod或原版的物品，提供材料信息<br>
 * 主要有四个功能：为材料添加材料特征，为材料特征添加材料物品类型，添加 物品->材料信息 ，添加 材料-材料物品类型->物品。<br>
 * 虽然说这些东西看起来很奇怪而且令人血压升高。<br><p>
 * 请注意：在这些工具被使用的时候，几乎所有注册都还没有开始。因此，您<font color="red">不应该在这一阶段调用任何注册物容器的内容</font>
 *
 * @see MaterialReflectDataGatherEvent
 * @see System$Material#gatherData()
 */
public class Handler$Material {
    public static final Logger LOGGER = LogManager.getLogger("BREA:Material:ExtraHandle");

    private boolean avaliable = true;

    void setLock() {
        this.avaliable = false;
    }

    Handler$Material() {
    }

    //为指定的材料添加额外材料特征
    public void addFeatureForMaterial(DeferredHolder<Material, ? extends Material> materialHolder, IMaterialFeature<?> feature) {
        addFeatureForMaterial(materialHolder.getId(), feature);
    }

    public void addFeatureForMaterial(ResourceLocation id, IMaterialFeature<?> feature) {
        if (avaliable) {
            System$Material.MF4M_ADDITION.put(id, feature);
        } else {
            LOGGER.warn("Can't add feature(type={}) to Material(id={}) as the init stage is finished.", feature.getType().get().id(), id);
            LOGGER.warn(new IllegalStateException());
        }
    }

    //为指定材料特征类型添加可用的物品形态
    public void addMitForFeatureType(DeferredHolder<MaterialFeatureType<?>, ? extends MaterialFeatureType<?>> feature, DeferredHolder<MaterialItemType, ? extends MaterialItemType>... mit) {
        addMitForFeatureType(feature.getId(), mit);
    }

    public void addMitForFeatureType(ResourceLocation id, DeferredHolder<MaterialItemType, ? extends MaterialItemType>... mit) {
        if (avaliable) {
            System$Material.MIT4MF_ADDITION.putAll(id, Arrays.asList(mit));
        } else {
            LOGGER.warn("Can't add MITs to MaterialFeature(id={}) as the init stage is finished.", id);
            LOGGER.warn(new IllegalStateException());
        }
    }

    //为某个物品添加映射材料特征，例如为铁锭(原版)添加铁(材料)与锭(物品类型)指定。
    public void registryReflectItemMaterialInfo(Holder<Item> itemHolder, Supplier<ITypedMaterialObj> info) {
        if (avaliable) {
            System$Material.I2TMI_PRE.add(new Pair<>(itemHolder, info));
        } else {
            LOGGER.warn("Can't attach material info for item(id={}) as the init stage is finished.", itemHolder.unwrapKey().map(ResourceKey::toString).orElse("[invalid key]"));
            LOGGER.warn(new IllegalStateException());
        }
    }

    public void registryReflectItemMaterialInfo(Holder<Item> itemHolder, ResourceLocation material, ResourceLocation type) {
        registryReflectItemMaterialInfo(itemHolder, material, type, true, true);
    }

    public void registryReflectItemMaterialInfo(Holder<Item> itemHolder, ResourceLocation material, ResourceLocation type, boolean addItemMark, boolean overrideMark) {
        if (avaliable) {
            System$Material.I2TMI_PRE.add(new Pair<>(itemHolder, () -> new TypedMaterialInfo(material, type)));
            if (addItemMark) {
                markItemsMaterialInfo(material, type, () -> new ItemStack(itemHolder), overrideMark);
            }
        } else {
            LOGGER.warn("Can't attach material info for item(id={}) as the init stage is finished.", itemHolder.unwrapKey().map(ResourceKey::toString).orElse("[invalid key]"));
            LOGGER.warn(new IllegalStateException());
        }
    }

    //为某种特性的材料特征组指定对应的物品，可以视作和上一个方法正好相反。
    public void markItemsMaterialInfo(ResourceLocation material, ResourceLocation type, Supplier<ItemStack> item) {
        markItemsMaterialInfo(material, type, item, true);
    }

    public void markItemsMaterialInfo(ResourceLocation material, ResourceLocation type, Supplier<ItemStack> item, boolean override) {
        if (avaliable) {
            Pair<ResourceLocation, ResourceLocation> p = new Pair<>(material, type);
            if (!override && System$Material.M_MIT2I_PRE.contains(p)) return;
            System$Material.M_MIT2I_PRE.add(new Pair<>(p, item));
        } else {
            LOGGER.warn("Can't mark material({}) and type({}) for item stack as the init stage is finished.", material, type);
            LOGGER.warn(new IllegalStateException());
        }
    }


}