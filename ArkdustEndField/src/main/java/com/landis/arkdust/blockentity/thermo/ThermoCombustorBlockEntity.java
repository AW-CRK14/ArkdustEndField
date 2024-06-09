package com.landis.arkdust.blockentity.thermo;

import com.landis.arkdust.helper.MUIHelper;
import com.landis.arkdust.mui.AbstractArkdustIndustContainerUI;
import com.landis.arkdust.mui.MUIRelativeMenu;
import com.landis.arkdust.mui.abs.ItemWidget;
import com.landis.arkdust.mui.widget.item.FactoryLightInputItemView;
import com.landis.arkdust.mui.widget.viewgroup.IndsGroup;
import com.landis.arkdust.registry.BlockEntityRegistry;
import com.landis.arkdust.registry.MenuTypeRegistry;
import com.landis.breakdowncore.Registries;
import com.landis.breakdowncore.module.blockentity.container.*;
import com.landis.breakdowncore.system.thermodynamics.ThermoBlockEntity;
import icyllis.modernui.fragment.Fragment;
import icyllis.modernui.mc.neoforge.MenuScreenFactory;
import icyllis.modernui.util.DataSet;
import icyllis.modernui.view.Gravity;
import icyllis.modernui.view.LayoutInflater;
import icyllis.modernui.view.View;
import icyllis.modernui.view.ViewGroup;
import icyllis.modernui.widget.LinearLayout;
import icyllis.modernui.widget.RelativeLayout;
import icyllis.modernui.widget.TextView;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
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
    public long getM() {
        return 1000;
    }

    @Override
    public void onOverheating() {

    }

    public void thermoTick(Level pLevel, BlockPos pPos, BlockState pState) {
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


    public static class Menu extends MUIRelativeMenu<ThermoCombustorBlockEntity> {

        public final Container container;
        public final Inventory inventory;
        public final int invStartIndex;

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


        public class Data implements ContainerData {

            @Override
            public int get(int pIndex) {
                return switch (pIndex) {
                    case 0 -> belonging.remainTime;
                    case 1 -> belonging.thermalEfficiency;
                    case 2 -> (int) belonging.getT();
                    case 3 -> belonging.lastOutput[0];
                    case 4 -> belonging.lastOutput[1];
                    default -> -1;
                };
            }

            @Override
            public void set(int pIndex, int pValue) {
                switch (pIndex) {
                    case 0 -> belonging.remainTime = pValue;
                    case 1 -> belonging.thermalEfficiency = pValue;
                    case 2 -> belonging.setT(pValue);
                    case 3 -> belonging.lastOutput[0] = pValue;
                    case 4 -> belonging.lastOutput[1] = pValue;
                }
            }

            @Override
            public int getCount() {
                return 5;
            }

        }
    }

    public static class UI extends AbstractArkdustIndustContainerUI {
        protected List<TextView> thermoInfoTexts;


        public UI(Menu menu) {
            super(true, menu, false);
        }

        @Override
        public void notifyData(int index, int content) {
            super.notifyData(index, content);
            if(thermoInfoTexts == null) return;
            switch (index) {
                case 0 -> thermoInfoTexts.get(0).post(()-> thermoInfoTexts.get(0).setText(I18n.get("arkdust.industry.arkdust.mac.thermo.combustor.info.time") + ":" + content));
            }
        }

        @Override
        public @NotNull View onCreateView(LayoutInflater inflater, ViewGroup container, DataSet savedInstanceState) {
            ViewGroup group = (ViewGroup) super.onCreateView(inflater, container, savedInstanceState);
            defaultIndsGroup.renderNodeA = 0.6F;
            defaultIndsGroup.renderNodeB = 0.8F;

            RelativeLayout topLayout = new RelativeLayout(getContext());
            topLayout.setBackground(MUIHelper.withBorder());
            defaultIndsGroup.child.addView(topLayout);
            topLayout.setId(210010);
            {
                ItemWidget widget = new FactoryLightInputItemView(getContext(), menu.getSlot(0), 16, menu);
                widget.setId(210011);
                widgets.set(0, widget);
                RelativeLayout.LayoutParams params = widget.defaultPara();
                params.setMarginsRelative(group.dp(20 + 0.75F * IndsGroup.TOP_H), 0, 0, 0);
                params.addRule(RelativeLayout.CENTER_VERTICAL);
                topLayout.addView(widget, params);
            }

            LinearLayout thermoInfo = new LinearLayout(getContext());
            thermoInfo.setGravity(Gravity.CENTER_HORIZONTAL);
            thermoInfo.setOrientation(LinearLayout.VERTICAL);
            thermoInfo.setMinimumWidth(group.dp(80));
            thermoInfo.setBackground(MUIHelper.withBorder());
            {
                TextView tt = new TextView(getContext());
                tt.setTextSize(group.dp(6));
                tt.setTextColor(0xFF3B3B3B);
                tt.setText(I18n.get("arkdust.industry.arkdust.mac.thermo.combustor.info.title"));
                tt.setBackground(MUIHelper.withBorder());
                RelativeLayout.LayoutParams para = new RelativeLayout.LayoutParams(-2, -2);
                para.setMarginsRelative(0, 0, 0, group.dp(4));
                thermoInfo.addView(tt, para);

                List<TextView> thermoInfoTexts = new ArrayList<>(3);
                for (int i = 0; i < 3; i++) {
                    TextView v = new TextView(getContext());
                    v.setTextColor(0xFFA8A8A8);
                    v.setTextSize(group.dp(4));
                    v.setBackground(MUIHelper.withBorder());
                    RelativeLayout.LayoutParams p = new RelativeLayout.LayoutParams(-2, -2);
                    p.setMarginsRelative(0, 0, group.dp(1), 0);
                    thermoInfoTexts.add(v);
                    thermoInfo.addView(v, p);
                }

                thermoInfoTexts.get(0).setText(I18n.get("arkdust.industry.arkdust.mac.thermo.combustor.info.time") + ":" + ((Menu) menu).belonging.remainTime);
                thermoInfoTexts.get(1).setText(I18n.get("arkdust.industry.arkdust.mac.thermo.combustor.info.combust_effi") + ":" + I18n.get("arkdust.industry.message.need_module"));
                thermoInfoTexts.get(2).setText(I18n.get("arkdust.industry.arkdust.mac.thermo.combustor.info.combust_output") + ":" + I18n.get("arkdust.industry.message.need_module"));
                this.thermoInfoTexts = thermoInfoTexts;

            }
            RelativeLayout.LayoutParams thermoInfoPara = new RelativeLayout.LayoutParams(-2, -2);
            thermoInfoPara.setMarginsRelative(group.dp(16), group.dp(4), group.dp(4), group.dp(4));
            thermoInfoPara.addRule(RelativeLayout.RIGHT_OF, 210011);
            thermoInfoPara.addRule(RelativeLayout.ALIGN_RIGHT, 210010);
            topLayout.addView(thermoInfo, thermoInfoPara);


            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(-2, -2);
            params.addRule(RelativeLayout.BELOW, 200000);
            params.addRule(RelativeLayout.CENTER_HORIZONTAL);
            params.setMarginsRelative(0, group.dp(20), 0, 0);
            group.addView(new Inventory(((Menu) menu).invStartIndex, 16), params);


            return group;
        }
    }
}
