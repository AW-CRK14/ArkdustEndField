package com.landis.breakdowncore.system.material.expansion.materialfeature;

import com.landis.breakdowncore.BreaRegistries;
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
     *     <td>q</td>
     *     <td>Combustion Internal Energy</td>
     *     <td>燃烧内能</td>
     *     <td>kJ/mB</td>
     *   </tr>
     * </table>
     */
    public final long q;

    public CombustibleMF(int cv, int density) {
        this.q = (long) cv * density / 1000;
    }

    @Override
    public DeferredHolder<MaterialFeatureType<?>, MaterialFeatureType<CombustibleMF>> getType() {
        return BreaRegistries.MaterialReg.COMBUSTIBLE;
    }

    @Override
    public HashSet<MaterialItemType> forItemTypes() {
        if (types == null) {
            types = new HashSet<>();
            types.add(BreaRegistries.MaterialReg.COMBUSTIBLE_TYPE.get());
        }
        return types;
    }

    private HashSet<MaterialItemType> types;
}
