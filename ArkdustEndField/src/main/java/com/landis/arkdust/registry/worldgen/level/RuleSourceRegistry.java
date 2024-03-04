package com.landis.arkdust.registry.worldgen.level;

import com.landis.arkdust.Arkdust;
import com.landis.arkdust.worldgen.dimension.SarconDimension;
import com.mojang.serialization.Codec;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.levelgen.SurfaceRules;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class RuleSourceRegistry {
    public static final DeferredRegister<Codec<? extends SurfaceRules.RuleSource>> REGISTRY = DeferredRegister.create(BuiltInRegistries.MATERIAL_RULE, Arkdust.MODID);

//    public static void bootstrap(){
//    }

    public static final DeferredHolder<Codec<? extends SurfaceRules.RuleSource>,Codec<SarconDimension.SurfaceSource>> SARCON_SURFACE = REGISTRY.register("sarcon",()-> SarconDimension.SurfaceSource.CODEC);

//    public static ResourceKey<Codec<? extends SurfaceRules.RuleSource>> registry(String name,Codec<? extends SurfaceRules.RuleSource> codec){
//        ResourceKey<Codec<? extends SurfaceRules.RuleSource>> key = ResourceKey.create(BuiltInRegistries.MATERIAL_RULE.key(),new ResourceLocation(Arkdust.MODID,name));
//        Registry.register(BuiltInRegistries.MATERIAL_RULE,key,codec);
//        return key;
//    }
}
