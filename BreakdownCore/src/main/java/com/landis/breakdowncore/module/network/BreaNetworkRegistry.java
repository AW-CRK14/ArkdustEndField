package com.landis.breakdowncore.module.network;

import com.landis.breakdowncore.BreakdownCore;
import com.landis.breakdowncore.module.menu.SynMenuFluidPacker;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlerEvent;
import net.neoforged.neoforge.network.registration.IPayloadRegistrar;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class BreaNetworkRegistry {

    @SubscribeEvent
    public static void registryNetwork(RegisterPayloadHandlerEvent event) {
        IPayloadRegistrar infoChannel = event.registrar(BreakdownCore.MODID);
        registerServer(infoChannel, SynMenuFluidPacker.ID, SynMenuFluidPacker::new);
        registerClient(infoChannel, SynMenuFluidPacker.Feedback.ID, SynMenuFluidPacker.Feedback::new);
    }

    public static <T extends IServerCustomPacketPayload> void registerServer(IPayloadRegistrar registrar, ResourceLocation id, FriendlyByteBuf.Reader<T> reader) {
        registrar.play(id, reader, handle -> handle.client(IServerCustomPacketPayload::consume));
    }

    public static <T extends IClientCustomPacketPayload> void registerClient(IPayloadRegistrar registrar, ResourceLocation id, FriendlyByteBuf.Reader<T> reader) {
        registrar.play(id, reader, handle -> handle.server(IClientCustomPacketPayload::consume));
    }
}
