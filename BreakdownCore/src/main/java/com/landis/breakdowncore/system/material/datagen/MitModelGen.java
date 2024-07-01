package com.landis.breakdowncore.system.material.datagen;

import com.landis.breakdowncore.BreakdownCore;
import com.landis.breakdowncore.module.datagen.ExpandItemModelProvider;
import com.landis.breakdowncore.system.material.MaterialItemType;
import com.landis.breakdowncore.system.material.Registry$Material;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

public class MitModelGen extends ExpandItemModelProvider {
    public MitModelGen(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, BreakdownCore.MODID, existingFileHelper);
    }

    @Override
    public void registerModels() {
        super.registerModels();
        for(MaterialItemType type : Registry$Material.MATERIAL_ITEM_TYPE){
            type.gatherKeyForDatagen(this);
        }
    }
}
