package com.landis.breakdowncore.system.thermodynamics;

import com.landis.breakdowncore.Registries;
import com.landis.breakdowncore.system.material.Material;

public interface IThermoMatBackground extends IThermoBackground{

    Material getMaterial();

    default int maxT() {
        return (int) (getMaterial().getFeature(Registries.MaterialReg.PHASE_TRANSIT.get()).getInstance().mp * 0.6F);
    }

    default int getMC() {
        return (int) (getMaterial().getFeature(Registries.MaterialReg.THERMO.get()).getInstance().c * getM());
    }

    default float getK(){
        return getMaterial().getFeature(Registries.MaterialReg.THERMO.get()).getInstance().k;
    }

    int getM();
}
