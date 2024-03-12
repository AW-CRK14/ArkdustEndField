package com.landis.breakdowncore.material;

import com.landis.breakdowncore.unsafe.SkippedRegister;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import oshi.util.tuples.Pair;

import java.util.Arrays;
import java.util.function.Supplier;

/**Handler$Material<br>
 * 此类是Material系统的拓展功能，用于为不易于修改的，如其它mod或原版的物品，提供材料信息<br>
 * 主要有四个功能：为材料添加材料特征，为材料特征添加材料物品类型，添加 物品->材料信息 ，添加 材料-材料物品类型->物品。<br>
 * 虽然说这些东西看起来很奇怪而且令人血压升高。
 * */
public class Handler$Material {
    public static final Logger LOGGER = LogManager.getLogger("BREA:Material:ExtraHandle");

    Handler$Material(){}

    public void addForM(DeferredHolder<Material,? extends Material> materialHolder, IMaterialFeature<?> feature){
        if(System$Material.dataG) {
            System$Material.MF4M_ADDITION.put(materialHolder.getId(),feature);
        }else {
            LOGGER.warn("Can't add feature(type={}) to Material(id={}) as the pre stage is finished.",feature.getType().getId(),materialHolder.getId());
            LOGGER.warn(new IllegalStateException());
        }
    }

    public void addForMF(DeferredHolder<MaterialFeatureHandle<?>, ? extends MaterialFeatureHandle<?>> feature, DeferredHolder<MaterialItemType, ? extends MaterialItemType>... mit){
        if(System$Material.dataG) {
            System$Material.MIT4MF_ADDITION.putAll(feature.getId(), Arrays.asList(mit));
        }else {
            LOGGER.warn("Can't add MITs to MaterialFeature(id={}) as the pre stage is finished.",feature.getId());
            LOGGER.warn(new IllegalStateException());
        }
    }

    public void regItemForTMI(Holder<Item> itemHolder, Supplier<ITypedMaterialObj> info){
        if(System$Material.infoB){
            System$Material.I2TMIPre.add(new Pair<>(itemHolder,info));
        }else {
            LOGGER.warn("Can't attach material info for item(id={}).",itemHolder.unwrapKey().map(ResourceKey::toString).orElse("[invalid key]"));
            LOGGER.warn(new IllegalStateException());
        }
    }

    public void regMarkedItem(DeferredHolder<Material,? extends Material> material, DeferredHolder<MaterialItemType,? extends MaterialItemType> type, Supplier<ItemStack> item){
        if(System$Material.infoB){
            System$Material.M_MIT2IPre.add(new Pair<>(new Pair<>(material,type),item));
        }else {
            LOGGER.warn("Can't mark material({}) and type({}) for item stack.",material.getId(),type.getId());
            LOGGER.warn(new IllegalStateException());
        }
    }

}