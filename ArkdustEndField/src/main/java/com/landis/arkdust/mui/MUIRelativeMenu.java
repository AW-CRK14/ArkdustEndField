package com.landis.arkdust.mui;

import com.landis.arkdust.mui.abs.IMenu2ScreenNotifiable;
import com.landis.breakdowncore.module.blockentity.container.ExpandedContainerMenu;
import com.landis.breakdowncore.module.blockentity.gmui.IMenuChangeNotify;
import icyllis.modernui.fragment.Fragment;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.Nullable;

public abstract class MUIRelativeMenu<T extends BlockEntity> extends ExpandedContainerMenu<T> implements IMenu2ScreenNotifiable{
    protected MUIRelativeMenu(@Nullable MenuType<?> pMenuType, int pContainerId, T blockEntity, Player player) {
        super(pMenuType, pContainerId, blockEntity, player);
    }


    private @Nullable IMenuChangeNotify fragment;

    @Override
    public void bingFragment(IMenuChangeNotify mui) {
        if (fragment == null) {
            fragment = mui;
        }
    }

    @Override
    public void notifySlotChanged(int index) {
        if (fragment != null)
            fragment.notify(index);
    }

    @Override
    public void notifyDataChanged(int index, int content) {
        if(fragment != null)
            fragment.notifyData(index,content);
    }

    @Override
    public void setItem(int pSlotId, int pStateId, ItemStack pStack) {
        super.setItem(pSlotId, pStateId, pStack);
        notifySlotChanged(pSlotId);
    }

    @Override
    public void setData(int pId, int pData) {
        super.setData(pId, pData);
        notifyDataChanged(pId, pData);
    }
}
