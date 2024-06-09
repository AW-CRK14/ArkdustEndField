package com.landis.breakdowncore.module.blockentity.container;

import com.landis.breakdowncore.helper.ContainerHelper;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.NonNullList;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerListener;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class ExpandedContainerMenu<T extends BlockEntity> extends AbstractContainerMenu implements ISlotTypeExpansion, ContainerListener {

    public final T belonging;
    public final Player user;
    public final boolean isClient;

    protected ExpandedContainerMenu(@Nullable MenuType<?> pMenuType, int pContainerId, T blockEntity, Player player) {
        super(pMenuType, pContainerId);
        addSlotListener(this);
        this.belonging = blockEntity;
        this.user = player;
        this.isClient = player instanceof LocalPlayer;
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
    public void setStackInSlot(int index, @NotNull ItemStack stack) {
        setItem(index,0,stack);
    }

    @Override
    public boolean stillValid(@NotNull Player pPlayer) {
        return true;
    }

    //由Menu下发
    public void slotChanged(AbstractContainerMenu menu, int slotIndex, ItemStack stack){
        onContentsChanged(slotIndex);//IFIH已收到提醒
    };

    public void dataChanged(AbstractContainerMenu pContainerMenu, int pDataSlotIndex, int pValue){

    };


    /**
     * @see ExpandedContainerMenu#addSlot(Slot, SlotType) addSlot(Slot, SlotType)
     */
    @Override
    @Deprecated
    protected @NotNull Slot addSlot(@NotNull Slot pSlot) {
        return addSlot(pSlot, null);
    }

    protected Slot addSlot(Slot slot, SlotType type) {
        super.addSlot(slot);
        if (type == null) {
            if (slot.container instanceof ISlotTypeExpansion t) {
                type = t.getSlotType(slot.getSlotIndex());
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

    public final NonNullList<SlotType> typeList = NonNullList.create();

    @Override
    public SlotType getSlotType(int index) {
        return typeList.get(index);
    }

    @Override
    public int getSlots() {
        return slots.size();
    }

    @Override
    public @NotNull ItemStack getStackInSlot(int slot) {
        return slots.get(slot).getItem();
    }


}
