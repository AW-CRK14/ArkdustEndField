package com.landis.breakdowncore.system.material;

import net.neoforged.bus.api.Event;
import net.neoforged.fml.event.IModBusEvent;

import java.util.function.Consumer;

public class MaterialReflectDataGatherEvent extends Event implements IModBusEvent {
    public final Handler$Material handler;
    public MaterialReflectDataGatherEvent(Handler$Material handle){
        this.handler = handle;
    }

    public Handler$Material getHandler() {
        return handler;
    }

    public void accept(Consumer<Handler$Material> consumer){
        consumer.accept(handler);
    }
}
