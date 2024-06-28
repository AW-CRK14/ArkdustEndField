package com.landis.breakdowncore.module.fluid;

import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;

public interface IMultiSlotFluidHandler extends IFluidHandler {
    default FluidStack drain(int slot, int maxDrain, FluidAction action) {
        return drain(maxDrain, action);
    }

    default FluidStack drain(int slot, FluidStack resource, FluidAction action) {
        return drain(resource, action);
    }

    default int fill(int slot, FluidStack resource, FluidAction action) {
        return fill(resource, action);
    }
}
