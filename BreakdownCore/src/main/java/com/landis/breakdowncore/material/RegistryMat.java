package com.landis.breakdowncore.material;

import com.landis.breakdowncore.BreakdownCore;
import com.landis.breakdowncore.Registries;
import com.landis.breakdowncore.unsafe.SkippedRegister;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.registries.RegistryBuilder;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public class RegistryMat {

    public static final Registry<MaterialItemType> MATERIAL_ITEM_TYPE = new RegistryBuilder<>(Keys.MATERIAL_ITEM_TYPE).sync(true).create();
    public static final Registry<MaterialFeatureHandle<?>> MATERIAL_FEATURE = new RegistryBuilder<>(Keys.MATERIAL_FEATURE).sync(true).create();

    public static class Keys{
        public static final ResourceKey<Registry<MaterialItemType>> MATERIAL_ITEM_TYPE = create("material_item_type");
        public static final ResourceKey<Registry<MaterialFeatureHandle<?>>> MATERIAL_FEATURE = create("material_feature");

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
}
