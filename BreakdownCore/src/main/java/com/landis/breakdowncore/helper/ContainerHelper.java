package com.landis.breakdowncore.helper;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandler;
import org.apache.commons.lang3.tuple.Pair;

public class ContainerHelper {
    /**用于快捷处理物品的放入，与 {@link IItemHandler IItemHandle} 配合使用
     * @param original 对应槽位原有的物品堆。
     * @param maxCountFix 修正数量上限，槽位最终物品数量将会取物品堆数量上限与此值的小值。设置为<0表示不限制。
     * @param inserter 放入的新物品。这一过程不会修改原物品，而是创建其副本。
     * @param simulate 模拟。如果模拟，原物品堆不会被修改。
     * @return 前一个ItemStack代表原有物品堆，后一个代表放入堆副本的处理结果。*/
    public static Pair<ItemStack,ItemStack> itemInsert(ItemStack original, int maxCountFix, ItemStack inserter, boolean simulate){
        maxCountFix = maxCountFix < 0 ? original.getMaxStackSize() : Math.min(maxCountFix,original.getMaxStackSize());
        if(inserter.isEmpty() || maxCountFix == 0){
            return Pair.of(original,inserter);
        }else if(original.getItem().equals(inserter.getItem()) && original.getCount() < maxCountFix){
            int took = Math.min(maxCountFix - original.getCount(),inserter.getCount());
            if(!simulate){
                original.grow(took);
            }
            if(took >= inserter.getCount()){
                inserter = ItemStack.EMPTY;
            }else {
                inserter = inserter.copy();
                inserter.shrink(took);
            }
            return Pair.of(original,inserter);
        } else if (original.isEmpty()) {
            ItemStack neo = new ItemStack(original.getItem(),0);
            int took = Math.min(maxCountFix,inserter.getCount());
            if(took >= inserter.getCount()){
                inserter = ItemStack.EMPTY;
            }else {
                inserter = inserter.copy();
                inserter.shrink(took);
            }
            if(!simulate){
                neo.setCount(took);
                return Pair.of(neo,inserter);
            }else return Pair.of(original,inserter);
        }
        return Pair.of(original,inserter);
    }

    /**用于快捷处理物品的提出，与 {@link IItemHandler IItemHandle} 配合使用
     * @param original 对应槽位原有的物品堆。
     * @param extractCount 需要提取的数量。
     * @param simulate 模拟。如果模拟，原物品堆不会被修改。
     * @return 前一个ItemStack代表原有物品堆，后一个代表提取结果*/
    public static Pair<ItemStack,ItemStack> itemExtract(ItemStack original, int extractCount, boolean simulate){
        if(original.isEmpty() || extractCount <= 0) return Pair.of(original,ItemStack.EMPTY);

        boolean flag = extractCount > original.getCount();
        int took = flag ? original.getCount() : extractCount;
        if(!simulate){
            if (flag) {
                original = ItemStack.EMPTY;
            } else {
                original.shrink(took);
            }
        }
        return Pair.of(original,new ItemStack(original.getItem(),took));
    }

    public static ItemStack inventoryStacksQuickMove(int slotStartIndex, AbstractContainerMenu menu, ItemStack stack, int checkedIndex) {
        if (menu.moveItemStackTo(stack, 0, 1, false)) { //Forge Fix Shift Clicking in beacons with stacks larger then 1.
            return ItemStack.EMPTY;
        } else if (checkedIndex >= slotStartIndex && checkedIndex < slotStartIndex + 27) {
            if (!menu.moveItemStackTo(stack, slotStartIndex + 27, slotStartIndex + 36, false)) {
                return ItemStack.EMPTY;
            }
        } else if (checkedIndex >= slotStartIndex + 27 && checkedIndex < slotStartIndex + 36) {
            if (!menu.moveItemStackTo(stack, 1, 28, false)) {
                return ItemStack.EMPTY;
            }
        } else if (!menu.moveItemStackTo(stack, slotStartIndex, slotStartIndex + 36, false)) {
            return ItemStack.EMPTY;
        }
        return stack;
    }

    public static void handleSlotClick(int actionIndex, int slotIndex, Player player, AbstractContainerMenu menu, boolean isClient) {
        Slot slot = menu.getSlot(slotIndex);
        if (actionIndex == -1) {
            menu.quickMoveStack(player, slotIndex);
        } else {//非快速移动
            ItemStack item = slot.getItem();
            ItemStack carried = isClient ? menu.remoteCarried : menu.getCarried();
            ItemStack newCarried = null;
            if (carried.isEmpty()) {//无悬浮物品
                int count = slot.getItem().getCount();
                if (item.isEmpty() || !slot.isActive() || !slot.mayPickup(player)) {

                } else if ((actionIndex & 0b1) == 1) {//左键提取
                    newCarried = slot.safeTake(count, slot.getItem().getMaxStackSize(), player);
                } else if (((actionIndex & 0b10) >> 1) == 1) {//右键提取
                    newCarried = slot.safeTake(count, (count + 1) / 2, player);
                }
            } else {//有悬浮物品
                if (slot.isActive()) {
                    if (slot.mayPlace(carried)) {//如果允许放入
                        if ((actionIndex & 0b1) == 1) {//左键全部放入
                            newCarried = slot.safeInsert(carried);
                        } else if (((actionIndex & 0b10) >> 1) == 1) {//右键放入一个
                            newCarried = slot.safeInsert(carried, 1);
                        }
                    } else if (ItemStack.isSameItemSameTags(carried, item) && slot.mayPickup(player) && (actionIndex & 0b1) == 1) {//在不允许放入时，尝试取出
                        carried.grow(slot.safeTake(slot.getItem().getCount(), carried.getMaxStackSize() - carried.getCount(), player).getCount());
                        newCarried = carried;
                    }
                }
            }
            if(newCarried != null && newCarried != carried){
                if (isClient) {
                    menu.remoteCarried = newCarried;
                } else {
                    menu.setCarried(newCarried);
                }
            }
//            menu.slotsChanged(slot.container);//服务端玩家每tick会自动请求同步数据
        }
    }
}
