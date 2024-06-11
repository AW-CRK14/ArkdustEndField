package com.landis.breakdowncore.util;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.util.FastColor;
import net.minecraft.util.Mth;

public class LoadingRenderUtil {

    public static void drawProgressBar(GuiGraphics drawContext, float opacity,float currentProgress) {
        // 计算进度条的宽度和位置
        int barWidth = drawContext.guiWidth();
        int barX = 0;
        // 计算进度条的填充宽度
        int fillWidth = Mth.ceil((float)(barWidth * currentProgress));
        // 设置进度条的颜色
        int j = Math.round(opacity * 255.0f);
        int k = FastColor.ARGB32.color(j, 254, 230, 60);
        // 绘制进度条的填充部分
        drawContext.fill(barX, drawContext.guiWidth() / 2 - 2, barX + fillWidth / 2, drawContext.guiHeight() / 2, k);
        drawContext.fill(barX + barWidth - fillWidth / 2, drawContext.guiWidth() / 2 - 2, barX + barWidth, drawContext.guiHeight() / 2, k);
    }

    public static int replaceAlpha(int pColor, int pAlpha) {
        return pColor & 16777215 | pAlpha << 24;
    }
}
