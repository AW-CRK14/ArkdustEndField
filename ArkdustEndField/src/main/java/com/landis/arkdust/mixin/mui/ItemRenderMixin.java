package com.landis.arkdust.mixin.mui;

import com.mojang.blaze3d.vertex.PoseStack;
import icyllis.arc3d.engine.DirectContext;
import icyllis.arc3d.engine.DrawableInfo;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.model.BakedModel;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

//@Mixin(targets = "icyllis.modernui.mc.ContainerDrawHelper$DrawItem")
//public class ItemRenderMixin {
//
//    @Inject(method = "draw", at = @At(value = "INVOKE",target = "Lcom/mojang/blaze3d/vertex/PoseStack;mulPoseMatrix(Lorg/joml/Matrix4f;)V"), locals = LocalCapture.CAPTURE_FAILHARD)
//    private void poseTrans(DirectContext dContext, DrawableInfo info, CallbackInfo ci, Minecraft minecraft, Matrix4f oldProjection, BakedModel model, boolean light2D, PoseStack localTransform){
//        localTransform.scale(-1,1,1);
//    }
//
//    @Inject(method = "draw",at = @At(value = "HEAD"))
//    private void test(DirectContext dContext, DrawableInfo info, CallbackInfo ci){
//        System.out.println("mui mixin test");
//    }
//}
