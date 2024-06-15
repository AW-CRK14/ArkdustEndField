package com.landis.arkdust.blockentity.thermo;

import com.landis.arkdust.registry.BlockEntityRegistry;
import com.landis.breakdowncore.BreaRegistries;
import com.landis.breakdowncore.system.thermodynamics.ThermoBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class ThermoCarrierBlockEntity extends ThermoBlockEntity {
    public ThermoCarrierBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(BlockEntityRegistry.THERMO_CARRIER.get(), pPos, pBlockState, BreaRegistries.MaterialReg.IRON.get());
    }

    @Override
    public long getM() {
        return 1000;
    }

    @Override
    public void onOverheating() {

    }

//    @Override
//    public void thermoTick(Level pLevel, BlockPos pPos, BlockState pState) {
//        System.out.println("ThermoCarrierLog:{pos=" + pPos + ",state=" + pState + "}");
//        BlockPos targetPos;
//        for(Direction direction : Direction.values()){
//            IThermoBackground itbg = null;
//            targetPos = pPos.relative(direction);
//            if(pLevel.getBlockEntity(targetPos) instanceof IThermoBackground bg) itbg = bg;
//            BlockState state = pLevel.getBlockState(targetPos);
//            float q = getQ();
//            long i = interactWith(itbg,false,state,direction,targetPos,pLevel);
//            System.out.println("    try interact on Direction:" + direction + " with target{state=" + state + ",itbg=" + itbg + "}. With q=" + q + " to q=" + getQ() + " with TransHeat{t=" + i + ",a=" + (q - getQ()) +"}");
//        }
//    }
}
