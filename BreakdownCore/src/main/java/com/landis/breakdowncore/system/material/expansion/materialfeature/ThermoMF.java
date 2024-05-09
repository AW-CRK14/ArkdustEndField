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
    public final float c;//比热容(Specific Heat Capacity) 适应mc体系，单位为J/ml*C° 在现实世界体系中，这应当是J/g*C°,数值等价kJ/kg*C°
    public final float k;//热导率(Heat Transfer Rate) 适应mc体系，单位变为J/Kt，即面积，距离均取1 在现实世界中，这应当是W/m*C°

    public ThermoMF(float specificHeatCapacity, float heatTransferRate, float density) {  //density  单位g/cm3
        if(specificHeatCapacity <= 0 | heatTransferRate < 0){
            LOGGER.error("The C must be over 0 and the K must not be lower than 0. Currently, they're {} and {}",specificHeatCapacity,heatTransferRate);
            LOGGER.error("比热容必须高于0而热导率必须不低于0。而它们现在的数值为{}和{}",specificHeatCapacity,heatTransferRate);
            throw new IllegalArgumentException("The C or K value isn't correct.");
        }
        if(specificHeatCapacity < heatTransferRate){
            LOGGER.warn("As the Heat Transfer Rate became the value represent the heat average transfer per C° in 1 second, the value must be lower than Specific Heat Capacity.");
            LOGGER.warn("So the Heat Transfer Rate is set to Specific Heat Capacity. Transfer: {}->{}",heatTransferRate,specificHeatCapacity);
            LOGGER.warn("由于热导率被处理成了代表一秒内每摄氏度提供的热量转移的数值，您需要将其设置为低于比热容的值。因此，热导率将被设置为比热容的值。转换结果: {}->{}",heatTransferRate,specificHeatCapacity);
            heatTransferRate = specificHeatCapacity;
        }
        this.c = specificHeatCapacity;
        this.k = heatTransferRate/(c*density*1000);
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
