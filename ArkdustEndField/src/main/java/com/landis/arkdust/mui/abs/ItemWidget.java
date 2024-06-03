package com.landis.arkdust.mui.abs;

import com.landis.arkdust.Arkdust;
import com.landis.arkdust.helper.MUIHelper;
import icyllis.modernui.animation.ObjectAnimator;
import icyllis.modernui.core.Context;
import icyllis.modernui.graphics.*;
import icyllis.modernui.util.FloatProperty;
import icyllis.modernui.view.Gravity;
import icyllis.modernui.view.MotionEvent;
import icyllis.modernui.widget.ImageView;
import icyllis.modernui.widget.RelativeLayout;
import icyllis.modernui.widget.TextView;
import net.minecraft.client.Minecraft;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Range;

import javax.annotation.Nonnull;


public abstract class ItemWidget extends RelativeLayout {
    public static final Image SLOT_BACKGROUND = Image.create(Arkdust.MODID, "gui/slot_bg.png");
    public static final Image SLOT_HOVER = Image.create(Arkdust.MODID, "gui/slot_hover.png");


    public final Slot slot;
    public final float width;
    public final AbstractContainerMenu menu;


    protected final TextView text;

    protected ImageView hoverImage;

    public ItemWidget(Context context, Slot slot, AbstractContainerMenu menu) {
        this(context, slot, 16,menu);
    }

    public ItemWidget(Context context, Slot slot, float width, AbstractContainerMenu menu) {
        super(context);
        this.slot = slot;
        this.width = width;
        this.menu = menu;
        TextView textCache = new TextView(getContext());
        LayoutParams params = configureText(textCache);
        if (params != null) {
            this.addView(textCache, params);
            this.text = textCache;
        } else {
            this.text = null;
        }
        setClickable(true);
        if (this.text != null) {
            refresh();
        }
//        this.setBackground(MUIHelper.withBorder());//test
    }

    public @Nullable
    abstract LayoutParams configureText(TextView text);

    public RelativeLayout.LayoutParams defaultPara() {
        return new RelativeLayout.LayoutParams(dp(2 * width), dp(2 * width));
    }

    public void refresh() {
        if (text != null) {
            if (slot.hasItem() && slot.getItem().getMaxStackSize() != 1) {
                text.setText("" + slot.getItem().getCount());
            } else {
                text.setText("");
            }
            invalidate();
            text.invalidate();
        }
    }

    protected void attachMouseEventListener(){
//        setOnTouchListener((v,event)->{
//
//        });
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int actuallyLos = Math.min(getWidth(), getHeight());
        float xAmend = (getWidth() - actuallyLos) / 2F;
        float yAmend = (getHeight() - actuallyLos) / 2F;

        drawContext(actuallyLos, xAmend, yAmend, canvas);
    }

    public abstract void drawContext(int actuallyLos, float xAmend, float yAmend, Canvas canvas);

    protected void onClick(int mouseButton, int typeIndex){
        ClickType type = null;//TODOc
        menu.clicked(slot.index,mouseButton,type, Minecraft.getInstance().player);
    }

//    public abstract @Nonnull ItemStack getFloating();
//    public abstract void setFloating();
}
