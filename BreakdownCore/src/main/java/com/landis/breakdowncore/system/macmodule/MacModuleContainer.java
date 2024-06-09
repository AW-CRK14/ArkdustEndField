package com.landis.breakdowncore.system.macmodule;

import com.google.common.collect.ImmutableMap;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.util.INBTSerializable;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.function.Function;

public class MacModuleContainer implements INBTSerializable<CompoundTag> {
    public final int maxElesignLoad;
    public final int maxEnegridLoad;
    public final Function<MacModule<?>, Boolean> installPermission;
    private int elesignPoints;
    private int enegridPoints;

    public MacModuleContainer(int maxElectricitySignLoad, int maxEnergyGridLoad, Function<MacModule<?>, Boolean> installPermission) {
        this.maxElesignLoad = maxElectricitySignLoad;
        this.maxEnegridLoad = maxEnergyGridLoad;
        this.installPermission = installPermission;
    }

    private final Map<MacModuleType<?>, ImmutableMap<ResourceLocation, Float>> addons = new HashMap<>();
    private final Map<ResourceLocation, Float> values = new HashMap<>();
    //Warn: 不要直接对此变量进行添加！
    public final Map<MacModuleType<?>, List<MacModule<?>>> modules = new HashMap<>();

    //---[数据访问 Data accessor]---
    public Optional<Float> getValue(ResourceLocation id){
        return values.containsKey(id) ? Optional.of(values.get(id)) : Optional.empty();
    }

    public boolean containsValueKey(ResourceLocation key){
        return values.containsKey(key);
    }



    //---[模块安装 Module install]---

    /**
     * 安装一个升级组件。返回值内容代表安装是否成功与失败原因
     *
     * @return 0 -> 安装成功 <br>
     * 1 -> 不允许的升级模块 <br>
     * 2 -> 前置等级未安装 <br>
     * 3 -> 模块已被安装<br>
     * 4 -> 信息荷载超过上限<br>
     * 5 -> 能量荷载超过上限
     */
    public byte installModule(@Nonnull MacModule<?> mod) {
        //可用性前置判断
        if (!installPermission.apply(mod)) return 1;
        int index = mod.getType().maxLevel() <= 1 ? 0 : mod.getActuallyLevel() - 1;
        List<MacModule<?>> list = modules.computeIfAbsent(mod.getType(), k -> new ArrayList<>());
        if (index != 0 && list.size() < index) return 2;
        if (list.size() > index) return 3;
        if (elesignPoints + mod.installElesignCost() > elesignPoints) return 4;
        if (enegridPoints + mod.installEnegridCost() > enegridPoints) return 5;

        //加载新的模块
        list.add(mod);
        elesignPoints += mod.installElesignCost();
        enegridPoints += mod.installEnegridCost();
        notifyAddonValueChange(mod.getType());
        return 0;
    }

    /**
     * (续上)
     *
     * @return 11 -> 物品不具有模块特征
     */
    public byte installModule(@Nonnull ItemStack target) {
        MacModule<?> module = MacModuleType.unwrapData(target);
        if (module != null) {
            module.bindItemStack(target);
            return installModule(module);
        }
        return 11;
    }

    /**
     * 尝试卸载一个组件。返回被卸载的对象。
     *
     * @return null表示没有组件被卸载。
     */
    public <T extends MacModule<T>> MacModule<T> uninstallModule(MacModuleType<T> type) {
        List<MacModule<?>> l = modules.get(type);
        if (l != null && !l.isEmpty()) {
            MacModule<T> mod = (MacModule<T>) l.remove(l.size() - 1);
            elesignPoints -= mod.installElesignCost();
            enegridPoints -= mod.installEnegridCost();
            notifyAddonValueChange(type);
            return mod;
        }
        return null;
    }

    /**
     * 提醒某一类的模块的addon数据更新
     */
    public void notifyAddonValueChange(@Nonnull MacModuleType<?> type) {
        ImmutableMap<ResourceLocation, Float> origin = addons.get(type);
        ImmutableMap<ResourceLocation, Float> alternative = null;

        try {
            List<MacModule<?>> list = modules.get(type);
            alternative = ImmutableMap.copyOf(list.get(list.size() - 1).dataAddon());
        } catch (Throwable ignored) {
        }

        if (origin != null) {
            origin.forEach((k, v) -> {
                if (values.containsKey(k)) {
                    values.put(k, values.get(k) - v);
                }
            });
        }

        if (alternative != null) {
            alternative.forEach((k, v) -> {
                values.put(k, values.getOrDefault(k, 0F) + v);
            });
        }

        addons.put(type, alternative);
    }

    public void notifyAddonValueChange() {
        values.clear();
        for (ImmutableMap<ResourceLocation, Float> addon : addons.values()) {
            addon.forEach((k, v) -> {
                values.put(k, values.getOrDefault(k, 0F) + v);
            });
        }
    }

    //---[数据处理 Data handle]---

    @Override
    public CompoundTag serializeNBT() {
        return null;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {

    }
}
