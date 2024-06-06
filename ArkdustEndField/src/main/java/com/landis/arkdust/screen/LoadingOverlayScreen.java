package com.landis.arkdust.screen;

import com.mojang.blaze3d.platform.GlConst;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.LoadingOverlay;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ReloadInstance;
import net.minecraft.util.FastColor;
import net.minecraft.util.Mth;
import net.neoforged.fml.StartupMessageManager;
import net.neoforged.fml.earlydisplay.ColourScheme;
import net.neoforged.fml.earlydisplay.DisplayWindow;
import net.neoforged.fml.loading.progress.ProgressMeter;
import net.neoforged.neoforge.client.loading.NeoForgeLoadingOverlay;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL30C;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.awt.*;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class LoadingOverlayScreen extends LoadingOverlay {

    private final Minecraft minecraft;
    private final ReloadInstance reload;
    private final Consumer<Optional<Throwable>> onFinish;
    private final DisplayWindow displayWindow;
    private final ProgressMeter progressMeter;
    private float currentProgress;
    private long fadeOutStart = -1L;

    public LoadingOverlayScreen(final Minecraft mc, final ReloadInstance reloader, final Consumer<Optional<Throwable>> errorConsumer, DisplayWindow displayWindow) {
        super(mc, reloader, errorConsumer, false);
        this.minecraft = mc;
        this.reload = reloader;
        this.onFinish = errorConsumer;
        this.displayWindow = displayWindow;
        displayWindow.addMojangTexture(mc.getTextureManager().getTexture(new ResourceLocation("textures/gui/title/mojangstudios.png")).getId());
        this.progressMeter = StartupMessageManager.prependProgressBar("Minecraft Progress", 1000);
    }

    public static Supplier<LoadingOverlay> newInstance(Supplier<Minecraft> mc, Supplier<ReloadInstance> ri, Consumer<Optional<Throwable>> handler, DisplayWindow window) {
        return () -> new LoadingOverlayScreen(mc.get(), ri.get(), handler, window);
    }


    @Override
    public void render(final @NotNull GuiGraphics graphics, final int mouseX, final int mouseY, final float partialTick) {
        // 获取当前时间的毫秒数
        long millis = Util.getMillis();
        // 计算淡出计时器，如果已经开始淡出，则计算淡出的时间
        float fadeouttimer = this.fadeOutStart > -1L ? (float) (millis - this.fadeOutStart) / 1000.0F : -1.0F;
        // 更新当前进度，使用一个简单的指数移动平均来平滑进度更新
        this.currentProgress = Mth.clamp(this.currentProgress * 0.95F + this.reload.getActualProgress() * 0.05F, 0.0F, 1.0F);
        // 设置进度条的绝对进度值
        progressMeter.setAbsolute(Mth.ceil(this.currentProgress * 1000));
        // 计算当前的透明度值
        var fade = 1.0F - Mth.clamp(fadeouttimer - 1.0F, 0.0F, 1.0F);
        // 获取背景颜色
        var colour = new ColourScheme.Colour(22, 22, 22);
        // 设置渲染状态的着色颜色，包括透明度
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, fade);
        // 如果已经开始淡出，渲染屏幕内容
        if (fadeouttimer >= 1.0F) {
            if (this.minecraft.screen != null) {
                this.minecraft.screen.render(graphics, 0, 0, partialTick);
            }
            displayWindow.render(0xff);
        } else {
            // 清除屏幕，设置背景颜色
            GlStateManager._clearColor(colour.redf(), colour.greenf(), colour.bluef(), 1f);
            GlStateManager._clear(GlConst.GL_COLOR_BUFFER_BIT, Minecraft.ON_OSX);
            displayWindow.render(0xFF);
        }
        // 启用混合模式，设置混合函数
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GlConst.GL_SRC_ALPHA, GlConst.GL_ONE_MINUS_SRC_ALPHA);
        // 获取当前窗口的宽度和高度
        var fbWidth = this.minecraft.getWindow().getWidth();
        var fbHeight = this.minecraft.getWindow().getHeight();
        // 设置视口大小
        GL30C.glViewport(0, 0, fbWidth, fbHeight);
        // 获取显示窗口的宽度和高度
        final var twidth = this.displayWindow.context().width();
        final var theight = this.displayWindow.context().height();
        // 计算缩放比例
        var wscale = (float) fbWidth / twidth;
        var hscale = (float) fbHeight / theight;
        var scale = this.displayWindow.context().scale() * Math.min(wscale, hscale) / 2f;
        // 计算窗口的四个边缘位置
        var wleft = Mth.clamp(fbWidth * 0.5f - scale * twidth, 0, fbWidth);
        var wtop = Mth.clamp(fbHeight * 0.5f - scale * theight, 0, fbHeight);
        var wright = Mth.clamp(fbWidth * 0.5f + scale * twidth, 0, fbWidth);
        var wbottom = Mth.clamp(fbHeight * 0.5f + scale * theight, 0, fbHeight);
        // 设置活动纹理单元
        GlStateManager.glActiveTexture(GlConst.GL_TEXTURE0);
        // 禁用背面剔除
        RenderSystem.disableCull();
        // 获取缓冲构建器
        BufferBuilder bufferbuilder = Tesselator.getInstance().getBuilder();
        // 设置渲染状态的着色颜色，包括透明度
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, fade);
        // 设置模型视图矩阵为单位矩阵
        RenderSystem.getModelViewMatrix().identity();
        // 设置投影矩阵为正交投影
        RenderSystem.setProjectionMatrix(new Matrix4f().setOrtho(0.0F, fbWidth, 0.0F, fbHeight, 0.1f, -0.1f), VertexSorting.ORTHOGRAPHIC_Z);
        // 设置渲染状态的着色器
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        // 开始绘制边缘填充
        bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
        // 绘制顶部、底部、左侧和右侧的填充
        addQuad(bufferbuilder, 0, fbWidth, wtop, fbHeight, colour, fade);
        addQuad(bufferbuilder, 0, fbWidth, 0, wtop, colour, fade);
        addQuad(bufferbuilder, 0, wleft, wtop, wbottom, colour, fade);
        addQuad(bufferbuilder, wright, fbWidth, wtop, wbottom, colour, fade);
        // 绘制缓冲区内容
        BufferUploader.drawWithShader(bufferbuilder.end());

        // 绘制实际的加载屏幕数据
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GlConst.GL_SRC_ALPHA, GlConst.GL_ONE_MINUS_SRC_ALPHA);
        RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
        int barX = 0;

        // 计算进度条的填充宽度
        int fillWidth = Mth.ceil((float)(fbWidth * this.currentProgress));

        // 设置进度条的颜色
        int color = new Color(254, 230, 60, fade).getRGB();

        // 开始绘制进度条
        bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);

        // 绘制左侧填充部分
        bufferbuilder.vertex(barX, fbHeight / 2 - 2, 0).color(color).endVertex();
        bufferbuilder.vertex(barX + fillWidth / 2, fbHeight / 2 - 2, 0).color(color).endVertex();
        bufferbuilder.vertex(barX + fillWidth / 2, fbHeight / 2, 0).color(color).endVertex();
        bufferbuilder.vertex(barX, fbHeight / 2, 0).color(color).endVertex();

        // 绘制右侧填充部分
        bufferbuilder.vertex(barX + fbWidth - fillWidth / 2, fbHeight / 2 - 2, 0).color(color).endVertex();
        bufferbuilder.vertex(barX + fbWidth, fbHeight / 2 - 2, 0).color(color).endVertex();
        bufferbuilder.vertex(barX + fbWidth, fbHeight / 2, 0).color(color).endVertex();
        bufferbuilder.vertex(barX + fbWidth - fillWidth / 2, fbHeight / 2, 0).color(color).endVertex();
        // 绘制缓冲区内容
        GL30C.glTexParameterIi(GlConst.GL_TEXTURE_2D, GlConst.GL_TEXTURE_MIN_FILTER, GlConst.GL_NEAREST);
        GL30C.glTexParameterIi(GlConst.GL_TEXTURE_2D, GlConst.GL_TEXTURE_MAG_FILTER, GlConst.GL_NEAREST);
        BufferUploader.drawWithShader(bufferbuilder.end());
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableBlend();
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1f);

        // 如果淡出计时器超过2秒，完成进度条并关闭显示窗口
        if (fadeouttimer >= 2.0F) {
            progressMeter.complete();
            this.minecraft.setOverlay(null);
            this.displayWindow.close();
        }

        // 如果还没有开始淡出并且重载完成，则开始淡出过程
        if (this.fadeOutStart == -1L && this.reload.isDone()) {
            this.fadeOutStart = Util.getMillis();
            try {
                this.reload.checkExceptions();
                this.onFinish.accept(Optional.empty());
            } catch (Throwable throwable) {
                this.onFinish.accept(Optional.of(throwable));
            }

            // 如果有屏幕对象，初始化它
            if (this.minecraft.screen != null) {
                this.minecraft.screen.init(this.minecraft, this.minecraft.getWindow().getGuiScaledWidth(), this.minecraft.getWindow().getGuiScaledHeight());
            }
        }
    }

    private static void addQuad(BufferVertexConsumer bufferbuilder, float x0, float x1, float y0, float y1, ColourScheme.Colour colour, float fade) {
        bufferbuilder.vertex(x0, y0, 0f).color(colour.redf(), colour.greenf(), colour.bluef(), fade).endVertex();
        bufferbuilder.vertex(x0, y1, 0f).color(colour.redf(), colour.greenf(), colour.bluef(), fade).endVertex();
        bufferbuilder.vertex(x1, y1, 0f).color(colour.redf(), colour.greenf(), colour.bluef(), fade).endVertex();
        bufferbuilder.vertex(x1, y0, 0f).color(colour.redf(), colour.greenf(), colour.bluef(), fade).endVertex();
    }

}
