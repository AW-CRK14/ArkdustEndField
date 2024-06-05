package com.landis.arkdust.blockentity.thermo;

import com.landis.arkdust.mui.AbstractArkdustIndustContainerUI;
import com.landis.arkdust.mui.abs.IMenu2ScreenNotifiable;
import com.landis.arkdust.mui.abs.ItemWidget;
import com.landis.arkdust.mui.widget.viewgroup.InventoryWidgets;
import com.landis.arkdust.registry.BlockEntityRegistry;
import com.landis.arkdust.registry.MenuTypeRegistry;
import com.landis.arkdust.system.ArkdustContainerMenu;
import com.landis.breakdowncore.Registries;
import com.landis.breakdowncore.module.blockentity.container.*;
import com.landis.breakdowncore.system.thermodynamics.ThermoBlockEntity;
import icyllis.modernui.fragment.Fragment;
import icyllis.modernui.mc.neoforge.MenuScreenFactory;
import icyllis.modernui.util.DataSet;
import icyllis.modernui.view.LayoutInflater;
import icyllis.modernui.view.View;
import icyllis.modernui.view.ViewGroup;
import icyllis.modernui.widget.RelativeLayout;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class ThermoCombustorBlockEntity extends ThermoBlockEntity implements IWrappedMenuProvider, MenuScreenFactory<ThermoCombustorBlockEntity.Menu> {


    public ThermoCombustorBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(BlockEntityRegistry.THERMO_COMBUSTOR.get(), pPos, pBlockState, Registries.MaterialReg.IRON.get());
    }

    protected ExpandedContainer container = new ExpandedContainer(SlotType.INPUT);
    protected ExpandedContainer burning = new ExpandedContainer(SlotType.INFO);

    public int getRemainTime() {
        return remainTime;
    }

    public int getThermalEfficiency() {
        return thermalEfficiency;
    }

    public ItemStack getBurningItem() {
        return burning.getItem(0);
    }

    private int remainTime;
    private int thermalEfficiency;


    //---[基础数据处理 basic data handle]--

    @Override
    protected void saveAdditional(CompoundTag pTag) {
        super.saveAdditional(pTag);
        pTag.put("container", container.serializeNBT());
        pTag.put("burning", burning.serializeNBT());
        pTag.putInt("rt", remainTime);
        pTag.putInt("te", thermalEfficiency);
    }

    @Override
    public void load(CompoundTag pTag) {
        super.load(pTag);
        container.deserializeNBT(pTag.getCompound("container"));
        burning.deserializeNBT(pTag.getCompound("burning"));
        remainTime = pTag.getInt("rt");
        thermalEfficiency = pTag.getInt("te");
    }

    //---[热力处理 thermo handle]---

    @Override
    public int getM() {
        return 1000;
    }

    @Override
    public void onOverheating() {

    }

    public void thermoTick(Level pLevel, BlockPos pPos, BlockState pState) {
        this.insertHeat(2000000, false);
        super.thermoTick(pLevel, pPos, pState);
    }

    //---[菜单与容器 menu and container]---

    @Override
    public Container getContainer() {
        return container;
    }

    @Override
    public @NotNull Component getDisplayName() {
        return Component.translatable("arkdust.industry.arkdust.mac.thermo.combustor.test");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int pContainerId, Inventory pPlayerInventory, Player pPlayer) {
        return new Menu(pContainerId, pPlayerInventory, container, pPlayer, this);
    }

    @NotNull
    @Override
    public Fragment createFragment(Menu menu) {
        return new UI(menu);
    }


    public static class Menu extends ExpandedContainerMenu<ThermoCombustorBlockEntity> implements IMenu2ScreenNotifiable {

        public final Container container;
        public final Inventory inventory;
        public final int invStartIndex;
        private @Nullable AbstractArkdustIndustContainerUI fragment;

        public Menu(int pContainerId, Inventory inventory, FriendlyByteBuf buffer) {
            super(MenuTypeRegistry.THERMO_COMBUSTOR.get(), pContainerId, (ThermoCombustorBlockEntity) inventory.player.level().getBlockEntity(buffer.readBlockPos()), inventory.player);
            this.container = belonging.container;
            this.inventory = inventory;
            this.invStartIndex = container.getContainerSize();
            addSlots(inventory, container, belonging);
        }

        private void addSlots(Inventory inventory, Container container, ThermoCombustorBlockEntity entity) {
            for (int i = 0; i < container.getContainerSize(); i++) {
                addSlot(new FixedSlot(container, i, 0, 0), SlotType.INPUT);
            }

            for (int i = 0; i < inventory.getContainerSize(); i++) {
                addSlot(new FixedSlot(inventory, i, 0, 0), SlotType.PLAYER_INVENTORY);
            }

            addSlot(new FixedSlot(entity.burning, 0, 0, 0), SlotType.INFO);//index = 37

            addDataSlots(new Data());
        }

        public Menu(int pContainerId, Inventory inventory, Container container, Player user, ThermoCombustorBlockEntity entity) {
            super(MenuTypeRegistry.THERMO_COMBUSTOR.get(), pContainerId, entity, inventory.player);
            this.container = container;
            this.inventory = inventory;
            this.invStartIndex = container.getContainerSize();
            addSlots(inventory, container, entity);
        }

        @Override
        public int inventoryStartIndex() {
            return invStartIndex;
        }

        //---[UI响应处理 UI response handle]---

        @Override
        public void bingFragment(AbstractArkdustIndustContainerUI mui) {
            if (fragment == null) {
                fragment = mui;
            }
        }

        @Override
        public void notifySlotChanged(int index) {
            if (fragment != null)
                fragment.notify(index);
        }

        @Override
        public void setItem(int pSlotId, int pStateId, ItemStack pStack) {
            super.setItem(pSlotId, pStateId, pStack);
            notifySlotChanged(pSlotId);
        }

        public class Data implements ContainerData {

            @Override
            public int get(int pIndex) {
                return pIndex == 0 ? belonging.thermalEfficiency : belonging.remainTime;
            }

            @Override
            public void set(int pIndex, int pValue) {
                if (pIndex == 0) {
                    belonging.thermalEfficiency = pValue;
                } else {
                    belonging.remainTime = pValue;
                }
            }

            @Override
            public int getCount() {
                return 2;
            }

        }
    }

    public static class UI extends AbstractArkdustIndustContainerUI {

        public UI(Menu menu) {
            super(true, menu, false);
        }

        @Override
        public @NotNull View onCreateView(LayoutInflater inflater, ViewGroup container, DataSet savedInstanceState) {
//            ViewGroup group = (ViewGroup) super.onCreateView(inflater, container, savedInstanceState);
            ViewGroup group = new RelativeLayout(getContext());
//            ItemWidget s1 = new ItemWidget(getContext(), menu.getSlot(0));
//            s1.setTranslationX(group.dp(20));
//            s1.setTranslationY(group.dp(40));
//            group.addView(s1, s1.defaultPara());
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(-2, -2);
            group.addView(new Inventory(((Menu) menu).invStartIndex, 16), params);
            return group;
        }
    }
}
