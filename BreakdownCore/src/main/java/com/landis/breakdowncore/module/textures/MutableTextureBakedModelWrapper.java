package com.landis.breakdowncore.module.textures;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.model.BakedModelWrapper;
import net.neoforged.neoforge.client.model.data.ModelData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import static net.minecraft.world.inventory.InventoryMenu.BLOCK_ATLAS;

public class MutableTextureBakedModelWrapper<I extends BakedModel> extends BakedModelWrapper<I> {
    private static TextureAtlasSprite MISSING;
    @Nullable
    public ResourceLocation path;
    public TextureAtlasSprite texture;
    private final Map<TextureAtlasSprite,List<BakedQuad>> cache = new HashMap<>();
    public final boolean overrideParticle;
    public MutableTextureBakedModelWrapper(I originalModel,boolean overrideParticle) {
        super(originalModel);
        this.overrideParticle = overrideParticle;

    }

    public void setTexture(ResourceLocation texture){
        if(texture.equals(path)) return;
        this.texture = ((TextureAtlas)(Minecraft.getInstance().getTextureManager().getTexture(BLOCK_ATLAS))).getSprite(texture);
        this.path = texture;
    }

    public void setTexture(TextureAtlasSprite sprite){
        if(sprite.equals(texture)) return;
        this.texture = sprite;
        this.path = null;
    }

    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, RandomSource rand) {
        List<BakedQuad> original = super.getQuads(state, side, rand);
        return getBakedQuads(original);
    }

    private List<BakedQuad> getBakedQuads(List<BakedQuad> original) {
        TextureAtlasSprite sprite = getSprite();
        if(sprite.equals(getMissing())){
            return original;
        }
        List<BakedQuad> cached = cache.get(sprite);
        if(cached == null){
            cached = new ArrayList<>(original.size());
            for(BakedQuad q : original){
                BakedQuad n = new BakedQuad(q.getVertices(),q.getTintIndex(),q.getDirection(),sprite,q.isShade());
                cached.add(n);
            }
            cache.put(sprite,cached);
        }
        return cached;
    }

    @Override
    public @NotNull List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, @NotNull RandomSource rand, @NotNull ModelData extraData, @Nullable RenderType renderType) {
        List<BakedQuad> original = super.getQuads(state, side, rand, extraData, renderType);
        return getBakedQuads(original);
    }


    public static TextureAtlasSprite getMissing(){
        if(MISSING == null){
            MISSING = ((TextureAtlas)Minecraft.getInstance().getTextureManager().getTexture(BLOCK_ATLAS)).missingSprite;
        }
        return MISSING;
    }


    public @NotNull TextureAtlasSprite getSprite() {
        return texture == null ? getMissing() : texture;
    }

    @Override
    public TextureAtlasSprite getParticleIcon() {
        return overrideParticle ? getSprite() : super.getParticleIcon();
    }

    @Override
    public TextureAtlasSprite getParticleIcon(@NotNull ModelData data) {
        return overrideParticle ? getSprite() : super.getParticleIcon();
    }
}
