package com.landis.breakdowncore.module.blockentity.container;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.util.INBTSerializable;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import net.neoforged.neoforge.items.ItemHandlerHelper;
import org.jetbrains.annotations.NotNull;

public interface IFriendlyItemHandle extends IItemHandler, IItemHandlerModifiable, INBTSerializable<CompoundTag> {
    @Override
    @NotNull
    default ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
        if (stack.isEmpty())
            return ItemStack.EMPTY;

        if (!isItemValid(slot, stack))
            return stack;

        ItemStack existing = getStackInSlot(slot);

        int limit = getStackLimit(slot, stack);

        if (!existing.isEmpty()) {
            if (!ItemHandlerHelper.canItemStacksStack(stack, existing))
                return stack;

            limit -= existing.getCount();
        }

        if (limit <= 0)
            return stack;

        boolean reachedLimit = stack.getCount() > limit;

        if (!simulate) {
            if (existing.isEmpty()) {
                setStackInSlot(slot, reachedLimit ? ItemHandlerHelper.copyStackWithSize(stack, limit) : stack);
            } else {
                existing.grow(reachedLimit ? limit : stack.getCount());
            }
            onContentsChanged(slot);
        }

        return reachedLimit ? ItemHandlerHelper.copyStackWithSize(stack, stack.getCount() - limit) : ItemStack.EMPTY;
    }

    @Override
    @NotNull
    default ItemStack extractItem(int slot, int amount, boolean simulate) {
        if (amount == 0)
            return ItemStack.EMPTY;

        ItemStack existing = getStackInSlot(slot);

        if (existing.isEmpty())
            return ItemStack.EMPTY;

        int toExtract = Math.min(amount, existing.getMaxStackSize());

        if (existing.getCount() <= toExtract) {
            if (!simulate) {
                setStackInSlot(slot, ItemStack.EMPTY);
                onContentsChanged(slot);
                return existing;
            } else {
                return existing.copy();
            }
        } else {
            if (!simulate) {
                setStackInSlot(slot, ItemHandlerHelper.copyStackWithSize(existing, existing.getCount() - toExtract));
                onContentsChanged(slot);
            }

            return ItemHandlerHelper.copyStackWithSize(existing, toExtract);
        }
    }

    //    @Override
//    default @NotNull ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
//        Pair<ItemStack, ItemStack> r = ContainerHelper.itemInsert(getStackInSlot(slot), getSlotLimit(slot), stack, simulate);
//        if (!simulate && r.getLeft() != getStackInSlot(slot)) {
//            setItem(slot, r.getLeft());
//        }
//        return r.getRight();
//    }
//
//    @Override
//    default @NotNull ItemStack extractItem(int slot, int amount, boolean simulate){
//        Pair<ItemStack, ItemStack> r = ContainerHelper.itemExtract(getStackInSlot(slot), amount, simulate);
//        if (!simulate && r.getLeft() != getStackInSlot(slot)) {
//            setItem(slot, r.getLeft());
//        }
//        return r.getRight();
//    }


    default int getStackLimit(int slot, ItemStack stack){
        return Math.min(getSlotLimit(slot),stack.getMaxStackSize());
    };

    //value: -1 表示不计索引的变动
    default void onContentsChanged(int slot){};

    @Override
    default CompoundTag serializeNBT(){
        ListTag nbtTagList = new ListTag();
        for (int i = 0; i < getSlots(); i++) {
            if (!getStackInSlot(i).isEmpty()) {
                CompoundTag itemTag = new CompoundTag();
                itemTag.putInt("Slot", i);
                getStackInSlot(i).save(itemTag);
                nbtTagList.add(itemTag);
            }
        }
        CompoundTag nbt = new CompoundTag();
        nbt.put("Items", nbtTagList);
        return nbt;
    };

    @Override
    default void deserializeNBT(CompoundTag nbt){
        ListTag tagList = nbt.getList("Items", Tag.TAG_COMPOUND);
        for (int i = 0; i < tagList.size(); i++) {
            CompoundTag itemTags = tagList.getCompound(i);
            int slot = itemTags.getInt("Slot");

            if (slot >= 0 && slot < getSlots()) {
                setStackInSlot(slot, ItemStack.of(itemTags));
            }
        }
        onLoad();
    }

    default void onLoad() {
    }
}
