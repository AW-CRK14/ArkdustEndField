package com.landis.arkdust.mixin;



import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.LoadingOverlay;
import net.minecraft.server.packs.resources.ReloadInstance;
import net.neoforged.fml.loading.progress.ProgressMeter;
import net.neoforged.neoforge.client.loading.NeoForgeLoadingOverlay;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;
import java.util.function.Consumer;


@Mixin(NeoForgeLoadingOverlay.class)
public class NeoLoadingClientMixin extends LoadingOverlay {

	public NeoLoadingClientMixin(Minecraft pMinecraft, ReloadInstance pReload, Consumer<Optional<Throwable>> pOnFinish, boolean pFadeIn) {
		super(pMinecraft, pReload, pOnFinish, pFadeIn);
	}

	@Inject(method = "render", at = @At("HEAD"), cancellable = true)
	public void render(GuiGraphics c, int x, int y, float t, CallbackInfo ci) {
		ci.cancel();
		super.render(c,x,y,t);
	}

}