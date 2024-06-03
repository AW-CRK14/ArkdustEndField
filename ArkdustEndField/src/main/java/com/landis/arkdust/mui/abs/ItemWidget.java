package com.landis.arkdust.mui.abs;

import com.landis.arkdust.Arkdust;
import com.landis.arkdust.helper.MUIHelper;
import icyllis.modernui.animation.ObjectAnimator;
import icyllis.modernui.core.Context;
import icyllis.modernui.graphics.*;
import icyllis.modernui.graphics.Canvas;
import icyllis.modernui.graphics.Image;
import icyllis.modernui.util.FloatProperty;
import icyllis.modernui.view.Gravity;
import icyllis.modernui.view.MotionEvent;
import icyllis.modernui.widget.ImageView;
import icyllis.modernui.widget.RelativeLayout;
import icyllis.modernui.widget.TextView;
import net.minecraft.client.Minecraft;
import net.minecraft.client.MouseHandler;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.player.Input;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Range;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWMouseButtonCallback;

import javax.annotation.Nonnull;
import java.awt.*;
import java.awt.event.InputEvent;


public abstract class ItemWidget extends RelativeLayout {
    public static final Image SLOT_BACKGROUND = Image.create(Arkdust.MODID, "gui/slot_bg.png");
    public static final Image SLOT_HOVER = Image.create(Arkdust.MODID, "gui/slot_hover.png");


    public final Slot slot;
    public final float width;
    public final AbstractContainerMenu menu;


    protected final TextView text;

    protected ImageView hoverImage;

    public ItemWidget(Context context, Slot slot, AbstractContainerMenu menu) {
        this(context, slot, 16, menu);
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
        setOnClickListener(v -> ((ItemWidget)v).onClick());
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

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int actuallyLos = Math.min(getWidth(), getHeight());
        float xAmend = (getWidth() - actuallyLos) / 2F;
        float yAmend = (getHeight() - actuallyLos) / 2F;

        drawContext(actuallyLos, xAmend, yAmend, canvas);
    }

    public abstract void drawContext(int actuallyLos, float xAmend, float yAmend, Canvas canvas);

    protected void onClick() {
        Player player = Minecraft.getInstance().player;
        if(Screen.hasShiftDown()){
            menu.quickMoveStack(player,slot.index);
        }else {//非快速移动

            MouseHandler mouseHandler = Minecraft.getInstance().mouseHandler;
            ItemStack floating = getFloating();
            ItemStack item = slot.getItem();
            if (floating.isEmpty()) {//无悬浮物品
                int count = slot.getItem().getCount();
                if(item.isEmpty() || !slot.isActive() || !slot.mayPickup(player)){

                }else if (mouseHandler.isLeftPressed()) {//左键提取
                    floating = slot.safeTake(count, slot.getItem().getMaxStackSize(), player);
                } else if (mouseHandler.isRightPressed()) {//右键提取
                    floating = slot.safeTake(count, (count + 1) / 2, player);
                }
            } else {//有悬浮物品
                if(slot.isActive()){
                    if(slot.mayPlace(floating)){//如果允许放入
                        if(mouseHandler.isLeftPressed()){//左键全部放入
                            floating = slot.safeInsert(floating);
                        }else if (mouseHandler.isRightPressed()){//右键放入一个
                            floating = slot.safeInsert(floating,1);
                        }
                    }else if(ItemStack.isSameItemSameTags(floating,item) && slot.mayPickup(player)){//在不允许放入时，尝试取出
                        floating.grow(slot.safeTake(slot.getItem().getCount(),floating.getMaxStackSize() - floating.getCount(),player).getCount());
                    }
                }
            }
            setFloating(floating);
        }
    }

    /**
     * 预留方法<br>
     * 可以在onHoverEvent中选择性调用
     */
    protected boolean handleShiftSlideQuickMove() {
        if (Screen.hasShiftDown()) {
            Player player = Minecraft.getInstance().player;
            if (slot.isActive() && slot.allowModification(player)) {
                menu.quickMoveStack(player, slot.index);
                return true;
            }
        }
        return false;
    }

    public abstract @Nonnull ItemStack getFloating();
    public abstract void setFloating(ItemStack stack);
}
