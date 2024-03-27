package com.landis.breakdowncore.system.material.expansion;

import com.landis.breakdowncore.system.material.Material;
import com.landis.breakdowncore.system.material.MaterialItemType;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.registries.RegisterEvent;

import java.util.HashMap;

public class IngotMaterialType extends MaterialItemType {
    public final HashMap<Material, IngotItem> INGOTS = new HashMap<>();
    public IngotMaterialType(float purity, ResourceLocation id) {
        super(90, purity, id);
    }

    @Override
    public void secondaryRegistry(RegisterEvent event, Material material) {
        IngotItem ingot = new IngotItem(material);
        event.register(Registries.ITEM,material.id.withPath(id -> id + "_material_ingot"),()->ingot);
        INGOTS.put(material,ingot);
    }
}
