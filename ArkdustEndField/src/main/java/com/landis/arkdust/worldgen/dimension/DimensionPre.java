package com.landis.arkdust.worldgen.dimension;

import com.google.common.collect.Sets;
import it.unimi.dsi.fastutil.ints.IntIntPair;
import net.minecraft.SharedConstants;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.util.Mth;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.NoiseColumn;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.biome.BiomeManager;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.levelgen.*;
import net.minecraft.world.level.levelgen.blending.Blender;
import org.apache.commons.lang3.mutable.MutableObject;

import javax.annotation.Nullable;
import java.util.Objects;
import java.util.OptionalInt;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Predicate;

public class DimensionPre {
    public static abstract class YRelativeDimensionGenerator extends NoiseBasedChunkGenerator {
//        public static final Codec<YRelativeDimensionGenerator> CODEC = RecordCodecBuilder.create(obj -> obj.group(
//                            BiomeSource.CODEC.fieldOf("biome_source").forGetter(ins -> ins.biomeSource),
//                            NoiseGeneratorSettings.CODEC.fieldOf("settings").forGetter(NoiseBasedChunkGenerator::generatorSettings),
//                            Codec.FLOAT.fieldOf("scale").forGetter(ins -> ins.routerScaleFactor)
//                    ).apply(obj, obj.stable(YRelativeDimensionGenerator::new)));

        public final float routerScaleFactor;

        public YRelativeDimensionGenerator(BiomeSource pBiomeSource, Holder<NoiseGeneratorSettings> settings, float routerScaleFactor) {
            super(pBiomeSource,settings);
            this.routerScaleFactor = routerScaleFactor;
        }

        @Override
        public int getMinY() {
            return (int) (generatorSettings().value().noiseSettings().minY() * routerScaleFactor + extremeYOffset(false));
        }

        @Override
        public int getGenDepth() {
            return (int) (generatorSettings().value().noiseSettings().height() * routerScaleFactor + extremeYOffset(true) - extremeYOffset(false));
        }

        public abstract int offsetYAt(int x, int z);

        public abstract int extremeYOffset(boolean max);


        @Override
        public CompletableFuture<ChunkAccess> fillFromNoise(Executor pExecutor, Blender pBlender, RandomState pRandom, StructureManager pStructureManager, ChunkAccess pChunk) {
            NoiseSettings noisesettings = this.generatorSettings().value().noiseSettings().clampToHeightAccessor(pChunk.getHeightAccessorForGeneration());
            int minY = getMinY();
            int minCellIndex = Mth.floorDiv(minY, noisesettings.getCellHeight());
            int heightCell = Mth.floorDiv(getGenDepth(), noisesettings.getCellHeight());
            if (heightCell <= 0) {
                return CompletableFuture.completedFuture(pChunk);
            } else {
                int l = pChunk.getSectionIndex(heightCell * noisesettings.getCellHeight() - 1 + minY);
                int i1 = pChunk.getSectionIndex(minY);
                Set<LevelChunkSection> set = Sets.newHashSet();

                for(int j1 = l; j1 >= i1; --j1) {
                    LevelChunkSection levelchunksection = pChunk.getSection(j1);
                    levelchunksection.acquire();
                    set.add(levelchunksection);
                }

                return CompletableFuture.supplyAsync(
                                Util.wrapThreadWithTaskName("wgen_fill_noise", () -> this.doFill(pBlender, pStructureManager, pRandom, pChunk, minCellIndex, heightCell)),
                                Util.backgroundExecutor()
                        )
                        .whenCompleteAsync((p_224309_, p_224310_) -> {
                            for(LevelChunkSection levelchunksection1 : set) {
                                levelchunksection1.release();
                            }
                        }, pExecutor);
            }
        }

        @Override
        public ChunkAccess doFill(Blender blender, StructureManager structureManager, RandomState random, ChunkAccess chunkAccess, int minCellY, int cellCountY) {
            NoiseChunk noiseChunk = chunkAccess.getOrCreateNoiseChunk(access -> this.createNoiseChunk(access, structureManager, blender, random));
            Heightmap oceanFloorHeightmap = chunkAccess.getOrCreateHeightmapUnprimed(Heightmap.Types.OCEAN_FLOOR_WG);
            Heightmap worldSurfaceHeightmap = chunkAccess.getOrCreateHeightmapUnprimed(Heightmap.Types.WORLD_SURFACE_WG);
            ChunkPos chunkPos = chunkAccess.getPos();
            int chunkMinX = chunkPos.getMinBlockX();
            int chunkMinZ = chunkPos.getMinBlockZ();
            Aquifer aquifer = noiseChunk.aquifer();
            noiseChunk.initializeForFirstCellX();
            BlockPos.MutableBlockPos mutableBlockPos = new BlockPos.MutableBlockPos();
            int cellWidth = noiseChunk.cellWidth();
            int cellHeight = noiseChunk.cellHeight();
            int cellCountWidth = 16 / cellWidth;
            int noiseHeight = generatorSettings().value().noiseSettings().height();
            int lastCellY = -127;

            //进行区块地表内容生成
            for(int selectCellX = 0 ; selectCellX < cellCountWidth ; ++selectCellX){
                noiseChunk.advanceCellX(selectCellX);
                for (int selectCellZ = 0 ; selectCellZ < cellCountWidth ; ++selectCellZ) {
                    //获取一个列区块(chunkAccess)中的空间区块(levelChunkSection),由最高位置的区块开始获取
                    int sectionIndex = chunkAccess.getSectionsCount() - 1;
                    LevelChunkSection levelChunkSection = chunkAccess.getSection(sectionIndex);
                    //预备单元选择
//                    noiseChunk.selectCellYZ(lastCellY,selectCellZ);

                    //执行在指定单元内的位置演算
                    for (int x = 0; x < cellWidth; x++) {
                        int blockX = chunkMinX + selectCellX * cellWidth + x;
                        int xInChunk = blockX & 15;
                        double xRatio = (double) x / (double) cellWidth;
                        for (int z = 0; z < cellWidth; z++) {
                            int blockZ = chunkMinZ + selectCellZ * cellWidth + z;
                            int zInChunk = blockZ & 15;
                            double zRatio = (double) z / (double) cellWidth;

                            //进行高度演算
                            //预计算指定位置的基础高度
                            int baseHeight = offsetYAt(blockX, blockZ);
                            for (int noiseY = noiseHeight - 1; noiseY >= 0 ; noiseY--) {
                                int y = (int) (baseHeight + (generatorSettings().value().noiseSettings().minY() + noiseY)* routerScaleFactor);
//                                int y = baseHeight + (generatorSettings().value().noiseSettings().minY() + noiseY);

                                //确定噪音区块的单元位置
                                lastCellY = noiseY / cellHeight;
                                noiseChunk.selectCellYZ(lastCellY, selectCellZ);
//                              noiseChunk.selectCellYZ(lastCellY, 0);

                                noiseChunk.updateForY(noiseY, (noiseY - lastCellY * cellHeight)/(double)cellHeight);
                                noiseChunk.updateForX(blockX, xRatio);
                                noiseChunk.updateForZ(blockZ, zRatio);

                                BlockState state = Objects.requireNonNullElse(noiseChunk.getInterpolatedState(),generatorSettings().value().defaultBlock());
                                //表示到达最低高度或检测到位置非空
//                                if(noiseY == 0 || !state.isAir()){
                                if(!state.isAir()){
                                    state = generatorSettings().value().defaultBlock();
                                    //进行方块放置
                                    for (int blockY = y ; blockY >= getMinY() ; blockY--){
                                        int sectionIndexInChunk = chunkAccess.getSectionIndex(blockY);
                                        if (sectionIndex != sectionIndexInChunk) {
                                            sectionIndex = sectionIndexInChunk;
                                            levelChunkSection = chunkAccess.getSection(sectionIndex);
                                        }
                                        int yInChunk = blockY & 15;
                                        levelChunkSection.setBlockState(xInChunk, yInChunk, zInChunk, state, false);
                                        oceanFloorHeightmap.update(xInChunk, blockY, zInChunk, state);
                                        worldSurfaceHeightmap.update(xInChunk, blockY, zInChunk, state);
                                        if (aquifer.shouldScheduleFluidUpdate() && !state.getFluidState().isEmpty()) {
                                            mutableBlockPos.set(blockX, blockY, blockZ);
                                            chunkAccess.markPosForPostprocessing(mutableBlockPos);
                                        }
                                    }
                                    //跳出此位置的y检测
                                    break;
                                }
                            }
                        }
                    }
                }

                noiseChunk.swapSlices();
            }

            noiseChunk.stopInterpolation();
//            return chunkAccess;

//            for(int selectCellX = 0; selectCellX < cellCountWidth; ++selectCellX) {
//                noiseChunk.advanceCellX(selectCellX);
//
//                for(int selectCellZ = 0; selectCellZ < cellCountWidth; ++selectCellZ) {
//                    int sectionIndex = chunkAccess.getSectionsCount() - 1;
//                    LevelChunkSection levelchunksection = chunkAccess.getSection(sectionIndex);
//
//                    for(int selectCellY = cellWidth - 1; selectCellY >= 0; --selectCellY) {
//                        noiseChunk.selectCellYZ(selectCellY, selectCellZ);
//
//                        for(int yInCell = cellHeight - 1; yInCell >= 0; --yInCell) {
//                            int relativeY = (minCellY + selectCellY) * cellHeight + yInCell;
//
//                            noiseChunk.updateForY(relativeY, (double)yInCell / (double)cellHeight);
//
//                            for(int x = 0; x < cellWidth; ++x) {
//                                int blockX = chunkPos.getMinBlockX() + selectCellX * cellWidth + x;
//                                int xInChunk = blockX & 15;
//                                noiseChunk.updateForX(blockX, (double)x / (double)cellWidth);
//                                for(int z = 0; z < cellWidth; ++z) {
//                                    int blockZ = chunkPos.getMinBlockZ() + selectCellZ * cellWidth + z;
//                                    int zInChunk = blockZ & 15;
//                                    noiseChunk.updateForZ(blockZ, (double)z / (double)cellWidth);
//
//                                    BlockState blockstate = noiseChunk.getInterpolatedState();
//                                    if (blockstate == null) {
//                                        blockstate = generatorSettings().value().defaultBlock();
//                                    }
//
//                                    blockstate = this.debugPreliminarySurfaceLevel(noiseChunk, blockX, relativeY, blockZ, blockstate);
//                                    if (blockstate != Blocks.AIR.defaultBlockState() && !SharedConstants.debugVoidTerrain(chunkAccess.getPos())) {
//                                        for(int actuallyY = offsetYAt(blockX,blockZ) + relativeY ; actuallyY >= getMinY() ; actuallyY--) {
//                                            int index = chunkAccess.getSectionIndex(actuallyY);
//                                            if (sectionIndex != index) {
//                                                sectionIndex = index;
//                                                levelchunksection = chunkAccess.getSection(index);
//                                            }
//                                            levelchunksection.setBlockState(xInChunk, actuallyY & 15, zInChunk, blockstate, false);
//                                            worldSurfaceHeightmap.update(xInChunk, relativeY, zInChunk, blockstate);
//                                            oceanFloorHeightmap.update(xInChunk, relativeY, zInChunk, blockstate);
//                                            if (aquifer.shouldScheduleFluidUpdate() && !blockstate.getFluidState().isEmpty()) {
//                                                mutableBlockPos.set(blockX, relativeY, blockZ);
//                                                chunkAccess.markPosForPostprocessing(mutableBlockPos);
//                                            }
//                                        }
//                                    }
//
////                                    if (blockstate != Blocks.AIR.defaultBlockState() && !SharedConstants.debugVoidTerrain(chunkAccess.getPos())) {
////                                        levelchunksection.setBlockState(xInChunk,relativeY & 15, zInChunk, blockstate, false);
////                                        worldSurfaceHeightmap.update(xInChunk, relativeY, zInChunk, blockstate);
////                                        oceanFloorHeightmap.update(xInChunk, relativeY, zInChunk, blockstate);
////                                        if (aquifer.shouldScheduleFluidUpdate() && !blockstate.getFluidState().isEmpty()) {
////                                            mutableBlockPos.set(blockX, relativeY, blockZ);
////                                            chunkAccess.markPosForPostprocessing(mutableBlockPos);
////                                        }
////                                    }
//                                }
//                            }
//                        }
//                    }
//                }
//
//                noiseChunk.swapSlices();
//            }
//
//            noiseChunk.stopInterpolation();
            return chunkAccess;
        }

        public BlockState debugPreliminarySurfaceLevel(NoiseChunk pChunk, int pX, int pY, int pZ, BlockState pState) {
            return pState;
        }


        //        public void columnScale(MutableObject<NoiseColumn> column,RandomState random,int x,int z){
//            BlockState[] states = column.getValue().column;
//            float fr = 1F / routerScaleFactor;
//            int height = (int) (states.length * routerScaleFactor);
//            BlockState[] ne
//            for(int i = 0 ; i < height ; i++){
//                column.getValue().setBlock(());
//            }
//            column.setValue(new NoiseColumn(column.getValue().minY,));
//
//        }

        //注：此生成不考虑高版本的巨型洞穴
        public OptionalInt iterateNoiseColumn(
                LevelHeightAccessor pLevel,
                RandomState pRandom,
                int x,
                int z,
                @Nullable MutableObject<NoiseColumn> pColumn,
                @Nullable Predicate<BlockState> pStoppingState
        ) {
            OptionalInt i = super.iterateNoiseColumn(pLevel, pRandom, x, z, pColumn, pStoppingState);
            return OptionalInt.of((int) getActuallyHeight(i.orElse(0),x,z));
//            int cellHeight = generatorSettings().value().noiseSettings().getCellHeight();//单元高度
//            int minY = generatorSettings().value().noiseSettings().minY() + offsetYAt(x,z);//最小y值
//            int minCell = Mth.floorDiv(minY, cellHeight);//最小单元的索引编号，向下取整
//            int inRangeCell = Mth.floorDiv(generatorSettings().value().noiseSettings().height(), cellHeight);//处于范围内的单元的数量，向下取整
//
//            if (inRangeCell <= 0) {//如果范围内没有单元，返回空
//                return OptionalInt.empty();
//            } else {
//                //在有需求时，初始化NoiseColumn
//                BlockState[] ablockstate;
//                if (pColumn == null) {
//                    ablockstate = null;
//                } else {
//                    ablockstate = new BlockState[generatorSettings().value().noiseSettings().height()];
//                    pColumn.setValue(new NoiseColumn(minY, ablockstate));
//                }
//
//                //分别求出x与z方向的单元索引，单元内相对位置，单元位置，准确位置在单元中的相对位置
//                int cellWidth = generatorSettings().value().noiseSettings().getCellWidth();
//                int xCellIndex = Math.floorDiv(x, cellWidth);
//                int zCellIndex = Math.floorDiv(z, cellWidth);
//                int xOffset = Math.floorMod(x, cellWidth);
//                int zOffset = Math.floorMod(z, cellWidth);
//                int xCellFrom = xCellIndex * cellWidth;
//                int zCellFrom = zCellIndex * cellWidth;
//                double xOffsetInCell = (double) xOffset / (double) cellWidth;
//                double zOffsetInCell = (double) zOffset / (double) cellWidth;
//
//                //创建噪音区块 这部分我不想再额外搞了
//                NoiseChunk noisechunk = new NoiseChunk(
//                        1,
//                        pRandom,
//                        xCellFrom,
//                        zCellFrom,
//                        generatorSettings().value().noiseSettings(),
//                        DensityFunctions.BeardifierMarker.INSTANCE,
//                        this.generatorSettings().value(),
//                        this.globalFluidPicker.get(),
//                        Blender.empty()
//                );
//                noisechunk.initializeForFirstCellX();
//                noisechunk.advanceCellX(0);
//
//                //获取在噪音块中的相对高度位置
//                int uncheckedHeight = 0;
//                target:
//                for (int l2 = inRangeCell - 1; l2 >= 0; --l2) {
//                    noisechunk.selectCellYZ(l2, 0);
//                    for (int i3 = cellHeight - 1; i3 >= 0; --i3) {
//                        int j3 = (minCell + l2) * cellHeight + i3;
//                        double d2 = (double) i3 / (double) cellHeight;
//                        noisechunk.updateForY(j3, d2);
//                        noisechunk.updateForX(x, xOffsetInCell);
//                        noisechunk.updateForZ(z, zOffsetInCell);
//                        BlockState blockstate = noisechunk.getInterpolatedState();
//                        BlockState blockstate1 = blockstate == null ? this.generatorSettings().value().defaultBlock() : blockstate;
//                        if (ablockstate != null) {
//                            int k3 = l2 * cellHeight + i3;
//                            ablockstate[k3] = blockstate1;
//                        }
//
//                        if (pStoppingState != null && pStoppingState.test(blockstate1)) {
//                            noisechunk.stopInterpolation();
//                            uncheckedHeight = j3 + 1;
//                            break target;
//                        }
//                    }
//                }
//                noisechunk.stopInterpolation();
//
//                //进行高度再处理
//                return OptionalInt.of((int)getActuallyHeight(uncheckedHeight,x,z));
//            }
        }

        public float getActuallyHeight(int noiseHeight,int x,int z){
            return offsetYAt(x, z) + noiseHeight * routerScaleFactor;
        }
    }


}
