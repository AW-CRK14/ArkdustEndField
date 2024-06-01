package com.landis.breakdowncore.helper;

import net.minecraft.network.chat.TextColor;
import net.minecraft.world.item.ItemStack;

public class RenderHelper {
    public static int itemStackDisplayNameColor(ItemStack stack) {
        if (stack == null || stack.isEmpty()) return 0xFFFFFFFF;
        TextColor color = stack.getDisplayName().getStyle().getColor();
        return color == null ? 0xFFFFFFFF : color.getValue();
    }
}
