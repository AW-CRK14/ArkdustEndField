package com.landis.arkdust.registry;

import com.landis.arkdust.Arkdust;
import com.landis.arkdust.network.InfoPayload;
import com.landis.arkdust.network.SynMenuSlotClick;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlerEvent;
import net.neoforged.neoforge.network.registration.IPayloadRegistrar;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class NetworkRegistry {

    @SubscribeEvent
    public static void registryNetwork(RegisterPayloadHandlerEvent event){
        IPayloadRegistrar infoChannel = event.registrar(Arkdust.MODID);
        InfoPayload.bootstrap(infoChannel);
        infoChannel.play(SynMenuSlotClick.Pack.ID,SynMenuSlotClick.Pack::new,handle -> handle.server(SynMenuSlotClick::handle));
    }
}
