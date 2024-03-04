package com.landis.arkdust.datagen;

import com.landis.arkdust.Arkdust;
import com.landis.arkdust.registry.ItemRegistry;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.DiggerItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SwordItem;
import net.neoforged.neoforge.client.model.generators.ItemModelBuilder;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.registries.DeferredHolder;

public class ItemModelGen extends ItemModelProvider {
    public ItemModelGen(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, Arkdust.MODID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        for (DeferredHolder<Item, ? extends Item> item : ItemRegistry.getEntries()){
            if(!(item.get() instanceof BlockItem)) {
                if (item.get() instanceof DiggerItem || item.get() instanceof SwordItem) {
                    handheld(item);
                } else {
                    generated(item);
                }
            }
        }
    }

    private ItemModelBuilder generated(DeferredHolder<Item, ? extends Item> item){
        return withExistingParent(item.getId().getPath(),new ResourceLocation("item/generated"))
                .texture("layer0",new ResourceLocation(Arkdust.MODID,"item/" + item.getId().getPath()));
    }

    private ItemModelBuilder handheld(DeferredHolder<Item, ? extends Item> item){
        return withExistingParent(item.getId().getPath(),new ResourceLocation("item/handheld"))
                .texture("layer0",new ResourceLocation(Arkdust.MODID,"item/" + item.getId().getPath()));
    }

}
