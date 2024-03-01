package com.arkdust.helper;

import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

public class PlayerHelper {
    public static boolean isAdvancementAchieved(ServerPlayer player, ResourceLocation location){
        return player.getAdvancements().getOrStartProgress(player.server.getAdvancements().get(location)).isDone();
    }

}
