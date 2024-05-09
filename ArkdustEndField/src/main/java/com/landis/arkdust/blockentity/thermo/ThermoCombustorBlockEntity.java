package com.landis.arkdust.blockentity.thermo;

import com.landis.arkdust.registry.BlockEntityRegistry;
import com.landis.breakdowncore.system.thermodynamics.ThermoBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class ThermoCombustorBlockEntity extends ThermoBlockEntity {
    public ThermoCombustorBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(BlockEntityRegistry.TEST_GENERATOR.get(), pPos, pBlockState,null);//TODO
    }


    @Override
    public void onOverheating() {

    }


}
