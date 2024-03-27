package com.landis.breakdowncore.system.material.datagen;

import com.landis.breakdowncore.BreakdownCore;
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

    public final List<ResourceLocation> ITEMS = new ArrayList<>();
    public final List<ResourceLocation> BLOCKS = new ArrayList<>();
    public static final ResourceLocation GENERATED = new ResourceLocation("item/generated");

    @Override
    protected void registerModels() {
        for(ResourceLocation item : ITEMS){
            getBuilder(item.toString())
                    .parent(new ModelFile.UncheckedModelFile("item/generated"))
                    .texture("layer0", new ResourceLocation(item.getNamespace(), "brea/mit/" + item.getPath()));
        }
        //TODO 方块模型处理
    }
}
