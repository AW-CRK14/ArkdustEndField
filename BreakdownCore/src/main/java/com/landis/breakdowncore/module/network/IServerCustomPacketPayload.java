package com.landis.breakdowncore.module.network;

import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.PlayPayloadContext;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;

public interface IServerCustomPacketPayload extends IDecoratedCustomPacketPayload {
    default void send(Player... players) {
        if (players == null || players.length == 0) {
            PacketDistributor.ALL.noArg().send(this);
        } else {
            Arrays.stream(players).filter(player -> player instanceof ServerPlayer).forEach(player -> PacketDistributor.PLAYER.with((ServerPlayer) player).send(this));
        }
    }
}
