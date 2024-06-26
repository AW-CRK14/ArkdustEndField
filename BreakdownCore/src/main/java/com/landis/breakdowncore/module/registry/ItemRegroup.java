package com.landis.breakdowncore.module.registry;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredHolder;

public record ItemRegroup<T extends Item>(DeferredHolder<Item, T> root) {
    public T getItem() {
        return root.get();
    }

    public ResourceLocation getId() {
        return root.getId();
    }
}
