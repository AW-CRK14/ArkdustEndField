package com.landis.breakdowncore.system.material;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderSet;
import net.minecraft.resources.HolderSetCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;
import net.minecraft.world.level.storage.loot.providers.number.NumberProviders;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Set;

public class RecipeMaterialGroupIngredient extends Ingredient {
    public static final Logger LOGGER = LogManager.getLogger("BREA:RecipeIng/MGI");
    private static final Codec<HolderSet<Material>> MATERIAL_CODEC = HolderSetCodec.create(Registry$Material.MATERIAL.key(), Registry$Material.MATERIAL.holderByNameCodec(), false);
    private static final Codec<HolderSet<MaterialItemType>> TYPE_CODEC = HolderSetCodec.create(Registry$Material.MATERIAL_ITEM_TYPE.key(), Registry$Material.MATERIAL_ITEM_TYPE.holderByNameCodec(), false);
    private static final Codec<HolderSet<MaterialFeatureType<?>>> FEATURE_CODEC = HolderSetCodec.create(Registry$Material.MATERIAL_FEATURE.key(), Registry$Material.MATERIAL_FEATURE.holderByNameCodec(), false);

    public static final Codec<RecipeMaterialGroupIngredient> CODEC = RecordCodecBuilder.create(i -> i.group(
            MATERIAL_CODEC.fieldOf("material").forGetter(ins -> ins.materials),
            TYPE_CODEC.fieldOf("type").forGetter(ins -> ins.types),
            FEATURE_CODEC.fieldOf("feature").forGetter(ins -> ins.features),
            NumberProviders.CODEC.fieldOf("purity").forGetter(ins -> ins.purityRange),
            NumberProviders.CODEC.fieldOf("content").forGetter(ins -> ins.contentRange),
            NumberProviders.CODEC.fieldOf("valid").forGetter(ins -> ins.validRange)
    ).apply(i, RecipeMaterialGroupIngredient::of));


    public final HolderSet<Material> materials;
    public final HolderSet<MaterialItemType> types;
    public final HolderSet<MaterialFeatureType<?>> features;
    public final NumberProvider purityRange;
    public final NumberProvider contentRange;
    public final NumberProvider validRange;

    private RecipeMaterialGroupIngredient(HolderSet<Material> materials, HolderSet<MaterialItemType> types, HolderSet<MaterialFeatureType<?>> features, NumberProvider purityRange, NumberProvider contentRange, NumberProvider validRange) {
        super();
        this.materials = materials;
        this.types = types;
        this.features = features;
        this.purityRange = purityRange;
        this.contentRange = contentRange;
        this.validRange = validRange;
    }

    //material->or type->or feature->and
    public static RecipeMaterialGroupIngredient of(HolderSet<Material> materials, HolderSet<MaterialItemType> types, HolderSet<MaterialFeatureType<?>> features, NumberProvider purityRange, NumberProvider contentRange, NumberProvider validRange) {
        if(materials == null&& types == null && features == null && purityRange == null && contentRange == null && validRange == null){
            LOGGER.error("Deserialize failed as all the elements are null. The class require at least one element is nonnull.");
            LOGGER.error("由于每一个参数均为null，无法解析。此类要求至少一个参数为非null。");
            LOGGER.error(new IllegalArgumentException());
            return EMPTY;
        }


        return new RecipeMaterialGroupIngredient(materials, types, features, purityRange, contentRange, validRange);
    }

    private static final RecipeMaterialGroupIngredient EMPTY = new RecipeMaterialGroupIngredient(null,null,null,null,null,null){
        @Override
        public boolean test(@Nullable ItemStack pStack) {
            return false;
        }
    };
}
