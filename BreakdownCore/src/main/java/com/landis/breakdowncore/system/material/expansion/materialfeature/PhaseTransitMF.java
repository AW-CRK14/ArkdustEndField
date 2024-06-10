package com.landis.breakdowncore.system.material.expansion.materialfeature;

import com.landis.breakdowncore.Registries;
import com.landis.breakdowncore.system.material.IMaterialFeature;
import com.landis.breakdowncore.system.material.MaterialFeatureType;
import com.landis.breakdowncore.system.material.MaterialItemType;
import net.neoforged.neoforge.registries.DeferredHolder;

import java.util.HashSet;

public class PhaseTransitMF implements IMaterialFeature<PhaseTransitMF> {
    // mp | Melting point    | 熔点    | ℃
    // bp | Boiling point    | 沸点    | ℃
    public final int mp;
    public final int bp;

    public PhaseTransitMF(int meltingPoint, int boilingPoint){
        this.mp = meltingPoint;
        this.bp = boilingPoint;
    }

    @Override
    public DeferredHolder<MaterialFeatureType<?>, MaterialFeatureType<PhaseTransitMF>> getType() {
        return Registries.MaterialReg.PHASE_TRANSIT;
    }

    @Override
    public HashSet<MaterialItemType> forItemTypes() {
        return new HashSet<>();//TODO 添加气态与液态支持
        //在mp大于或等于bp时，不提供液态
    }
}
