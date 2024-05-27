package com.landis.breakdowncore.module.blockentity.container;

import com.landis.breakdowncore.helper.ContainerHelper;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandler;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public interface ISlotTypeExpansion extends IItemHandler {
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
    default @NotNull ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
        Pair<ItemStack, ItemStack> r = ContainerHelper.itemInsert(getStackInSlot(slot), getSlotLimit(slot), stack, simulate);
        if (!simulate && r.getLeft() != getStackInSlot(slot)) {
            setItem(slot, r.getLeft());
        }
        return r.getRight();
    }

    @Override
    default @NotNull ItemStack extractItem(int slot, int amount, boolean simulate){
        Pair<ItemStack, ItemStack> r = ContainerHelper.itemExtract(getStackInSlot(slot), amount, simulate);
        if (!simulate && r.getLeft() != getStackInSlot(slot)) {
            setItem(slot, r.getLeft());
        }
        return r.getRight();
    }

    @Override
    default int getSlotLimit(int slot){
        return getStackInSlot(slot).getMaxStackSize();
    };

    @Override
    default boolean isItemValid(int slot, @NotNull ItemStack stack){
        return true;
    };

    void setItem(int index, ItemStack stack);
}
