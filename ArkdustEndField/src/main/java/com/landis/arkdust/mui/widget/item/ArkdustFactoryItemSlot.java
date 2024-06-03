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

public abstract class ArkdustFactoryItemSlot extends ItemWidget {
    public static final Image FOREGROUND = Image.create(Arkdust.MODID, "gui/slot.png");

    public final Paint PAINT = new Paint();
    {
        PAINT.setColor(0x707070);
    }

    public ArkdustFactoryItemSlot(Context context, Slot slot, AbstractContainerMenu menu) {
        super(context, slot,menu);
    }

    public ArkdustFactoryItemSlot(Context context, Slot slot, float width, AbstractContainerMenu menu) {
        super(context, slot, width,menu);
    }

    @Override
    public LayoutParams configureText(TextView text) {
        text.setTextColor(0xE5E2E3);
        text.setTextSize(dp(0.35F));
        text.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL);
        LayoutParams params = new LayoutParams(-1, -2);
        params.addRule(ALIGN_BOTTOM);
        params.addRule(CENTER_HORIZONTAL);
        params.setMarginsRelative(0, 0, 0, dp(0.1F * this.width));
        return params;
    }

    @Override
    public void drawContext(int actuallyLos, float xAmend, float yAmend, Canvas canvas) {
        ItemStack stack = slot.getItem();
        float itemLos = actuallyLos * 0.8F;
        float y0 = getTop() + yAmend;
        float x0 = getLeft() + xAmend;
        float y1 = y0 + actuallyLos;
        float x1 = x0 + actuallyLos;
        if(stack.isEmpty()){
            canvas.drawImage(SLOT_BACKGROUND, null, new RectF(x0, y0, x1, y1), null);
        }else {
            //背景渲染
            canvas.drawImage(FOREGROUND, null, new RectF(x0, y0, x1, y1), null);
            //物品渲染
            ContainerDrawHelper.drawItem(canvas, stack, x0 + actuallyLos * 0.5F, y0 + 0.4F * actuallyLos , 0, itemLos, 1713015070);
            //耐久条渲染
            if (stack.isBarVisible()) {
                float f = (float) stack.getDamageValue() / stack.getMaxDamage();
                PAINT.setColor(0x6E6E6E);
                canvas.drawRect(x0 + (1 - f) * actuallyLos, y0 + 0.8F * actuallyLos, x1, y0 + 0.95F * actuallyLos, PAINT);
            }
            //色条渲染
            canvas.save();
            canvas.clipRect(x0, y0 + actuallyLos * 0.95F, x1,y1);
            this.PAINT.setColor(RenderHelper.itemStackDisplayNameColor(stack));
            canvas.drawRoundRect(x0, y0 + 0.9F * actuallyLos, x1, y1, 0.05F * actuallyLos, Gravity.BOTTOM, PAINT);
            canvas.restore();
        }

    }
}
