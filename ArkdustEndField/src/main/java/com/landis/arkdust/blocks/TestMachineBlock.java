//TODO THERMODYNAMICS TEST
package com.landis.arkdust.blocks;

import com.landis.arkdust.Arkdust;
import com.landis.arkdust.registry.BlockEntityRegistry;
import com.landis.arkdust.registry.DataAttachmentRegistry;
import com.landis.arkdust.registry.ItemRegistry;
import com.landis.arkdust.screen.TestMachineMenu;
import com.landis.arkdust.system.industry.thermodynamics.IHeatBackground;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.Nullable;


public class TestMachineBlock extends BaseEntityBlock {
    public TestMachineBlock(Properties p) {
        super(p);
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return simpleCodec(TestMachineBlock::new);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return new TestMachineBlock.Entity(pPos,pState);
    }


    @Deprecated //TODO onlyForTest
    @Override
    public InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHit) {
        ItemStack stack = pPlayer.getItemInHand(pHand);
        IHeatBackground background = (IHeatBackground) (pLevel.getBlockEntity(pPos));
        if(!pLevel.isClientSide){
            pPlayer.sendSystemMessage(Component.literal("heat store:" + background.getHeat() + ", max store:" + background.maxHeatStore()));
            BlockEntity entity = pLevel.getBlockEntity(pPos);
            if(entity instanceof Entity){
                pPlayer.openMenu(getMenuProvider(pState,pLevel,pPos),pPos);
            } else {
                throw new IllegalStateException("Our Container provider is missing!");
            }
        }
        return InteractionResult.sidedSuccess(pLevel.isClientSide);
    }


    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState, BlockEntityType<T> pBlockEntityType) {
        return createTickerHelper(pBlockEntityType,BlockEntityRegistry.TEST_MACHINE.get(), TestMachineBlock::tick);
    }

    @Override
    public void onRemove(BlockState pState, Level pLevel, BlockPos pPos, BlockState pNewState, boolean pMovedByPiston) {
        if(pState.getBlock() != pNewState.getBlock()){
            BlockEntity blockEntity = pLevel.getBlockEntity(pPos);
            if(blockEntity instanceof Entity){
                ((Entity)blockEntity).drops();
            }
        }
        super.onRemove(pState, pLevel, pPos, pNewState, pMovedByPiston);
    }

    public static void tick(Level pLevel, BlockPos pPos, BlockState pState, Entity pBlockEntity){
        pBlockEntity.tick(pLevel,pPos,pState,pBlockEntity);
    }

    public static class Entity extends BlockEntity implements IHeatBackground, MenuProvider {
        //TODO--------------------------------------------------------------------------------

        public final ItemStackHandler itemHandler = new ItemStackHandler(3){
            @Override
            protected void onContentsChanged(int slot) {
                setChanged();
            }
        };
        protected final ContainerData data;
        private int progress = 0;
        private int maxProgress = 100;
        public Entity(BlockPos pPos, BlockState pBlockState) {
            super(BlockEntityRegistry.TEST_MACHINE.get(), pPos, pBlockState);
            this.data = new ContainerData() {
                @Override
                public int get(int pIndex) {
                    return switch (pIndex){
                        case 0 -> Entity.this.progress;
                        case 1 -> Entity.this.maxProgress;
                        default -> 0;
                    };
                }

                @Override
                public void set(int pIndex, int pValue) {
                    switch (pIndex){
                        case 0 -> Entity.this.progress = pValue;
                        case 1 -> Entity.this.maxProgress = pValue;
                    };
                }

                @Override
                public int getCount() {
                    return 2;
                }
            };
        }
        @Override
        public Component getDisplayName() {
            return Component.literal("Test Machine Block");
        }
        @Nullable
        @Override
        public AbstractContainerMenu createMenu(int pContainerId, Inventory pPlayerInventory, Player pPlayer) {
            return new TestMachineMenu(pContainerId,pPlayerInventory,this,this.data);
        }

        @Override
        protected void saveAdditional(CompoundTag pTag) {
            pTag.put("inventory",itemHandler.serializeNBT());
            pTag.putInt("ticker",ticker);
            pTag.putInt("test_machine.progress",this.progress);
            super.saveAdditional(pTag);
        }

        @Override
        public void load(CompoundTag pTag) {
            super.load(pTag);
            itemHandler.deserializeNBT(pTag.getCompound("inventory"));
            progress = pTag.getInt("test_machine.progress");
            ticker = pTag.getInt("ticker");
        }
        public void drops(){
            SimpleContainer inventory = new SimpleContainer(itemHandler.getSlots());
            for(int i = 0 ; i < itemHandler.getSlots() ; i++){
                inventory.setItem(i,itemHandler.getStackInSlot(i));
            }
            Containers.dropContents(this.level,this.worldPosition,inventory);
        }
        public void tick(Level pLevel, BlockPos pPos, BlockState pState,Entity pEntity){
            if(level.isClientSide()){
                return;
            }
            if(ticker++ >= heatInteractionCycle){
                for (Direction direction : Arkdust.DIRECTIONS) {
                    interactWith((IHeatBackground)(pLevel.getBlockEntity(pPos.relative(direction))),false);
                }
                ticker = 0;
            }
            if(hasRecipe(pEntity)){
                pEntity.progress++;
                setChanged(pLevel,pPos,pState);
                if(pEntity.progress >= pEntity.maxProgress) {
                    craftItem(pEntity);
                }
            } else {
                pEntity.resetProgress();
                setChanged(pLevel,pPos,pState);
            }
        }
        private void resetProgress(){
            this.progress = 0;
        }
        private static void craftItem(Entity pEntity){
            if(hasRecipe(pEntity)){
                pEntity.itemHandler.extractItem(1,1,false);
                pEntity.itemHandler.setStackInSlot(2,new ItemStack(ItemRegistry.TEST_ITEM.get(),
                        pEntity.itemHandler.getStackInSlot(2).getCount() + 1));
                pEntity.resetProgress();
            }
        }
        private static boolean hasRecipe(Entity pEntity){
            SimpleContainer inventory = new SimpleContainer(pEntity.itemHandler.getSlots());
            for(int i = 0; i < pEntity.itemHandler.getSlots(); i++) {
                inventory.setItem(i, pEntity.itemHandler.getStackInSlot(i));
            }
            boolean hasItemInFirstSlot = pEntity.itemHandler.getStackInSlot(1).getItem() == ItemRegistry.SPIRIT_STONE.get();
            return hasItemInFirstSlot && canInsertItemIntoOutputSlot(inventory) &&
                    canInsertItemIntoOutputSlot(inventory, new ItemStack(ItemRegistry.SPIRIT_STONE.get(),1));
        }
        private static boolean canInsertItemIntoOutputSlot(SimpleContainer inventory, ItemStack stack) {
            return inventory.getItem(2).getItem() == stack.getItem() || inventory.getItem(2).isEmpty();
        }
        private static boolean canInsertItemIntoOutputSlot(SimpleContainer inventory) {
            return inventory.getItem(2).getMaxStackSize() > inventory.getItem(2).getCount();
        }


        //TODO*----------------------------------------------------------------------------------


        private int ticker;

//        public Entity(BlockEntityType<?> pType, BlockPos pPos, BlockState pBlockState) {
//            super(pType, pPos, pBlockState);
//        }
        /*
        public Entity(BlockPos pPos, BlockState pBlockState) {
            super(BlockEntityRegistry.TEST_MACHINE.get(), pPos, pBlockState);
        }*/


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