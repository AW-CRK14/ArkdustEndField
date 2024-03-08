package com.landis.breakdowncore.material;

import com.google.common.collect.ImmutableSet;
import com.landis.breakdowncore.Registries;
import com.landis.breakdowncore.unsafe.SkippedRegister;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.registries.DeferredHolder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashSet;
import java.util.List;

public final class MaterialFeatureHandle<I extends IMaterialFeature<I>>{
    public static final Logger LOGGER = LogManager.getLogger("BREA:Material/MFH");

    public final ResourceLocation id;
    public final Class<I> clazz;
    private final HashSet<SkippedRegister.Holder<MaterialItemType,? extends MaterialItemType>> typeSet;
    private ImmutableSet<MaterialItemType> typeInsSet;

    public MaterialFeatureHandle(ResourceLocation id,Class<I> clazz, HashSet<SkippedRegister.Holder<MaterialItemType,? extends MaterialItemType>> typeSet){
        this.typeSet = typeSet;
        this.clazz = clazz;
        this.id = id;
    }

    public MaterialFeatureHandle(String modid,String path,Class<I> clazz){
        this(new ResourceLocation(modid,path),clazz,new HashSet<>());
    }

    @SafeVarargs
    public MaterialFeatureHandle(String modid, String path,Class<I> clazz, SkippedRegister.Holder<MaterialItemType,? extends MaterialItemType>... holder){
        this(new ResourceLocation(modid,path),clazz,new HashSet<>(List.of(holder)));
    }

    @SafeVarargs
    public final void addType(SkippedRegister.Holder<MaterialItemType,? extends MaterialItemType>... holder){
        if(EventHandleMat.isPreregLock()){
            LOGGER.error("Can't add MaterialItemType as the registry pre stage is finished");
        }else{
            typeSet.addAll(List.of(holder));
        }
    }


    public ImmutableSet<MaterialItemType> getOrCreateSet(){
        if(typeInsSet == null){
            typeInsSet = typeSet.stream().map(DeferredHolder::get).collect(ImmutableSet.toImmutableSet());
        }
        return typeInsSet;
    }
}
