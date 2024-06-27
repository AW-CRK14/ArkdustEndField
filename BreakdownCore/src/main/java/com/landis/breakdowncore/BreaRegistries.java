package com.landis.breakdowncore;

import com.landis.breakdowncore.module.registry.ItemRegroup;
import com.landis.breakdowncore.system.material.*;
import com.landis.breakdowncore.system.material.expansion.CombustibleType;
import com.landis.breakdowncore.system.material.expansion.IngotType;
import com.landis.breakdowncore.system.material.expansion.MissingMaterial;
import com.landis.breakdowncore.system.material.expansion.materialfeature.CombustibleMF;
import com.landis.breakdowncore.system.material.expansion.materialfeature.MetalMF;
import com.landis.breakdowncore.system.material.expansion.materialfeature.PhaseTransitMF;
import com.landis.breakdowncore.system.material.expansion.materialfeature.ThermoMF;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Function;

public class BreaRegistries {
    public static final DeferredRegister<CreativeModeTab> TAB = DeferredRegister.create(net.minecraft.core.registries.Registries.CREATIVE_MODE_TAB, BreakdownCore.MODID);
    public static final DeferredRegister<Item> ITEM = DeferredRegister.createItems(BreakdownCore.MODID);

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> BREA_TAB = TAB.register("default", () -> CreativeModeTab.builder().displayItems((parameters, output) -> {
        for (DeferredHolder<Item, ? extends Item> i : ITEM.getEntries()) {
            output.accept((DeferredItem<Item>) i);
        }
    }).title(Component.translatable("tab.brea.default")).icon(() -> new ItemStack(Items.COMMAND_BLOCK)).build());

    public static final ItemRegroup<Item> PLACEHOLDER = BreakdownCore.REGISTER.item("placeholder").addExplainPre().texture().build();



    /**
     * 自定义的材料系统的注册。
     *
     * @see Handler$Material 对原版物品的材料特征附加
     */
    public static class MaterialReg {
        public static final DeferredRegister<MaterialItemType> TYPE = DeferredRegister.create(Registry$Material.MATERIAL_ITEM_TYPE, BreakdownCore.MODID);
        public static final DeferredRegister<MaterialFeatureType<?>> FEATURE = DeferredRegister.create(Registry$Material.MATERIAL_FEATURE, BreakdownCore.MODID);
        public static final DeferredRegister<Material> MATERIAL = DeferredRegister.create(Registry$Material.MATERIAL, BreakdownCore.MODID);

        public static <I extends MaterialItemType> DeferredHolder<MaterialItemType, I> type(String id, Function<ResourceLocation, I> provider) {
            return TYPE.register(id, () -> provider.apply(new ResourceLocation(TYPE.getNamespace(), id)));
        }

        public static <I extends IMaterialFeature<I>> DeferredHolder<MaterialFeatureType<?>, MaterialFeatureType<I>> feature(String id, Class<I> provider) {
            return FEATURE.register(id, () -> new MaterialFeatureType<>(new ResourceLocation(FEATURE.getNamespace(), id), provider));
        }

        public static <I extends Material> DeferredHolder<Material, I> material(String id, Function<ResourceLocation, I> provider) {
            return MATERIAL.register(id, () -> provider.apply(new ResourceLocation(MATERIAL.getNamespace(), id)));
        }

        public static final DeferredHolder<MaterialItemType, IngotType> INGOT = type("ingot", location -> new IngotType(90, location));
        public static final DeferredHolder<MaterialItemType, CombustibleType> COMBUSTIBLE_TYPE = type("combustible", CombustibleType::new);


        public static final DeferredHolder<MaterialFeatureType<?>, MaterialFeatureType<PhaseTransitMF>> PHASE_TRANSIT = feature("phase_transit", PhaseTransitMF.class);
        public static final DeferredHolder<MaterialFeatureType<?>, MaterialFeatureType<ThermoMF>> THERMO = feature("thermo", ThermoMF.class);
        public static final DeferredHolder<MaterialFeatureType<?>, MaterialFeatureType<MetalMF>> METAL = feature("metal", MetalMF.class);
        public static final DeferredHolder<MaterialFeatureType<?>, MaterialFeatureType<CombustibleMF>> COMBUSTIBLE = feature("combustible", CombustibleMF.class);


        public static final DeferredHolder<Material, MissingMaterial> MISSING = MATERIAL.register("missing", MissingMaterial::new);
        public static final DeferredHolder<Material, Material> IRON = material("iron", r -> new Material(r, true, new MetalMF(), new PhaseTransitMF(1539, 3000), new ThermoMF(0.46F, 80.9F, 7.87F)));
        public static final DeferredHolder<Material, Material> LIGNITE = material("lignite", r -> new Material(r, false, new CombustibleMF(18000, 1150)));
    }

    public static class JsonCodecReg {
        public static final DeferredRegister<LootPoolEntryType> LOOT_POOL = DeferredRegister.create(Registries.LOOT_POOL_ENTRY_TYPE, BreakdownCore.MODID);

        public static final DeferredHolder<LootPoolEntryType, LootPoolEntryType> MATERIAL_ITEM_LOOT_POOL = LOOT_POOL.register("material_item", () -> new LootPoolEntryType(LootMaterialItem.CODEC));
    }
}