package com.landis.breakdowncore.module.registry;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.registries.DeferredHolder;
import org.jetbrains.annotations.NotNull;

public record ItemRegroup<T extends Item>(DeferredHolder<Item, T> root) implements ItemLike {
    public T getItem() {
        return root.get();
    }

    public ResourceLocation getId() {
        return root.getId();
    }

    @Override
    public @NotNull Item asItem() {
        return getItem();
    }
}
