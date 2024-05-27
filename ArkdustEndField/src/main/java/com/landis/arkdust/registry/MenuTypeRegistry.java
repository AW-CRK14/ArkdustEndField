package com.landis.arkdust.registry;

import com.landis.arkdust.Arkdust;
//import com.landis.arkdust.screen.TestMachineMenu;
import com.landis.arkdust.blockentity.thermo.ThermoCombustorBlockEntity;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class MenuTypeRegistry {
    public static final DeferredRegister<MenuType<?>> REGISTER = DeferredRegister.create(Registries.MENU, Arkdust.MODID);

    public static final DeferredHolder<MenuType<?>,MenuType<ThermoCombustorBlockEntity.Menu>> THERMO_COMBUSTOR = REGISTER.register("testing",()-> IMenuTypeExtension.create(ThermoCombustorBlockEntity.Menu::new));
}
