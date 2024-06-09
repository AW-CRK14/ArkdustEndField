package com.landis.breakdowncore.system.macmodule;

import com.landis.breakdowncore.BreakdownCore;
import com.landis.breakdowncore.system.material.Material;
import com.landis.breakdowncore.system.material.MaterialFeatureType;
import com.landis.breakdowncore.system.material.MaterialItemType;
import com.landis.breakdowncore.system.material.Registry$Material;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.registries.RegistryBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Registry$MacModule {
    public static final Logger LOGGER = LogManager.getLogger("BREA:MacModule:Registry");
    public static final Registry<MacModuleType<? extends MacModule<?>>> MODULE_TYPES = new RegistryBuilder<>(Keys.MODULE_TYPE).sync(true).create();

    public static class Keys{
        public static final ResourceKey<Registry<MacModuleType<? extends MacModule<?>>>> MODULE_TYPE = create("material_item_type");
        public static <T> ResourceKey<Registry<T>> create(String name){
            return ResourceKey.createRegistryKey(new ResourceLocation(BreakdownCore.MODID,name));
        }
    }


}
