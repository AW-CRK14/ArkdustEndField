package com.landis.arkdust.mui.widget.item;

import com.landis.arkdust.Arkdust;
import com.landis.arkdust.mui.abs.ItemWidget;
import com.landis.breakdowncore.helper.RenderHelper;
import icyllis.modernui.animation.ObjectAnimator;
import icyllis.modernui.animation.TimeInterpolator;
import icyllis.modernui.core.Context;
import icyllis.modernui.graphics.Canvas;
import icyllis.modernui.graphics.Image;
import icyllis.modernui.graphics.Paint;
import icyllis.modernui.graphics.RectF;
import icyllis.modernui.mc.ContainerDrawHelper;
import icyllis.modernui.util.FloatProperty;
import icyllis.modernui.view.Gravity;
import icyllis.modernui.view.MotionEvent;
import icyllis.modernui.widget.RelativeLayout;
import icyllis.modernui.widget.TextView;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class FactoryDecoratedItemViewBeta extends FactoryDecoratedItemViewAlpha {
    public static final Image FOREGROUND = Image.create(Arkdust.MODID, "gui/slots/factory_decorated_beta.png");

    protected TextView countHoverStroke;
    protected TextView nameHover;
    protected TextView nameHoverStroke;

    private float animatorFlag = 0;
    protected ObjectAnimator animator = ObjectAnimator.ofFloat(this, ANIMATOR_PROPERTY, 0, 1).setDuration(200);

    {
        PAINT.setColor(COLOR_DAMAGED);
        IMAGE_RETAIN.setColor(0xFFE2E2EC);
        IMAGE_RETAIN.setStrokeWidth(dp(1));
        IMAGE_RETAIN.setStroke(true);

        animator.setInterpolator(TimeInterpolator.ACCELERATE_DECELERATE);
    }


    public FactoryDecoratedItemViewBeta(Context context, Slot slot, AbstractContainerMenu menu) {
        super(context, slot, menu);
        init();
    }

    public FactoryDecoratedItemViewBeta(Context context, Slot slot, float width, AbstractContainerMenu menu) {
        super(context, slot, width, menu);
        init();
    }

    protected void init() {
        LayoutParams countHoverPara = (LayoutParams) text.getLayoutParams();
        countHoverPara.setMargins(0, 0, 0, dp(0.5F));

        countHoverStroke = new TextView(getContext());
        LayoutParams countHoverStrokePara = configureText(countHoverStroke);
        countHoverStrokePara.setMargins(0, 0, 0, 0);
        countHoverStroke.setTextColor(COLOR_DAMAGE_BAR);
        countHoverStroke.setTextSize(0.4F * width + 1);
        this.addView(countHoverStroke, countHoverStrokePara);

        nameHover = new TextView(getContext());
        nameHover.setAlpha(0);
        LayoutParams nameHoverPara = configureText(countHoverStroke);
        nameHoverPara.setMargins(0, 0, 0, dp(0.5F));
        this.addView(nameHover, nameHoverPara);

        nameHoverStroke = new TextView(getContext());
        nameHoverStroke.setAlpha(0);
        LayoutParams nameHoverStrokePara = configureText(nameHoverStroke);
        nameHoverStrokePara.setMargins(0, 0, 0, 0);
        nameHoverStroke.setTextColor(COLOR_DAMAGE_BAR);
        nameHoverStroke.setTextSize(0.4F * width + 1);
        this.addView(nameHoverStroke, nameHoverStrokePara);

        setFocusable(true);
        setOnHoverListener((v, t) -> {
            switch (t.getAction()) {
                case MotionEvent.ACTION_HOVER_ENTER -> animator.start();
                case MotionEvent.ACTION_HOVER_EXIT -> animator.reverse();
            }
            return false;
        });

        refresh();
    }

    @Override
    public void refresh() {
        if (countHoverStroke != null) {
            super.refresh();
            ItemStack itemStack = slot.getItem();
            post(() -> {
                countHoverStroke.setText(itemStack.getCount() == 0 ? "" : itemStack.getCount() + "");
                nameHover.setText(itemStack.isEmpty() ? "" : itemStack.getDisplayName().getString());
                nameHoverStroke.setText(itemStack.isEmpty() ? "" : itemStack.getDisplayName().getString());
            });
        }
    }

    @Override
    public void drawContext(int actuallyLos, float x0, float y0, Canvas canvas) {
        ItemStack stack = slot.getItem();
        float itemLos = actuallyLos * 0.7F;
        float y1 = y0 + actuallyLos;
        float x1 = x0 + actuallyLos;
        if (stack.isEmpty()) {
            canvas.drawImage(SLOT_BACKGROUND, null, new RectF(x0, y0, x1, y1), null);
        } else {
            //背景渲染
            canvas.drawImage(FOREGROUND, null, new RectF(x0, y0, x1, y1), null);
            //物品渲染
            ContainerDrawHelper.drawItem(canvas, stack, x0 + actuallyLos * 0.5F, y0 + 0.5F * actuallyLos, 0, itemLos, 1713015070);
            //耐久条渲染
            if (stack.isBarVisible()) {
                float f = (float) stack.getDamageValue() / stack.getMaxDamage();
                PAINT.setColor(0xFF6E6E6E);
                canvas.drawRect(x0 + (1 - animatorFlag) * (1 - f) * actuallyLos, y1 - actuallyLos / 7.5F, x1, y1 - actuallyLos / 30F, PAINT);
            }
            //色条渲染
            int color = 0xFF000000 | RenderHelper.itemStackDisplayNameColor(stack);
            canvas.save();
            canvas.clipRect(x0, y0 + actuallyLos * 0.95F, x1, y1);
            this.PAINT.setColor(color);
            canvas.drawRoundRect(x0, y1 + actuallyLos / 15F, x1, y1, actuallyLos / 30F, Gravity.BOTTOM, PAINT);
            canvas.restore();

            if (color != 0xFFFFFFFF) {
                int cb = color & 0x8FFFFFFF;
                int ct = color & 0x00FFFFFF;
                canvas.drawRectGradient(x0, y0 + 0.4F * actuallyLos, x1, y1 - actuallyLos / 7.5F, ct, ct, ct, ct, PAINT);
            }
        }

        if (animatorFlag > 0) {
            canvas.save();
            canvas.clipRect(x0, y0, x1, y0 + actuallyLos * animatorFlag);
            canvas.drawRoundRect(x0, y0, x1, y1, actuallyLos / 30F, IMAGE_RETAIN);
            canvas.restore();
        }

    }

    protected static final FloatProperty<FactoryDecoratedItemViewBeta> ANIMATOR_PROPERTY = new FloatProperty<FactoryDecoratedItemViewBeta>("fd_itemview_beta") {
        @Override
        public void setValue(FactoryDecoratedItemViewBeta object, float value) {
            object.animatorFlag = value;
            object.text.setAlpha(1 - value);
            object.countHoverStroke.setAlpha(1 - value);
            object.nameHover.setAlpha(value);
            object.nameHoverStroke.setAlpha(value);
            object.IMAGE_RETAIN.setAlphaF(value);
        }

        @Override
        public Float get(FactoryDecoratedItemViewBeta object) {
            return object.animatorFlag;
        }
    };
}
