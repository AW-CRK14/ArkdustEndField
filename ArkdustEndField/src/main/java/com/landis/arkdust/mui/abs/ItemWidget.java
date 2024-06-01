package com.landis.arkdust.mui.abs;

import com.landis.arkdust.Arkdust;
import com.landis.arkdust.helper.MUIHelper;
import icyllis.modernui.core.Context;
import icyllis.modernui.graphics.*;
import icyllis.modernui.view.Gravity;
import icyllis.modernui.widget.RelativeLayout;
import icyllis.modernui.widget.TextView;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public abstract class ItemWidget extends RelativeLayout {
    public static final Image SLOT_BACKGROUND = Image.create(Arkdust.MODID, "gui/slot_bg.png");


    public final Slot slot;
    public final float width;


    protected final TextView text;

    public ItemWidget(Context context, Slot slot) {
        this(context, slot, 16);
    }

    public ItemWidget(Context context, Slot slot, float width) {
        super(context);
//        setX(dp(16 * scale));
//        setY(dp(16 * scale));
        this.slot = slot;
        this.width = width;
        TextView textCache = new TextView(getContext());
        LayoutParams params = configureText(textCache);
        if(params != null) {
            this.addView(textCache, params);
            this.text = textCache;
        }else {
            this.text = null;
        }
        setClickable(true);
        setFocusable(true);
        if (this.text != null) {
            this.text.setBackground(MUIHelper.withBorder());
        }
//        this.setBackground(MUIHelper.withBorder());//test
    }

    public @Nullable abstract LayoutParams configureText(TextView text);

    public RelativeLayout.LayoutParams defaultPara() {
        return new RelativeLayout.LayoutParams(dp(2 * width), dp(2 * width));
    }

    public void refresh() {
        if (slot.hasItem() && slot.getItem().getMaxStackSize() != 1) {
            text.setText("" + slot.getItem().getCount());
        } else {
            text.setText("");
        }
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int actuallyLos = Math.min(getWidth(), getHeight());
//        float xAmend = (getWidth() - actuallyLos) / 2F + getLeft();
//        float yAmend = (getHeight() - actuallyLos) / 2F + getTop();
        float xAmend = (getWidth() - actuallyLos) / 2F;
        float yAmend = (getHeight() - actuallyLos) / 2F;

        drawContext(actuallyLos, xAmend, yAmend, canvas);
//        float itemLos = actuallyLos * 0.75F;
//        //背景渲染
//        canvas.drawImage(Image.create(Arkdust.MODID,"gui/slot.png"),null,new RectF(getLeft() + xAmend,getTop() + yAmend,getRight() - xAmend,getBottom() - yAmend),null);
//        ContainerDrawHelper.drawItem(canvas,slot.getItem(),xAmend + actuallyLos * 0.125F,yAmend,0,itemLos,1713015070);
    }

    public abstract void drawContext(int actuallyLos, float xAmend, float yAmend, Canvas canvas);

    public abstract void drawWhenHovering(int actuallyLos, float xAmend, float yAmend, Canvas canvas, float alpha);
}
