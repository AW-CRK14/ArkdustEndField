package com.landis.breakdowncore.system.material.expansion.materialfeature;

import com.landis.breakdowncore.Registries;
import com.landis.breakdowncore.system.material.IMaterialFeature;
import com.landis.breakdowncore.system.material.MaterialFeatureType;
import com.landis.breakdowncore.system.material.MaterialItemType;
import net.neoforged.neoforge.registries.DeferredHolder;

import java.util.HashSet;

public class CombustibleMF implements IMaterialFeature<CombustibleMF> {
    // cv                 | Calorific value    | 热值    | KJ/kg
    // density            | Density            | 密度    | kg/m3
    // Heat               | Heat               | 总热量  | KJ
    // P = 2000           | Power              | 功率    | KJ/tick
    // defaultBurningTime | defaultBurningTime | 燃烧时间 | tick/m3
    public final int defaultBurningTime;

    public CombustibleMF(int cv, int density) {
        this.defaultBurningTime = cv * density / 2000;
    }
    @Override
    public DeferredHolder<MaterialFeatureType<?>, MaterialFeatureType<CombustibleMF>> getType() {
        return Registries.MaterialReg.COMBUSTIBLE;
    }

    @Override
    public HashSet<MaterialItemType> forItemTypes() {
        if (types == null) {
            types = new HashSet<>();
            types.add(Registries.MaterialReg.COMBUSTIBLE_TYPE.get());
        }
        return types;
    }

    private HashSet<MaterialItemType> types;
}
