package com.landis.arkdust.mui.widget.item;

import com.landis.arkdust.Arkdust;
import com.landis.arkdust.mui.abs.ItemWidget;
import com.landis.breakdowncore.helper.RenderHelper;
import icyllis.modernui.core.Context;
import icyllis.modernui.graphics.Canvas;
import icyllis.modernui.graphics.Image;
import icyllis.modernui.graphics.Paint;
import icyllis.modernui.graphics.RectF;
import icyllis.modernui.mc.neoforge.ContainerDrawHelper;
import icyllis.modernui.view.Gravity;
import icyllis.modernui.widget.RelativeLayout;
import icyllis.modernui.widget.TextView;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class ArkdustContainerItemSlot extends ItemWidget {
    public static final Image FOREGROUND = Image.create(Arkdust.MODID, "gui/slot_c.png");

    public final Paint EDGE_PAINT = new Paint();

    {
        EDGE_PAINT.setColor(0xFF707070);
        EDGE_PAINT.setStrokeWidth(dp(1));
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
        text.setTextColor(0x707070);
        text.setTextSize(dp(width * 0.05F));
        LayoutParams params = new LayoutParams(-2, -2);
        params.addRule(RelativeLayout.ALIGN_RIGHT);
        params.addRule(RelativeLayout.ALIGN_BOTTOM);
        params.setMarginsRelative(0, 0, dp(width / 16F), dp(width * 0.15F));
        textLayout = params;
        return params;
    }

    @Override
    public void refresh() {
        boolean state = slot.getItem().isBarVisible() || (slot.getItem().getCount() == 1 && slot.getItem().getMaxStackSize() == 1);
        if(state != rise){
            textLayout.setMarginsRelative(0, 0, dp(width / 16F), state ? dp(width * 0.15F) : dp(width / 16F));
            requestLayout();
            rise = state;
        }
        super.refresh();
    }

    private LayoutParams textLayout;
    private boolean rise = true;

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
//            ContainerDrawHelper.drawItem(canvas, stack, x0 + actuallyLos * 0.5F, y0 + 0.45F * actuallyLos, 0, itemLos, 1713015070);
            //耐久条渲染

            float decHeight = actuallyLos * 0.05F;  // 8 / 160
            float interval = actuallyLos * 0.0625F; //10 / 160
            if (stack.isBarVisible()) {
                renderRDCircle(actuallyLos, canvas, x1, y1, stack);
//                float width = actuallyLos - 3 * interval - 0.5F * decHeight;
                float width = actuallyLos * 0.7875F;
                canvas.drawRoundRect(x0 + interval, y1 - interval - decHeight, x0 + interval + width * (1 - (float) stack.getDamageValue() / stack.getMaxDamage()), y1 - interval, decHeight / 2, EDGE_PAINT);
                canvas.drawRoundRect(x0 + interval, y1 - interval - decHeight, x0 + interval + width, y1 - interval, decHeight / 2, EDGE_PAINT);
            } else {
                if(rise) renderRDCircle(actuallyLos, canvas, x1, y1, stack);
            }

        }
        canvas.drawImage(SLOT_BACKGROUND, null, new RectF(x0, y0, x1, y1), null);

    }

    private void renderRDCircle(int actuallyLos, Canvas canvas, float x1, float y1, ItemStack stack) {
        canvas.drawCircle(x1 - 0.0875F * actuallyLos, y1 - 0.0875F * actuallyLos, actuallyLos * 0.25F, EDGE_PAINT);
        FILL_PAINT.setColor(RenderHelper.itemStackDisplayNameColor(stack));
        canvas.drawCircle(x1 - 0.0875F * actuallyLos, y1 - 0.0875F * actuallyLos, actuallyLos * 0.01875F, FILL_PAINT);
    }


    @Override
    public void drawWhenHovering(int actuallyLos, float xAmend, float yAmend, Canvas canvas, float alpha) {

    }
}
