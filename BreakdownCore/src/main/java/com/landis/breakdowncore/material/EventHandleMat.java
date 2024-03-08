package com.landis.breakdowncore.material;

import com.google.common.collect.ImmutableMap;
import com.landis.breakdowncore.BreakdownCore;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;

/**本类用于处理在注册与注册前阶段的各种额外处理需求*/
@Mod.EventBusSubscriber(modid = BreakdownCore.MODID,bus = Mod.EventBusSubscriber.Bus.MOD)
public class EventHandleMat {
    public static boolean isPreregLock() {
        return regLock;
    }
    private static boolean regLock = true;

    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void setup(FMLCommonSetupEvent event){
        {//在注册完成后创建CLASS-MFH表
            ImmutableMap.Builder<Class<?>,MaterialFeatureHandle<?>> builder = ImmutableMap.builder();
            for (MaterialFeatureHandle<?> materialFeatureHandle : RegistryMat.MATERIAL_FEATURE) {
                builder.put(materialFeatureHandle.clazz,materialFeatureHandle);
            }
            RegistryMat.MF_CLASS2MFH = builder.build();
        }
    }
}
