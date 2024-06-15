package com.landis.breakdowncore.system.material.client;

import com.landis.breakdowncore.BreakdownCore;
import com.landis.breakdowncore.BreaRegistries;
import com.landis.breakdowncore.module.render.model.ItemORResolveOnly;
import com.landis.breakdowncore.system.material.ITypedMaterialObj;
import com.landis.breakdowncore.system.material.Material;
import com.landis.breakdowncore.system.material.MaterialItemType;
import com.landis.breakdowncore.system.material.System$Material;
import com.mojang.math.Transformation;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.model.SimpleModelState;
import net.neoforged.neoforge.client.model.data.ModelData;
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
    private BlockModel shapeModel;
    public final MaterialItemType type;
    private final Map<Material, BakedModel> modelCache = new HashMap<>();
    private final Map<Integer,BakedModel> idpCache = new HashMap<>();
    private BakedModel missingModel;
//    public final TextureAtlasSprite MISSING_SPRITE = Minecraft.getInstance().getTextureAtlas(new ResourceLocation(BreakdownCore.MODID,"material")).apply(new ResourceLocation(BreakdownCore.MODID,"material/missing"));
    private TextureAtlasSprite missingSprite;
    private boolean cached = false;
    private boolean inited = false;
    @Nonnull
    private Material materialType = BreaRegistries.MaterialReg.MISSING.get();

    public TMIModel(ModelBakery bakery, MaterialItemType type) {
        this.type = type;
        this.bakery = bakery;
    }

    public void setMaterialType(Material materialType) {
        if(materialType != this.materialType) {
            this.cached = false;
            this.materialType = Objects.requireNonNullElse(materialType, BreaRegistries.MaterialReg.MISSING.get());
        }
    }
    @Nonnull
    public Material getMaterialType() {
        return materialType;
    }

    @Override
    public @NotNull List<BakedQuad> getQuads(@Nullable BlockState pState, @Nullable Direction pDirection, RandomSource pRandom) {
        bootstrapCache();
        return missingModel.getQuads(pState, pDirection, pRandom, ModelData.EMPTY, null);
    }

    public @NotNull BakedModel getModel(){
        bootstrapCache();
        return modelCache.get(materialType);
    }

    private void bootstrapCache(){
        if(!inited) {
            this.missingSprite = Minecraft.getInstance().getTextureAtlas(BLOCK_ATLAS).apply(new ResourceLocation(BreakdownCore.MODID,"material/missing"));

            ResourceLocation bakeName = new ResourceLocation(BreakdownCore.MODID, "material_item_bake/" + type.id.toString().replace(":", "_"));

            this.shapeModel = (BlockModel) bakery.getModel(System$Material.basicModel(this.type.id));
//            this.shapeModel.customData.setRenderTypeHint(new ResourceLocation(BreakdownCore.MODID,"material"));
            this.missingModel = BreakdownCore.getItemModelgen().generateBlockModel(net.minecraft.client.resources.model.Material::sprite, shapeModel)
                    .bake(bakery.new ModelBakerImpl((location, material) -> material.sprite(), bakeName), net.minecraft.client.resources.model.Material::sprite, new SimpleModelState(Transformation.identity()), bakeName);//好丑……
            inited = true;
        }

        if(!cached) {
            if (!modelCache.containsKey(materialType)) {
                ResourceLocation bakeName = new ResourceLocation(BreakdownCore.MODID, "material_item_bake/" + type.id.toString().replace(":", "_"));
//                BakedModel baked = BreakdownCore.getItemModelgen().generateBlockModel(m -> MaterialAtlasManager.getInstance().getSprite(materialType,type), shapeModel)
                BakedModel baked;
                if(!materialType.intermediateProduct || idpCache.containsKey(materialType.x16color)) {
                    TextureAtlasSprite sprite = ((TextureAtlas) Minecraft.getInstance().getTextureManager().getTexture(BLOCK_ATLAS)).getSprite(materialType.intermediateProduct ? System$Material.idpForAtlasID(materialType.x16color, type) : System$Material.combineForAtlasID(materialType, type));
                    baked = BreakdownCore.getItemModelgen().generateBlockModel(m -> sprite, shapeModel)
                            .bake(bakery.new ModelBakerImpl((m, n) -> sprite, bakeName),
                                    m -> sprite,
                                    new SimpleModelState(Transformation.identity()),
                                    bakeName
                            );
                }else {
                    baked = idpCache.get(materialType.x16color);
                }
                modelCache.put(materialType,baked);
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

}
