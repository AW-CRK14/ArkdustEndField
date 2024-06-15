package com.landis.arkdust.network;

import com.landis.arkdust.Arkdust;
import com.landis.breakdowncore.helper.ContainerHelper;
import icyllis.modernui.view.MotionEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.PlayPayloadContext;

public class SynMenuSlotClick {
    public record Pack(ResourceLocation menuType, int slotIndex, int mouseButtonIndex) implements CustomPacketPayload {

        public static final ResourceLocation ID = new ResourceLocation(Arkdust.MODID, "gui/click");

        public Pack(FriendlyByteBuf byteBuf) {
            this(new ResourceLocation(byteBuf.readUtf()), byteBuf.readInt(), byteBuf.readInt());
        }

        @Override
        public void write(FriendlyByteBuf pBuffer) {
            pBuffer.writeUtf(menuType.toString());
            pBuffer.writeInt(slotIndex);
            pBuffer.writeInt(mouseButtonIndex);
        }

        @Override
        public ResourceLocation id() {
            return ID;
        }
    }

    public static void send(MenuType<?> menuType, int slotIndex, int mouseButtonIndex) {
        PacketDistributor.SERVER.noArg().send(new Pack(BuiltInRegistries.MENU.getKey(menuType), slotIndex, mouseButtonIndex));
    }

    public static void handle(Pack pack, PlayPayloadContext context) {
        context.workHandler().execute(() -> context.player().ifPresent(player -> {
            AbstractContainerMenu menu = ((ServerPlayer) player).containerMenu;
            if (BuiltInRegistries.MENU.getKey(menu.getType()).equals(pack.menuType)) {
                ContainerHelper.handleSlotClick(pack.mouseButtonIndex, pack.slotIndex, player, menu);
            }
        }));
    }


}
