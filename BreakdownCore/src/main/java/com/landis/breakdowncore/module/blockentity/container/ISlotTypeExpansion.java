package com.landis.breakdowncore.module.blockentity.container;

import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public interface ISlotTypeExpansion extends IAutoHandleItemHandle {
    List<SlotType> getSlotTypeReflect();

    default SlotType getForType(int index) {
        return getSlotTypeReflect().get(index);
    }

    ;

    default List<Integer> getSlotIndexForType(SlotType type, boolean useRootCheck) {
        List<SlotType> types = getSlotTypeReflect();
        List<Integer> indexes = new ArrayList<>();
        for (int i = 0; i < types.size(); i++) {
            if (useRootCheck ? types.get(i).isFor(type) : types.get(i) == type)
                indexes.add(i);
        }
        return indexes;
    }

    default ItemStack getSlot(SlotType type, int index, boolean useRootCheck) {
        List<Integer> indexes = getSlotIndexForType(type, useRootCheck);
        return this.getStackInSlot(indexes.get(index));
    }

    default List<ItemStack> getStacksForType(SlotType type, boolean ignoreEmpty, boolean useRootCheck) {
        List<ItemStack> stacks = new ArrayList<>();
        ItemStack item;
        for (int i : getSlotIndexForType(type, useRootCheck)) {
            item = this.getStackInSlot(i);
            if (!item.isEmpty() || !ignoreEmpty) {
                stacks.add(item);
            }
        }
        return stacks;
    }



    @Override
    default int getSlotLimit(int slot){
        return getStackInSlot(slot).getMaxStackSize();
    };

    @Override
    default boolean isItemValid(int slot, @NotNull ItemStack stack){
        return true;
    };

}
