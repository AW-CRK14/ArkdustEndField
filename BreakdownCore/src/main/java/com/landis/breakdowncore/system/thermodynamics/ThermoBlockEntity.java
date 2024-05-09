package com.landis.breakdowncore.system.thermodynamics;

import com.landis.breakdowncore.system.material.Material;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public abstract class ThermoBlockEntity extends BlockEntity implements IThermoMatBackground{
    public final Material material;
    protected long q = 0;
    public ThermoBlockEntity(BlockEntityType<?> pType, BlockPos pPos, BlockState pBlockState, Material material) {
        super(pType, pPos, pBlockState);
        this.material = material;
    }

    @Override
    public long getQx10000() {
        return q;
    }

    @Override
    public void setQx10000(long heat) {
        this.q = Math.max(Math.min(heat,maxQx10000()),0);
    }

    @Override
    public Material getMaterial() {
        return material;
    }

    @Override
    public void load(CompoundTag pTag) {
        super.load(pTag);
        q = pTag.getLong("thermo_q");
    }

    @Override
    protected void saveAdditional(CompoundTag pTag) {
        super.saveAdditional(pTag);
        pTag.putLong("thermo_q",q);
    }
}
