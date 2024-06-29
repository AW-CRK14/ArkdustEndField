package com.landis.breakdowncore.module.network;

import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.handling.PlayPayloadContext;

public interface IDecoratedCustomPacketPayload extends CustomPacketPayload {
    default void handle(PlayPayloadContext context) {
        context.workHandler().execute(() -> consume(context));
    }

    void consume(PlayPayloadContext context);

    void send(Player... players);
}
