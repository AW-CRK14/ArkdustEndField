package com.landis.breakdowncore.system.material.expansion;

import com.landis.breakdowncore.ModBusConsumer;
import com.landis.breakdowncore.system.material.Material;
import com.landis.breakdowncore.system.material.MaterialItemType;
import com.landis.breakdowncore.system.material.TypedMaterialItem;
import com.landis.breakdowncore.system.material.datagen.MitModelGen;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.registries.RegisterEvent;

import java.util.HashMap;

public class IngotType extends MaterialItemType {
    public final HashMap<Material, IngotItem> WITH_MATERIAL = new HashMap<>();
    public final HashMap<ResourceLocation, IngotItem> WITH_RESOURCE = new HashMap<>();
    public IngotType(float purity, ResourceLocation id) {
        super(90, purity, id);
    }

    @Override
    public void secondaryRegistry(RegisterEvent event, Material material) {
        Registry<Item> registry = (Registry<Item>) ModBusConsumer.REGS_MAP.get(Registries.ITEM);

        if (material.id.toString().equals("brea:missing")) {
            return;
        }
        IngotItem ingot = new IngotItem(material);
        ResourceLocation location = material.id.withPath(id -> id + "_material_ingot");
        Registry.register(registry,location,ingot);
        WITH_MATERIAL.put(material, ingot);
        WITH_RESOURCE.put(location, ingot);
    }

    @Override
    public void gatherKeyForDatagen(MitModelGen ins) {
        super.gatherKeyForDatagen(ins);
        for(ResourceLocation id : WITH_RESOURCE.keySet()){
            ins.basicItem(id);
        }
    }

    @Override
    public void attachToCreativeTab(BuildCreativeModeTabContentsEvent event) {
        super.attachToCreativeTab(event);
        for(IngotItem item : WITH_RESOURCE.values()){
            event.accept(item);
        }
    }
}
