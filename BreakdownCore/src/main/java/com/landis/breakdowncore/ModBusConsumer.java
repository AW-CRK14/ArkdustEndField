package com.landis.breakdowncore;

import com.landis.breakdowncore.material.MaterialAboutRegistry;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.registries.NewRegistryEvent;

@Mod.EventBusSubscriber(modid = BreakdownCore.MODID,bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModBusConsumer {
    @SubscribeEvent
    public static void newRegistry(NewRegistryEvent event){
        event.register(MaterialAboutRegistry.MATERIAL_ITEM_TYPE);
    }
}
