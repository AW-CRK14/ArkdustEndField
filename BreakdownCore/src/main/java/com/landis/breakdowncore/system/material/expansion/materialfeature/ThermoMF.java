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

    /**
     * <table border="1">
     *   <caption>热力学属性表</caption>
     *   <tr>
     *     <th style="padding-right: 10px;">变量名</th>
     *     <th style="padding-left: 10px;">英文名称</th>
     *     <th>中文名称</th>
     *     <th>单位</th>
     *   </tr>
     *   <tr>
     *     <td>ic</td>
     *     <td>Input Specific Heat Capacity</td>
     *     <td>设定比热容</td>
     *     <td>J/(g·°C)</td>
     *   </tr>
     *   <tr>
     *     <td>ik</td>
     *     <td>Input Heat Transfer Rate</td>
     *     <td>设定热导率</td>
     *     <td>W/(m·°C)</td>
     *   </tr>
     *   <tr>
     *     <td>density</td>
     *     <td>Density</td>
     *     <td>密度</td>
     *     <td>g/cm³</td>
     *   </tr>
     *   <tr>
     *     <td>V = 1</td>
     *     <td>Volume</td>
     *     <td>体积</td>
     *     <td>m³</td>
     *   </tr>
     *   <tr>
     *     <td>c</td>
     *     <td>Specific Heat Capacity</td>
     *     <td>比热容</td>
     *     <td>kJ/(°C·m³)</td>
     *   </tr>
     *   <tr>
     *     <td>k</td>
     *     <td>Heat Transfer Rate</td>
     *     <td>热导率</td>
     *     <td>kJ/(°C·5tick)</td>
     *   </tr>
     * </table>
     */
    public final float c;
    public final float k;

    public ThermoMF(float ic, float ik, float density) {
        if(ic <= 0 | ik < 0){
            LOGGER.error("The C must be over 0 and the K must not be lower than 0. Currently, they're {} and {}",ic,ik);
            LOGGER.error("比热容必须高于0而热导率必须不低于0。而它们现在的数值为{}和{}",ic,ik);
            throw new IllegalArgumentException("The C or K value isn't correct.");
        }
        this.c = ic * density * 1000;
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
