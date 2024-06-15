package com.landis.arkdust.mui.abs;

import com.landis.arkdust.Arkdust;
import com.landis.arkdust.mui.mouse.BaseMouseInfo;
import com.landis.arkdust.network.SynMenuSlotClick;
import com.landis.breakdowncore.helper.ContainerHelper;
import icyllis.modernui.core.Context;
import icyllis.modernui.graphics.Canvas;
import icyllis.modernui.graphics.Image;
import icyllis.modernui.view.MotionEvent;
import icyllis.modernui.view.ViewGroup;
import icyllis.modernui.widget.ImageView;
import icyllis.modernui.widget.RelativeLayout;
import icyllis.modernui.widget.TextView;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;


public abstract class ItemWidget extends RelativeLayout {
    public static final Image SLOT_BACKGROUND = Image.create(Arkdust.MODID, "gui/slots/background.png");
    public static final Image SLOT_HOVER = Image.create(Arkdust.MODID, "gui/slots/hovering.png");
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
        setOnTouchListener((v, e) -> {
            onClick(e);
            return false;
        });
//        this.setBackground(MUIHelper.withBorder());//test
    }

    public @Nullable
    abstract LayoutParams configureText(TextView text);

    public ViewGroup.LayoutParams defaultPara() {
        return new ViewGroup.LayoutParams(dp(2 * width), dp(2 * width));
    }

    public void refresh() {
        post(()->{
            if (text != null) {
                if (slot.hasItem() && slot.getItem().getMaxStackSize() != 1) {
                    text.setText("" + slot.getItem().getCount());
                } else {
                    text.setText("");
                }
                invalidate();
                text.invalidate();
            }
        });
    }


    //---[Render Part 渲染部分]---

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int actuallyLos = Math.min(getWidth(), getHeight());
        float xAmend = (getWidth() - actuallyLos) / 2F;
        float yAmend = (getHeight() - actuallyLos) / 2F;

        drawContext(actuallyLos, xAmend, yAmend, canvas);
    }


    public abstract void drawContext(int actuallyLos, float x0, float y0, Canvas canvas);


    //---[Interaction Part 交互部分]---

    protected void onClick(MotionEvent event) {
        int actionIndex = (Screen.hasShiftDown() ? -1 : 1) * event.getActionButton();
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            SynMenuSlotClick.send(menu.getType(), slot.index, actionIndex);
        }

//        ContainerHelper.handleSlotClick(actionIndex, slot.index, Minecraft.getInstance().player, menu, true);
//        this.refresh();
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


}
