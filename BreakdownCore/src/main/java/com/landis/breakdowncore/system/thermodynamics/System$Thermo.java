package com.landis.breakdowncore.system.thermodynamics;

import com.landis.breakdowncore.BreaRegistries;
import com.landis.breakdowncore.system.material.IMaterialFeature;
import com.landis.breakdowncore.system.material.ITypedMaterialObj;
import com.landis.breakdowncore.system.material.System$Material;
import com.landis.breakdowncore.system.material.expansion.materialfeature.CombustibleMF;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nonnull;
import java.util.OptionalDouble;

public class System$Thermo {
    public static OptionalDouble getCombustionInternalEnergy(@Nonnull ItemStack stack) {
        ITypedMaterialObj matinfo = System$Material.getMaterialInfo(stack.getItem());
        if (matinfo == null) return OptionalDouble.empty();
        IMaterialFeature<CombustibleMF> combus = matinfo.getMaterialOrMissing(stack).getFeature(BreaRegistries.MaterialReg.COMBUSTIBLE.get());
        if (combus == null) return OptionalDouble.empty();
        return OptionalDouble.of(matinfo.getAvailable() * combus.getInstance().q);
    }
}
