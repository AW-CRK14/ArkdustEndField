package com.arkdust.registry;

import com.arkdust.Arkdust;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class CreativeTabRegistry{


    public static final DeferredRegister<CreativeModeTab> TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, Arkdust.MODID);

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> DEFAULT = TABS.register("default", () -> createBuilder(ItemRegistry.ITEMS_DEFAULT,"default")
                    .icon(() -> ItemRegistry.SPIRIT_STONE_UNACTIVATED.get().getDefaultInstance()).build());


    private static CreativeModeTab.Builder createBuilder(DeferredRegister.Items register,String name){
        return CreativeModeTab.builder().displayItems((parameters, output) -> {
            for(DeferredHolder<Item, ? extends Item> i : ItemRegistry.ITEMS_DEFAULT.getEntries()){
                output.accept((DeferredItem<Item>)i);
            }}).title(Component.translatable("tab.arkdust." + name));
    }
}
