package com.landis.breakdowncore.module.menu;

import com.landis.breakdowncore.module.blockentity.container.IFriendlyItemHandle;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public interface ISlotTypeExpansion extends IFriendlyItemHandle {
    SlotType getSlotType(int index);

    default List<Integer> getSlotIndexForType(SlotType type, boolean useRootCheck) {
        List<Integer> list = new ArrayList<>();
        for(int i = 0 ; i < getSlots() ; i++){
            if(useRootCheck ? getSlotType(i).isFor(type) : getSlotType(i) == type){
                list.add(i);
            }
        }
        return list;
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
