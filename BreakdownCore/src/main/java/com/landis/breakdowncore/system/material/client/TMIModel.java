package com.landis.breakdowncore.system.material.client;

import com.landis.breakdowncore.BreakdownCore;
import com.landis.breakdowncore.Registries;
import com.landis.breakdowncore.module.render.model.BakedQuadsCache;
import com.landis.breakdowncore.module.render.model.ItemORResolveOnly;
import com.landis.breakdowncore.system.material.*;
import com.landis.breakdowncore.system.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.*;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static net.minecraft.world.inventory.InventoryMenu.BLOCK_ATLAS;

public class TMIModel implements BakedModel {
    private final ItemOverrides ITEM_OVERRIDES = new ItemORResolveOnly((model, stack, level, entity, seed) -> {
        ITypedMaterialObj info = System$Material.getMaterialInfo(stack.getItem());
        if(info != null){
            setMaterialType(info.getMaterialOrMissing(stack));
            return getModel();
        }
        return model;
    });

    public final ModelBakery bakery;
    private BakedModel shapeModel;
    public final MaterialItemType type;
    private final Map<Material, BakedQuadsCache> quadsCache = new HashMap<>();
    private final Map<Material, BakedModel> modelCache = new HashMap<>();
    private BakedQuadsCache missingModel;
//    public final TextureAtlasSprite MISSING_SPRITE = Minecraft.getInstance().getTextureAtlas(new ResourceLocation(BreakdownCore.MODID,"material")).apply(new ResourceLocation(BreakdownCore.MODID,"material/missing"));
    private TextureAtlasSprite missingSprite;
    private boolean cached = false;
    private boolean inited = false;
    @Nonnull
    private Material materialType = Registries.MaterialReg.MISSING.get();

    public TMIModel(ModelBakery bakery, MaterialItemType type) {
        this.type = type;
        this.bakery = bakery;
    }

    public void setMaterialType(Material materialType) {
        if(materialType != this.materialType) {
            this.cached = false;
            this.materialType = Objects.requireNonNullElse(materialType, Registries.MaterialReg.MISSING.get());
        }
    }
    @Nonnull
    public Material getMaterialType() {
        return materialType;
    }

    @Override
    public @NotNull List<BakedQuad> getQuads(@Nullable BlockState pState, @Nullable Direction pDirection, RandomSource pRandom) {
        bootstrapCache();
        return quadsCache.get(materialType).getQuads(pDirection);
    }

    public @NotNull BakedModel getModel(){
        bootstrapCache();
        return modelCache.get(materialType);
//        return Minecraft.getInstance().getModelManager().getModel(new ModelResourceLocation(BuiltInRegistries.ITEM.getKey(Items.GOLD_INGOT),"inventory"));
    }

    private void bootstrapCache(){
        if(!inited) {
            this.missingSprite = Minecraft.getInstance().getTextureAtlas(BLOCK_ATLAS).apply(new ResourceLocation(BreakdownCore.MODID,"material/missing"));

//            ResourceLocation bakeName = new ResourceLocation(BreakdownCore.MODID, "material_item_bake/" + type.id.toString().replace(":", "_"));
//            BlockModel model = (BlockModel) bakery
//                    .getModel(System$Material.MIT_BASIC_MODEL_LOCATION.apply(type.id));
//            this.shapeModel = BreakdownCore.getItemModelgen()
//                    .generateBlockModel(net.minecraft.client.resources.model.Material::sprite, model)
//                    .bake(bakery.new ModelBakerImpl((location, material) -> material.sprite(), bakeName),
//                            model, net.minecraft.client.resources.model.Material::sprite, BlockModelRotation.X0_Y0, bakeName,false);

//            UnbakedModel model = bakery.getModel(System$Material.MIT_BASIC_MODEL_LOCATION.apply(type.id));
//            this.shapeModel = model.bake(bakery.new ModelBakerImpl((location, material) -> material.sprite(), bakeName),
//                    net.minecraft.client.resources.model.Material::sprite, BlockModelRotation.X0_Y0, bakeName);//好丑……

            shapeModel = Minecraft.getInstance().getModelManager().getModel(System$Material.basicModel(this.type.id));

            this.missingModel = new BakedQuadsCache(shapeModel);
//            this.quadsCache.put(materialType, missingModel);
//            this.modelCache.put(materialType, new Wrapped(missingModel, missingSprite));
            inited = true;
        }

        if(!cached) {
            if (!quadsCache.containsKey(materialType)) {
                BakedQuadsCache c = missingModel.copy(withMaterial(materialType));
                quadsCache.put(materialType,c);
                modelCache.put(materialType,new Wrapped(c, c.quads.isEmpty() ? missingSprite : c.quads.get(0).getSprite()));
            }
            cached = true;
        }
    }

    @Override
    public boolean useAmbientOcclusion() {
        return true;
    }

    @Override
    public boolean isGui3d() {
        return false;
    }

    @Override
    public boolean usesBlockLight() {
        return true;
    }

    @Override
    public boolean isCustomRenderer() {
        return false;
    }

    @Override
    public TextureAtlasSprite getParticleIcon() {
        return missingSprite;
    }

    @Override
    public ItemOverrides getOverrides() {
        return ITEM_OVERRIDES;
    }

    private static BakedQuadsCache.BakedQuadHandle WITH_MISSING;

    public static BakedQuadsCache.BakedQuadHandle withMaterial(Material material) {
        TextureAtlasSprite textures = System$Material.getTexture(material);
        if (textures == null) {
            if(WITH_MISSING == null){
                WITH_MISSING = withMaterial(Registries.MaterialReg.MISSING.get());
            }
            return WITH_MISSING;
        }
        return (vertices, tintIndex, direction, sprite, shade, hasAmbientOcclusion) -> new BakedQuad(vertices, tintIndex, direction, textures, shade, hasAmbientOcclusion);
    }

    private record Wrapped(BakedQuadsCache cache,TextureAtlasSprite particle) implements BakedModel{
        @Override
        public List<BakedQuad> getQuads(@Nullable BlockState pState, @Nullable Direction pDirection, RandomSource pRandom) {
            return cache.getQuads(pDirection);
        }

        @Override
        public boolean useAmbientOcclusion() {
            return true;
        }

        @Override
        public boolean isGui3d() {
            return false;
        }

        @Override
        public boolean usesBlockLight() {
            return true;
        }

        @Override
        public boolean isCustomRenderer() {
            return false;
        }

        @Override
        public TextureAtlasSprite getParticleIcon() {
            return particle;
        }

        @Override
        public ItemOverrides getOverrides() {
            return ItemOverrides.EMPTY;
        }
    }
}
