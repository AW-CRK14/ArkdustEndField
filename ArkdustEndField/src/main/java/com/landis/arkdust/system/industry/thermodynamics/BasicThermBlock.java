/*package com.arkdust.system.industry.thermodynamics;

import com.arkdust.Arkdust;
import com.arkdust.registry.BlockEntityRegistry;
import com.arkdust.registry.DataAttachmentRegistry;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BeaconBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

public class BasicThermBlock extends BaseEntityBlock {
    public BasicThermBlock(Properties p) {
        super(p);
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return simpleCodec(BasicThermBlock::new);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return new Entity(pPos,pState);
    }


    @Deprecated //TODO onlyForTest
    @Override
    public InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHit) {
        ItemStack stack = pPlayer.getItemInHand(pHand);
        IHeatBackground background = (IHeatBackground) (pLevel.getBlockEntity(pPos));
        if(stack.is(Items.COAL)){
            stack.shrink(1);
            background.insertHeat(1000,false);
            return InteractionResult.SUCCESS;
        }
        if(!pLevel.isClientSide){
            pPlayer.sendSystemMessage(Component.literal("heat store:" + background.getHeat() + ", max store:" + background.maxHeatStore()));
        }
        return InteractionResult.sidedSuccess(pLevel.isClientSide);
    }


    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState, BlockEntityType<T> pBlockEntityType) {
        return createTickerHelper(pBlockEntityType,BlockEntityRegistry.BASIC_THERM.get(),BasicThermBlock::tick);
    }

    public static void tick(Level pLevel, BlockPos pPos, BlockState pState, Entity pBlockEntity){
        pBlockEntity.tick(pLevel,pPos,pState);
    }

    public static class Entity extends BlockEntity implements IHeatBackground{
        private int ticker;

        public Entity(BlockEntityType<?> pType, BlockPos pPos, BlockState pBlockState) {
            super(pType, pPos, pBlockState);
        }

        public Entity(BlockPos pPos, BlockState pBlockState) {
            super(BlockEntityRegistry.BASIC_THERM.get(), pPos, pBlockState);
        }

        public void tick(Level pLevel, BlockPos pPos, BlockState pState){
            if(ticker++ >= heatInteractionCycle){
                for (Direction direction : Arkdust.DIRECTIONS) {
                    interactWith((IHeatBackground)(pLevel.getBlockEntity(pPos.relative(direction))),false);
                }
                ticker = 0;
            }
        }

        @Override
        protected void saveAdditional(CompoundTag pTag) {
            super.saveAdditional(pTag);
            pTag.putInt("ticker",ticker);
        }

        @Override
        public void load(CompoundTag pTag) {
            super.load(pTag);
            ticker = pTag.getInt("ticker");
        }

        @Override
        public int maxTemperature() {
            return 50;
        }

        @Override
        public int getSHC() {
            return 200;
        }

        @Override
        public void onOverheating() {

        }

        @Override
        public int getHeat() {
            return getData(DataAttachmentRegistry.HEAT);
        }

        @Override
        public void setHeat(int heat) {
            setData(DataAttachmentRegistry.HEAT,heat);
            setChanged();
        }
    }
}
*/