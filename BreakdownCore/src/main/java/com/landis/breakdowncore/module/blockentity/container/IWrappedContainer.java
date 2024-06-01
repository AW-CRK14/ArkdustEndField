package com.landis.breakdowncore.module.blockentity.container;

import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public interface IWrappedContainer extends Container {
    Container getContainer();

    @Override
    default int getContainerSize(){return getContainer().getContainerSize();}

    @Override
    default boolean isEmpty(){return getContainer().isEmpty();}

    @Override
    default @NotNull ItemStack getItem(int pSlot){return getContainer().getItem(pSlot);}

    @Override
    default @NotNull ItemStack removeItem(int pSlot, int pAmount){return getContainer().removeItem(pSlot,pAmount);}

    @Override
    default @NotNull ItemStack removeItemNoUpdate(int pSlot){return getContainer().removeItemNoUpdate(pSlot);}

    @Override
    default void setItem(int pSlot, @NotNull ItemStack pStack){ getContainer().setItem(pSlot, pStack);};

    @Override
    default void setChanged(){getContainer().setChanged();}

    @Override
    default boolean stillValid(Player pPlayer){return getContainer().stillValid(pPlayer);}

    @Override
    default void clearContent(){getContainer().clearContent();}
}
