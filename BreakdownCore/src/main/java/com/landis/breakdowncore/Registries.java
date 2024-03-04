package com.landis.breakdowncore;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class Registries {
    public static final DeferredRegister<CreativeModeTab> TAB = DeferredRegister.create(net.minecraft.core.registries.Registries.CREATIVE_MODE_TAB,BreakdownCore.MODID);
    public static final DeferredRegister<Item> ITEM = DeferredRegister.createItems(BreakdownCore.MODID);

    public static final DeferredHolder<CreativeModeTab,CreativeModeTab> BREA_TAB = TAB.register("default",()->CreativeModeTab.builder().displayItems((parameters, output) -> {
        for(DeferredHolder<Item, ? extends Item> i : ITEM.getEntries()){
            output.accept((DeferredItem<Item>)i);
        }}).title(Component.translatable("tab.brea.default")).build());
}
