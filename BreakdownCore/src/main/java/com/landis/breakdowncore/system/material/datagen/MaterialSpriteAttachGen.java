package com.landis.breakdowncore.system.material.datagen;

import com.landis.breakdowncore.BreakdownCore;
import com.landis.breakdowncore.system.material.Material;
import com.landis.breakdowncore.system.material.MaterialItemType;
import com.landis.breakdowncore.system.material.Registry$Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.atlas.sources.DirectoryLister;
import net.minecraft.client.renderer.texture.atlas.sources.SingleFile;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.common.data.SpriteSourceProvider;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class MaterialSpriteAttachGen extends SpriteSourceProvider {
    public MaterialSpriteAttachGen(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, BreakdownCore.MODID, existingFileHelper);
    }

    @Override
    protected void gather() {
        SourceList sourceList = atlas(BLOCKS_ATLAS);
//        for(Material material : Registry$Material.MATERIAL){
//            sourceList.addSource(new SingleFile(material.id.withPath(id -> "brea/material/" + id), Optional.empty()));
//        }
//        for(MaterialItemType type : Registry$Material.MATERIAL_ITEM_TYPE){
//            sourceList.addSource(new SingleFile(type.id.withPath(s -> "brea/mit/" + s),Optional.empty()));
//            if(this.existingFileHelper.exists(type.id.withPath(s -> "textures/brea/mit_cover/" + s + ".png"), PackType.CLIENT_RESOURCES)){
//                sourceList.addSource(new SingleFile(type.id.withPath(s -> "brea/mit_cover/" + s),Optional.empty()));
//            }
//        }

        sourceList.addSource(new DirectoryLister("brea/material/mit","brea/material/mit/"));
//        sourceList.addSource(new DirectoryLister("brea/mit","brea/mit/"));
//        sourceList.addSource(new DirectoryLister("brea/mit_cover","brea/mit_cover/"));
    }
}
