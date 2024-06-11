package com.landis.breakdowncore.mixin;



import com.landis.breakdowncore.BreakdownCore;
import com.landis.breakdowncore.util.LoadingRenderUtil;
import com.mojang.blaze3d.platform.GlConst;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.LoadingOverlay;
import net.minecraft.client.gui.screens.Overlay;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ReloadInstance;
import net.minecraft.util.FastColor;
import net.minecraft.util.Mth;
import net.neoforged.fml.earlydisplay.ColourScheme;
import net.neoforged.fml.earlydisplay.DisplayWindow;
import net.neoforged.fml.earlydisplay.RenderElement;
import net.neoforged.fml.loading.progress.ProgressMeter;
import net.neoforged.neoforge.client.loading.NeoForgeLoadingOverlay;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL30C;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.IntSupplier;


@Mixin(NeoForgeLoadingOverlay.class)
public class NeoLoadingClientMixin extends LoadingOverlay {

	@Final
	@Shadow
	private ProgressMeter progressMeter;

	public NeoLoadingClientMixin(Minecraft pMinecraft, ReloadInstance pReload, Consumer<Optional<Throwable>> pOnFinish, boolean pFadeIn) {
		super(pMinecraft, pReload, pOnFinish, pFadeIn);
	}

	@Inject(method = "render", at = @At("HEAD"), cancellable = true)
	public void render(GuiGraphics c, int x, int y, float t, CallbackInfo ci) {
		ci.cancel();
		super.render(c,x,y,t);
	}

}