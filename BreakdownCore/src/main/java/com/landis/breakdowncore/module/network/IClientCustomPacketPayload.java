package com.landis.breakdowncore.module.network;

import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.PacketDistributor;

public interface IClientCustomPacketPayload extends IDecoratedCustomPacketPayload{
    default void send(Player... players) {
        PacketDistributor.SERVER.noArg().send(this);
    }
}
