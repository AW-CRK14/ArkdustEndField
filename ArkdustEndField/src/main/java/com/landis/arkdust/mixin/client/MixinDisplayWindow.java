package com.landis.arkdust.mixin.client;


import com.landis.arkdust.screen.LoadingOverlayScreen;
import net.neoforged.fml.earlydisplay.DisplayWindow;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.function.Function;
import java.util.stream.Collectors;


@Mixin(DisplayWindow.class)
public class MixinDisplayWindow {
	@Shadow
	private Method loadingOverlay;
	@Inject(method = "updateModuleReads",at = @At(value = "HEAD"), cancellable = true)
	public void updateModuleReads(ModuleLayer layer, CallbackInfo ci) {
		var fm = layer.findModule("neoforge").orElseThrow();
		getClass().getModule().addReads(fm);
		var clz = LoadingOverlayScreen.class;
		var methods = Arrays.stream(clz.getMethods()).filter(m-> Modifier.isStatic(m.getModifiers())).collect(Collectors.toMap(Method::getName, Function.identity()));
		loadingOverlay = methods.get("newInstance");
		ci.cancel();
	}
}