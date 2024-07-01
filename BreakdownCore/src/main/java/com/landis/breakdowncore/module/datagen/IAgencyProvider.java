package com.landis.breakdowncore.module.datagen;

import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface IAgencyProvider<I> {
    void addAgency(I instance);
    List<I> getAgency();

    void execute(I instance);

    default void agency(){
        for(I instance : getAgency()){
            execute(instance);
        }
    }
}
