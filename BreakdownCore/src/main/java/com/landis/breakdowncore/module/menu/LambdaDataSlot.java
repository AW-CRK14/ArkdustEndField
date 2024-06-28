package com.landis.breakdowncore.module.menu;

import net.minecraft.world.inventory.DataSlot;

import java.util.function.IntConsumer;
import java.util.function.IntSupplier;

public class LambdaDataSlot extends DataSlot {
    public final IntSupplier getter;
    public final IntConsumer setter;

    public LambdaDataSlot(IntSupplier getter, IntConsumer setter){
        this.getter = getter;
        this.setter = setter;
    }
    @Override
    public int get() {
        return getter.getAsInt();
    }

    @Override
    public void set(int pValue) {
        setter.accept(pValue);
    }
}
