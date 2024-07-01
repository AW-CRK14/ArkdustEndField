package com.landis.breakdowncore.module.datagen;

import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

import java.util.ArrayList;
import java.util.List;

public abstract class ExpandItemModelProvider extends ItemModelProvider implements IAgencyProvider<ExpandItemModelProvider>{
    private List<ExpandItemModelProvider> agency = new ArrayList<>();
    public ExpandItemModelProvider(PackOutput output, String modid, ExistingFileHelper existingFileHelper) {
        super(output, modid, existingFileHelper);
    }

    public void addAgency(ExpandItemModelProvider provider){
        this.agency.add(provider);
    }

    @Override
    public List<ExpandItemModelProvider> getAgency() {
        return agency;
    }

    @Override
    public void execute(ExpandItemModelProvider instance) {
        instance.registerModels();
        this.generatedModels.putAll(instance.generatedModels);
    }

    @Override
    public void registerModels() {
        agency();
    }

}
