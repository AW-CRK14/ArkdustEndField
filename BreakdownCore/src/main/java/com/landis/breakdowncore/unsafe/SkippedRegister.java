package com.landis.breakdowncore.unsafe;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public class SkippedRegister<I> extends DeferredRegister<I> {
    protected SkippedRegister(ResourceKey<? extends Registry<I>> registryKey, String namespace) {
        super(registryKey, namespace);
    }

    @Override
    public <S extends I> @NotNull Holder<I, S> register(@NotNull String name, Supplier<? extends S> sup) {
        S i = sup.get();
        return register(name,i,()->i);
    }

    public <S extends I> @NotNull Holder<I, S> register(@NotNull String name, S instance, Supplier<? extends S> sup) {
        return ((Holder<I, S>) super.register(name, sup)).setUncheckedIns(instance);
    }

    public <S extends I> @NotNull Holder<I, S> register(@NotNull String name, S instance) {
        return register(name, (Supplier<? extends S>) ()->instance);
    }

    @Override
    protected <S extends I> @NotNull Holder<I, S> createHolder(ResourceKey<? extends Registry<I>> registryKey, ResourceLocation key) {
        return new Holder<>(ResourceKey.create(registryKey,key));
    }


    public static class Holder<N,R extends N> extends DeferredHolder<N,R> {
        private R unchecked;
        protected Holder(ResourceKey<N> key) {
            super(key);
        }

        protected Holder<N,R> setUncheckedIns(R instance){
            if(unchecked == null) this.unchecked = instance;
            return this;
        }

        public R getUnchecked(boolean tryGet){
            if(tryGet){
                try {
                    return get();
                } catch (NullPointerException ignored){}
            }
            return unchecked;
        }
    }
}
