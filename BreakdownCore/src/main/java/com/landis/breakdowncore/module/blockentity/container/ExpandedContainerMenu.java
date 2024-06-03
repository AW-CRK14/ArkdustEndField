package com.landis.breakdowncore.module.blockentity.container;

import com.landis.breakdowncore.helper.ContainerHelper;
import com.landis.breakdowncore.module.blockentity.gmui.ISlotChangeNotify;
import net.minecraft.core.NonNullList;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public abstract class ExpandedContainerMenu extends AbstractContainerMenu implements ISlotTypeExpansion {
    public final NonNullList<SlotType> typeList = NonNullList.create();
    private @Nullable ISlotChangeNotify belonging;

    protected ExpandedContainerMenu(@Nullable MenuType<?> pMenuType, int pContainerId) {
        super(pMenuType, pContainerId);
    }

    /**
     * @see ExpandedContainerMenu#addSlot(Slot, SlotType) addSlot(Slot, SlotType)
     */
    @Override
    @Deprecated
    protected @NotNull Slot addSlot(@NotNull Slot pSlot) {
        return addSlot(pSlot, null);
    }

    public abstract int inventoryStartIndex();

    @Override
    public @NotNull ItemStack quickMoveStack(@NotNull Player pPlayer, int pIndex) {
        int invStartIndex = inventoryStartIndex();
        ItemStack stack = getSlot(pIndex).getItem();
        if (!stack.isEmpty()) {
            if (pIndex < invStartIndex) {//在物品栏外，移入物品栏
                moveItemStackTo(stack, invStartIndex, invStartIndex + 36, true);
            } else if (invStartIndex == 0 || !moveItemStackTo(stack, 0, invStartIndex, true) || (invStartIndex + 36 > slots.size() && !moveItemStackTo(stack, invStartIndex + 36, slots.size(), true))) {//在物品栏内，先尝试移至前区或后区
                ContainerHelper.inventoryStacksQuickMove(invStartIndex, this, stack, pIndex);//再尝试内部移动
            }
        }
        return stack.isEmpty() ? ItemStack.EMPTY : stack;
    }

    @Override
    public boolean stillValid(@NotNull Player pPlayer) {
        return true;
    }

    protected Slot addSlot(Slot slot, SlotType type) {
        super.addSlot(slot);
        if (type == null) {
            if (slot.container instanceof ISlotTypeExpansion t) {
                type = t.getForType(slot.getSlotIndex());
            } else {
                type = SlotType.DEFAULT;
            }
        }
        typeList.add(type);
        if (slot instanceof ISlotTypeMarkable) {
            ((ISlotTypeMarkable) slot).mark(type);
        }
        return slot;
    }


    @Override
    public List<SlotType> getSlotTypeReflect() {
        return null;
    }

    @Override
    public void setStackInSlot(int index, @NotNull ItemStack stack) {
        Slot slot = slots.get(index);
        slot.container.setItem(slot.getSlotIndex(), stack);
    }

    @Override
    public int getSlots() {
        return slots.size();
    }

    @Override
    public @NotNull ItemStack getStackInSlot(int slot) {
        return slots.get(slot).getItem();
    }

    @Override
    public void onContentsChanged(int slot) {
        ISlotTypeExpansion.super.onContentsChanged(slot);
        if (belonging != null) {
            belonging.notify(slot);
        }
    }

    public void setBelonging(ISlotChangeNotify belonging) {
        if(this.belonging == null){
            this.belonging = belonging;
        }
    }
}
