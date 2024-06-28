package com.landis.breakdowncore.module.fluid;

import net.neoforged.neoforge.fluids.IFluidTank;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;

public class FluidSlot implements IFluidSlot {
    public int index = -1;

    public final IFluidTank root;

    public FluidSlot(IFluidTank root) {
        this.root = root;
    }


    @Override
    public void setIndex(int index) {
        this.index = index;
    }

    @Override
    public int getIndex() {
        return index;
    }

    @Override
    public IFluidTank root() {
        return root;
    }
}
