package com.landis.breakdowncore.module.menu;


import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerListener;
import net.neoforged.neoforge.fluids.FluidStack;

public interface IExpandedContainerListener extends ContainerListener {
    void fluidChanged(AbstractContainerMenu pContainerMenu, int pFluidSlotIndex, FluidStack pValue);
}
