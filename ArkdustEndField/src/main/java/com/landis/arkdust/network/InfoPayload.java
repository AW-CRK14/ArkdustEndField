package com.landis.arkdust.network;

import com.landis.arkdust.Arkdust;
import com.landis.arkdust.gui.info.LevelStateFragment;
import com.landis.arkdust.resource.Tags;
import icyllis.modernui.mc.MuiModApi;
import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.PlayPayloadContext;
import net.neoforged.neoforge.network.registration.IPayloadRegistrar;

public class InfoPayload {
    public static void bootstrap(IPayloadRegistrar r){
        LevelStateGUI.bootstrap(r);
    }



    /**LevelState用于将维度的状态同步给客户端。<br>
     * 此内容只应由服务端向客户端发送，且只用于为玩家打开世界状态面板。
     * */
    public static class LevelStateGUI implements CustomPacketPayload{
        public static final ResourceLocation ID = new ResourceLocation(Arkdust.MODID,"info/level_state_ui");
        public final CompoundTag info;
        public LevelStateGUI(CompoundTag info){
            this.info = info;
        }

        public LevelStateGUI(FriendlyByteBuf byteBuf){
            this.info = byteBuf.readNbt();
        }


        @Override
        public void write(FriendlyByteBuf pBuffer) {
            pBuffer.writeNbt(info);
        }

        @Override
        public ResourceLocation id() {
            return ID;
        }

        public static void clientHandle(final LevelStateGUI data,final PlayPayloadContext context){
            context.level().ifPresent(level-> Minecraft.getInstance().execute(()->MuiModApi.openScreen(new LevelStateFragment(data.info))));
        }

        public static void bootstrap(IPayloadRegistrar r){
            r.play(ID,LevelStateGUI::new,handle -> handle.client(LevelStateGUI::clientHandle));
        }

        public static void send(ServerPlayer player){
            ServerLevel level = player.serverLevel();
//            player.getServer().registryAccess()

            level.registryAccess().lookup(Registries.DIMENSION_TYPE).ifPresent(lookup ->{
                if(lookup.get(Tags.DimensionTypeTags.HAS_WORLD_STATE).get().contains(level.dimensionTypeRegistration())){
                    CompoundTag info = new CompoundTag();
//                    WeatherSavedData.getInstance(level).ifPresent(data -> data.synSimpleInfoToClient(info));

                    player.connection.send(new InfoPayload.LevelStateGUI(info));
                }else {
                    player.displayClientMessage(Component.translatable("gui.arkdust.warn.level_without_state"),false);
                }
            });
        }
    }
}
