package com.landis.breakdowncore.system.thermodynamics;

import com.landis.breakdowncore.BreaRegistries;
import com.landis.breakdowncore.system.material.Material;

public interface IThermoMatBackground extends IThermoBackground {

    Material getMaterial();

    default int maxT() {
        return (int) (getMaterial().getFeature(BreaRegistries.MaterialReg.PHASE_TRANSIT.get()).getInstance().mp * 1.14F);
    }

    default long getMC() {
        return (long) (getMaterial().getFeature(BreaRegistries.MaterialReg.THERMO.get()).getInstance().c * getM() / 1000);
    }

    default float getK() {
        return getMaterial().getFeature(BreaRegistries.MaterialReg.THERMO.get()).getInstance().k;
    }

    long getM();//单位mB
}
