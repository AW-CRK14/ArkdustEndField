package com.landis.breakdowncore.module.network;

import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.PlayPayloadContext;
import org.jetbrains.annotations.Nullable;

public interface IServerCustomPacketPayload extends CustomPacketPayload {
    default void send(ServerPlayer... players) {
        if (players == null || players.length == 0) {
            PacketDistributor.ALL.noArg().send(this);
        } else {
            for (ServerPlayer player : players) {
                PacketDistributor.PLAYER.with(player).send(this);
            }
        }
    }

    default void handle(PlayPayloadContext context) {
        context.workHandler().execute(() -> consume(context));
    }

    void consume(PlayPayloadContext context);
}
