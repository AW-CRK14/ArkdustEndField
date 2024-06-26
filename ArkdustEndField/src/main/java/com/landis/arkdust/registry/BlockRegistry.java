package com.landis.arkdust.registry;

import com.landis.arkdust.Arkdust;
import com.landis.arkdust.blocks.ExplainableBlock;
import com.landis.arkdust.blocks.levelblocks.SpiritStoneBlocks;
import com.landis.arkdust.blocks.levelblocks.ThermoBlocks;
import com.landis.arkdust.datagen.BlockStateGen;
import com.landis.arkdust.datagen.BlockTagGen;
import com.landis.arkdust.datagen.ItemExplainGen;
import com.landis.arkdust.datagen.LootTableGen;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class BlockRegistry {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(Arkdust.MODID);

    public static final DeferredBlock<Block> SPIRIT_STONE_ACTIVATED = Builder.create("spirit_stone_activated", BlockBehaviour.Properties.ofFullCopy(Blocks.OBSIDIAN).lightLevel((i) -> 8), true).breakLev(3).state(BlockStateGen.cubeAll()).tool(BlockTagGen.ToolType.PICKAXE).loot(LootTableGen.SELF).build();
    public static final DeferredBlock<Block> SPIRIT_STONE_UNACTIVATED = Builder.create("spirit_stone_unactivated", SpiritStoneBlocks.Unactivated::new).breakLev(4).state(BlockStateGen.cubeAll()).tool(BlockTagGen.ToolType.PICKAXE).loot(LootTableGen.other(SPIRIT_STONE_ACTIVATED)).build();
    public static final DeferredBlock<Block> SPIRIT_STONE = Builder.create("spirit_stone", BlockBehaviour.Properties.ofFullCopy(Blocks.OBSIDIAN).lightLevel((i) -> 2), true).breakLev(3).state(BlockStateGen.cubeAll()).tool(BlockTagGen.ToolType.PICKAXE).loot(LootTableGen.SELF).build();
    public static final DeferredBlock<Block> SPIRIT_STELA = Builder.create("spirit_stela", SpiritStoneBlocks.Stela::new, true).breakLev(3).state(BlockStateGen.specialModel(new ResourceLocation(Arkdust.MODID, "block/spirit_stela"))).tool(BlockTagGen.ToolType.PICKAXE).loot(LootTableGen.SELF).build();
    public static final DeferredBlock<Block> SPIRIT_PORTAL = Builder.create("spirit_portal", SpiritStoneBlocks.Portal::new, false).breakLev(3).state(BlockStateGen.cubeAll()).build();
    //public static final DeferredBlock<Block> BASIC_THERM_BLOCK = Builder.create("basic_therm_block",()-> new BasicThermBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.OBSIDIAN)),false).breakLev(3).state(BlockStateGen.cubeAll()).build();
//    public static final DeferredBlock<Block> TEST_GENERATOR_BLOCK = Builder.create("test_generator_block",()-> new TestGeneratorBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.OBSIDIAN)),false).breakLev(3).state(BlockStateGen.cubeAll()).build();
//    public static final DeferredBlock<Block> TEST_MACHINE_BLOCK = Builder.create("test_machine_block",()-> new TestMachineBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.OBSIDIAN)),false).breakLev(3).state(BlockStateGen.cubeAll()).build();


    public static final DeferredBlock<Block> TEST_COMBUSTOR = BLOCKS.register("test_combustor", () -> new ThermoBlocks.CombustorBlock(2000, 0.2F));
    public static final DeferredBlock<Block> TEST_CARRIER = BLOCKS.register("test_carrier", ThermoBlocks.CarrierBlock::new);


    private static class Builder {
        private final DeferredBlock<Block> obj;
        private int breakLevel = 0;
        private BlockTagGen.ToolType toolType = null;
        private BlockStateGen.IStateFunc stateFunc = null;
        private LootTableGen.ILootFunc lootFunc = null;
        private boolean explain = false;

        private Builder(DeferredBlock<Block> obj) {
            this.obj = obj;
        }

        protected static Builder create(DeferredBlock<Block> obj) {
            return new Builder(obj);
        }

        protected static Builder create(String name, Supplier<? extends Block> supplier) {
            return new Builder(BLOCKS.register(name, supplier));
        }

        protected static Builder create(String name, Supplier<? extends Block> supplier, boolean explain) {
            return new Builder(BLOCKS.register(name, supplier)).explain(explain);
        }

        protected static Builder create(String name, BlockBehaviour.Properties properties) {
            return new Builder(BLOCKS.register(name, () -> new ExplainableBlock(properties, false)));
        }

        protected static Builder create(String name, BlockBehaviour.Properties properties, boolean explain) {
            return new Builder(BLOCKS.register(name, () -> new ExplainableBlock(properties, explain))).explain();
        }

        protected Builder breakLev(int level) {
            breakLevel = level;
            return this;
        }

        protected Builder tool(BlockTagGen.ToolType type) {
            this.toolType = type;
            return this;
        }

        protected Builder state(BlockStateGen.IStateFunc func) {
            this.stateFunc = func;
            return this;
        }

        protected Builder loot(LootTableGen.ILootFunc func) {
            this.lootFunc = func;
            return this;
        }

        protected Builder explain() {
            this.explain = true;
            return this;
        }

        protected Builder explain(boolean explain) {
            this.explain = explain;
            return this;
        }

        protected DeferredBlock<Block> build() {
            BlockTagGen.add(obj, toolType, breakLevel);
            BlockStateGen.add(obj, stateFunc);
            LootTableGen.BlockLoot.add(obj, lootFunc);
            ItemExplainGen.addBlock(obj, explain);
            return obj;
        }
    }
}
