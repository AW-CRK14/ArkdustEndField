package com.arkdust.datagen;

import com.mojang.datafixers.util.Pair;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.neoforged.neoforge.registries.DeferredBlock;

import java.util.*;
import java.util.function.BiConsumer;

public class LootTableGen{

    public static LootTableProvider create(PackOutput packOutput){
        return new LootTableProvider(packOutput,Set.of(),List.of(
                new LootTableProvider.SubProviderEntry(BlockLoot::new, LootContextParamSets.BLOCK)
        ));
    }

    public static class BlockLoot extends BlockLootSubProvider{
        public static final List<Pair<DeferredBlock<?>, ILootFunc>> loots = new ArrayList<>();
        public static void add(DeferredBlock<?> block, ILootFunc loot){
            if(loot != null){
                loots.add(Pair.of(block, loot));
            }
        };
        protected BlockLoot() {
            super(Set.of(), FeatureFlags.REGISTRY.allFlags());
        }

        @Override
        protected void generate() {
            for (Pair<DeferredBlock<?>, ILootFunc> pair : loots){
                pair.getSecond().consumer(this,pair.getFirst().get());
            }
            //TODO ore:see createOreDrop
        }

        @Override
        public void dropSelf(Block pBlock) {
            super.dropSelf(pBlock);
        }

        @Override
        public void dropOther(Block pBlock, ItemLike pItem) {
            super.dropOther(pBlock, pItem);
        }

        public void generate(BiConsumer<ResourceLocation, LootTable.Builder> pOutput) {
            this.generate();
            Set<ResourceLocation> set = new HashSet<>();

            for (Block block : getKnownBlocks()) {
                if (block.isEnabled(this.enabledFeatures)) {
                    ResourceLocation resourcelocation = block.getLootTable();
                    if (resourcelocation != BuiltInLootTables.EMPTY && set.add(resourcelocation)) {
                        LootTable.Builder loottable$builder = this.map.remove(resourcelocation);
                        if(loottable$builder!=null) {
                            pOutput.accept(resourcelocation, loottable$builder);
                        }
                    }
                }
            }
        }
    }

    public interface ILootFunc {
        void consumer(BlockLoot loot,Block block);
    }

    public static final ILootFunc SELF = BlockLoot::dropSelf;
    public static ILootFunc self(){return SELF;}
    public static ILootFunc other(ItemLike itemLike){return ((loot, block) -> loot.dropOther(block,itemLike));}
}
