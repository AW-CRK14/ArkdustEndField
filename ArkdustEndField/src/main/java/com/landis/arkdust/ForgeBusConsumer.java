package com.landis.arkdust;

import com.landis.arkdust.dataattach.PlayerInfo;
import com.landis.arkdust.registry.DataAttachmentRegistry;
import com.landis.arkdust.worldgen.dimension.SarconDimension;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.Heightmap;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.level.LevelEvent;

import java.util.Random;

@Mod.EventBusSubscriber(modid = Arkdust.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ForgeBusConsumer {
//    @SubscribeEvent
//    public static void onLevelLoad(LevelEvent.Load event){
////        ((ServerLevel)(event.getLevel())).getDataStorage().get()
//    }

    @SubscribeEvent
    public static void onPlayerFirstLogin(PlayerEvent.PlayerLoggedInEvent event) {
        ServerPlayer player = (ServerPlayer) event.getEntity();
        PlayerInfo info = player.getData(DataAttachmentRegistry.PLAYER_INFO);
        if (!info.isFirstLoginInit()) {
            MinecraftServer server = event.getEntity().getServer();
            ServerLevel sarcon = server.getLevel(SarconDimension.LEVEL);
            Random random = new Random();
            BlockPos pos = new BlockPos(random.nextInt(-32, 32), 0, random.nextInt(-32, 32));
            pos = new BlockPos(pos.getX(),SarconDimension.Generator.getHeight(pos.getX(),pos.getZ()),pos.getZ());
            player.setRespawnPosition(SarconDimension.LEVEL, pos, 0, false, false);
            player.teleportTo(sarcon, pos.getX(), pos.getY(), pos.getZ(), player.getYRot(), player.getXRot());
            info.init();
        }
    }


}
