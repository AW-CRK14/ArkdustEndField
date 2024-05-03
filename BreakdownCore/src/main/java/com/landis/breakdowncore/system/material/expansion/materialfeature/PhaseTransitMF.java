package com.landis.breakdowncore.system.material.expansion.materialfeature;

import com.landis.breakdowncore.Registries;
import com.landis.breakdowncore.system.material.IMaterialFeature;
import com.landis.breakdowncore.system.material.MaterialFeatureType;
import com.landis.breakdowncore.system.material.MaterialItemType;
import net.neoforged.neoforge.registries.DeferredHolder;

import java.util.HashSet;

public class PhaseTransitMF implements IMaterialFeature<PhaseTransitMF> {
    public final float mp;//熔点
    public final float bp;//沸点

    public PhaseTransitMF(float meltingPoint, float boilingPoint){
        this.mp = meltingPoint;
        this.bp = boilingPoint;
    }

    @Override
    public DeferredHolder<MaterialFeatureType<?>, MaterialFeatureType<PhaseTransitMF>> getType() {
        return Registries.MaterialReg.PHASE_TRANSIT;
    }

    @Override
    public HashSet<? extends MaterialItemType> forItemTypes() {
        return new HashSet<>();//TODO 添加气态与液态支持
        //在mp大于或等于bp时，不提供液态
    }
}
