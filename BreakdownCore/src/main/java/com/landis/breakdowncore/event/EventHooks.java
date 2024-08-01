package com.landis.breakdowncore.event;

import com.landis.breakdowncore.event.render.SpriteBeforeStitchEvent;
import com.landis.breakdowncore.system.material.Handler$Material;
import com.landis.breakdowncore.system.material.datagen.MaterialReflectDataGatherEvent;
import net.minecraft.client.renderer.texture.SpriteContents;
import net.neoforged.fml.ModLoader;

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

    public static void postMaterialReflectDataGatherEvent(Handler$Material handle){
        ModLoader.get().postEventWrapContainerInModOrder(new MaterialReflectDataGatherEvent(handle));
    }
}
