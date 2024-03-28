package com.landis.breakdowncore.system.material.datagen;

import com.landis.breakdowncore.BreakdownCore;
import com.landis.breakdowncore.system.material.MaterialItemType;
import com.landis.breakdowncore.system.material.Registry$Material;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

import java.util.ArrayList;
import java.util.List;

public class MitModelGen extends ItemModelProvider {
    public MitModelGen(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, BreakdownCore.MODID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        for(MaterialItemType type : Registry$Material.MATERIAL_ITEM_TYPE){
            type.gatherKeyForDatagen(this);
        }
    }
}
