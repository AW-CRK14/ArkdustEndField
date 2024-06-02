package com.landis.arkdust.mui.widget.item;

import com.landis.arkdust.Arkdust;
import com.landis.arkdust.mui.abs.ItemWidget;
import com.landis.breakdowncore.helper.RenderHelper;
import icyllis.modernui.core.Context;
import icyllis.modernui.graphics.Canvas;
import icyllis.modernui.graphics.Image;
import icyllis.modernui.graphics.Paint;
import icyllis.modernui.graphics.RectF;
import icyllis.modernui.mc.ContainerDrawHelper;
import icyllis.modernui.view.Gravity;
import icyllis.modernui.widget.RelativeLayout;
import icyllis.modernui.widget.TextView;
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

    public final Paint FILL_PAINT = new Paint();

    public ArkdustContainerItemSlot(Context context, Slot slot) {
        super(context, slot);
    }

    public ArkdustContainerItemSlot(Context context, Slot slot, float width) {
        super(context, slot, width);
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

    public void drawContext(int actuallyLos, float xAmend, float yAmend, Canvas canvas) {
        ItemStack stack = slot.getItem();
        float itemLos = actuallyLos * 0.9F;
        float y0 = getTop() + yAmend;
        float x0 = getLeft() + xAmend;
        float y1 = y0 + actuallyLos;
        float x1 = x0 + actuallyLos;

//        canvas.drawImage(SLOT_BACKGROUND, null, new RectF(x0, y0, x1, y1), null);

        if (!stack.isEmpty()) {
            //背景渲染
            canvas.drawImage(FOREGROUND, null, new RectF(x0, y0, x1, y1), null);
            //物品渲染
            ContainerDrawHelper.drawItem(canvas, stack, x0 + actuallyLos * 0.5F, y0 + 0.45F * actuallyLos, 0, itemLos, 1713015070);
            //耐久条渲染

            float decHeight = actuallyLos * 0.06F;
            float interval = actuallyLos * 0.09F;
            if (stack.isBarVisible()) {
                renderRDCircle(actuallyLos, canvas, x1, y1, stack);
                float width = actuallyLos - 3 * interval - 0.5F * decHeight;
//                float width = actuallyLos * 0.7875F;

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


    @Override
    public void drawWhenHovering(int actuallyLos, float xAmend, float yAmend, Canvas canvas, float alpha) {

    }
}
