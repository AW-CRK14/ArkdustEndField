package com.landis.arkdust.blockentity.thermo;

import com.landis.arkdust.blocks.levelblocks.ThermoBlocks;
import com.landis.arkdust.helper.MUIHelper;
import com.landis.arkdust.mui.AbstractArkdustIndustContainerUI;
import com.landis.arkdust.mui.MUIRelativeMenu;
import com.landis.arkdust.mui.widget.viewgroup.IndsGroup;
import com.landis.arkdust.registry.BlockEntityRegistry;
import com.landis.arkdust.registry.MenuTypeRegistry;
import com.landis.breakdowncore.Registries;
import com.landis.breakdowncore.module.blockentity.container.*;
import com.landis.breakdowncore.system.thermodynamics.ThermoBlockEntity;
import icyllis.modernui.fragment.Fragment;
import icyllis.modernui.graphics.Canvas;
import icyllis.modernui.graphics.Paint;
import icyllis.modernui.mc.neoforge.MenuScreenFactory;
import icyllis.modernui.text.PrecomputedText;
import icyllis.modernui.text.Spannable;
import icyllis.modernui.text.Typeface;
import icyllis.modernui.text.style.ForegroundColorSpan;
import icyllis.modernui.text.style.StyleSpan;
import icyllis.modernui.util.DataSet;
import icyllis.modernui.view.Gravity;
import icyllis.modernui.view.LayoutInflater;
import icyllis.modernui.view.View;
import icyllis.modernui.view.ViewGroup;
import icyllis.modernui.widget.ImageView;
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
        this.thermalEfficiency = (int) (basicOutputEffi / basicConversionRate);
        this.conversionRate = basicConversionRate;
        this.outputEffi = basicOutputEffi;
        this.basicOutputEffi = basicOutputEffi;
        this.basicConversionRate = basicConversionRate;
    }

    protected ExpandedContainer container = new ExpandedContainer(SlotType.INPUT);

    private ItemStack burning = ItemStack.EMPTY;
    private int remainTime;//当前状态下 这一个燃料可以燃烧的时间 t = Q / P = Q * conversionRate / P0
    private int thermalEfficiency;//根据计算获得的实际燃料消耗功率 P = P0 / conversionRate
    private float conversionRate;//根据计算获得的实际转化率
    private int outputEffi;//根据计算获得的实际输出功率

    public int getRemainTime() {
        return remainTime;
    }

    public int getThermalEfficiency() {
        return thermalEfficiency;
    }

    public float getConversionRate() {
        return conversionRate;
    }

    public float getOutputEffi() {
        return outputEffi;
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
                    case 0 -> belonging.outputEffi;
                    case 1 -> (int) belonging.getT() * 10;
                    case 2 -> (int) (belonging.conversionRate * 1000);
                    case 3 -> belonging.remainTime;
                    default -> -1;
                };
            }

            @Override
            public void set(int pIndex, int pValue) {
                switch (pIndex) {
                    case 0 -> belonging.outputEffi = pValue;
                    case 1 -> belonging.setT(pValue / 10);
                    case 2 -> belonging.conversionRate = pValue / 1000F;
                    case 3 -> belonging.remainTime = pValue;
                }
            }

            @Override
            public int getCount() {
                return 5;
            }

        }
    }

    public static class UI extends AbstractArkdustIndustContainerUI {
        public static final int SECONDARY_COLOR = 0x99FFFFFF;
        protected List<TextView> thermoInfoTexts;

        public static final ForegroundColorSpan temComColor = new ForegroundColorSpan(0x2ADFDFDF);
        public static final ForegroundColorSpan temSubColor = new ForegroundColorSpan(0xFFFFFFFF);
        public static final StyleSpan overbold = new StyleSpan(Typeface.BOLD);

        public UI(Menu menu) {
            super(true, menu, false);
        }

        @Override
        public void notifyData(int index, int content) {
            super.notifyData(index, content);
            if (thermoInfoTexts == null) return;
            switch (index) {
                case 0 -> thermoInfoTexts.get(0).post(() -> thermoInfoTexts.get(0).setText(content + "kJ/t"));
                case 1 ->
                        MUIHelper.digitComplementAndSet(thermoInfoTexts.get(1), 4, "" + content, '0', temComColor, temComColor, overbold);
//                case 2 ->
//                        thermoInfoTexts.get(2).post(() -> thermoInfoTexts.get(2).setText(I18n.get("arkdust.industry.arkdust.mac.thermo.combustor.info.time") + ":" + (content / 20) + "s"));
////                case 3 -> ;
            }
        }

        @Override
        public @NotNull View onCreateView(LayoutInflater inflater, ViewGroup container, DataSet savedInstanceState) {
            //基本数据预备
            ViewGroup group = (ViewGroup) super.onCreateView(inflater, container, savedInstanceState);
            defaultIndsGroup.renderNodeA = 0;
            defaultIndsGroup.renderNodeB = 0.1F;
            defaultIndsGroup.bottomBarHeight = 25;
            RelativeLayout.LayoutParams childLayoutParaFixer = (RelativeLayout.LayoutParams) defaultIndsGroup.child.getLayoutParams();
            childLayoutParaFixer.leftMargin += group.dp(IndsGroup.TOP_H * 0.75F);
            childLayoutParaFixer.bottomMargin += group.dp(25);
            ThermoCombustorBlockEntity entity = ((Menu) menu).belonging;
            List<TextView> infoTexts = new ArrayList<>();

            //左侧数值部分内容
            LinearLayout leftInfoBoard = new LinearLayout(getContext());
            leftInfoBoard.setOrientation(LinearLayout.VERTICAL);
            leftInfoBoard.setGravity(Gravity.RIGHT);
            leftInfoBoard.setBackground(MUIHelper.withBorder());
            RelativeLayout.LayoutParams leftInfoBoardPara = new RelativeLayout.LayoutParams(-2, -2);
            leftInfoBoardPara.setMargins(group.dp(30), group.dp(15), 0, group.dp(15));
            leftInfoBoardPara.addRule(RelativeLayout.CENTER_VERTICAL);
            defaultIndsGroup.child.addView(leftInfoBoard, leftInfoBoardPara);
            {
                //效率显示组件
                TextView efficiencyTextView = new TextView(getContext());
                efficiencyTextView.setText("Effi = " + entity.outputEffi);
                efficiencyTextView.setTextColor(0x66FFFFFF);
                efficiencyTextView.setTextSize(12);
                efficiencyTextView.setBackground(MUIHelper.withBorder());
                infoTexts.add(efficiencyTextView);
                LinearLayout.LayoutParams efficiencyTextViewPara = new LinearLayout.LayoutParams(-2, -2);
                efficiencyTextViewPara.setMargins(0, 0, 0, group.dp(30));
                leftInfoBoard.addView(efficiencyTextView, efficiencyTextViewPara);

                //温度显示组件
                LinearLayout temperatureInfoGroup = new LinearLayout(getContext());
                temperatureInfoGroup.setGravity(Gravity.LEFT);
                temperatureInfoGroup.setOrientation(LinearLayout.VERTICAL);
                temperatureInfoGroup.setBackground(MUIHelper.withBorder());
                LinearLayout.LayoutParams temperatureInfoGroupPara = new LinearLayout.LayoutParams(-2, -2);
                temperatureInfoGroupPara.gravity = Gravity.LEFT;
                temperatureInfoGroupPara.setMargins(0, 0, group.dp(20), group.dp(30));
                leftInfoBoard.addView(temperatureInfoGroup, temperatureInfoGroupPara);
                {
                    ImageView thermoDec = new ImageView(getContext());
                    thermoDec.setImage(ResourceQuote$Thermo.THERMOS_DEC);
                    thermoDec.setAlpha(0.6F);
                    temperatureInfoGroup.addView(thermoDec, new LinearLayout.LayoutParams(group.dp(58), group.dp(10)));
                    TextView titleText = new TextView(getContext());
                    titleText.setText(I18n.get("arkdust.industry.arkdust.mac.thermo.combustor.info.temp"));
                    titleText.setTextSize(12);
                    titleText.setTextColor(SECONDARY_COLOR);
                    temperatureInfoGroup.addView(titleText);

                    RelativeLayout currentTempera = new RelativeLayout(getContext());
                    currentTempera.setId(210110);
                    currentTempera.setBackground(MUIHelper.withBorder());
                    temperatureInfoGroup.addView(currentTempera, new LinearLayout.LayoutParams(group.dp(108), -2));
                    {
                        View decorator = new View(getContext()) {
                            private final Paint RETAIN = new Paint();

                            {
                                RETAIN.setStroke(true);
                                RETAIN.setColor(SECONDARY_COLOR);
                                RETAIN.setStrokeWidth(dp(1.5F));
                            }

                            public void onDraw(Canvas canva) {
                                canva.drawRect(1, 1, getWidth() - 1, getHeight() - 1, RETAIN);
                            }
                        };
                        RelativeLayout.LayoutParams decoratorPara = new RelativeLayout.LayoutParams(group.dp(8), group.dp(8));
                        decoratorPara.setMargins(group.dp(4), group.dp(8), group.dp(4), group.dp(8));
                        decoratorPara.addRule(RelativeLayout.ALIGN_BOTTOM, 210112);
                        currentTempera.addView(decorator, decoratorPara);

                        TextView unit = new TextView(getContext());
                        unit.setId(210111);
                        unit.setTextColor(SECONDARY_COLOR);
                        unit.setTextSize(15);
                        unit.setText("°C");
                        RelativeLayout.LayoutParams unitPara = new RelativeLayout.LayoutParams(-2, -2);
                        unitPara.setMargins(group.dp(2),0,0,group.dp(4));
                        unitPara.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                        unitPara.addRule(RelativeLayout.ALIGN_BOTTOM, 210112);
                        currentTempera.addView(unit, unitPara);

                        TextView temperature = new TextView(getContext());
                        temperature.setId(210112);
                        temperature.setTextSize(30);
                        temperature.setTextColor(0xFFFFFFFF);
                        MUIHelper.digitComplementAndSet(temperature, 4, "" + (int) entity.getT(), '0', temComColor, temSubColor, overbold);
                        infoTexts.add(temperature);
                        RelativeLayout.LayoutParams temperaturePara = new RelativeLayout.LayoutParams(-2, -2);
                        temperaturePara.addRule(RelativeLayout.LEFT_OF, 210111);
                        temperaturePara.addRule(RelativeLayout.CENTER_VERTICAL);
//                        temperaturePara.setMarginsRelative(0,0,0,group.dp(8));
                        currentTempera.addView(temperature, temperaturePara);
                    }

                }
            }


            //添加玩家物品栏
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(-2, -2);
            params.addRule(RelativeLayout.BELOW, 200000);
            params.addRule(RelativeLayout.CENTER_HORIZONTAL);
            params.setMarginsRelative(0, group.dp(50), 0, 0);
            group.addView(new Inventory(((Menu) menu).invStartIndex, 16), params);

            thermoInfoTexts = infoTexts;
            return group;
        }
    }
}
