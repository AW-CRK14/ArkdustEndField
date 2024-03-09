package com.landis.breakdowncore.material;

import com.google.common.collect.ImmutableMap;
import com.landis.breakdowncore.BreakdownCore;
import net.minecraft.core.Holder;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import oshi.util.tuples.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

/**本类用于处理在注册与注册前阶段的各种额外处理需求*/
@Mod.EventBusSubscriber(modid = BreakdownCore.MODID,bus = Mod.EventBusSubscriber.Bus.MOD)
public class EventHandleMat {
    public static boolean isDataGatherStageFinished() {
        return regLock;
    }
    private static boolean regLock = false;

    public static boolean isFinalDataHandleFinished() {
        return regLock;
    }
    private static boolean buildLock = false;



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
