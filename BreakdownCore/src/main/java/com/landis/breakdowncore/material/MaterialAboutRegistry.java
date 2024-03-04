package com.landis.breakdowncore.material;

import com.landis.breakdowncore.BreakdownCore;
import com.landis.breakdowncore.Registries;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.RegistryBuilder;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;
import java.util.function.Supplier;

public class MaterialAboutRegistry {
    public static final Registry<MaterialItemType> MATERIAL_ITEM_TYPE = new RegistryBuilder<>(Keys.MATERIAL_ITEM_TYPE).sync(true).create();

    public static class Keys{
        public static final ResourceKey<Registry<MaterialItemType>> MATERIAL_ITEM_TYPE = create("material_item_type");

        public static <T> ResourceKey<Registry<T>> create(String name){
            return ResourceKey.createRegistryKey(new ResourceLocation(BreakdownCore.MODID,name));
        }
    }

    public static class MaterialItemRegistry extends DeferredRegister<MaterialItemType> {
        protected MaterialItemRegistry(String namespace) {
            super(Keys.MATERIAL_ITEM_TYPE, namespace);
        }

        @Override
        public <I extends MaterialItemType> @NotNull DeferredHolder<MaterialItemType, I> register(@NotNull String name, Supplier<? extends I> sup) {
            I type = sup.get();
            type.primaryRegister(Registries.ITEM,new ResourceLocation(this.getNamespace(),name));
            return super.register(name, ()->type);
        }

        @Override
        protected <I extends MaterialItemType> DeferredHolder<MaterialItemType, I> createHolder(ResourceKey<? extends Registry<MaterialItemType>> registryKey, ResourceLocation key) {
            return super.createHolder(registryKey, key);
        }

        public static class DeferredMIT<I extends MaterialItemType> extends DeferredHolder<MaterialItemType,I>{
            private I uncheckedIns;
            protected DeferredMIT(ResourceKey<MaterialItemType> key) {
                super(key);
            }

            protected void setUncheckedIns(I uncheckedIns) {
                if(this.uncheckedIns == null){
                    this.uncheckedIns = uncheckedIns;
                }
            }


        }
    }
}
