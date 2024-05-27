package com.landis.breakdowncore.system.thermodynamics;

import com.landis.breakdowncore.module.blockentity.ITickable;
import com.landis.breakdowncore.system.material.Material;
import com.landis.breakdowncore.system.material.Registry$Material;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public abstract class ThermoBlockEntity extends BlockEntity implements IThermoMatBackground, ITickable {
    private Material material;
    protected long q = 0;

    public ThermoBlockEntity(BlockEntityType<?> pType, BlockPos pPos, BlockState pBlockState, Material material) {
        super(pType, pPos, pBlockState);
        this.material = material;
    }

    public ThermoBlockEntity(BlockEntityType<?> pType, BlockPos pPos, BlockState pBlockState) {
        super(pType, pPos, pBlockState);
    }

    public void setMaterial(Material material) {
        if (this.material == null) {
            this.material = material;
        }
    }

    @Override
    public long getQ() {
        return q;
    }

    @Override
    public void setQ(long heat) {
        this.q = Math.max(Math.min(heat, maxQ()), -273L * getMC());
    }

    @Override
    public Material getMaterial() {
        return material;
    }

    @Override
    public void load(CompoundTag pTag) {
        super.load(pTag);
        q = pTag.getLong("thermo_q");
        material = Registry$Material.MATERIAL.get(new ResourceLocation(pTag.getString("material")));
    }

    @Override
    protected void saveAdditional(CompoundTag pTag) {
        super.saveAdditional(pTag);
        pTag.putLong("thermo_q", q);
        pTag.putString("material", material.id.toString());
    }

    public void tick(Level pLevel, BlockPos pPos, BlockState pState) {
        if (pLevel.isClientSide()) return;
        if (((ServerLevel) pLevel).getServer().getTickCount() % 5 == 0) {
            thermoTick(pLevel, pPos, pState);
        }
    }

    public void thermoTick(Level pLevel, BlockPos pPos, BlockState pState) {
        BlockPos targetPos;
        for (Direction direction : Direction.values()) {
            IThermoBackground itbg = null;
            targetPos = pPos.relative(direction);
            if (pLevel.getBlockEntity(targetPos) instanceof IThermoBackground bg) itbg = bg;
            interactWith(itbg, false, pLevel.getBlockState(targetPos), direction, targetPos, pLevel);
        }
    }

    ;
}
