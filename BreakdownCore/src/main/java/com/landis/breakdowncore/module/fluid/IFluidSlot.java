package com.landis.breakdowncore.module.fluid;

import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.IFluidTank;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;

public interface IFluidSlot {
    void setIndex(int index);

    int getIndex();

    IFluidTank root();

    default FluidStack getStack() {
        return root().getFluid();
    }

    default void setStack(FluidStack stack) {
        if (root() instanceof FluidTank tank) {
            tank.setFluid(stack);
        } else {
            root().drain(Integer.MAX_VALUE, IFluidHandler.FluidAction.EXECUTE);
            root().fill(stack, IFluidHandler.FluidAction.EXECUTE);
        }
    }
}
