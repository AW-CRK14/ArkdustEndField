package com.landis.breakdowncore.module.blockentity;

import com.landis.breakdowncore.system.thermodynamics.ThermoBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.state.BlockState;

public interface ITickable {
    void tick(Level world, BlockPos pos, BlockState state);

    static <T extends BlockEntity & ITickable> BlockEntityTicker<T> ticker(){
        return (pLevel,pPos,pState,pBlockEntity)-> pBlockEntity.tick(pLevel,pPos,pState);
    }
}
