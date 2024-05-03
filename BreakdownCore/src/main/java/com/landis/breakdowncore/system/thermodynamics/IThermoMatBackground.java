package com.landis.breakdowncore.system.thermodynamics;

import com.landis.breakdowncore.Registries;
import com.landis.breakdowncore.system.material.Material;

public interface IThermoMatBackground extends IThermoBackground{

    Material getMaterial();

    default int maxTx100() {
        return (int) (getMaterial().getFeature(Registries.MaterialReg.THERMO.get()).getInstance().k * 100);
    }

    default int getCx100() {
        return (int) (getMaterial().getFeature(Registries.MaterialReg.THERMO.get()).getInstance().c * 100);
    }
}
