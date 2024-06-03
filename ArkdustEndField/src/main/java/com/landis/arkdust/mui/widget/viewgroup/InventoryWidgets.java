package com.landis.arkdust.mui.widget.viewgroup;

import com.landis.arkdust.Arkdust;
import com.landis.arkdust.mui.abs.ItemWidget;
import com.landis.arkdust.mui.widget.item.ArkdustContainerItemSlot;
import icyllis.modernui.core.Context;
import icyllis.modernui.graphics.Canvas;
import icyllis.modernui.graphics.Paint;
import icyllis.modernui.widget.RelativeLayout;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

//public abstract class InventoryWidgets extends IndsGroup {
public abstract class InventoryWidgets extends IndsGroup {
    public final AbstractContainerMenu menu;
    public final int itemsHeadIndex;
    public final int signLos;
    public final Paint paint = new Paint();

    {
        paint.setColor(0x707070);
        paint.setAlphaF(0.4F);
        paint.setStrokeWidth(dp(1));
    }

    protected List<ItemWidget> items = new ArrayList<>(36);
    protected final RelativeLayout itemsTable;

    public InventoryWidgets(Context context, AbstractContainerMenu menu, int itemsHeadIndex, int signLosOri) {//TODO 更多兼容
        super(context, new ResourceLocation(Arkdust.MODID, "backpack"), -1, false, 0, 0.2F);
        disableLeftDec();
        int signLos = dp(signLosOri);
        this.menu = menu;
        this.itemsHeadIndex = itemsHeadIndex;
        this.signLos = signLos;

        int interval = (int) (signLos * 0.15F);

        int width = 9 * signLos + 8 * interval;
        int height = 4 * signLos + 5 * interval;
        //加载背景


        //加载物品组件
        itemsTable = new RelativeLayout(context) {
            @Override
            protected void onDraw(Canvas canvas) {
                super.onDraw(canvas);
                int y = 6 * InventoryWidgets.this.signLos + 7 * interval;
                canvas.drawLine(3 * interval, y, this.getWidth() - 3 * interval, y, paint);
            }
        };
        for (int y = 0; y < 4; y++) {
            int yPos = (y * (signLos + interval) + (y == 3 ? 2 * interval : 0));
            for (int x = 0; x < 9; x++) {
                int xPos = x * (signLos + interval);
                int index = y == 3 ? itemsHeadIndex + x : itemsHeadIndex + y * 9 + x + 9;
                ItemWidget item = createItemWidget(index, signLosOri);
                RelativeLayout.LayoutParams params = item.defaultPara();
                item.setTranslationX(xPos);
                item.setTranslationY(yPos);
                itemsTable.addView(item, params);
                items.add(item);
            }
        }

        LayoutParams para = new LayoutParams(width * 2, height * 2);
        para.setMarginsRelative(dp(signLos * 1.6F), dp(signLos * 0.3F), dp(signLos * 0.4F), dp(signLos * 0.4F));
        child.addView(itemsTable, para);
//        itemsTable.setBackground(MUIHelper.withBorder());
    }


    public ItemWidget createItemWidget(int index, float los) {
        ItemWidget widget;
        if (los > 0) {
            widget = new SlotView(index, los, menu);
        } else {
            widget = new SlotView(index, menu);
        }
        setItemWidget(widget, index);
        return widget;
    }

    protected abstract void setItemWidget(ItemWidget widget, int index);

    protected abstract void setFloating(ItemStack stack);

    protected abstract ItemStack getFloating();

    protected class SlotView extends ArkdustContainerItemSlot {
        public SlotView(int index, AbstractContainerMenu menu) {
            super(InventoryWidgets.this.getContext(), menu.getSlot(index), menu);
        }

        public SlotView(int index, float width, AbstractContainerMenu menu) {
            super(InventoryWidgets.this.getContext(), menu.getSlot(index), width, menu);
        }

        @NotNull
        @Override
        public ItemStack getFloating() {
            return InventoryWidgets.this.getFloating();
        }

        @Override
        public void setFloating(ItemStack stack) {
            InventoryWidgets.this.setFloating(stack);
        }
    }

//    public abstract ItemStack getFloating();
//
//    public abstract void setFloating(ItemStack stack);

}
