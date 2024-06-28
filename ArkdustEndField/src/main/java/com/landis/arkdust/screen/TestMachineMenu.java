package com.landis.arkdust.screen;

//import com.landis.arkdust.blocks.TestMachineBlock;
//TODO ThermoTest
//public class TestMachineMenu extends AbstractContainerMenu {
//    public final TestMachineBlock.Entity blockEntity;
//    private final Level level;
//    private final ContainerData data;
//    private FluidStack fluidStack;
//
//
//    public TestMachineMenu(int id, Inventory inv, FriendlyByteBuf extraData){
//        this(id, inv, inv.player.level().getBlockEntity(extraData.readBlockPos()), new SimpleContainerData(2));
//    }
//    public TestMachineMenu(int id, Inventory inv, BlockEntity entity, ContainerData data) {
//        super(MenuTypeRegistry.TESTING.get(), id);
//        checkContainerSize(inv, 3);
//        blockEntity = (TestMachineBlock.Entity) entity;
//        this.level = inv.player.level();
//        this.data = data;
//        //this.fluidStack =
//
//        addPlayerInventory(inv);
//
//        this.addSlot(new SlotItemHandler(blockEntity.itemHandler, 0, 12, 15));
//        this.addSlot(new SlotItemHandler(blockEntity.itemHandler, 1, 86, 15));
//        this.addSlot(new SlotItemHandler(blockEntity.itemHandler, 2, 86, 60));
//
//        addDataSlots(data);
//    }
//    private void addPlayerInventory(Inventory playerInventory) {
//        for (int i = 0; i < 3; ++i) {
//            for (int l = 0; l < 9; ++l) {
//                this.addSlot(new Slot(playerInventory, l + i * 9 + 9, 8 + l * 18, 86 + i * 18));
//            }
//        }
//    }
//
//    @Override
//    public ItemStack quickMoveStack(Player player, int i) {
//        return null;//
//    }
//
//    @Override
//    public boolean stillValid(Player player) {
//        return true;
//    }
//}
