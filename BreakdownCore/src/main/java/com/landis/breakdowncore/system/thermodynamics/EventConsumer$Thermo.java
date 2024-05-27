package com.landis.breakdowncore.system.thermodynamics;

import com.landis.breakdowncore.BreakdownCore;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.dimension.BuiltinDimensionTypes;
import net.minecraft.world.level.dimension.DimensionType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.event.TickEvent;

import java.util.HashMap;
import java.util.Map;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, modid = BreakdownCore.MODID)
public class EventConsumer$Thermo {
    @SubscribeEvent
    public static void capAttach(RegisterCapabilitiesEvent event) {
        for (BlockEntityType<?> type : BuiltInRegistries.BLOCK_ENTITY_TYPE) {
            try {
                BlockEntity e = type.create(null, null);
                if (e instanceof IThermoBackground) {
                    event.registerBlockEntity(Capability$Thermo.THERMO_BLOCK, type, (be, n) -> (IThermoBackground) be);
                }
            } catch (Exception ignored) {
            }
        }
    }

    @Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE, modid = BreakdownCore.MODID)
    public static class ForgeBus {
        private static int timer = 0;

        @SubscribeEvent
        public static void thermoTickHandle(TickEvent.ServerTickEvent event) {
            timer++;
            if (timer > 5) {
                timer = 0;
            }
        }

        public static int getTick() {
            return timer;
        }
    }
}
