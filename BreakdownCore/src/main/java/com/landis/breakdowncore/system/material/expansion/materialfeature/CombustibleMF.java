package com.landis.breakdowncore.system.material.expansion.materialfeature;

import com.landis.breakdowncore.Registries;
import com.landis.breakdowncore.system.material.IMaterialFeature;
import com.landis.breakdowncore.system.material.MaterialFeatureType;
import com.landis.breakdowncore.system.material.MaterialItemType;
import net.neoforged.neoforge.registries.DeferredHolder;

import java.util.HashSet;

public class CombustibleMF implements IMaterialFeature<CombustibleMF> {
    public final double hoc; //Heat of Combustion燃烧热 单位kj/B <=> j/mB
    public final int defaultBurningTime;//默认燃烧时间 单位tick/B

    //密度density g/cm³ <=> kg/m³ <=> kg/B
    //燃烧热hoc导入单位 kj/kg
    public CombustibleMF(double hoc, float density, int defaultBurningTime) {
        this.hoc = hoc * density;
        this.defaultBurningTime = defaultBurningTime;
    }

    public CombustibleMF(double hoc, int defaultBurningTime) {
        this.hoc = hoc;
        this.defaultBurningTime = defaultBurningTime;
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
