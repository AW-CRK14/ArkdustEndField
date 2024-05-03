package com.landis.breakdowncore.system.thermodynamics;

import com.landis.breakdowncore.BreakdownCore;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD,modid = BreakdownCore.MODID)
public class EventConsumer$Thermo {
    @SubscribeEvent
    public static void capAttach(RegisterCapabilitiesEvent event){
        for(BlockEntityType<?> type : BuiltInRegistries.BLOCK_ENTITY_TYPE){
            try {
                BlockEntity e = type.create(null,null);
                if(e instanceof IThermoBackground){
                    event.registerBlockEntity(Capability$Thermo.THERMO_BLOCK,type,(be,n)->(IThermoBackground)be);
                }
            }catch (Exception ignored){}
        }
    }
}
