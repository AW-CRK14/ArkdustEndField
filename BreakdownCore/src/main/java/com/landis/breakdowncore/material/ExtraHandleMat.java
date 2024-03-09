package com.landis.breakdowncore.material;

import com.landis.breakdowncore.unsafe.SkippedRegister;
import net.minecraft.core.Holder;
import net.minecraft.world.item.Item;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.function.Supplier;

/**ExtraHandleMat<br>
 * 此类是Material系统的拓展功能，用于为不易于修改的，如其它mod或原版的物品，提供材料信息<br>
 * 主要有四个功能：为材料添加材料特征，为材料特征添加材料物品类型，添加 物品->材料信息 ，添加 材料-材料物品类型->物品。<br>
 * 虽然说这些东西看起来很奇怪而且令人血压升高。
 * */
public class ExtraHandleMat {
    public static final Logger LOGGER = LogManager.getLogger("BREA:Material:ExtraHandle");

    public void addForM(RegistryMat.MRegister.Holder<? extends Material> materialHolder,IMaterialFeature<?> feature){
        if(!EventHandleMat.isDataGatherStageFinished()) {
            materialHolder.addFeature(feature);
        }else {
            LOGGER.warn("Can't add feature(type={}) to Material(id={}) as the pre stage is finished.",feature.getType().id,materialHolder.get());
            LOGGER.warn(new RuntimeException());
        }
    }

    public void addForMF(SkippedRegister.Holder<MaterialFeatureHandle<?>, ? extends MaterialFeatureHandle<?>> feature, SkippedRegister.Holder<MaterialItemType, ? extends MaterialItemType>... mit){
        if(!EventHandleMat.isDataGatherStageFinished()) {
            feature.getUnchecked(false).addType(mit);
        }else {
            LOGGER.warn("Can't add mits to MaterialFeature(id={}) as the pre stage is finished.",feature.getId());
            LOGGER.warn(new RuntimeException());
        }
    }

    public void regItemForTMI(Holder<Item> itemHolder, Supplier<ITypedMaterialObj> info){
        if(!EventHandleMat.isFinalDataHandleFinished()){

        }
    }
}