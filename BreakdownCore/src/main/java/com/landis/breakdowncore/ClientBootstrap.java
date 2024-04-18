package com.landis.breakdowncore;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.resources.model.AtlasSet;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.resources.ResourceLocation;

public class ClientBootstrap {
    public static final ResourceLocation MATERIAL_SHEET = new ResourceLocation(BreakdownCore.MODID,"textures/atlas/material.png");
    public static final ResourceLocation MATERIAL_ATLAS = new ResourceLocation(BreakdownCore.MODID,"material");

    static void bootstrapClientPreSetup(){
//        ModelManager.VANILLA_ATLASES.put(MATERIAL_SHEET,MATERIAL_ATLAS);
//
//        TextureAtlas material = new TextureAtlas(MATERIAL_SHEET);
//        Minecraft.getInstance().getTextureManager().register(MATERIAL_SHEET,material);
//        Minecraft.getInstance().getModelManager().atlases.atlases.put(MATERIAL_SHEET,AtlasSet.A)
    }

}
