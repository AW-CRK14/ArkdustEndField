package com.landis.breakdowncore;


import com.mojang.logging.LogUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ItemModelGenerator;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import org.slf4j.Logger;

import java.io.IOException;

@Mod(BreakdownCore.MODID)
public class BreakdownCore {
    public static final String MODID = "brea";
    public static final Logger LOGGER = LogUtils.getLogger();

    public BreakdownCore(IEventBus modEventBus) throws IOException {

        Registries.ITEM.register(modEventBus);
        Registries.TAB.register(modEventBus);
        Registries.MaterialReg.MATERIAL.register(modEventBus);
        Registries.MaterialReg.FEATURE.register(modEventBus);
        Registries.MaterialReg.TYPE.register(modEventBus);
    }

    private static ItemModelGenerator ITEM_MODELGEN;
    public static ItemModelGenerator getItemModelgen(){
        if(ITEM_MODELGEN == null){
            ITEM_MODELGEN = new ItemModelGenerator();
        }
        return ITEM_MODELGEN;
    }

    public static boolean checkResource(ResourceLocation location){
        return Minecraft.getInstance().getResourceManager().getResource(location).isPresent();
    }

    public static ResourceLocation covertToModelID(ResourceLocation location){
        return location.withPath(s -> "models/" + s + ".json");
    }
}
