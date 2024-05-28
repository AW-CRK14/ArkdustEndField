package com.landis.arkdust.blockentity.thermo;

import com.landis.arkdust.registry.BlockEntityRegistry;
import com.landis.arkdust.registry.MenuTypeRegistry;
import com.landis.breakdowncore.Registries;
import com.landis.breakdowncore.module.blockentity.container.ExpandedContainer;
import com.landis.breakdowncore.module.blockentity.container.ExpandedContainerMenu;
import com.landis.breakdowncore.module.blockentity.container.SlotType;
import com.landis.breakdowncore.system.thermodynamics.ThermoBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class ThermoCombustorBlockEntity extends ThermoBlockEntity {


    public ThermoCombustorBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(BlockEntityRegistry.THERMO_COMBUSTOR.get(), pPos, pBlockState, Registries.MaterialReg.IRON.get());
    }

    protected ExpandedContainer container = new ExpandedContainer(SlotType.INPUT);

    public int getRemainTime() {
        return remainTime;
    }

    public int getThermalEfficiency() {
        return thermalEfficiency;
    }

    public ItemStack getBurningItem() {
        return burningItem;
    }

    private int remainTime;
    private int thermalEfficiency;
    private ItemStack burningItem = ItemStack.EMPTY;

    @Override
    protected void saveAdditional(CompoundTag pTag) {
        super.saveAdditional(pTag);
        pTag.put("container",container.serializeNBT());
        pTag.put("burning",burningItem.save(new CompoundTag()));
        pTag.putInt("rt",remainTime);
        pTag.putInt("te",thermalEfficiency);
    }

    @Override
    public void load(CompoundTag pTag) {
        super.load(pTag);
        container.deserializeNBT(pTag.getCompound("container"));
        burningItem = ItemStack.of(pTag.getCompound("burning"));
        remainTime = pTag.getInt("rt");
        thermalEfficiency = pTag.getInt("te");
    }

    @Override
    public int getM() {
        return 1000;
    }

    @Override
    public void onOverheating() {

    }

    public void thermoTick(Level pLevel, BlockPos pPos, BlockState pState){
        this.insertHeat(2000000,false);
        super.thermoTick(pLevel, pPos, pState);
    }


    public static class Menu extends ExpandedContainerMenu {
        public final Container container;
        public final Inventory inventory;
        public final int invStartIndex;

        public Menu(int pContainerId, Inventory inventory, FriendlyByteBuf buffer){
            super(MenuTypeRegistry.THERMO_COMBUSTOR.get(),pContainerId);
            this.container = new ExpandedContainer(1, SlotType.ITEM);
            this.inventory = inventory;
            this.invStartIndex = 1;
        }
        public Menu(int pContainerId, Inventory inventory, Container container) {
            super(MenuTypeRegistry.THERMO_COMBUSTOR.get(), pContainerId);
            this.container = container;
            this.inventory = inventory;
            this.invStartIndex = container.getContainerSize();
            for(int i = 0 ; i < container.getContainerSize() ; i++){
                addSlot(new Slot(container,i,0,0),SlotType.INPUT);
            }

            for(int i = 0 ; i < inventory.getContainerSize() ; i++){
                addSlot(new Slot(inventory,0,0,0),SlotType.PLAYER_INVENTORY);
            }
        }

        @Override
        public int inventoryStartIndex() {
            return invStartIndex;
        }
    }
}
