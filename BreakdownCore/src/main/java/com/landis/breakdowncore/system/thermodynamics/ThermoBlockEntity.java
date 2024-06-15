package com.landis.breakdowncore.system.thermodynamics;

import com.landis.breakdowncore.helper.MathHelper;
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
    protected double q = 0;
    protected int[] lastOutput = new int[]{0, 0};

    /**仅用于在客户端暂存部分参考数据。不应被作为精确而实时的有效数据使用。*/
    public int clientCachedT = 0;
    public int clientCachedTMax = 0;


    public ThermoBlockEntity(BlockEntityType<?> pType, BlockPos pPos, BlockState pBlockState, Material material) {
        super(pType, pPos, pBlockState);
        this.material = material;
        this.setQ(getMC() * 27);
    }

    public ThermoBlockEntity(BlockEntityType<?> pType, BlockPos pPos, BlockState pBlockState) {
        super(pType, pPos, pBlockState);
        this.setQ(getMC() * 27);
    }

    public void setMaterial(Material material) {
        if (this.material == null) {
            this.material = material;
        }
    }

    @Override
    public double getQ() {
        return q;
    }

    @Override
    public void setQ(double heat) {
        this.q = Math.max(Math.min(heat, maxQ()), -273L * getMC());
    }

    @Override
    public Material getMaterial() {
        return material;
    }

    @Override
    public void load(CompoundTag pTag) {
        super.load(pTag);
        q = pTag.getFloat("thermo_temperature") * getMC();
        material = Registry$Material.MATERIAL.get(new ResourceLocation(pTag.getString("material")));
    }

    @Override
    protected void saveAdditional(CompoundTag pTag) {
        super.saveAdditional(pTag);
        pTag.putFloat("thermo_temperature", getT());
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
        double output = 0;
        for (Direction direction : Direction.values()) {
            IThermoBackground itbg = null;
            targetPos = pPos.relative(direction);
            if (pLevel.getBlockEntity(targetPos) instanceof IThermoBackground bg) itbg = bg;
            output += interactWith(itbg, false, pLevel.getBlockState(targetPos), direction, targetPos, pLevel);
        }
        this.lastOutput = MathHelper.doubleToInt(output);
    }
}
