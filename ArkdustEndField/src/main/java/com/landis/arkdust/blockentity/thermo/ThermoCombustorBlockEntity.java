package com.landis.arkdust.blockentity.thermo;

import com.landis.arkdust.blocks.levelblocks.ThermoBlocks;
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

    public final int basicOutputEffi;
    public final float basicConversionRate;

    public ThermoCombustorBlockEntity(BlockPos pPos, BlockState pBlockState) {
        this(pPos, pBlockState, ((ThermoBlocks.CombustorBlock) pBlockState.getBlock()).basicOutputEffi, ((ThermoBlocks.CombustorBlock) pBlockState.getBlock()).basicConversionRate);
    }

    public ThermoCombustorBlockEntity(BlockPos pPos, BlockState pBlockState, int basicOutputEffi, float basicConversionRate) {
        super(BlockEntityRegistry.THERMO_COMBUSTOR.get(), pPos, pBlockState, Registries.MaterialReg.IRON.get());
        this.thermalEfficiency = basicOutputEffi;
        this.conversionRate = basicConversionRate;
        this.basicOutputEffi = basicOutputEffi;
        this.basicConversionRate = basicConversionRate;
    }

    protected ExpandedContainer container = new ExpandedContainer(SlotType.INPUT);

    private ItemStack burning = ItemStack.EMPTY;
    private int remainTime;//当前状态下 这一个燃料可以燃烧的时间 t = Q / P = Q * conversionRate / P0
    private int thermalEfficiency;//根据计算获得的实际燃料消耗功率 P = P0 / conversionRate
    private float conversionRate;//根据计算获得的实际转化率

    public int getRemainTime() {
        return remainTime;
    }

    public int getThermalEfficiency() {
        return thermalEfficiency;
    }

    public float getConversionRate() {
        return conversionRate;
    }

    public ItemStack getBurningItem() {
        return burning;
    }


    //---[基础数据处理 basic data handle]--

    @Override
    protected void saveAdditional(CompoundTag pTag) {
        super.saveAdditional(pTag);
        pTag.put("container", container.serializeNBT());
        pTag.put("burning", burning.save(new CompoundTag()));
        pTag.putInt("rt", remainTime);
    }

    @Override
    public void load(CompoundTag pTag) {
        super.load(pTag);
        container.deserializeNBT(pTag.getCompound("container"));
        burning = ItemStack.of(pTag.getCompound("burning"));
        remainTime = pTag.getInt("rt");
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
                    case 0 -> belonging.thermalEfficiency;
                    case 1 -> (int) (belonging.conversionRate * 1000);
                    case 2 -> belonging.remainTime;
                    case 3 -> (int) belonging.getT();
                    default -> -1;
                };
            }

            @Override
            public void set(int pIndex, int pValue) {
                switch (pIndex) {
                    case 0 -> belonging.thermalEfficiency = pValue;
                    case 1 -> belonging.conversionRate = pValue / 1000F;
                    case 2 -> belonging.remainTime = pValue;
                    case 3 -> belonging.setT(pValue);
                }
            }

            @Override
            public int getCount() {
                return 4;
            }

        }
    }

    public static class UI extends AbstractArkdustIndustContainerUI {
        protected List<TextView> thermoInfoTexts;
        protected TextView temperature;


        public UI(Menu menu) {
            super(true, menu, false);
        }

        @Override
        public void notifyData(int index, int content) {
            super.notifyData(index, content);
            if (thermoInfoTexts == null) return;
            switch (index) {
                case 0 ->
                        thermoInfoTexts.get(0).post(() -> thermoInfoTexts.get(0).setText(I18n.get("arkdust.industry.arkdust.mac.thermo.combustor.info.combust_effi") + ":" + content + "kJ/t"));
                case 1 ->
                        thermoInfoTexts.get(1).post(() -> thermoInfoTexts.get(1).setText(I18n.get("arkdust.industry.arkdust.mac.thermo.combustor.info.conversion_rate") + ":" + (content / 10F) + "%"));
                case 2 ->
                        thermoInfoTexts.get(2).post(() -> thermoInfoTexts.get(2).setText(I18n.get("arkdust.industry.arkdust.mac.thermo.combustor.info.time") + ":" + (content / 20) + "s"));
//                case 3 -> ;
            }
        }

        @Override
        public @NotNull View onCreateView(LayoutInflater inflater, ViewGroup container, DataSet savedInstanceState) {
            ViewGroup group = (ViewGroup) super.onCreateView(inflater, container, savedInstanceState);
            defaultIndsGroup.renderNodeA = 0.6F;
            defaultIndsGroup.renderNodeB = 0.8F;

            RelativeLayout topLayout = new RelativeLayout(getContext());
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

                LinearLayout thermoInfo = new LinearLayout(getContext());
                thermoInfo.setGravity(Gravity.CENTER_HORIZONTAL);
                thermoInfo.setOrientation(LinearLayout.VERTICAL);
                thermoInfo.setMinimumWidth(group.dp(80));
                {
                    TextView tt = new TextView(getContext());
                    tt.setTextSize(group.dp(6));
                    tt.setTextColor(0xFF3B3B3B);
                    tt.setText(I18n.get("arkdust.industry.arkdust.mac.thermo.combustor.info.title"));
                    RelativeLayout.LayoutParams para = new RelativeLayout.LayoutParams(-2, -2);
                    para.setMarginsRelative(0, 0, 0, group.dp(4));
                    thermoInfo.addView(tt, para);

                    List<TextView> thermoInfoTexts = new ArrayList<>(3);
                    for (int i = 0; i < 3; i++) {
                        TextView v = new TextView(getContext());
                        v.setTextColor(0xFF888888);
                        v.setTextSize(group.dp(4));
                        RelativeLayout.LayoutParams p = new RelativeLayout.LayoutParams(-2, -2);
                        p.setMarginsRelative(0, 0, group.dp(1), 0);
                        thermoInfoTexts.add(v);
                        thermoInfo.addView(v, p);
                    }
                    thermoInfoTexts.get(0).setText(I18n.get("arkdust.industry.arkdust.mac.thermo.combustor.info.combust_effi") + ":" + ((Menu) menu).belonging.thermalEfficiency + "kJ/t");
                    thermoInfoTexts.get(1).setText(I18n.get("arkdust.industry.arkdust.mac.thermo.combustor.info.conversion_rate") + ":" + (((Menu) menu).belonging.conversionRate * 100) + "%");
                    thermoInfoTexts.get(2).setText(I18n.get("arkdust.industry.arkdust.mac.thermo.combustor.info.time") + ":" + (((Menu) menu).belonging.remainTime / 20) + "s");
                    this.thermoInfoTexts = thermoInfoTexts;
                }
                RelativeLayout.LayoutParams thermoInfoPara = new RelativeLayout.LayoutParams(-2, -2);
                thermoInfoPara.setMarginsRelative(group.dp(16), group.dp(4), group.dp(4), group.dp(4));
                thermoInfoPara.addRule(RelativeLayout.RIGHT_OF, 210011);
                thermoInfoPara.addRule(RelativeLayout.ALIGN_RIGHT, 210010);
                topLayout.addView(thermoInfo, thermoInfoPara);
            }

            LinearLayout temInfoLayout = new LinearLayout(getContext());
            temInfoLayout.setOrientation(LinearLayout.VERTICAL);
            temInfoLayout.setHorizontalGravity(Gravity.CENTER_HORIZONTAL);
            temInfoLayout.setMinimumWidth(group.dp(300));
            RelativeLayout.LayoutParams temInfoPara = new RelativeLayout.LayoutParams(-2,-2);
            temInfoPara.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
            temInfoPara.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            defaultIndsGroup.child.addView(temInfoLayout,temInfoPara);
            {
                LinearLayout texts = new LinearLayout(getContext());
                temInfoLayout.setOrientation(LinearLayout.HORIZONTAL);

                temperature = new TextView(getContext());
                temperature.setTextSize(group.dp(7));
                temperature.setTextColor(0xFF393939);
            }

            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(-2, -2);
            params.addRule(RelativeLayout.BELOW, 200000);
            params.addRule(RelativeLayout.CENTER_HORIZONTAL);
            params.setMarginsRelative(0, group.dp(20), 0, 0);
            group.addView(new Inventory(((Menu) menu).invStartIndex, 16), params);

            return group;
        }
    }
}
