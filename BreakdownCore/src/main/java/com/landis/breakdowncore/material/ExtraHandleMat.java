package com.landis.breakdowncore.material;

import com.landis.breakdowncore.unsafe.SkippedRegister;
import net.minecraft.core.Holder;
import net.neoforged.neoforge.registries.DeferredHolder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ExtraHandleMat {
    public static final Logger LOGGER = LogManager.getLogger("BREA:Material:ExtraHandle");

    public void addForM(RegistryMat.MRegister.Holder<? extends Material> materialHolder,IMaterialFeature<?> feature){
        if(!EventHandleMat.isPreregLock()) {
            materialHolder.addFeature(feature);
        }else {
            LOGGER.warn("Can't add feature(type={}) to Material(id={}) as the pre stage is finished.",feature.getType().id,materialHolder.get());
            LOGGER.warn(new RuntimeException());
        }
    }

    @SafeVarargs
    public final void addForMF(SkippedRegister.Holder<MaterialFeatureHandle<?>, ? extends MaterialFeatureHandle<?>> feature, SkippedRegister.Holder<MaterialItemType, ? extends MaterialItemType>... mit){
        if(!EventHandleMat.isPreregLock()) {
            feature.getUnchecked(false).addType(mit);
        }else {
            LOGGER.warn("Can't add mits to MaterialFeature(id={}) as the pre stage is finished.",feature.getId());
            LOGGER.warn(new RuntimeException());
        }
    }
}