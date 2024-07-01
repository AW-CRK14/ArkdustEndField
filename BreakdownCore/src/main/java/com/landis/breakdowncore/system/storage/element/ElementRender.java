package com.landis.breakdowncore.system.storage.element;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;

public class ElementRender {

    public void render(GuiGraphics gui, MagicElement element, int x, int y, int width, int height) {
        gui.blit(element.getTexture(), x, y, width, height,0,0);
    }


    public void render(GuiGraphics gui, MagicElement element, int x, int y, int width, int height, int color) {
        int r = (color >> 16) & 0xFF;
        int g = (color >> 8) & 0xFF;
        int b = color & 0xFF;
        int a = (color >> 24) & 0xFF;
        gui.pose().popPose();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShaderColor(r, g, b, a);
        gui.blit(element.getTexture(), x, y, width, height,0,0);
        RenderSystem.disableBlend();
        gui.pose().pushPose();
    }

}
