package com.landis.breakdowncore;

import com.landis.breakdowncore.system.macmodule.Registry$MacModule;
import com.landis.breakdowncore.system.material.ITypedMaterialObj;
import com.landis.breakdowncore.system.material.MaterialReflectDataGatherEvent;
import com.landis.breakdowncore.system.material.Registry$Material;
import com.landis.breakdowncore.system.material.System$Material;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.event.entity.item.ItemEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.registries.NewRegistryEvent;
import net.neoforged.neoforge.registries.RegisterEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

//@Mod.EventBusSubscriber(modid = BreakdownCore.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ForgeBusConsumer {
    public static final Logger logger = LogManager.getLogger();

//    @SubscribeEvent
//    public static void onRightClickItem(PlayerInteractEvent.RightClickItem event){
//        ItemStack s = event.getItemStack();
//        ITypedMaterialObj m = System$Material.getMaterialInfo(s.getItem());
//        if(m != null){
//            logger.info("ItemStack:{}", s);
//            logger.info("Material:{}", m.getMaterialId(s));
//            logger.info("MIT:{}", m.getMIType());
//        }
//    }
}
