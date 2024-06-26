package com.landis.breakdowncore.module.datagen;

import net.minecraft.client.renderer.texture.atlas.SpriteSource;
import net.minecraft.client.renderer.texture.atlas.sources.SingleFile;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.common.data.SpriteSourceProvider;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public abstract class ExpandSpriteSourceProvider extends SpriteSourceProvider {
    public ExpandSpriteSourceProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, String modId, ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, modId, existingFileHelper);
    }

    public SourceList getAtlas(ResourceLocation id) {
        return atlas(id);
    }

    public void add(ResourceLocation atlas, SpriteSource source) {
        this.atlas(atlas).addSource(source);
    }

    public void add(ResourceLocation atlas, ResourceLocation single) {
        this.atlas(atlas).addSource(new SingleFile(single, Optional.empty()));
    }

    public void add(ResourceLocation single) {
        this.atlas(BLOCKS_ATLAS).addSource(new SingleFile(single, Optional.empty()));
    }
}
