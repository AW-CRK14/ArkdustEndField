package com.arkdust.registry;

import com.arkdust.Arkdust;
import com.arkdust.network.InfoPayload;
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
    }
}
