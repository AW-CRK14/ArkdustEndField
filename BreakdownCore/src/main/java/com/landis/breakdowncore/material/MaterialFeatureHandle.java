package com.landis.breakdowncore.material;

import com.landis.breakdowncore.unsafe.SkippedRegister;
import net.minecraft.resources.ResourceLocation;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public record MaterialFeatureHandle<I extends IMaterialFeature<I>>(ResourceLocation id,Class<I> clazz,
                                                                   HashSet<SkippedRegister.Holder<MaterialItemType,? extends MaterialItemType>> typeSet){
    public MaterialFeatureHandle(String modid,String path,Class<I> clazz){
        this(new ResourceLocation(modid,path),clazz,new HashSet<>());
    }

    @SafeVarargs
    public MaterialFeatureHandle(String modid, String path,Class<I> clazz, SkippedRegister.Holder<MaterialItemType,? extends MaterialItemType>... holder){
        this(new ResourceLocation(modid,path),clazz,new HashSet<>(List.of(holder)));
    }


}
