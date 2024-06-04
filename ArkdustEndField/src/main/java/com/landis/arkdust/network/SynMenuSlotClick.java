package com.landis.arkdust.network;

import com.landis.arkdust.Arkdust;
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

                int slotIndex = pack.slotIndex;
                Slot slot = menu.getSlot(slotIndex);
                if (pack.mouseButtonIndex == -1) {
                    menu.quickMoveStack(player, slotIndex);
                } else {//非快速移动
                    ItemStack item = slot.getItem();
                    ItemStack carried = menu.getCarried();
                    if (carried.isEmpty()) {//无悬浮物品
                        int count = slot.getItem().getCount();
                        if (item.isEmpty() || !slot.isActive() || !slot.mayPickup(player)) {

                        } else if (pack.mouseButtonIndex == MotionEvent.BUTTON_PRIMARY) {//左键提取
                            menu.setCarried(slot.safeTake(count, slot.getItem().getMaxStackSize(), player));
                        } else if (pack.mouseButtonIndex == MotionEvent.BUTTON_SECONDARY) {//右键提取
                            menu.setCarried(slot.safeTake(count, (count + 1) / 2, player));
                        }
                    } else {//有悬浮物品
                        if (slot.isActive()) {
                            if (slot.mayPlace(carried)) {//如果允许放入
                                if (pack.mouseButtonIndex == MotionEvent.BUTTON_PRIMARY) {//左键全部放入
                                    menu.setCarried(slot.safeInsert(carried));
                                } else if (pack.mouseButtonIndex == MotionEvent.BUTTON_SECONDARY) {//右键放入一个
                                    menu.setCarried(slot.safeInsert(carried, 1));
                                }
                            } else if (ItemStack.isSameItemSameTags(carried, item) && slot.mayPickup(player) && pack.mouseButtonIndex == MotionEvent.BUTTON_PRIMARY) {//在不允许放入时，尝试取出
                                carried.grow(slot.safeTake(slot.getItem().getCount(), carried.getMaxStackSize() - carried.getCount(), player).getCount());
                            }
                        }
                    }
                    menu.slotsChanged(slot.container);
                }
            }
        }));
    }
}
