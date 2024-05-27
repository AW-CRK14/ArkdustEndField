package com.landis.arkdust.system;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public abstract class ArkdustContainerMenu extends AbstractContainerMenu {
    protected ArkdustContainerMenu(@Nullable MenuType<?> pMenuType, int pContainerId) {
        super(pMenuType, pContainerId);
    }

    @Override
    protected Slot addSlot(Slot pSlot) {
        return super.addSlot(pSlot);
    }



}
