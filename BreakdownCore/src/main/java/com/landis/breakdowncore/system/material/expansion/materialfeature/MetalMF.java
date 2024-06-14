package com.landis.breakdowncore.system.material.expansion.materialfeature;

import com.landis.breakdowncore.BREARegistries;
import com.landis.breakdowncore.system.material.IMaterialFeature;
import com.landis.breakdowncore.system.material.MaterialFeatureType;
import com.landis.breakdowncore.system.material.MaterialItemType;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.registries.DeferredHolder;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.List;

public class MetalMF implements IMaterialFeature<MetalMF> {
    public MetalMF(){}

    @Override
    public DeferredHolder<MaterialFeatureType<?>, MaterialFeatureType<MetalMF>> getType() {
//        return Registries.MaterialReg.METAL;
        return BREARegistries.MaterialReg.METAL;
    }

    @Override
    public HashSet<MaterialItemType> forItemTypes() {
        if(types == null){
            types = new HashSet<>();
//            types.addAll(List.of(Registries.MaterialReg.INGOT.get()));
            types.add(BREARegistries.MaterialReg.INGOT.get());
        }
        return types;
    }

    @Override
    public @Nullable List<ResourceLocation> dependencies() {
        return List.of(BREARegistries.MaterialReg.PHASE_TRANSIT.getId(), BREARegistries.MaterialReg.THERMO.getId());
    }

    private HashSet<MaterialItemType> types;
}
