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
    // ic      | Input Specific Heat Capacity | 设定比热容 | J/(kg·℃)
    // ik      | Input Heat Transfer Rate     | 设定热导率 | W/(m·℃)
    // density | Density                      | 密度      | kg/m3
    // V = 1   | Volume                       | 体积      | m3
    // c       | Specific Heat Capacity       | 比热容    | KJ/℃
    // k       | Heat Transfer Rate           | 热导率    | KJ/(℃·5tick)
    public final float c;
    public final float k;

    public ThermoMF(float ic, float ik, float density) {
        if(ic <= 0 | ik < 0){
            LOGGER.error("The C must be over 0 and the K must not be lower than 0. Currently, they're {} and {}",ic,ik);
            LOGGER.error("比热容必须高于0而热导率必须不低于0。而它们现在的数值为{}和{}",ic,ik);
            throw new IllegalArgumentException("The C or K value isn't correct.");
        }
        this.c = ic / ( density * 1 * 1000 );
        this.k = ik / 4000;
    }

    @Override
    public DeferredHolder<MaterialFeatureType<?>, MaterialFeatureType<ThermoMF>> getType() {
        return Registries.MaterialReg.THERMO;
    }

    @Override
    public HashSet<MaterialItemType> forItemTypes() {
        return new HashSet<>();
    }
}
