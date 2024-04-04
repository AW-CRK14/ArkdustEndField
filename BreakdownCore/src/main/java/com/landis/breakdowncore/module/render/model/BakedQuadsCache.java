package com.landis.breakdowncore.module.render.model;

import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.neoforged.neoforge.client.model.data.ModelData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BakedQuadsCache {
    public final List<BakedQuad> quads = new ArrayList<>();
    public final Map<Direction, List<BakedQuad>> directional = new HashMap<>();

    public BakedQuadsCache(BakedModel original) {
        this(original.getQuads(null, null, RandomSource.create(), ModelData.EMPTY, null));
    }

    public BakedQuadsCache(List<BakedQuad> quads) {
        this.quads.addAll(quads);
        for (Direction e : Direction.values()) {
            directional.put(e, new ArrayList<>());
        }
        for (BakedQuad q : quads) {
            directional.get(q.getDirection()).add(q);
        }
    }

    public BakedQuadsCache copy(){
        return copy(NO_EXTRA);
    }
    public BakedQuadsCache copy(BakedQuadHandle handle) {
        return new BakedQuadsCache(quads.stream().map(handle::convert).toList());
    }

    public List<BakedQuad> getQuads(Direction direction) {
        return direction == null ? quads : directional.get(direction);
    }

    private static final BakedQuadHandle NO_EXTRA = BakedQuad::new;

    public interface BakedQuadHandle {
        BakedQuad convert(int[] vertices, int tintIndex, Direction direction, TextureAtlasSprite sprite, boolean shade, boolean hasAmbientOcclusion);

        default BakedQuad convert(BakedQuad quad) {
            return convert(quad.getVertices(), quad.getTintIndex(), quad.getDirection(), quad.getSprite(), quad.isShade(), quad.hasAmbientOcclusion());
        }
    }
}
