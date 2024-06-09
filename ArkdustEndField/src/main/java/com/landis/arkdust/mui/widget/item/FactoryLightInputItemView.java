package com.landis.arkdust.mui.widget.item;

import com.landis.arkdust.mui.abs.ItemWidget;
import icyllis.modernui.animation.ObjectAnimator;
import icyllis.modernui.core.Context;
import icyllis.modernui.graphics.Canvas;
import icyllis.modernui.graphics.Paint;
import icyllis.modernui.mc.ContainerDrawHelper;
import icyllis.modernui.util.FloatProperty;
import icyllis.modernui.view.MotionEvent;
import icyllis.modernui.widget.RelativeLayout;
import icyllis.modernui.widget.TextView;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class FactoryLightInputItemView extends ItemWidget {

    public final Paint BG_PAINTER = new Paint();

    {
        BG_PAINTER.setColor(0xFF7C7C7C);
        BG_PAINTER.setStrokeWidth(dp(2));
    }

    public final Paint D_PAINTER = new Paint();

    {
        D_PAINTER.setStrokeWidth(dp(1));
    }

    public FactoryLightInputItemView(Context context, Slot slot, AbstractContainerMenu menu) {
        super(context, slot, menu);
    }

    public FactoryLightInputItemView(Context context, Slot slot, float width, AbstractContainerMenu menu) {
        super(context, slot, width, menu);
    }

    @Override
    public LayoutParams configureText(TextView text) {
        text.setTextColor(0xFFA8A8A8);
        text.setTextSize(dp(width * 0.3F));
        LayoutParams params = new LayoutParams(-2, -2);
        params.addRule(RelativeLayout.CENTER_HORIZONTAL);
        params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        params.setMarginsRelative(0, 0, dp(width / 8F), 0);
        return params;
    }

    protected float v = 0F;

    protected ObjectAnimator animator = ObjectAnimator.ofFloat(this,animProperty,0,1);

    {
        setAlpha(0.8F);
        setOnHoverListener((view, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_HOVER_ENTER -> animator.start();
                case MotionEvent.ACTION_HOVER_EXIT -> animator.reverse();
            }
            return false;
        });
    }

    @Override
    public void drawContext(int actuallyLos, float xAmend, float yAmend, Canvas canvas) {
        ItemStack stack = slot.getItem();
        float itemLos = actuallyLos * 0.9F;
        float y0 = getPaddingTop() + yAmend;
        float x0 = getPaddingLeft() + xAmend;
        float y1 = y0 + actuallyLos;
        float x1 = x0 + actuallyLos;

        float grid = actuallyLos / 5F;
        if (v == 0) {
            canvas.drawPoint(x0 + grid, y0 + grid, BG_PAINTER);
            canvas.drawPoint(x0 + grid, y1 - grid, BG_PAINTER);
            canvas.drawPoint(x1 - grid, y1 - grid, BG_PAINTER);
            canvas.drawPoint(x1 - grid, y0 + grid, BG_PAINTER);
        } else {
            float width = grid * 3 * v;
            canvas.drawLine(x0 + grid, y0 + grid, x0 + grid, y0 + grid + width, BG_PAINTER);
            canvas.drawLine(x0 + grid, y1 - grid, x0 + grid + width, y1 - grid, BG_PAINTER);
            canvas.drawLine(x1 - grid, y1 - grid, x1 - grid, y1 - grid - width, BG_PAINTER);
            canvas.drawLine(x1 - grid, y0 + grid, x1 - grid - width, y0 + grid, BG_PAINTER);
        }

        if (!stack.isEmpty()) {
            ContainerDrawHelper.drawItem(canvas, stack, x0 + actuallyLos * 0.5F, y0 + actuallyLos * 0.5F, 0, itemLos, 742019199);
            if (stack.isBarVisible()) {
                D_PAINTER.setColor(stack.getBarColor() | 0xFF000000);
                canvas.drawLine(x0 + grid, y1 - grid, x0 + grid + (grid * 3F * (1 - (float) stack.getDamageValue() / stack.getMaxDamage())), y1 - grid, D_PAINTER);
            }
        }
    }

    private static final FloatProperty<FactoryLightInputItemView> animProperty = new FloatProperty<>("anim_fli") {
        @Override
        public void setValue(FactoryLightInputItemView object, float value) {
            object.v = value;
            object.setAlpha(0.8F + 0.2F * value);
        }

        @Override
        public Float get(FactoryLightInputItemView object) {
            return object.v;
        }
    };
}
