package com.landis.breakdowncore.event.render;

import net.minecraft.client.renderer.texture.SpriteContents;
import net.minecraft.client.renderer.texture.atlas.SpriteResourceLoader;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.event.IModBusEvent;
import oshi.annotation.concurrent.Immutable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executor;

/**此事件用于处理材质纹理的附加，在缝合之前触发。<br>
 * 您可以在这一事件中为某个纹理图集添加自定义的内容。<br>
 * 此事件于mod总线上触发，仅客户端。
 * @see com.landis.breakdowncore.mixin.SpriteLoaderMixin
 */
public class SpriteBeforeStitchEvent extends Event implements IModBusEvent {
    public final ResourceManager manager;
    public final ResourceLocation atlasLocation;
    public final int mipmap;
    public final Executor executor;
    public final SpriteResourceLoader loader;
    @Immutable
    public final List<SpriteContents> contents;
    private List<SpriteContents> attached = new ArrayList<>();

    public SpriteBeforeStitchEvent(ResourceManager manager, ResourceLocation atlasLocation, int mipmap, Executor executor, SpriteResourceLoader loader, List<SpriteContents> contents){
        this.manager = manager;
        this.atlasLocation = atlasLocation;
        this.mipmap = mipmap;
        this.executor = executor;
        this.loader = loader;
        this.contents = contents;
    }

    public List<SpriteContents> getAttached() {
        return attached;
    }

    public void register(SpriteContents... contents){
        attached.addAll(Arrays.asList(contents));
    }

    void recycle(){
        this.attached = null;
    }
}
