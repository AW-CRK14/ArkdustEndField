package com.landis.breakdowncore.module.fluid;

import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

public class WaterTank extends FluidTank {
    public WaterTank(int capacity) {
        super(capacity, s -> s.is(Fluids.WATER));
    }
}
