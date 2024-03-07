package com.landis.breakdowncore.material;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.landis.breakdowncore.BreakdownCore;
import com.landis.breakdowncore.Registries;
import com.landis.breakdowncore.unsafe.SkippedRegister;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.RegistryBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

public class RegistryMat {
    public static final Logger LOGGER = LogManager.getLogger("BREA:Material");


    static ImmutableMap<Class<?>,MaterialFeatureHandle<?>> MF_CLASS2MFH;
    public static <I extends IMaterialFeature<I>> MaterialFeatureHandle<I> getMFH(Class<I> c){
        MaterialFeatureHandle<?> handle = MF_CLASS2MFH.get(c);
        if(isC2MFHExist()){
            try {
                return (MaterialFeatureHandle<I>) handle;
            } catch (ClassCastException castException){
                LOGGER.error("Can't cast MFH({}) with base class {}",handle,c);
                throw castException;
            }
        }
        LOGGER.error("Can't get MFH with class{}, you should check if it's correctly registered.",c);
        throw new RuntimeException();
    }
    public static boolean isC2MFHExist(){
        return MF_CLASS2MFH != null;
    }

    public static final Registry<MaterialItemType> MATERIAL_ITEM_TYPE = new RegistryBuilder<>(Keys.MATERIAL_ITEM_TYPE).sync(true).create();
    public static final Registry<MaterialFeatureHandle<?>> MATERIAL_FEATURE = new RegistryBuilder<>(Keys.MATERIAL_FEATURE).sync(true).create();
    public static final Registry<Material> MATERIAL = new RegistryBuilder<>(Keys.MATERIAL).sync(true).create();

    public static class Keys{
        public static final ResourceKey<Registry<MaterialItemType>> MATERIAL_ITEM_TYPE = create("material_item_type");
        public static final ResourceKey<Registry<MaterialFeatureHandle<?>>> MATERIAL_FEATURE = create("material_feature");
        public static final ResourceKey<Registry<Material>> MATERIAL = create("material");

        public static <T> ResourceKey<Registry<T>> create(String name){
            return ResourceKey.createRegistryKey(new ResourceLocation(BreakdownCore.MODID,name));
        }
    }

    /**MIT(MaterialItemType)Register<br>
     * 这一部分使用了跳过延迟注册。小心使用以避免出现报错。
     * */
    public static class MITRegister extends SkippedRegister<MaterialItemType> {
        public MITRegister(String namespace) {
            super(Keys.MATERIAL_ITEM_TYPE, namespace);
        }

        @Override
        public <I extends MaterialItemType> @NotNull Holder<MaterialItemType, I> register(@NotNull String name,I ins , Supplier<? extends I> sup) {
            Holder<MaterialItemType, I> holder = super.register(name,sup);
            holder.getUnchecked(false).primaryRegister(Registries.ITEM,holder.getId());
            return holder;
        }
    }

    public static class MRegister extends DeferredRegister<Material>{
        protected MRegister( String namespace) {
            super(Keys.MATERIAL, namespace);
        }

        public DeferredHolder<Material,? extends Material> registry(String id, With builder,IMaterialFeature<?>... features){
            ImmutableList.Builder<IMaterialFeature<?>> listBuilder = new ImmutableList.Builder<>();
            listBuilder.addAll(Arrays.asList(features));
            AtomicReference<Holder<?>> ato = new AtomicReference<>();
            ato.set((Holder<?>) super.register(id,()->{
                ato.get().rel();
                return builder.create(ato.get().list,ato.get().getId());
            }));
            ato.get().list = listBuilder;
            return ato.get();
        }

        public interface With {
            Material create(ImmutableList.Builder<IMaterialFeature<?>> features,ResourceLocation name);
        }

        @Override
        protected <I extends Material> Holder<I> createHolder(ResourceKey<? extends Registry<Material>> registryKey, ResourceLocation key) {
            return new Holder<>(ResourceKey.create(registryKey,key));
        }

        public static class Holder<I extends Material> extends DeferredHolder<Material,I>{
            private ImmutableList.Builder<IMaterialFeature<?>> list = new ImmutableList.Builder<>();
            protected Holder(ResourceKey<Material> key) {
                super(key);
            }

            public void addFeature(IMaterialFeature<?> feature) {
                if(list != null) {
                    list.add(feature);
                }else {
                    LOGGER.warn("RegistryPre stage has finished and {} can't be add into.",feature);
                }
            }

            public void rel(){
                list = null;
            }
        }
    }
}
