package com.arkdust.worldgen.feature;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.SkullBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;

import java.util.ArrayList;
import java.util.List;

public class SingleBlockFeature extends Feature<SingleBlockFeature.BlocksConf> {
    public SingleBlockFeature(Codec<BlocksConf> pCodec) {
        super(pCodec);
    }

    @Override
    public boolean place(FeaturePlaceContext pContext) {
        WorldGenLevel level = pContext.level();
        if(level.getBlockState(pContext.origin()).isAir()){
            BlockState state = ((BlocksConf)pContext.config()).getElement(pContext.random());
            if(state.getBlock() instanceof SkullBlock){
                state = state.setValue(SkullBlock.ROTATION,pContext.random().nextInt(16));
            }
            level.setBlock(pContext.origin(),state,2);
            return true;
        }
        return false;
    }

    public record BlocksConf(List<BlockState> states) implements FeatureConfiguration{
        public BlocksConf(){ this(new ArrayList<>());}
        public BlocksConf(BlockState state){ this(List.of(state));}
        public BlocksConf(BlockState... states){ this(List.of(states));}

        public void addElement(BlockState state){
            states.add(state);
        }

        public void addElement(BlockState state,int count){
            for(int i = 0 ; i < count ; i++) {
                states.add(state);
            }
        }

        public BlockState getElement(RandomSource random){
            return states.get(random.nextInt(states.size()));
        }

        public List<BlockState> states(){
            return this.states;
        }

        public static final Codec<BlocksConf> CODEC = RecordCodecBuilder.create(obj -> obj.group(
                BlockState.CODEC.listOf().fieldOf("states").forGetter(p -> p.states)
        ).apply(obj,BlocksConf::new));
    }
}
