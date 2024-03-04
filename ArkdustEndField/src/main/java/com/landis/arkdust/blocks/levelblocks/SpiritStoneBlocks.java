package com.landis.arkdust.blocks.levelblocks;

import com.landis.arkdust.blockentity.portal.SpiritPortalBlockEntity;
import com.landis.arkdust.blocks.ExplainableBlock;
import com.landis.arkdust.registry.BlockRegistry;
import com.landis.arkdust.worldgen.dimension.SarconDimension;
import com.landis.arkdust.helper.RenderHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;
import net.minecraft.world.level.block.state.pattern.BlockPattern;
import net.minecraft.world.level.block.state.pattern.BlockPatternBuilder;
import net.minecraft.world.level.block.state.predicate.BlockPredicate;
import net.minecraft.world.level.block.state.predicate.BlockStatePredicate;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.common.CommonHooks;
import net.neoforged.neoforge.common.util.ITeleporter;
import org.jetbrains.annotations.Nullable;

public class SpiritStoneBlocks {
    private static BlockPattern PATTERN;
    public static BlockPattern createPattern(){
        if(PATTERN!=null) return PATTERN;
        PATTERN = BlockPatternBuilder.start()
                .aisle("00xxxxx00",
                        "0xbbbbbx0",
                        "xbbbbbbbx",
                        "xbbbbbbbx",
                        "xbbbbbbbx",
                        "xbbbbbbbx",
                        "xbbbbbbbx",
                        "0xbbbbbx0",
                        "00xxxxx00")
                .aisle("000000000",
                        "0000K0000",
                        "00K000K00",
                        "000000000",
                        "0K00000K0",
                        "000000000",
                        "00K000K00",
                        "0000K0000",
                        "000000000")
                .aisle("000000000",
                        "0000n0000",
                        "00n000n00",
                        "000000000",
                        "0n00000n0",
                        "000000000",
                        "00n000n00",
                        "0000n0000",
                        "000000000")
                .where('0', BlockInWorld.hasState(BlockStatePredicate.ANY))
                .where('x', BlockInWorld.hasState(BlockPredicate.forBlock(Blocks.CRYING_OBSIDIAN)))
                .where('b', BlockInWorld.hasState(BlockPredicate.forBlock(BlockRegistry.SPIRIT_STONE.get())))
                .where('k', BlockInWorld.hasState(BlockPredicate.forBlock(BlockRegistry.SPIRIT_STELA.get())))
                .where('n', BlockInWorld.hasState(BlockPredicate.forBlock(Blocks.WITHER_SKELETON_SKULL)))
                .build();
        return PATTERN;
    }


    public static class Unactivated extends ExplainableBlock {
        public Unactivated() {
            super(BlockBehaviour.Properties.ofFullCopy(Blocks.OBSIDIAN).lightLevel((i)->1),false);
        }

        public float getDestroyProgress(BlockState pState, Player pPlayer, BlockGetter pLevel, BlockPos pPos) {
            if(CommonHooks.isCorrectToolForDrops(pState, pPlayer)){
                RenderHelper.spawnParticles(pPlayer.level(), pPos, ParticleTypes.SOUL_FIRE_FLAME,3);
                return super.getDestroyProgress(pState, pPlayer, pLevel, pPos);
            }
            RenderHelper.spawnParticles(pPlayer.level(), pPos, ParticleTypes.ENCHANT,2);
            return 0;
        }

        @Override
        public void onRemove(BlockState state, Level level, BlockPos pos, BlockState toState, boolean pMovedByPiston) {
            super.onRemove(state, level, pos, toState, pMovedByPiston);
            //condition checker
            if(!check(level, pos.offset(3,0,0)) || !check(level, pos.offset(2,0,2)) || !check(level, pos.offset(0,0,3)) || !check(level, pos.offset(-2,0,2)) ||
                    !check(level, pos.offset(-3,0,0)) || !check(level, pos.offset(-2,0,-2)) || !check(level, pos.offset(0,0,-3)) || !check(level, pos.offset(2,0,2)))
                return;
            for(int x = -4 ; x <= 4 ; x++) for (int z = -4 ; z <= 4 ; z++){
                int length = Math.abs(x) + Math.abs(z);
                if (length == 6 || ((Math.abs(x) == 4 || Math.abs(z) == 4)&&length <= 6)){
                    if(!level.getBlockState(pos.offset(x,-1,z)).is(BlockRegistry.SPIRIT_STONE_ACTIVATED)) return;
                }else if(length <= 5){
                    if(!level.getBlockState(pos.offset(x,-1,z)).is(BlockRegistry.SPIRIT_STONE)) return;
                }
            }

            //create
            level.setBlock(pos.offset(3,1,0),Blocks.AIR.defaultBlockState(),2);
            level.setBlock(pos.offset(2,1,2),Blocks.AIR.defaultBlockState(),2);
            level.setBlock(pos.offset(0,1,3),Blocks.AIR.defaultBlockState(),2);
            level.setBlock(pos.offset(-2,1,2),Blocks.AIR.defaultBlockState(),2);
            level.setBlock(pos.offset(-3,1,0),Blocks.AIR.defaultBlockState(),2);
            level.setBlock(pos.offset(-2,1,-2),Blocks.AIR.defaultBlockState(),2);
            level.setBlock(pos.offset(0,1,-3),Blocks.AIR.defaultBlockState(),2);
            level.setBlock(pos.offset(2,1,-2),Blocks.AIR.defaultBlockState(),2);
            for(int x = -4 ; x <= 4 ; x++) for (int z = -4 ; z <= 4 ; z++){
                int length = Math.abs(x) + Math.abs(z);
                if (length == 6 || ((Math.abs(x) == 4 || Math.abs(z) == 4)&&length <= 6)){
                    level.setBlock(pos.offset(x,-1,z),Blocks.CRYING_OBSIDIAN.defaultBlockState(),2);
                }else if(length <= 5){
                    level.setBlock(pos.offset(x,-1,z),BlockRegistry.SPIRIT_PORTAL.get().defaultBlockState(),2);
                }
            }
        }

        private boolean check(Level level,BlockPos pos){
            return level.getBlockState(pos).is(BlockRegistry.SPIRIT_STELA) && level.getBlockState(pos.above()).is(Blocks.WITHER_SKELETON_SKULL);
        }
    }

    public static class Stela extends ExplainableBlock {
        public Stela() {
            super(BlockBehaviour.Properties.ofFullCopy(Blocks.OBSIDIAN).lightLevel((i)->6).strength(20.0F, 200.0F).noOcclusion(),true);
        }

        @Override
        public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
            return Shapes.or(
                    Shapes.create(0,0,0,1,0.1875,1),
                    Shapes.create(0.0625,0.1875,0.0625,0.9375,0.3125,0.9375),
                    Shapes.create(0.25,0.3125,0.25,0.75,1,0.75),
                    Shapes.create(0.125,0.8125,0.125,0.875,0.9375,0.875)
            );
        }
    }

    public static class Portal extends ExplainableBlock implements EntityBlock , ITeleporter {
        public Portal() {
            super(BlockBehaviour.Properties.of()
                    .strength(-1.0F)
                    .sound(SoundType.GLASS)
                    .pushReaction(PushReaction.BLOCK)
                    .lightLevel((i)->15).noOcclusion(),false);
        }



        @Override
        public void onNeighborChange(BlockState state, LevelReader level, BlockPos pos, BlockPos neighbor) {
            super.onNeighborChange(state, level, pos, neighbor);
            if(level.getBlockState(pos).isAir() && level instanceof Level && !level.isClientSide()){
                ((Level) level).destroyBlock(pos,false);
            }
        }




        @Override
        public BlockState updateShape(BlockState pState, Direction pDirection, BlockState pNeighborState, LevelAccessor pLevel, BlockPos pPos, BlockPos pNeighborPos) {
            if(pDirection.getAxis().equals(Direction.Axis.Y) || !pNeighborState.isAir()){
                return super.updateShape(pState, pDirection, pNeighborState, pLevel, pPos, pNeighborPos);
            }
            return Blocks.AIR.defaultBlockState();
        }

        @Override
        public boolean skipRendering(BlockState pState, BlockState pAdjacentState, Direction pDirection) {
            return true;
        }

        @Nullable
        @Override
        public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
            return new SpiritPortalBlockEntity(pPos,pState);
        }

        @Override
        public void updateEntityAfterFallOn(BlockGetter pLevel, Entity pEntity) {
            if(pEntity instanceof Player && pLevel instanceof ServerLevel serverLevel){
                pEntity.changeDimension(serverLevel.getServer().getLevel(SarconDimension.LEVEL),this);
            }
        }
    }
}
