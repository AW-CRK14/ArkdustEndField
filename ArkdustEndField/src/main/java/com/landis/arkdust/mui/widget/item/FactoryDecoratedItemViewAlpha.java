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
import icyllis.modernui.widget.TextView;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nonnull;


public class FactoryDecoratedItemViewAlpha extends ItemWidget {
    public static final Image FOREGROUND = Image.create(Arkdust.MODID, "gui/slots/factory_decorated_alpha.png");
    public static final int COLOR_DAMAGE_BAR = 0xFF454545;
    public static final int COLOR_DAMAGED = 0xFF6E6E6E;

    public final Paint PAINT = new Paint();
    public final Paint IMAGE_RETAIN = new Paint();

    {
        PAINT.setColor(0xFF707070);
        IMAGE_RETAIN.setAlphaF(0.7F);
    }

    public FactoryDecoratedItemViewAlpha(Context context, Slot slot, AbstractContainerMenu menu) {
        super(context, slot, menu);
    }

    public FactoryDecoratedItemViewAlpha(Context context, Slot slot, float width, AbstractContainerMenu menu) {
        super(context, slot, width, menu);
    }

    @Override
    public @Nonnull LayoutParams configureText(TextView text) {
        text.setTextColor(0xFFE2E2EC);
        text.setTextSize(0.6F * width);
        text.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL);
        LayoutParams params = new LayoutParams(-1, -2);
        params.addRule(ALIGN_PARENT_BOTTOM);
        params.addRule(CENTER_HORIZONTAL);
        params.setMarginsRelative(0, 0, 0, dp(0.05F * this.width));
        return params;
    }

    @Override
    public void drawContext(int actuallyLos, float x0, float y0, Canvas canvas) {
        ItemStack stack = slot.getItem();
        float itemLos = actuallyLos * 0.8F;
        float y1 = y0 + actuallyLos;
        float x1 = x0 + actuallyLos;
        if (stack.isEmpty()) {
            canvas.drawImage(SLOT_BACKGROUND, null, new RectF(x0, y0, x1, y1), null);
        } else {
            //背景渲染
            canvas.drawImage(FOREGROUND, null, new RectF(x0, y0, x1, y1), IMAGE_RETAIN);
            //物品渲染
            ContainerDrawHelper.drawItem(canvas, stack, x0 + actuallyLos * 0.5F, y0 + 0.4F * actuallyLos, 0, itemLos, 1713015070);
            //耐久条渲染
            if (stack.isBarVisible()) {
                float f = (float) stack.getDamageValue() / stack.getMaxDamage();
                PAINT.setColor(COLOR_DAMAGED);
                canvas.drawRect(x0 + (1 - f) * actuallyLos, y0 + 0.8F * actuallyLos, x1, y0 + 0.95F * actuallyLos, PAINT);
            }
            //色条渲染
            canvas.save();
            canvas.clipRect(x0, y0 + actuallyLos * 0.95F, x1, y1);
            this.PAINT.setColor(0xFF000000 | RenderHelper.itemStackDisplayNameColor(stack));
            canvas.drawRoundRect(x0, y0 + 0.9F * actuallyLos, x1, y1, 0.05F * actuallyLos, Gravity.BOTTOM, PAINT);
            canvas.restore();
        }

    }
}
