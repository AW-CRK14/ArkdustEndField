package com.landis.arkdust.mixin;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Overlay;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.server.packs.resources.ReloadInstance;
import net.minecraft.util.FastColor;
import net.minecraft.util.Mth;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.IntSupplier;

@Mixin(net.minecraft.client.gui.screens.LoadingOverlay.class)
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
	@Final
	@Shadow
	private boolean fadeIn;
	@Shadow
	private long fadeInStart;
	@Shadow
	private long fadeOutStart;
	@Final
	@Shadow
	private Minecraft minecraft;
	@Shadow
	@Final
	private ReloadInstance reload;
	@Shadow
	@Final
	private Consumer<Optional<Throwable>> onFinish;
	@Unique
	private static final IntSupplier BRAND_BACKGROUND = () -> (Boolean)Minecraft.getInstance().options.darkMojangStudiosBackground().get() ? LOGO_BACKGROUND_COLOR_DARK : LOGO_BACKGROUND_COLOR;



	@Inject(method = "render", at = @At("HEAD"), cancellable = true)
	public void render(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick, CallbackInfo ci) {
		int i = pGuiGraphics.guiWidth();
		int j = pGuiGraphics.guiHeight();
		long k = Util.getMillis();
		if (this.fadeIn && this.fadeInStart == -1L) {
			this.fadeInStart = k;
		}

		float f = this.fadeOutStart > -1L ? (float)(k - this.fadeOutStart) / 1000.0F : -1.0F;
		float f1 = this.fadeInStart > -1L ? (float)(k - this.fadeInStart) / 500.0F : -1.0F;
		int l1;
		if (f >= 1.0F) {
			if (this.minecraft.screen != null) {
				this.minecraft.screen.render(pGuiGraphics, 0, 0, pPartialTick);
			}

			l1 = Mth.ceil((1.0F - Mth.clamp(f - 1.0F, 0.0F, 1.0F)) * 255.0F);
			pGuiGraphics.fill(RenderType.guiOverlay(), 0, 0, i, j, replaceAlpha(BRAND_BACKGROUND.getAsInt(), l1));
		} else if (this.fadeIn) {
			if (this.minecraft.screen != null && f1 < 1.0F) {
				this.minecraft.screen.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
			}

			l1 = Mth.ceil(Mth.clamp((double)f1, 0.15, 1.0) * 255.0);
			pGuiGraphics.fill(RenderType.guiOverlay(), 0, 0, i, j, replaceAlpha(BRAND_BACKGROUND.getAsInt(), l1));
		} else {
			l1 = BRAND_BACKGROUND.getAsInt();
			float f3 = (float)(l1 >> 16 & 255) / 255.0F;
			float f4 = (float)(l1 >> 8 & 255) / 255.0F;
			float f5 = (float)(l1 & 255) / 255.0F;
			GlStateManager._clearColor(f3, f4, f5, 1.0F);
			GlStateManager._clear(16384, Minecraft.ON_OSX);
		}
		float f6 = this.reload.getActualProgress();
		this.currentProgress = Mth.clamp(this.currentProgress * 0.95F + f6 * 0.050000012F, 0.0F, 1.0F);
		if (f < 1.0F) {
			float opacity = 1.0F - Mth.clamp(f, 0.0F, 1.0F);
			onDrawProgressBar(pGuiGraphics, opacity);
			onDrawProgressText(pGuiGraphics,opacity);
		}

		if (f >= 2.0F) {
			this.minecraft.setOverlay((Overlay)null);
		}

		if (this.fadeOutStart == -1L && this.reload.isDone() && (!this.fadeIn || f1 >= 2.0F)) {
			this.fadeOutStart = Util.getMillis();

			try {
				this.reload.checkExceptions();
				this.onFinish.accept(Optional.empty());
			} catch (Throwable var23) {
				this.onFinish.accept(Optional.of(var23));
			}

			if (this.minecraft.screen != null) {
				this.minecraft.screen.init(this.minecraft, pGuiGraphics.guiWidth(), pGuiGraphics.guiHeight());
			}
		}
		ci.cancel();
	}

	private void onDrawProgressBar(GuiGraphics drawContext, float opacity) {
		// 计算进度条的宽度和位置
		int width = drawContext.guiWidth();
		int height = drawContext.guiHeight();
		int barX = 0;

		// 计算进度条的填充宽度
		int fillWidth = Mth.ceil((float)(width * this.currentProgress));

		// 设置进度条的颜色
		int j = Math.round(opacity * 255.0f);
		int k = FastColor.ARGB32.color(j, 254, 230, 60);
		double smoothProgress = (Math.round(this.currentProgress * 1000) / 10.0);
		if(smoothProgress > 99.5){
			fillWidth = width+1;
		}
		// 绘制进度条的填充部分
		drawContext.fill(barX, height / 2 - 2, barX + fillWidth / 2, height / 2, k);
		drawContext.fill(barX + width - fillWidth / 2, height / 2 - 2, barX + width, height / 2, k);
	}

	private void onDrawProgressText(GuiGraphics drawContext, float opacity) {
		int width = drawContext.guiWidth();
		int height = drawContext.guiHeight();
		int j = Math.round(opacity * 255.0f);
		int color = FastColor.ARGB32.color(j, 254, 230, 60);
		int fillWidth = Mth.ceil((float)(width * this.currentProgress));
		Font font = Minecraft.getInstance().font;
		double smoothProgress = (Math.round(this.currentProgress * 1000) / 10.0);
		String text;

		if(smoothProgress > 99.5){
			text = "100%";
			int f_w = font.width(text);
			drawContext.drawString(font,text,width / 2 - f_w / 2  , height / 2 - 10, color);
		}else{
			text = (int)smoothProgress + "%";
			int f_w = font.width(text);
			drawContext.drawString(font,text,fillWidth/2 - f_w, height / 2 - 10, color);
			drawContext.drawString(font,text,width - fillWidth / 2 , height / 2 - 10, color);
		}
	}

	@Unique
	private static int replaceAlpha(int pColor, int pAlpha) {
		return pColor & 16777215 | pAlpha << 24;
	}
}