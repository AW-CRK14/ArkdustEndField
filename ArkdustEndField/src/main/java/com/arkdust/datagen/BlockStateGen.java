package com.arkdust.datagen;

import com.arkdust.Arkdust;
import com.mojang.datafixers.util.Pair;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.registries.DeferredBlock;

import java.util.ArrayList;
import java.util.List;

public class BlockStateGen extends BlockStateProvider {
    private static final List<Pair<DeferredBlock<?>, IStateFunc>> LIST = new ArrayList<>();
    public static void add(DeferredBlock<Block> block,IStateFunc func){
        if(func != null) LIST.add(Pair.of(block,func));
    }

    public BlockStateGen(PackOutput output, ExistingFileHelper exFileHelper) {
        super(output, Arkdust.MODID, exFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        LIST.forEach(pair -> pair.getSecond().consumer(this,pair.getFirst().get()));
    }

    public interface IStateFunc {
        void consumer(BlockStateGen gen,Block block);
    }

    protected static final IStateFunc CUBE_ALL = ((gen, block) -> gen.simpleBlockWithItem(block, gen.cubeAll(block)));
    public static IStateFunc cubeAll(){ return CUBE_ALL;}
    public static IStateFunc specialModel(ResourceLocation location){return ((gen, block) -> gen.simpleBlockWithItem(block, new ModelFile.UncheckedModelFile(location)));}

}
