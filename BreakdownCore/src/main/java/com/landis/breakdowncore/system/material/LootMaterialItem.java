package com.landis.breakdowncore.system.material;

import com.landis.breakdowncore.BreaRegistries;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryType;
import net.minecraft.world.level.storage.loot.entries.LootPoolSingletonContainer;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.Consumer;

public class LootMaterialItem extends LootPoolSingletonContainer {
    public static final Codec<LootMaterialItem> CODEC = RecordCodecBuilder.create(ins -> ins.group(
                    Registry$Material.MATERIAL.holderByNameCodec().fieldOf("material").forGetter(i -> i.materialHolder),
                    Registry$Material.MATERIAL_ITEM_TYPE.holderByNameCodec().fieldOf("material_type").forGetter(i -> i.typeHolder))
            .and(singletonFields(ins))
            .apply(ins, LootMaterialItem::new)
    );

    private final Holder<Material> materialHolder;
    private final Holder<MaterialItemType> typeHolder;

    protected LootMaterialItem(Holder<Material> materialHolder, Holder<MaterialItemType> typeHolder, int pWeight, int pQuality, List<LootItemCondition> pConditions, List<LootItemFunction> pFunctions) {
        super(pWeight, pQuality, pConditions, pFunctions);
        this.materialHolder = materialHolder;
        this.typeHolder = typeHolder;
    }

    @Override
    protected void createItemStack(Consumer<ItemStack> pStackConsumer, LootContext pLootContext) {
        pStackConsumer.accept(System$Material.getFromMAndMIT(materialHolder.value(), typeHolder.value()));
    }

    @Override
    public @NotNull LootPoolEntryType getType() {
        return BreaRegistries.JsonCodecReg.MATERIAL_ITEM_LOOT_POOL.get();
    }
}
