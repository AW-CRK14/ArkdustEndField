package com.landis.breakdowncore;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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
