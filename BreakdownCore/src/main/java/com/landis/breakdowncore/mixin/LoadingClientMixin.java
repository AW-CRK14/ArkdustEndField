package com.landis.breakdowncore.mixin;


import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.LoadingOverlay;
import net.minecraft.util.FastColor;
import net.minecraft.util.Mth;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(LoadingOverlay.class)
public class LoadingClientMixin {
	@Mutable
	@Shadow
	@Final
	private static int LOGO_BACKGROUND_COLOR;

	@Mutable
	@Shadow
	@Final
	private static int LOGO_BACKGROUND_COLOR_DARK;
	@Shadow
	private float currentProgress;

	static {
		LOGO_BACKGROUND_COLOR = FastColor.ARGB32.color(255, 22, 22, 22);
		LOGO_BACKGROUND_COLOR_DARK = FastColor.ARGB32.color(255, 22, 22, 22);
	}


	@Inject(method = "drawProgressBar", at = @At("HEAD"), cancellable = true)
	private void onRenderProgressBar(GuiGraphics drawContext, int minX, int minY, int maxX, int maxY, float opacity, CallbackInfo ci) {
		// 计算进度条的宽度和位置
		int barWidth = drawContext.guiWidth();
		int barX = 0;

		// 计算进度条的填充宽度
		int fillWidth = Mth.ceil((float)(barWidth * this.currentProgress));

		// 设置进度条的颜色
		int j = Math.round(opacity * 255.0f);
		int k = FastColor.ARGB32.color(j, 254, 230, 60);

		// 绘制进度条的填充部分
		drawContext.fill(barX, drawContext.guiWidth() / 2 - 2, barX + fillWidth / 2, drawContext.guiHeight() / 2, k);
		drawContext.fill(barX + barWidth - fillWidth / 2, drawContext.guiHeight() / 2 - 2, barX + barWidth, drawContext.guiHeight() / 2, k);
		ci.cancel();
	}
}