package com.landis.breakdowncore.module.blockentity.container;

import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

import java.util.Optional;

public class FixedSlot extends Slot {
    public FixedSlot(Container pContainer, int pSlot, int pX, int pY) {
        super(pContainer, pSlot, pX, pY);
    }

    public Optional<ItemStack> tryRemove(int pCount, int pDecrement, Player pPlayer) {
        if (!this.mayPickup(pPlayer)) {
            return Optional.empty();
        } else {
            pCount = Math.min(pCount, pDecrement);
            ItemStack itemstack = this.remove(pCount);
            if (itemstack.isEmpty()) {
                return Optional.empty();
            } else {
                if (this.getItem().isEmpty()) {
                    this.setByPlayer(ItemStack.EMPTY, itemstack);
                }

                return Optional.of(itemstack);
            }
        }
    }
}
