package com.landis.breakdowncore.system.material.expansion.materialfeature;

import com.landis.breakdowncore.Registries;
import com.landis.breakdowncore.system.material.IMaterialFeature;
import com.landis.breakdowncore.system.material.MaterialFeatureType;
import com.landis.breakdowncore.system.material.MaterialItemType;
import net.neoforged.neoforge.registries.DeferredHolder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashSet;

public class ThermoMF implements IMaterialFeature<ThermoMF> {
    public static final Logger LOGGER = LogManager.getLogger("BREA:Material/MF:Thermo");
    public final float c;//比热容(Specific Heat Capacity) 适应mc体系，单位为kJ/B·K或J/mB·K 在现实世界体系中，这应当是kJ/kg·C°
    public final float k;//热导率(Heat Transfer Rate) 适应mc体系，单位变为J/K·m²，即距离，时间取1 在现实世界中，这应当是W/m·C°

    public ThermoMF(float specificHeatCapacity, float heatTransferRate, float density) {  //density密度  单位g/cm3,数值等价t/m3
        if(specificHeatCapacity <= 0 | heatTransferRate < 0){
            LOGGER.error("The C must be over 0 and the K must not be lower than 0. Currently, they're {} and {}",specificHeatCapacity,heatTransferRate);
            LOGGER.error("比热容必须高于0而热导率必须不低于0。而它们现在的数值为{}和{}",specificHeatCapacity,heatTransferRate);
            throw new IllegalArgumentException("The C or K value isn't correct.");
        }
        this.c = specificHeatCapacity * density * 1000; //c' = ρVc 此处V取1m3，c放大一千倍
        this.k = heatTransferRate;
    }

    @Override
    public DeferredHolder<MaterialFeatureType<?>, MaterialFeatureType<ThermoMF>> getType() {
        return Registries.MaterialReg.THERMO;
    }

    @Override
    public HashSet<? extends MaterialItemType> forItemTypes() {
        return new HashSet<>();
    }
}
