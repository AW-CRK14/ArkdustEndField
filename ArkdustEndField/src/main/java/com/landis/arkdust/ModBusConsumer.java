package com.landis.arkdust;

import com.landis.arkdust.blockentity.portal.SpiritPortalBlockEntity;
//import com.landis.arkdust.blocks.TestMachineBlock;
import com.landis.arkdust.datagen.*;
import com.landis.arkdust.registry.BlockEntityRegistry;
import com.landis.arkdust.registry.BlockRegistry;
import com.landis.arkdust.registry.regtype.ArkdustRegistry;
import com.landis.arkdust.registry.render.RenderTypeRegistry;
import com.landis.arkdust.registry.worldgen.level.BiomeRegistry;
import com.landis.arkdust.registry.worldgen.level.DimensionTypeRegistry;
import com.landis.arkdust.registry.worldgen.level.LevelStemRegistry;
import com.landis.arkdust.registry.worldgen.level.NoiseGenSettingRegistry;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterShadersEvent;
import net.neoforged.neoforge.common.data.DatapackBuiltinEntriesProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.neoforged.neoforge.registries.NewRegistryEvent;

import java.io.IOException;
import java.util.Collections;
import java.util.concurrent.CompletableFuture;

@Mod.EventBusSubscriber(modid = Arkdust.MODID,bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModBusConsumer {
    private static final RegistrySetBuilder BUILDER = new RegistrySetBuilder()
            .add(Registries.NOISE_SETTINGS, NoiseGenSettingRegistry::bootstrap)
            .add(Registries.DIMENSION_TYPE, DimensionTypeRegistry::bootstrap)
            .add(Registries.LEVEL_STEM, LevelStemRegistry::bootstrap)
//            .add(Registries.PROCESSOR_LIST, StructureProcessorListRegistry::bootstrap)
//            .add(Registries.STRUCTURE, StructureRegistry::bootstrap)
//            .add(Registries.TEMPLATE_POOL, ExtraStructureJigsawPool::bootstrap)
//            .add(Registries.DAMAGE_TYPE, DamageTypes::bootstrap)
//            .add(Registries.STRUCTURE_SET, ExtraStructureSet::bootstrap)
//            .add(Registries.CONFIGURED_FEATURE, ConfiguredFeatureRegistry::bootstrap);
//            .add(Registries.PLACED_FEATURE, PlacedFeatureRegistry::bootstrap);
            .add(Registries.BIOME, BiomeRegistry::bootstrap);

    //仅用于生成数据包 不在正常游戏过程中触发
    @SubscribeEvent
    public static void gatherData(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        PackOutput output = event.getGenerator().getPackOutput();
        ExistingFileHelper fileHelper = event.getExistingFileHelper();
        CompletableFuture<HolderLookup.Provider> lookup = event.getLookupProvider();

        generator.addProvider(event.includeClient(),new BlockStateGen(output,fileHelper));
        generator.addProvider(event.includeClient(),new ItemModelGen(output,fileHelper));
        generator.addProvider(event.includeClient(),new ItemExplainGen(output));

        generator.addProvider(event.includeClient(), LootTableGen.create(output));
        BlockTagGen blockTagGen = generator.addProvider(event.includeServer(),new BlockTagGen(output,lookup,fileHelper));
        generator.addProvider(event.includeServer(),new ItemTagGen(output,lookup,blockTagGen.contentsGetter(),fileHelper));

        generator.addProvider(event.includeServer(),new DatapackBuiltinEntriesProvider(output,lookup,BUILDER, Collections.singleton(Arkdust.MODID)));
    }

    @SubscribeEvent
    public static void blockEntityRenderer(EntityRenderersEvent.RegisterRenderers event){
        //Block entities renderer registry
        event.registerBlockEntityRenderer(BlockEntityRegistry.SPIRIT_PORTAL.get(), SpiritPortalBlockEntity.Renderer::new);
    }

//    @SubscribeEvent
//    public static void renderTypeRegistry(RegisterNamedRenderTypesEvent event){
//        event.register(new ResourceLocation(Arkdust.MODID,"spirit_portal"),RenderType.cutout(),RenderType.entityCutout(new ResourceLocation(Arkdust.MODID,"empty")), RenderTypeRegistry.SPIRIT_PORTAL);
//    }

    @SubscribeEvent
    public static void shadersRegistry(RegisterShadersEvent event) throws IOException {
        event.registerShader(new ShaderInstance(event.getResourceProvider(),new ResourceLocation(Arkdust.MODID,"spirit_portal"), DefaultVertexFormat.BLOCK),instance -> RenderTypeRegistry.SHADERINS_SPIRIT_PORTAL = instance);
    }

    @SubscribeEvent
    public static void newRegistry(NewRegistryEvent event){
        event.register(ArkdustRegistry.CLIMATE_PARAMETER);
        event.register(ArkdustRegistry.WEATHER);
        event.register(ArkdustRegistry.WEATHER_PROVIDER);
    }

    @SubscribeEvent
    public static void attachCapabilities(RegisterCapabilitiesEvent event){//TODO ThermoTest
//        event.registerBlock(Capabilities.ItemHandler.BLOCK,(block, pos, state, entity, direction)->{
//            if(entity instanceof TestMachineBlock.Entity t){
//                return t.itemHandler;
//            }
//            return new ItemStackHandler(3);
//        }, BlockRegistry.TEST_MACHINE_BLOCK.get());
    }


//    @SubscribeEvent
//    public static void capabilityRegistry(RegisterCapabilitiesEvent event){
//        event.registerBlockEntity(ArkdustCapability.HeatIndustryCap.BLOCK,BlockEntityRegistry.HEAT_BLOCK.get(),(object, context) -> new HeatCapability(300));
//    }
}
