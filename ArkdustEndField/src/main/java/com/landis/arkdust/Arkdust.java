package com.landis.arkdust;

import com.landis.arkdust.registry.*;
import com.landis.arkdust.registry.render.RenderTypeRegistry;
import com.landis.arkdust.registry.system.ClimateParameterRegistry;
import com.landis.arkdust.registry.worldgen.detector.ConfiguredFeatureRegistry;
import com.landis.arkdust.registry.worldgen.detector.FeatureRegistry;
import com.landis.arkdust.registry.worldgen.detector.PlacedFeatureRegistry;
import com.landis.arkdust.registry.worldgen.level.BiomeSourceRegistry;
import com.landis.arkdust.registry.worldgen.level.ChunkGeneratorRegistry;
import com.landis.arkdust.registry.worldgen.level.RuleSourceRegistry;
import com.landis.arkdust.resource.Tags;
import com.mojang.logging.LogUtils;
import net.minecraft.core.Direction;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import org.slf4j.Logger;

@Mod(Arkdust.MODID)
public class Arkdust
{
    public static final String MODID = "arkdust";
    private static final Logger LOGGER = LogUtils.getLogger();

    //ModId/type.element.xxx
    public static String getLogName(String type){
        return MODID + "/" + type;
    }

    public static final Direction[] DIRECTIONS = Direction.values();


    public Arkdust(IEventBus bus)
    {
        ItemRegistry.bootstrap(bus);
        BlockRegistry.BLOCKS.register(bus);
        CreativeTabRegistry.TABS.register(bus);
        BlockEntityRegistry.REGISTER.register(bus);
        DataAttachmentRegistry.REGISTER.register(bus);

//        DimensionTypeRegistry.REGISTER.register(bus);
        RuleSourceRegistry.REGISTRY.register(bus);
        FeatureRegistry.REGISTER.register(bus);
        ConfiguredFeatureRegistry.registry(bus);
        PlacedFeatureRegistry.registry(bus);
        BiomeSourceRegistry.REGISTER.register(bus);
        ChunkGeneratorRegistry.REGISTER.register(bus);

        RenderTypeRegistry.bootstrap();

        MenuTypeRegistry.REGISTER.register(bus);

        ClimateParameterRegistry.REGISTER.register(bus);


        Prepared.bootstrap();
        Tags.bootstrap();

        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.SPEC);

    }
}
/*TODO 1.0.0
雪地灵魂石生成  沙漠骷髅头生成
灵魂熵减水晶方块
传送门多方块的实现
传送积点环境变化

模型：转变后中间区模型，含材质变化
材质：灵魂熵减水晶(源晶体)
 */
