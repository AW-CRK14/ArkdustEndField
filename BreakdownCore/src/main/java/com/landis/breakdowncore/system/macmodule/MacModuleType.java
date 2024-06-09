package com.landis.breakdowncore.system.macmodule;

import it.unimi.dsi.fastutil.ints.Int2IntFunction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.Supplier;

public record MacModuleType<I extends MacModule<I>>(int maxLevel, Class<I> clazz,
                                                    Int2IntFunction elesignLoadForLevel, Int2IntFunction enegridLoadForLevel, //电信号（信息）负载计算与能量栅格负载计算
                                                    BiFunction<Integer, RandomSource, CompoundTag> randomDataCreator,//int 指示请求的等级 -> -1为请求随机等级
                                                    Supplier<I> emptyInstanceCreator,
                                                    Function<CompoundTag, I> deserializeFromItemTag) {

    public MacModuleType(int maxLevel, Class<I> clazz, Int2IntFunction elesignLoadForLevel, Int2IntFunction enegridLoadForLevel,
                         BiFunction<Integer, RandomSource, CompoundTag> randomDataCreator,//int 指示请求的等级 -> -1为请求随机等级
                         Supplier<I> emptyInstanceCreator) {
        this(maxLevel, clazz, elesignLoadForLevel, enegridLoadForLevel,
                randomDataCreator, emptyInstanceCreator, tag -> {
                    I ins = emptyInstanceCreator.get();
                    ins.deserializeWithoutItemData(tag);
                    return ins;
                });
    }



    public ResourceLocation getName() {
        return Registry$MacModule.MODULE_TYPES.getKey(this);
    }

    //---[对象生成 Object generate]---

    public boolean attachUpgradeToItemStack(ItemStack stack, boolean override) {
        return attachUpgradeToItemStack(stack, -1, override);
    }

    public boolean attachUpgradeToItemStack(ItemStack stack, int level, boolean override) {
        return attachUpgradeToItemStack(stack,level,RandomSource.create(),override);
    }
    public boolean attachUpgradeToItemStack(ItemStack stack, int level, RandomSource source, boolean override) {
        if (level > maxLevel || (level == 0 && maxLevel != 0) || level < -1 ||
                (!stack.getOrCreateTagElement("brea_data").getCompound("mac_module_upgrade").isEmpty() && !override))
            return false;
        stack.getOrCreateTagElement("brea_data").put("mac_module_upgrade", wrapData(randomDataCreator.apply(level,source), this));
        return true;
    }

    public static boolean attachUpgradeToItemStack(ItemStack stack, @Nonnull MacModule<?> instance, boolean override) {
        if (!stack.getOrCreateTagElement("brea_data").getCompound("mac_module_upgrade").isEmpty() && !override)
            return false;
        stack.getOrCreateTagElement("brea_data").put("mac_module_upgrade", wrapData(instance.serializeWithoutItemData(), instance.getTypeId()));
        return true;
    }



    //---[数据拆装 Data wrapper]---

    public static CompoundTag wrapData(CompoundTag saved, ResourceLocation id) {
        CompoundTag tag = new CompoundTag();
        tag.put("data", saved);
        tag.putString("type", id.toString());
        return tag;
    }

    public static CompoundTag wrapData(CompoundTag saved, MacModuleType<?> type) {
        return wrapData(saved, type.getName());
    }

    public static @Nullable MacModule<?> unwrapData(ItemStack stack) {
        CompoundTag tag = stack.getOrCreateTagElement("brea_data").getCompound("mac_module_upgrade");
        if (tag.isEmpty()) return null;
        return unwrapData(tag);
    }

    public static MacModule<?> unwrapData(CompoundTag saved) {
        MacModuleType<?> type = Registry$MacModule.MODULE_TYPES.get(new ResourceLocation(saved.getString("type")));
        return type == null ? null : type.deserializeFromItemTag.apply(saved.getCompound("data"));
    }
}
