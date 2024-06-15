package com.landis.breakdowncore.system.material.expansion.materialfeature;

import com.landis.breakdowncore.BREARegistries;
import com.landis.breakdowncore.system.material.IMaterialFeature;
import com.landis.breakdowncore.system.material.MaterialFeatureType;
import com.landis.breakdowncore.system.material.MaterialItemType;
import net.neoforged.neoforge.registries.DeferredHolder;

import java.util.HashSet;

public class CombustibleMF implements IMaterialFeature<CombustibleMF> {
    /**
     * <table border="1">
     *   <caption>数据单位对照</caption>
     *   <tr>
     *     <th>变量名</th>
     *     <th>英文名称</th>
     *     <th>中文名称</th>
     *     <th>单位</th>
     *   </tr>
     *   <tr>
     *     <td>cv</td>
     *     <td>Calorific Value</td>
     *     <td>热值</td>
     *     <td>kJ/kg</td>
     *   </tr>
     *   <tr>
     *     <td>density</td>
     *     <td>Density</td>
     *     <td>密度</td>
     *     <td>kg/m³</td>
     *   </tr>
     *   <tr>
     *     <td>heat</td>
     *     <td>Heat</td>
     *     <td>总热量</td>
     *     <td>kJ</td>
     *   </tr>
     *   <tr>
     *     <td>P = 2000</td>
     *     <td>Power</td>
     *     <td>功率</td>
     *     <td>kJ/tick</td>
     *   </tr>
     *   <tr>
     *     <td>defaultBurningTime</td>
     *     <td>Default Burning Time</td>
     *     <td>燃烧时间</td>
     *     <td>tick/m³</td>
     *   </tr>
     * </table>
     */
    public final int defaultBurningTime;

    public CombustibleMF(int cv, int density) {
        this.defaultBurningTime = cv * density / 2000;
    }

    @Override
    public DeferredHolder<MaterialFeatureType<?>, MaterialFeatureType<CombustibleMF>> getType() {
        return BREARegistries.MaterialReg.COMBUSTIBLE;
    }

    @Override
    public HashSet<MaterialItemType> forItemTypes() {
        if (types == null) {
            types = new HashSet<>();
            types.add(BREARegistries.MaterialReg.COMBUSTIBLE_TYPE.get());
        }
        return types;
    }

    private HashSet<MaterialItemType> types;
}
