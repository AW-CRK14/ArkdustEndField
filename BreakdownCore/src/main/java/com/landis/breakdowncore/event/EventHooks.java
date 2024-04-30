package com.landis.breakdowncore.event;

import com.landis.breakdowncore.event.render.SpriteBeforeStitchEvent;
import net.minecraft.client.renderer.texture.SpriteContents;
import net.neoforged.fml.ModLoader;
import net.neoforged.neoforge.common.NeoForge;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class EventHooks {
    public static List<SpriteContents> postSpriteBeforeStitchEvent(SpriteBeforeStitchEvent event){
        ModLoader.get().postEventWrapContainerInModOrder(event);
        List<SpriteContents> contents = new ArrayList<>(event.getAttached().stream().filter(Objects::nonNull).toList());
        contents.addAll(event.contents);
        return contents;
    }
}
