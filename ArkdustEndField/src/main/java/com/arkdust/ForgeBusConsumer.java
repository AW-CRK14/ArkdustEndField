package com.arkdust;

import com.arkdust.blocks.TestMachineBlock;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.event.level.LevelEvent;
import net.neoforged.neoforge.items.ItemStackHandler;

@Mod.EventBusSubscriber(modid = Arkdust.MODID,bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ForgeBusConsumer {
    @SubscribeEvent
    public static void onLevelLoad(LevelEvent.Load event){
//        ((ServerLevel)(event.getLevel())).getDataStorage().get()
    }


}
