package com.landis.breakdowncore;


import com.landis.breakdowncore.module.registry.RegroupController;
import com.landis.breakdowncore.system.material.datagen.MaterialSpriteAttachGen;
import com.landis.breakdowncore.system.material.datagen.MitModelGen;
import com.mojang.logging.LogUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ItemModelGenerator;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.slf4j.Logger;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;

@Mod(BreakdownCore.MODID)
public class BreakdownCore {
    public static RegroupController REGISTER;

    public static final String MODID = "brea";
    public static final Logger LOGGER = LogUtils.getLogger();

    public BreakdownCore(IEventBus modEventBus) throws IOException {
        REGISTER = RegroupController.create(modEventBus,MODID,((event, regroupController) -> {
            PackOutput output = event.getGenerator().getPackOutput();
            ExistingFileHelper fileHelper = event.getExistingFileHelper();
            CompletableFuture<HolderLookup.Provider> lookup = event.getLookupProvider();

            regroupController.itemModelProvider.addAgency(new MitModelGen(output,fileHelper));
            regroupController.spriteProvider.addAgency(new MaterialSpriteAttachGen(output,lookup,fileHelper));
        }));
        BreaRegistries.ITEM.register(modEventBus);
        BreaRegistries.TAB.register(modEventBus);
        BreaRegistries.INGREDIENT_TYPE.register(modEventBus);
        BreaRegistries.MaterialReg.MATERIAL.register(modEventBus);
        BreaRegistries.MaterialReg.FEATURE.register(modEventBus);
        BreaRegistries.MaterialReg.TYPE.register(modEventBus);
        BreaRegistries.JsonCodecReg.LOOT_POOL.register(modEventBus);
    }

    private static ItemModelGenerator ITEM_MODELGEN;

    public static ItemModelGenerator getItemModelgen() {
        if (ITEM_MODELGEN == null) {
            ITEM_MODELGEN = new ItemModelGenerator();
        }
        return ITEM_MODELGEN;
    }

    public static boolean checkResource(ResourceLocation location) {
        return Minecraft.getInstance().getResourceManager().getResource(location).isPresent();
    }

    public static ResourceLocation covertToModelID(ResourceLocation location) {
        return location.withPath(s -> "models/" + s + ".json");
    }

    public static String getLogName(String type) {
        return MODID + "/" + type;
    }

}
