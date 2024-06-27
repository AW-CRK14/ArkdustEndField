package com.landis.breakdowncore.system.material;

import com.google.common.collect.ImmutableSet;
import com.landis.breakdowncore.BreaRegistries;
import com.landis.breakdowncore.helper.CodecHelper;
import com.landis.breakdowncore.module.codec.NumberChecker;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.HolderSetCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class RecipeMaterialGroupIngredient extends Ingredient {
    public static final Logger LOGGER = LogManager.getLogger("BREA:RecipeIng/MGI");
    private static final Codec<HolderSet<Material>> MATERIAL_CODEC = HolderSetCodec.create(Registry$Material.MATERIAL.key(), Registry$Material.MATERIAL.holderByNameCodec(), false);
    private static final Codec<HolderSet<MaterialItemType>> TYPE_CODEC = HolderSetCodec.create(Registry$Material.MATERIAL_ITEM_TYPE.key(), Registry$Material.MATERIAL_ITEM_TYPE.holderByNameCodec(), false);
    private static final Codec<HolderSet<MaterialFeatureType<?>>> FEATURE_CODEC = HolderSetCodec.create(Registry$Material.MATERIAL_FEATURE.key(), Registry$Material.MATERIAL_FEATURE.holderByNameCodec(), false);

    public static final Codec<RecipeMaterialGroupIngredient> CODEC = RecordCodecBuilder.create(i -> i.group(
            MATERIAL_CODEC.fieldOf("material").forGetter(ins -> ins.materials),
            TYPE_CODEC.fieldOf("type").forGetter(ins -> ins.types),
            FEATURE_CODEC.fieldOf("feature").forGetter(ins -> ins.features),
            NumberChecker.CODEC.fieldOf("purity").forGetter(ins -> ins.purityRange),
            NumberChecker.CODEC.fieldOf("content").forGetter(ins -> ins.contentRange),
            NumberChecker.CODEC.fieldOf("valid").forGetter(ins -> ins.validRange)
    ).apply(i, RecipeMaterialGroupIngredient::of));


    public final HolderSet<Material> materials;
    public final HolderSet<MaterialItemType> types;
    public final HolderSet<MaterialFeatureType<?>> features;
    public final NumberChecker purityRange;
    public final NumberChecker contentRange;
    public final NumberChecker validRange;

    public ImmutableSet<ResourceLocation> getMaterialRLs() {
        return materialRLs;
    }

    public ImmutableSet<ResourceLocation> getTypeRLs() {
        return typeRLs;
    }

    public ImmutableSet<ResourceLocation> getFeatureRLs() {
        return featureRLs;
    }

    private ImmutableSet<ResourceLocation> materialRLs;
    private ImmutableSet<ResourceLocation> typeRLs;
    private ImmutableSet<ResourceLocation> featureRLs;
    private boolean cacheFlag;

    private RecipeMaterialGroupIngredient(HolderSet<Material> materials, HolderSet<MaterialItemType> types, HolderSet<MaterialFeatureType<?>> features, NumberChecker purityRange, NumberChecker contentRange, NumberChecker validRange) {
        super(Stream.of(new ItemValue(createInfo(materials, types, features, purityRange, contentRange, validRange))), BreaRegistries.MATERIAL_RECIPE_INGREDIENT);
        this.materials = materials;
        this.types = types;
        this.features = features;
        this.purityRange = purityRange;
        this.contentRange = contentRange;
        this.validRange = validRange;
    }

    private void initCache() {
        if (!cacheFlag) {
            cacheFlag = true;
            if (materials != null)
                materialRLs = materials.stream().map(Holder::unwrapKey).flatMap(Optional::stream).map(ResourceKey::location).collect(ImmutableSet.toImmutableSet());
            if (types != null)
                typeRLs = types.stream().map(Holder::unwrapKey).flatMap(Optional::stream).map(ResourceKey::location).collect(ImmutableSet.toImmutableSet());
            if (features != null)
                featureRLs = features.stream().map(Holder::unwrapKey).flatMap(Optional::stream).map(ResourceKey::location).collect(ImmutableSet.toImmutableSet());
        }
    }

    @Override
    public boolean test(@Nullable ItemStack pStack) {
        initCache();
        if (pStack == null) return false;
        @Nullable ITypedMaterialObj info = System$Material.getMaterialInfo(pStack.getItem());
        if (info == null) return false;
        Material material = info.getMaterial(pStack).orElse(null);
        return material != null &&
                (materials == null || materialRLs.contains(material.id)) &&
                (types == null || typeRLs.contains(info.getMIType().id)) &&
                (features == null || material.featureIDs.containsAll(featureRLs)) &&
                (purityRange == null || purityRange.is(info.getPurity())) &&
                (contentRange == null || contentRange.is(info.getContent())) &&
                (validRange == null || validRange.is(info.getAvailable()));
    }

    //material->or type->or feature->and
    public static RecipeMaterialGroupIngredient of(HolderSet<Material> materials, HolderSet<MaterialItemType> types, HolderSet<MaterialFeatureType<?>> features, NumberChecker purityRange, NumberChecker contentRange, NumberChecker validRange) {
        if (materials == null && types == null && features == null && purityRange == null && contentRange == null && validRange == null) {
            LOGGER.error("Deserialize failed as all the elements are null. The class require at least one element is nonnull.");
            LOGGER.error("由于每一个参数均为null，无法解析。此类要求至少一个参数为非null。");
            LOGGER.error(new IllegalArgumentException());
            return EMPTY;
        }
        return new RecipeMaterialGroupIngredient(materials, types, features, purityRange, contentRange, validRange);
    }

    private static ItemStack createInfo(HolderSet<Material> materials, HolderSet<MaterialItemType> types, HolderSet<MaterialFeatureType<?>> features, NumberChecker purityRange, NumberChecker contentRange, NumberChecker validRange) {
        ItemStack itemStack = new ItemStack(BreaRegistries.PLACEHOLDER);
        ListTag tags = new ListTag();
        if (materials != null) {
            Component c = Component.translatable("brea.module.recipe.material.material").append(": ").append(CodecHelper.unwrapHolderSet(materials.unwrap(), 6));
            tags.add(StringTag.valueOf(Component.Serializer.toJson(c)));
        }
        if (types != null) {
            Component c = Component.translatable("brea.module.recipe.material.type").append(": ").append(CodecHelper.unwrapHolderSet(types.unwrap(), 6));
            tags.add(StringTag.valueOf(Component.Serializer.toJson(c)));
        }
        if (features != null) {
            Component c = Component.translatable("brea.module.recipe.material.feature").append(": ").append(CodecHelper.unwrapHolderSet(features.unwrap(), 6));
            tags.add(StringTag.valueOf(Component.Serializer.toJson(c)));
        }
        if (purityRange != null) {
            Component c = Component.translatable("brea.module.recipe.material.purity").append(": ").append(purityRange.toString());
            tags.add(StringTag.valueOf(Component.Serializer.toJson(c)));
        }
        if (contentRange != null) {
            Component c = Component.translatable("brea.module.recipe.material.content").append(": ").append(contentRange.toString());
            tags.add(StringTag.valueOf(Component.Serializer.toJson(c)));
        }
        if (validRange != null) {
            Component c = Component.translatable("brea.module.recipe.material.valid").append(": ").append(validRange.toString());
            tags.add(StringTag.valueOf(Component.Serializer.toJson(c)));
        }
        itemStack.getOrCreateTagElement("display").put("Lore", tags);
        return itemStack;
    }

    private static final RecipeMaterialGroupIngredient EMPTY = new RecipeMaterialGroupIngredient(null, null, null, null, null, null) {
        @Override
        public boolean test(@Nullable ItemStack pStack) {
            return false;
        }
    };
}
