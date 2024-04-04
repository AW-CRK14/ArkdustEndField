package com.landis.breakdowncore;

import com.landis.breakdowncore.system.material.*;
import com.landis.breakdowncore.system.material.expansion.IngotType;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Function;

public class Registries {


    public static final DeferredRegister<CreativeModeTab> TAB = DeferredRegister.create(net.minecraft.core.registries.Registries.CREATIVE_MODE_TAB,BreakdownCore.MODID);
    public static final DeferredRegister<Item> ITEM = DeferredRegister.createItems(BreakdownCore.MODID);

    public static final DeferredHolder<CreativeModeTab,CreativeModeTab> BREA_TAB = TAB.register("default",()->CreativeModeTab.builder().displayItems((parameters, output) -> {
        for(DeferredHolder<Item, ? extends Item> i : ITEM.getEntries()){
            output.accept((DeferredItem<Item>)i);
        }}).title(Component.translatable("tab.brea.default")).icon(()->new ItemStack(Items.COMMAND_BLOCK)).build());





    public static class MaterialReg {
        public static final DeferredRegister<MaterialItemType> TYPE = DeferredRegister.create(Registry$Material.MATERIAL_ITEM_TYPE,BreakdownCore.MODID);
        public static final DeferredRegister<MaterialFeatureType<?>> FEATURE = DeferredRegister.create(Registry$Material.MATERIAL_FEATURE,BreakdownCore.MODID);
        public static final DeferredRegister<Material> MATERIAL = DeferredRegister.create(Registry$Material.MATERIAL,BreakdownCore.MODID);
        public static <I extends MaterialItemType> DeferredHolder<MaterialItemType,I> type(String id, Function<ResourceLocation,I> provider){
            return TYPE.register(id,()->provider.apply(new ResourceLocation(TYPE.getNamespace(),id)));
        }
        public static <I extends MaterialFeatureType<?>> DeferredHolder<MaterialFeatureType<?>,I> feature(String id, Function<ResourceLocation,I> provider){
            return FEATURE.register(id,()->provider.apply(new ResourceLocation(FEATURE.getNamespace(),id)));
        }
        public static <I extends Material> DeferredHolder<Material,I> material(String id, Function<ResourceLocation,I> provider){
            return MATERIAL.register(id,()->provider.apply(new ResourceLocation(MATERIAL.getNamespace(),id)));
        }

        public static final DeferredHolder<MaterialItemType, IngotType> INGOT = type("ingot", location -> new IngotType(90,location));
        public static final DeferredHolder<Material,Material> MISSING = material("missing", location -> new Material(location,0x888888));
    }
}
