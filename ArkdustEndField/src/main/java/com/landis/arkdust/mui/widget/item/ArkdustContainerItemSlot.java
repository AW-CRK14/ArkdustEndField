package com.landis.arkdust.mui.widget.item;

import com.landis.arkdust.Arkdust;
import com.landis.arkdust.mui.abs.ItemWidget;
import com.landis.breakdowncore.helper.RenderHelper;
import icyllis.modernui.animation.ObjectAnimator;
import icyllis.modernui.core.Context;
import icyllis.modernui.graphics.Canvas;
import icyllis.modernui.graphics.Image;
import icyllis.modernui.graphics.Paint;
import icyllis.modernui.graphics.RectF;
import icyllis.modernui.graphics.drawable.ImageDrawable;
import icyllis.modernui.mc.ContainerDrawHelper;
import icyllis.modernui.util.FloatProperty;
import icyllis.modernui.view.Gravity;
import icyllis.modernui.view.MotionEvent;
import icyllis.modernui.widget.ImageView;
import icyllis.modernui.widget.RelativeLayout;
import icyllis.modernui.widget.TextView;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class ArkdustContainerItemSlot extends ItemWidget {
    public static final Image FOREGROUND = Image.create(Arkdust.MODID, "gui/slot_c.png");

    public final Paint EDGE_PAINT = new Paint();

    {
        EDGE_PAINT.setColor(0xFFA0A0A0);
        EDGE_PAINT.setStrokeWidth(dp(0.5F));
        EDGE_PAINT.setStroke(true);
    }

    protected final ImageView hoverImage = new ImageView(getContext()) {
        private final ObjectAnimator hlaAnimator = ObjectAnimator.ofFloat(this, hlaProperty, 0F, 1F);

        private static final FloatProperty<ImageView> hlaProperty = new FloatProperty<>("hla") {
            @Override
            public void setValue(ImageView object, float value) {
                object.setAlpha(value);
            }

            @Override
            public Float get(ImageView object) {
                return object.getAlpha();
            }
        };

        {
            setFocusable(true);
            hlaAnimator.setDuration(100);
            setOnHoverListener((view, event) -> {
                boolean flag = true;
                switch (event.getAction()) {
                    case MotionEvent.ACTION_HOVER_ENTER -> hlaAnimator.start();
                    case MotionEvent.ACTION_HOVER_EXIT -> hlaAnimator.reverse();
                    default -> flag = false;
                }
                if (flag) invalidate();
                return false;
            });
        }

    };

    {
        hoverImage.setImage(SLOT_HOVER);
        hoverImage.setAlpha(0);
        LayoutParams imageLayoutParams = new LayoutParams(dp(2 * width), dp(2 * width));
        imageLayoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
        this.addView(hoverImage, imageLayoutParams);
    }

    public final Paint FILL_PAINT = new Paint();

    public ArkdustContainerItemSlot(Context context, Slot slot, AbstractContainerMenu menu) {
        super(context, slot, menu);
    }

    public ArkdustContainerItemSlot(Context context, Slot slot, float width, AbstractContainerMenu menu) {
        super(context, slot, width, menu);
    }

    @Override
    public LayoutParams configureText(TextView text) {
        text.setTextColor(0xFFA0A0A0);
        text.setTextSize(dp(width * 0.3F));
        text.setGravity(Gravity.BOTTOM | Gravity.RIGHT);
        LayoutParams params = new LayoutParams(-2, -2);
        params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        params.setMarginsRelative(0, 0, dp(width / 16F), 0);
        textLayout = params;
        return params;
    }

    private LayoutParams textLayout;
    private boolean rise = false;

    @Override
    public void refresh() {
        if (slot.hasItem() && (slot.getItem().getCount() > 1 || slot.getItem().getMaxStackSize() > 1) && slot.getItem().isBarVisible() != rise) {
            rise = !rise;
            if (rise) textLayout.setMarginsRelative(0, 0, dp(width / 8F), 0);
            else textLayout.setMarginsRelative(0, 0, dp(width / 16F), 0);
            text.requestLayout();
        }
        super.refresh();
    }

    @Override
    public void drawContext(int actuallyLos, float xAmend, float yAmend, Canvas canvas) {
        ItemStack stack = slot.getItem();
        float itemLos = actuallyLos * 0.9F;
        float y0 = getTop() + yAmend;
        float x0 = getLeft() + xAmend;
        float y1 = y0 + actuallyLos;
        float x1 = x0 + actuallyLos;


        if (!stack.isEmpty()) {
            //背景渲染
            canvas.drawImage(FOREGROUND, null, new RectF(x0, y0, x1, y1), null);
            //物品渲染
            ContainerDrawHelper.drawItem(canvas, stack, x0 + actuallyLos * 0.5F, y0 + 0.45F * actuallyLos, 0, itemLos, 1713015070);

            float decHeight = actuallyLos * 0.06F;
            float interval = actuallyLos * 0.09F;
            if (stack.isBarVisible()) {
                //渲染底部修饰圆与耐久条
                renderRDCircle(actuallyLos, canvas, x1, y1, stack);
                float width = actuallyLos - 3 * interval - 0.5F * decHeight;

                FILL_PAINT.setColor(stack.getBarColor() | 0xFF000000);
                canvas.drawRoundRect(x0 + interval, y1 - interval - decHeight, x0 + interval + width * (1 - (float) stack.getDamageValue() / stack.getMaxDamage()), y1 - interval, decHeight / 2, FILL_PAINT);
                canvas.drawRoundRect(x0 + interval, y1 - interval - decHeight, x0 + interval + width, y1 - interval, decHeight / 2, EDGE_PAINT);
            } else {
                if (stack.getMaxStackSize() == 1) renderRDCircle(actuallyLos, canvas, x1, y1, stack);
            }

        }
        canvas.drawImage(SLOT_BACKGROUND, null, new RectF(x0, y0, x1, y1), null);

    }

    private void renderRDCircle(int actuallyLos, Canvas canvas, float x1, float y1, ItemStack stack) {
        int color = RenderHelper.itemStackDisplayNameColor(stack);
        FILL_PAINT.setColor((color & 0x00FFFFFF) == 0xFFFFFF ? 0xFFA0A0A0 : color | 0xFF000000);
        float decHeight = actuallyLos * 0.06F;
        float interval = actuallyLos * 0.09F;
        canvas.drawCircle(x1 - decHeight * 0.5F - interval, y1 - decHeight * 0.5F - interval, decHeight * 0.5F, FILL_PAINT);
        canvas.drawCircle(x1 - decHeight * 0.5F - interval, y1 - decHeight * 0.5F - interval, decHeight * 0.5F, EDGE_PAINT);
    }
}
