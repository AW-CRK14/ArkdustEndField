package com.landis.arkdust.resource;

import com.landis.arkdust.Arkdust;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.dimension.DimensionType;

public class Tags {
    public static void bootstrap(){
        DimensionTypeTags.bootstrap();
    }


    public static class DimensionTypeTags {
        public static final TagKey<DimensionType> HAS_WORLD_STATE = tag("has_world_state");

        private static TagKey<DimensionType> tag(String path){
            return TagKey.create(Registries.DIMENSION_TYPE,new ResourceLocation(Arkdust.MODID,path));
        }
        public static void bootstrap(){
        }

    }
}
