package com.landis.breakdowncore.module.fluid;

import com.google.common.collect.ImmutableList;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import org.apache.commons.lang3.function.ToBooleanBiFunction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Range;

import java.util.function.Predicate;

public class FluidContainer implements IMultiSlotFluidHandler {
    public final int count;
    public final ImmutableList<FluidStack> stacks;
    public final ToBooleanBiFunction<Integer, FluidStack> validator;

    public FluidContainer(@Range(from = 0, to = Integer.MAX_VALUE) int stackCount) {
        this(stackCount, s -> true);
    }

    public FluidContainer(@Range(from = 0, to = Integer.MAX_VALUE) int stackCount, Predicate<FluidStack> validator) {
        this(stackCount, (i, f) -> validator.test(f));
    }

    public FluidContainer(@Range(from = 0, to = Integer.MAX_VALUE) int stackCount, ToBooleanBiFunction<Integer, FluidStack> validator) {
        this.count = stackCount;
        this.validator = validator;
        if (stackCount != 0) {
            ImmutableList.Builder<FluidStack> builder = new ImmutableList.Builder<>();
            for (int i = 0; i < stackCount; i++) {
                builder.add(FluidStack.EMPTY);
            }
            this.stacks = builder.build();
        } else {
            this.stacks = ImmutableList.of();
        }
    }

    @Override
    public int getTanks() {
        return count;
    }

    @Override
    public @NotNull FluidStack getFluidInTank(int tank) {
        return tank >= count || tank < 0 ? FluidStack.EMPTY : stacks.get(tank);
    }

    @Override
    public int getTankCapacity(int tank) {
        return getFluidInTank(tank).getAmount();
    }

    @Override
    public boolean isFluidValid(int tank, FluidStack stack) {
        return validator.applyAsBoolean(tank, stack);
    }

    @Override
    public int fill(FluidStack resource, FluidAction action) {
        return fill(0, resource, action);
    }

    @Override
    public FluidStack drain(FluidStack resource, FluidAction action) {
        return drain(0, resource, action);
    }

    @Override
    public FluidStack drain(int maxDrain, FluidAction action) {
        return drain(0, maxDrain, action);
    }

    @Override
    public FluidStack drain(int slot, int maxDrain, FluidAction action) {
        FluidStack fluid = getFluidInTank(slot);
        int drained = maxDrain;
        if (fluid.getAmount() < drained) {
            drained = fluid.getAmount();
        }
        FluidStack stack = new FluidStack(fluid, drained);
        if (action.execute() && drained > 0) {
            fluid.shrink(drained);
            onContentsChanged(slot);
        }
        return stack;
    }

    @Override
    public FluidStack drain(int slot, FluidStack resource, FluidAction action) {
        if (resource.isEmpty() || !resource.isFluidEqual(getFluidInTank(slot))) {
            return FluidStack.EMPTY;
        }
        return drain(slot, resource.getAmount(), action);
    }

    @Override
    public int fill(int slot, FluidStack resource, FluidAction action) {
        if (resource.isEmpty() || !isFluidValid(slot, resource)) {
            return 0;
        }
        FluidStack fluid = stacks.get(slot);
        int capacity = getTankCapacity(slot);
        if (action.simulate()) {
            if (fluid.isEmpty()) {
                return Math.min(capacity, resource.getAmount());
            }
            if (!fluid.isFluidEqual(resource)) {
                return 0;
            }
            return Math.min(capacity - fluid.getAmount(), resource.getAmount());
        }
        if (fluid.isEmpty()) {
            fluid = new FluidStack(resource, Math.min(capacity, resource.getAmount()));
            onContentsChanged(slot);
            return fluid.getAmount();
        }
        if (!fluid.isFluidEqual(resource)) {
            return 0;
        }
        int filled = capacity - fluid.getAmount();

        if (resource.getAmount() < filled) {
            fluid.grow(resource.getAmount());
            filled = resource.getAmount();
        } else {
            fluid.setAmount(capacity);
        }
        if (filled > 0)
            onContentsChanged(slot);
        return filled;
    }


    public void onContentsChanged(int slot) {
    }
}
