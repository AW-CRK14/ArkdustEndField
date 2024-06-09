package com.landis.breakdowncore.system.macmodule;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.util.INBTSerializable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Map;

public interface MacModule<I extends MacModule<I>> extends INBTSerializable<CompoundTag> {
    default I getInstance() {
        return (I) this;
    }

    default ResourceLocation getTypeId() {
        return Registry$MacModule.MODULE_TYPES.getKey(getType());
    }

    //应当保证在加载和移除时数值一致。
    default int installElesignCost(){
        return getType().elesignLoadForLevel().apply(getActuallyLevel());
    }

    default int installEnegridCost(){
        return getType().enegridLoadForLevel().apply(getActuallyLevel());
    }

    MacModuleType<I> getType();

    //-1 =>无等级限制
    int getActuallyLevel();

    //提供该升级的物品。
    @Nullable
    ItemStack providedItemStack();

    void bindItemStack(@Nonnull ItemStack item);

    CompoundTag serializeWithoutItemData();

    void deserializeWithoutItemData(CompoundTag tag);

    default @Nullable Map<ResourceLocation,Float> dataAddon(){
        return null;
    }
}
