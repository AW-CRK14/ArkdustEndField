package com.landis.arkdust.blocks.levelblocks;

import com.landis.arkdust.blockentity.thermo.ThermoCarrierBlockEntity;
import com.landis.arkdust.blockentity.thermo.ThermoCombustorBlockEntity;
import com.landis.arkdust.registry.BlockEntityRegistry;
import com.landis.breakdowncore.module.blockentity.ITickable;
import com.landis.breakdowncore.system.thermodynamics.ThermoBlockEntity;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

public class ThermoBlocks {
    public static class CombustorBlock extends BaseEntityBlock {
        public static final MapCodec<CombustorBlock> CODEC = simpleCodec(p -> new CombustorBlock());

        public CombustorBlock() {
            super(Properties.ofFullCopy(Blocks.IRON_BLOCK));
        }

        @Nullable
        @Override
        public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
            return new ThermoCombustorBlockEntity(pPos, pState);
        }

        @Nullable
        @Override
        public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState, BlockEntityType<T> pBlockEntityType) {
            return createTickerHelper(pBlockEntityType, BlockEntityRegistry.THERMO_COMBUSTOR.get(), ITickable.ticker());
        }

        @Override
        protected MapCodec<? extends BaseEntityBlock> codec() {
            return CODEC;
        }

        @Override
        public void onPlace(BlockState pState, Level pLevel, BlockPos pPos, BlockState pOldState, boolean pMovedByPiston) {
            super.onPlace(pState, pLevel, pPos, pOldState, pMovedByPiston);
            if (!pMovedByPiston)
                ((ThermoBlockEntity) pLevel.getBlockEntity(pPos)).init(pLevel, pPos, pState);
        }

        @Override
        public InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHit) {
            pPlayer.openMenu((MenuProvider) pLevel.getBlockEntity(pPos),pPos);
            return super.use(pState, pLevel, pPos, pPlayer, pHand, pHit);
        }
    }

    public static class CarrierBlock extends BaseEntityBlock {
        public static final MapCodec<CarrierBlock> CODEC = simpleCodec(p -> new CarrierBlock());

        public CarrierBlock() {
            super(Properties.ofFullCopy(Blocks.IRON_BLOCK));
        }

        @Nullable
        @Override
        public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
            return new ThermoCarrierBlockEntity(pPos, pState);
        }

        @Nullable
        @Override
        public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState, BlockEntityType<T> pBlockEntityType) {
            return createTickerHelper(pBlockEntityType, BlockEntityRegistry.THERMO_CARRIER.get(), ITickable.ticker());
        }

        @Override
        protected MapCodec<? extends BaseEntityBlock> codec() {
            return CODEC;
        }

        @Override
        public void onPlace(BlockState pState, Level pLevel, BlockPos pPos, BlockState pOldState, boolean pMovedByPiston) {
            super.onPlace(pState, pLevel, pPos, pOldState, pMovedByPiston);
            if (!pMovedByPiston)
                ((ThermoBlockEntity) pLevel.getBlockEntity(pPos)).init(pLevel, pPos, pState);
        }
    }
}
