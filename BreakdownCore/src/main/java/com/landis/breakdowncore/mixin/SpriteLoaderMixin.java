package com.landis.breakdowncore.mixin;

import com.landis.breakdowncore.event.EventHooks;
import com.landis.breakdowncore.event.render.SpriteBeforeStitchEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.SpriteContents;
import net.minecraft.client.renderer.texture.SpriteLoader;
import net.minecraft.client.renderer.texture.atlas.SpriteResourceLoader;
import net.minecraft.client.renderer.texture.atlas.SpriteSourceList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.metadata.MetadataSectionSerializer;
import net.minecraft.server.packs.resources.ResourceManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.Executor;
import java.util.function.Function;

import static net.minecraft.client.renderer.texture.SpriteLoader.runSpriteSuppliers;

@Mixin(SpriteLoader.class)
public abstract class SpriteLoaderMixin {

    @Shadow public abstract SpriteLoader.Preparations stitch(List<SpriteContents> pContents, int pMipLevel, Executor pExecutor);

    /**
     * @author Landis
     * @reason 添加一个额外的事件发布
     */
    @Overwrite
    public CompletableFuture<SpriteLoader.Preparations> loadAndStitch(
            ResourceManager pResourceManager, ResourceLocation pLocation, int pMipLevel, Executor pExecutor, Collection<MetadataSectionSerializer<?>> pSectionSerializers
    ) {
        SpriteResourceLoader spriteresourceloader = SpriteResourceLoader.create(pSectionSerializers);
        return CompletableFuture.<List<Function<SpriteResourceLoader, SpriteContents>>>supplyAsync(
                        () -> SpriteSourceList.load(pResourceManager, pLocation).list(pResourceManager), pExecutor
                )
                .thenCompose(p_293671_ -> runSpriteSuppliers(spriteresourceloader, p_293671_, pExecutor))
                .thenApply(list -> EventHooks.postSpriteBeforeStitchEvent(new SpriteBeforeStitchEvent(pResourceManager,pLocation,pMipLevel,pExecutor,spriteresourceloader,list)))
                .thenApply(p_261393_ -> this.stitch(p_261393_, pMipLevel, pExecutor));
    }

//    @ModifyArg(method = "loadAndStitch(Lnet/minecraft/server/packs/resources/ResourceManager;Lnet/minecraft/resources/ResourceLocation;ILjava/util/concurrent/Executor;Ljava/util/Collection;)Ljava/util/concurrent/CompletableFuture;",
//            at = @At(value = "INVOKE", target = "Ljava/util/concurrent/CompletableFuture;thenCompose(Ljava/util/function/Function;)Ljava/util/concurrent/CompletableFuture;"),
//            index = -1)
//    private <T> Function<? super T, ? extends CompletionStage<List<SpriteContents>>> eventPoster(Function<? super T, ? extends CompletionStage<List<SpriteContents>>> fn, ResourceManager pResourceManager, ResourceLocation pLocation, int pMipLevel, Executor pExecutor, Collection<MetadataSectionSerializer<?>> pSectionSerializers){
//        SpriteResourceLoader spriteresourceloader = SpriteResourceLoader.create(pSectionSerializers);
//        return l -> fn.apply(l).thenApply(list -> EventHooks.postSpriteBeforeStitchEvent(new SpriteBeforeStitchEvent(pResourceManager,pLocation,pMipLevel,pExecutor,spriteresourceloader,list)));
//    }

//    @Inject(method = "lambda$loadAndStitch$7",
//            at = @At(value = "HEAD"))
//    private static void eventPoster(SpriteResourceLoader spriteresourceloader, Executor pExecutor, List p_293671_, CallbackInfoReturnable<CompletionStage> cir){
//
//    }
}
