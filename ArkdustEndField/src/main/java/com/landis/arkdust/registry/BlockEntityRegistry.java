package com.landis.arkdust.registry;

import com.landis.arkdust.Arkdust;
import com.landis.arkdust.blockentity.portal.SpiritPortalBlockEntity;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class BlockEntityRegistry {
    public static final DeferredRegister<BlockEntityType<?>> REGISTER = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, Arkdust.MODID);

    public static final DeferredHolder<BlockEntityType<?>,BlockEntityType<SpiritPortalBlockEntity>> SPIRIT_PORTAL =
            REGISTER.register("spirit_portal",()->BlockEntityType.Builder.of(SpiritPortalBlockEntity::new,BlockRegistry.SPIRIT_PORTAL.get()).build(null));
   // public static final DeferredHolder<BlockEntityType<?>,BlockEntityType<BasicThermBlock.Entity>> BASIC_THERM =
    //        REGISTER.register("basic_therm",()->BlockEntityType.Builder.of(BasicThermBlock.Entity::new,BlockRegistry.BASIC_THERM_BLOCK.get()).build(null));
    //TODO ThermoTest
//    public static final DeferredHolder<BlockEntityType<?>,BlockEntityType<TestGeneratorBlock.Entity>> TEST_GENERATOR =
//            REGISTER.register("test_generator",()->BlockEntityType.Builder.of(TestGeneratorBlock.Entity::new,BlockRegistry.TEST_GENERATOR_BLOCK.get()).build(null));
//    public static final DeferredHolder<BlockEntityType<?>,BlockEntityType<TestMachineBlock.Entity>> TEST_MACHINE =
//            REGISTER.register("test_machine",()->BlockEntityType.Builder.of(TestMachineBlock.Entity::new,BlockRegistry.TEST_MACHINE_BLOCK.get()).build(null));
}
