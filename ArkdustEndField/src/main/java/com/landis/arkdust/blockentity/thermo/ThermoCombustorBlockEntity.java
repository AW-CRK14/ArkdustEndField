package com.landis.arkdust.blockentity.thermo;

import com.landis.arkdust.blocks.levelblocks.ThermoBlocks;
import com.landis.arkdust.helper.MUIHelper;
import com.landis.arkdust.mui.AbstractArkdustIndustContainerUI;
import com.landis.arkdust.mui.MUIRelativeMenu;
import com.landis.arkdust.mui.MUIResourceQuote;
import com.landis.arkdust.mui.widget.item.FactoryDecoratedItemViewBeta;
import com.landis.arkdust.mui.widget.viewgroup.IndsGroup;
import com.landis.arkdust.registry.BlockEntityRegistry;
import com.landis.arkdust.registry.MenuTypeRegistry;
import com.landis.breakdowncore.BreaRegistries;
import com.landis.breakdowncore.module.blockentity.container.*;
import com.landis.breakdowncore.module.render.color.GradientColors;
import com.landis.breakdowncore.system.material.ITypedMaterialObj;
import com.landis.breakdowncore.system.material.System$Material;
import com.landis.breakdowncore.system.thermodynamics.ThermoBlockEntity;
import icyllis.modernui.animation.TimeInterpolator;
import icyllis.modernui.animation.ValueAnimator;
import icyllis.modernui.fragment.Fragment;
import icyllis.modernui.graphics.Canvas;
import icyllis.modernui.graphics.Paint;
import icyllis.modernui.mc.neoforge.MenuScreenFactory;
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
import it.unimi.dsi.fastutil.floats.FloatArrayList;
import it.unimi.dsi.fastutil.floats.FloatList;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
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
import org.joml.Math;

import java.util.ArrayList;
import java.util.List;

public class ThermoCombustorBlockEntity extends ThermoBlockEntity implements IWrappedMenuProvider, MenuScreenFactory<ThermoCombustorBlockEntity.Menu> {

    public final int basicOutputEffi;
    public final float basicConversionRate;

    public ThermoCombustorBlockEntity(BlockPos pPos, BlockState pBlockState) {
        this(pPos, pBlockState, ((ThermoBlocks.CombustorBlock) pBlockState.getBlock()).basicOutputEffi, ((ThermoBlocks.CombustorBlock) pBlockState.getBlock()).basicConversionRate);
    }

    public ThermoCombustorBlockEntity(BlockPos pPos, BlockState pBlockState, int basicOutputEffi, float basicConversionRate) {
        super(BlockEntityRegistry.THERMO_COMBUSTOR.get(), pPos, pBlockState, BreaRegistries.MaterialReg.IRON.get());
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
                addSlot(new FixedSlot(container, i, 0, 0) {
                    @Override
                    public boolean mayPlace(ItemStack pStack) {
                        ITypedMaterialObj materialObj = System$Material.getMaterialInfo(pStack.getItem());
                        return materialObj != null && materialObj.getMaterialOrMissing(pStack).getFeature(BreaRegistries.MaterialReg.COMBUSTIBLE.get()) != null;
                    }
                }, SlotType.INPUT);
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
                    case 0 -> (int) (belonging.conversionRate * 1000);
                    case 1 -> (int) belonging.getT();
                    case 2 -> belonging.maxT();
                    case 3 -> belonging.outputEffi;
                    case 4 -> belonging.remainTime;
                    default -> -1;
                };
            }

            @Override
            public void set(int pIndex, int pValue) {
                switch (pIndex) {
                    case 0 -> belonging.conversionRate = pValue / 1000F;
                    case 1 -> belonging.clientCachedT = pValue;
                    case 2 -> belonging.clientCachedTMax = pValue;
                    case 3 -> belonging.outputEffi = pValue;
                    case 4 -> belonging.remainTime = pValue;
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
        public static final int SECONDARY_COLOR_B = 0x74F3F3FF;
        protected List<TextView> thermoInfoTexts;
        protected ThermoCircleImageView thermoCircle;

        public static final ForegroundColorSpan temComColor = new ForegroundColorSpan(0x2ADFDFE7);
        public static final ForegroundColorSpan temSubColor = new ForegroundColorSpan(0xFFFFFFFF);
        public static final StyleSpan overbold = new StyleSpan(Typeface.BOLD);

        public UI(Menu menu) {
            super(true, menu, false);
        }


        private int hoverT;
        private ValueAnimator temChangeAnimator;

        private ValueAnimator decorateTemChangeAnimator(int end) {
            return decorateTemChangeAnimator(end, thermoInfoTexts.get(1));
        }

        private ValueAnimator decorateTemChangeAnimator(int end, TextView obj) {
            ValueAnimator temChangeAnimator = ValueAnimator.ofInt(hoverT, end).setDuration(750);
            temChangeAnimator.setInterpolator(TimeInterpolator.DECELERATE);
            temChangeAnimator.addUpdateListener(i -> {
                hoverT = (int) i.getAnimatedValue();
                MUIHelper.digitComplementAndSet(obj, 4, "" + hoverT, '0', temComColor, temSubColor, overbold);
            });
            return temChangeAnimator;
        }

        @Override
        public void notifyData(int index, int content) {
            super.notifyData(index, content);
            if (thermoInfoTexts == null) return;
            TextView obj = index == 4 ? null : thermoInfoTexts.get(index);
            switch (index) {
                case 0 -> obj.post(() -> obj.setText(content / 100F + "%"));
                case 1 -> {
                    if (temChangeAnimator.isRunning()) {
                        temChangeAnimator.pause();
                    }
                    temChangeAnimator = decorateTemChangeAnimator(content);
                    obj.post(() -> temChangeAnimator.start());
                }
                case 2 -> obj.post(() -> obj.setText("/" + content));
                case 3 -> obj.post(() -> obj.setText(content + " kJ/t"));
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

            //右侧发热器内容
            RelativeLayout rightPart = new RelativeLayout(getContext());
            RelativeLayout.LayoutParams rightPartPara = new RelativeLayout.LayoutParams(group.dp(180), group.dp(180));
            rightPartPara.setMargins(group.dp(130), group.dp(30), group.dp(30), group.dp(30));
            defaultIndsGroup.child.addView(rightPart, rightPartPara);
            {
                ImageView backgroundThermoCircle = new ImageView(getContext());
                backgroundThermoCircle.setImage(ResourceQuote$Thermo.THERMO_CIR);
                backgroundThermoCircle.setAlpha(0.3F);
                rightPart.addView(backgroundThermoCircle, new RelativeLayout.LayoutParams(-1, -1));

                //背景图片显示
                thermoCircle = new ThermoCircleImageView();
                rightPart.addView(thermoCircle, new RelativeLayout.LayoutParams(-1, -1));

                FactoryDecoratedItemViewBeta item = new FactoryDecoratedItemViewBeta(getContext(), menu.getSlot(0), 24, menu);
                inventoryItemWidgets.set(0, item);
                RelativeLayout.LayoutParams itemPara = new RelativeLayout.LayoutParams(item.defaultPara());
                itemPara.addRule(RelativeLayout.CENTER_IN_PARENT);
                rightPart.addView(item, itemPara);

            }


            //左侧数值部分内容
            LinearLayout leftInfoBoard = new LinearLayout(getContext());
            leftInfoBoard.setOrientation(LinearLayout.VERTICAL);
            leftInfoBoard.setGravity(Gravity.RIGHT);
            RelativeLayout.LayoutParams leftInfoBoardPara = new RelativeLayout.LayoutParams(-2, -2);
            leftInfoBoardPara.setMargins(group.dp(30), group.dp(15), 0, group.dp(15));
            leftInfoBoardPara.addRule(RelativeLayout.CENTER_VERTICAL);
            defaultIndsGroup.child.addView(leftInfoBoard, leftInfoBoardPara);
            {
                //转化率显示组件
                LinearLayout conversionRateGroup = new LinearLayout(getContext());
                conversionRateGroup.setOrientation(LinearLayout.VERTICAL);
                conversionRateGroup.setHorizontalGravity(Gravity.RIGHT);
                leftInfoBoard.addView(conversionRateGroup, new LinearLayout.LayoutParams(-2, -2));
                {
                    TextView titleTextView = new TextView(getContext());
                    titleTextView.setText("Conversion Rate");
                    titleTextView.setTextColor(SECONDARY_COLOR_B);
                    titleTextView.setTextSize(5);
                    LinearLayout.LayoutParams titleTextViewPara = new LinearLayout.LayoutParams(-2, -2);
                    titleTextViewPara.gravity = Gravity.RIGHT;
                    conversionRateGroup.addView(titleTextView, titleTextViewPara);

                    TextView valueTextView = new TextView(getContext());
                    valueTextView.setText(entity.conversionRate * 100 + "%");
                    valueTextView.setTextColor(SECONDARY_COLOR);
                    valueTextView.setTextSize(12);
                    valueTextView.setTypeface(MUIResourceQuote.RAJDHANI);
                    infoTexts.add(valueTextView);
                    LinearLayout.LayoutParams valueTextViewPara = new LinearLayout.LayoutParams(-2, -2);
                    valueTextViewPara.gravity = Gravity.RIGHT;
                    conversionRateGroup.addView(valueTextView, valueTextViewPara);
                }

                //温度显示组件
                LinearLayout temperatureInfoGroup = new LinearLayout(getContext());
                temperatureInfoGroup.setGravity(Gravity.LEFT);
                temperatureInfoGroup.setOrientation(LinearLayout.VERTICAL);
                LinearLayout.LayoutParams temperatureInfoGroupPara = new LinearLayout.LayoutParams(-2, -2);
                temperatureInfoGroupPara.gravity = Gravity.LEFT;
                temperatureInfoGroupPara.setMargins(0, 0, group.dp(40), group.dp(15));
                leftInfoBoard.addView(temperatureInfoGroup, temperatureInfoGroupPara);
                {
                    ImageView thermoDec = new ImageView(getContext());
                    thermoDec.setId(210101);
                    thermoDec.setImage(ResourceQuote$Thermo.THERMO_DEC);
                    thermoDec.setAlpha(0.6F);
                    temperatureInfoGroup.addView(thermoDec, new LinearLayout.LayoutParams(group.dp(58), group.dp(10)));

                    TextView titleText = new TextView(getContext());
                    titleText.setText(I18n.get("arkdust.industry.arkdust.mac.thermo.combustor.info.temp"));
                    titleText.setTextSize(12);
                    titleText.setTextColor(SECONDARY_COLOR);
                    temperatureInfoGroup.addView(titleText);

                    RelativeLayout currentTempera = new RelativeLayout(getContext());
                    currentTempera.setId(210110);
                    LinearLayout.LayoutParams currentTemperaPara = new LinearLayout.LayoutParams(group.dp(93), -2);
                    temperatureInfoGroup.addView(currentTempera, currentTemperaPara);
                    {
                        View decorator = new View(getContext()) {
                            private final Paint RETAIN = new Paint();

                            {
                                RETAIN.setStroke(true);
                                RETAIN.setColor(SECONDARY_COLOR_B);
                                RETAIN.setStrokeWidth(dp(1.5F));
                            }

                            public void onDraw(Canvas canva) {
                                canva.drawRect(1, 1, getWidth() - 1, getHeight() - 1, RETAIN);
                            }
                        };
                        RelativeLayout.LayoutParams decoratorPara = new RelativeLayout.LayoutParams(group.dp(8), group.dp(8));
                        decoratorPara.setMargins(group.dp(4), 0, group.dp(4), group.dp(10));
                        decoratorPara.addRule(RelativeLayout.ALIGN_BOTTOM, 210112);
                        currentTempera.addView(decorator, decoratorPara);

                        TextView unit = new TextView(getContext());
                        unit.setId(210111);
                        unit.setTextColor(SECONDARY_COLOR_B);
                        unit.setTextSize(12);
                        unit.setText("°C");
                        RelativeLayout.LayoutParams unitPara = new RelativeLayout.LayoutParams(-2, -2);
                        unitPara.setMargins(group.dp(2), 0, 0, group.dp(4));
                        unitPara.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                        unitPara.addRule(RelativeLayout.ALIGN_BOTTOM, 210112);
                        currentTempera.addView(unit, unitPara);

                        TextView temperature = new TextView(getContext());
                        temperature.setId(210112);
                        temperature.setTextSize(30);
                        temperature.setTextColor(0xFFFFFFFF);
                        MUIHelper.digitComplementAndSet(temperature, 4, "0", '0', temComColor, temSubColor, overbold);
                        temChangeAnimator = decorateTemChangeAnimator(((Menu) menu).belonging.clientCachedT, temperature);
                        temChangeAnimator.start();
                        temperature.setTypeface(MUIResourceQuote.RAJDHANI);
                        infoTexts.add(temperature);
                        RelativeLayout.LayoutParams temperaturePara = new RelativeLayout.LayoutParams(-2, -2);
                        temperaturePara.addRule(RelativeLayout.LEFT_OF, 210111);
                        currentTempera.addView(temperature, temperaturePara);
                    }
                    TextView maxTemperatureText = new TextView(getContext());
                    maxTemperatureText.setTextSize(20);
                    maxTemperatureText.setTextColor(SECONDARY_COLOR);
                    maxTemperatureText.setText("/" + entity.clientCachedTMax);
                    maxTemperatureText.setTypeface(MUIResourceQuote.RAJDHANI);
                    infoTexts.add(maxTemperatureText);
                    LinearLayout.LayoutParams maxTemperatureTextPara = new LinearLayout.LayoutParams(-2, -2);
                    maxTemperatureTextPara.gravity = Gravity.RIGHT;
                    temperatureInfoGroup.addView(maxTemperatureText, maxTemperatureTextPara);
                }

                //效率显示组件
                LinearLayout efficiencyGroup = new LinearLayout(getContext());
                efficiencyGroup.setOrientation(LinearLayout.VERTICAL);
                efficiencyGroup.setHorizontalGravity(Gravity.RIGHT);
                LinearLayout.LayoutParams efficiencyGroupPara = new LinearLayout.LayoutParams(-2, -2);
                efficiencyGroupPara.setMargins(0, 0, 0, group.dp(5));
                leftInfoBoard.addView(efficiencyGroup, efficiencyGroupPara);
                {
                    TextView titleTextView = new TextView(getContext());
                    titleTextView.setText("Output Effi");
                    titleTextView.setTextColor(SECONDARY_COLOR_B);
                    titleTextView.setTextSize(5);
                    LinearLayout.LayoutParams titleTextViewPara = new LinearLayout.LayoutParams(-2, -2);
                    titleTextViewPara.gravity = Gravity.RIGHT;
                    efficiencyGroup.addView(titleTextView, titleTextViewPara);

                    TextView valueTextView = new TextView(getContext());
                    valueTextView.setText(entity.outputEffi + " kJ/t");
                    valueTextView.setTextColor(SECONDARY_COLOR);
                    valueTextView.setTextSize(12);
                    valueTextView.setTypeface(MUIResourceQuote.RAJDHANI);
                    infoTexts.add(valueTextView);
                    LinearLayout.LayoutParams valueTextViewPara = new LinearLayout.LayoutParams(-2, -2);
                    valueTextViewPara.gravity = Gravity.RIGHT;
                    efficiencyGroup.addView(valueTextView, valueTextViewPara);
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

        protected class ThermoCircleImageView extends ImageView {
            public static final GradientColors THERMO_CIRCLE_COLOR = new GradientColors(0x1E281F20, 0xFFFFF8E0)
                    .addKeyPoint(0xFF30130E, 0.1F)
                    .addKeyPoint(0xFFB21B18, 0.25F)
                    .addKeyPoint(0xFFF2382B, 0.45F)
                    .addKeyPoint(0xFFFF8454, 0.85F);

            public final Paint RETAIN = new Paint();
            public final Paint PB = new Paint();

            private float breathCycle = 0;

            public ThermoCircleImageView() {
                super(UI.this.getContext());
            }

            private int counter = 99999;
            private final IntList circles = new IntArrayList(4);
            private final FloatList colors = new FloatArrayList(4);

            public void animaTick() {
                if (counter >= 60 - 25 * Math.clamp(1600, 300, hoverT) / 1300F) {
                    counter = 0;
                    circles.add(0);
                    colors.add(Math.clamp(0, 1, (hoverT + 400) / 900F));
                }
                counter++;
                if (circles.getInt(0) >= 120) {
                    circles.removeInt(0);
                    colors.removeFloat(0);
                }
                for (int i = 0; i < circles.size(); i++) {
                    circles.set(i, circles.getInt(i) + 1);
                }
                invalidate();
            }

            {
                PB.setColor(0x64FFFFFF);
                RETAIN.setStroke(true);

                ValueAnimator breathAnimator = ValueAnimator.ofFloat(0, 1);
                breathAnimator.setDuration(3500); // 设置动画持续时间
                breathAnimator.setRepeatCount(ValueAnimator.INFINITE); // 设置无限重复
                breathAnimator.setRepeatMode(ValueAnimator.RESTART); // 设置无限重复
                breathAnimator.addUpdateListener(animation -> {
                    this.breathCycle = (float) animation.getAnimatedValue();
                    this.animaTick();
                });
                breathAnimator.start();
            }

            @Override
            protected void onDraw(@NotNull Canvas canvas) {
                super.onDraw(canvas);

                if (!circles.isEmpty()) {
                    for (int i = 0; i < circles.size(); i++) {
                        float fallout = circles.getInt(i) / 100F;
                        RETAIN.setColor(THERMO_CIRCLE_COLOR.getColor((1 - fallout) * colors.getFloat(i) + resizeBreath(fallout * 0.3F - 0.15F)));
                        RETAIN.setAlphaF(1.5F - fallout * fallout);
                        RETAIN.setStrokeWidth(dp(fallout <= 0.3F ? 7 * fallout + 0.5F : 3 - 1.75F * fallout));
                        canvas.drawCircle(getWidth() / 2F, getHeight() / 2F, dp(30 + fallout * 25), RETAIN);
                    }
                }


                canvas.drawCircle(getWidth() / 2F, getHeight() / 2F, dp(30), PB);
            }

            private float resizeBreath(float deviation) {
                return -0.075F * Math.sin(5 * (Math.clamp(0.3F, 0.7F, breathCycle - deviation) - 0.3F) * 3.14F);
            }
        }
    }
}
