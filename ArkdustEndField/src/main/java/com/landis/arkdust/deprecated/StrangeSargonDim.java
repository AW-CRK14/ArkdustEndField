package com.landis.arkdust.deprecated;

import com.google.common.collect.Sets;
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

@Deprecated
final class StrangeSargonDim extends NoiseBasedChunkGenerator {
//        public static final Codec<YRelativeDimensionGenerator> CODEC = RecordCodecBuilder.create(obj -> obj.group(
//                            BiomeSource.CODEC.fieldOf("biome_source").forGetter(ins -> ins.biomeSource),
//                            NoiseGeneratorSettings.CODEC.fieldOf("settings").forGetter(NoiseBasedChunkGenerator::generatorSettings),
//                            Codec.FLOAT.fieldOf("scale").forGetter(ins -> ins.routerScaleFactor)
//                    ).apply(obj, obj.stable(YRelativeDimensionGenerator::new)));

    public final float routerScaleFactor;
    public final int noiseHeight;

    public StrangeSargonDim(BiomeSource pBiomeSource, Holder<NoiseGeneratorSettings> settings) {
        super(pBiomeSource, settings);
        this.routerScaleFactor = 0.6F;
        this.noiseHeight = settings.value().noiseSettings().height();
    }

    public int offsetYAt(int x, int z) {
        // 定义一个常量k，表示直线y = -2x + k的截距
        final int k = -1200;
        // 定义一个变量height，用于存储高度值
        int height = 0;
        // 判断坐标(x, y)是否在直线y = -2x + k上
        if (z == -2 * x + k) {
            // 如果在直线上，高度值为344
            height = 343;
        } else {
            // 计算坐标(x, y)到直线y = -2x + k的距离
            float distance = Math.abs(z + 2 * x - k) / 2.236F;
            // 计算坐标(x, y)到原点(0, 0)的距离
            double origin = Math.sqrt(x * x + z * z);
            if (z < -2 * x + k) {//沙漠半区高度计算
                double m = Math.min(1200 + origin / 20, 2400);
                if (distance < 40) {//断崖区高度计算
                    height = 344 - (int) (distance * 3.2F);
                } else if (distance < m) {//退化草原区高度计算
                    height = 216 - (int) (24 * distance / origin);
                } else {//沙漠区高度计算
                    height = 192;
                }
            } else {//雨林半区高度计算
                // 计算n的值，高山草甸边界
                double n = Math.min(1800 + origin / 40, 2000);
                // 计算p的值，稀疏雨林边界
                double p = Math.min(4000 + origin / 30, 5000);
                // 判断距离是否小于n
                if (distance < n) {//高山草甸高度计算
                    height = 344 - (int) (30 * distance / n);
                } else if (distance < p) {//稀疏雨林高度计算
                    height = 314 - (int) (50 * (distance - n) / p);
                } else {//密林高度计算
                    height = Math.max(264 - (int) (40 * (distance - p) / 4000), 228);
                }
            }
        }
        // 返回高度值
        return height - 160;
    }

    ;

    public int extremeYOffset(boolean max) {
        return max ? 343 - 160 : 192 - 160;
    }

    public int getMinY() {
        return 0;
    }

    public int getGenDepth() {
        return (int) (generatorSettings().value().noiseSettings().height() * routerScaleFactor + extremeYOffset(true));
    }

    @Override
    public void applyCarvers(WorldGenRegion pLevel, long pSeed, RandomState pRandom, BiomeManager pBiomeManager, StructureManager pStructureManager, ChunkAccess pChunk, GenerationStep.Carving pStep) {
        //TODO CARVERS
    }

    @Override
    public CompletableFuture<ChunkAccess> fillFromNoise(Executor pExecutor, Blender pBlender, RandomState pRandom, StructureManager pStructureManager, ChunkAccess pChunk) {
        NoiseSettings noisesettings = this.generatorSettings().value().noiseSettings().clampToHeightAccessor(pChunk.getHeightAccessorForGeneration());
        int minY = getMinY();
        int height = getGenDepth();
        int minCellIndex = Mth.floorDiv(minY, noisesettings.getCellHeight());
        int heightCell = Mth.floorDiv(height, noisesettings.getCellHeight());
        if (heightCell <= 0) {
            return CompletableFuture.completedFuture(pChunk);
        } else {
            int l = pChunk.getSectionIndex(heightCell * noisesettings.getCellHeight() - 1 + minY);
            int i1 = pChunk.getSectionIndex(minY);
            Set<LevelChunkSection> set = Sets.newHashSet();

            for (int j1 = l; j1 >= i1; --j1) {
                LevelChunkSection levelchunksection = pChunk.getSection(j1);
                levelchunksection.acquire();
                set.add(levelchunksection);
            }

            return CompletableFuture.supplyAsync(
                            Util.wrapThreadWithTaskName("wgen_fill_noise", () -> this.doFill(pBlender, pStructureManager, pRandom, pChunk, minCellIndex, heightCell)),
                            Util.backgroundExecutor()
                    )
                    .whenCompleteAsync((p_224309_, p_224310_) -> {
                        for (LevelChunkSection levelchunksection1 : set) {
                            levelchunksection1.release();
                        }
                    }, pExecutor);
        }
    }

    @Override
    public ChunkAccess doFill(Blender blender, StructureManager structureManager, RandomState random, ChunkAccess chunkAccess, int minCellY, int cellCountY) {
        //初始化我们需要的数据
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

        //进行区块地表内容生成
        for (int selectCellX = 0; selectCellX < cellCountWidth; selectCellX++) {
            noiseChunk.advanceCellX(selectCellX);
            for (int selectCellZ = 0; selectCellZ < cellCountWidth; selectCellZ++) {
                //获取一个列区块(chunkAccess)中的空间区块(levelChunkSection),由最高位置的区块开始获取
                int sectionIndex = chunkAccess.getSectionsCount() - 1;
                LevelChunkSection levelChunkSection = chunkAccess.getSection(sectionIndex);
                //预备单元选择
                int lastCellY = noiseHeight / cellHeight - 1;
                noiseChunk.selectCellYZ(lastCellY, selectCellZ);

                //执行在指定单元内的位置演算
                for (int x = 0; x < cellWidth; ++x) {
                    int blockX = chunkMinX + selectCellX * cellWidth + x;
                    int xInChunk = blockX & 15;
                    double xRatio = (double) x / (double) cellWidth;
                    noiseChunk.updateForX(blockX, xRatio);
                    for (int z = 0; z < cellWidth; ++z) {
                        int blockZ = chunkMinZ + selectCellZ * cellWidth + z;
                        int zInChunk = blockZ & 15;
                        double zRatio = (double) z / (double) cellWidth;
                        noiseChunk.updateForZ(blockZ, zRatio);

                        //进行高度演算
                        //预计算指定位置的基础高度
                        int baseHeight = offsetYAt(blockX, blockZ);
                        for (int noiseY = noiseHeight - 1; noiseY >= 0; noiseY--) {
                            int y = (int) (baseHeight + (generatorSettings().value().noiseSettings().minY() + noiseY) * routerScaleFactor);

                            //确定噪音区块的单元位置
                            if (lastCellY != noiseY / cellHeight) {
                                lastCellY = noiseY / cellHeight;
                                noiseChunk.selectCellYZ(lastCellY, selectCellZ);
                            }

                            //进行检查区块选定
                            int nIndex = chunkAccess.getSectionIndex(y);
                            if (sectionIndex != nIndex) {
                                sectionIndex = nIndex;
                                levelChunkSection = chunkAccess.getSection(nIndex);
                            }

                            noiseChunk.updateForY(lastCellY * cellHeight, (noiseY - lastCellY * cellHeight) / (double) cellHeight);
                            BlockState state = Objects.requireNonNullElse(noiseChunk.getInterpolatedState(), generatorSettings().value().defaultBlock());
                            //表示到达最低高度或检测到位置非空
                            if (noiseY == 0 || !state.isAir()) {
                                //进行方块放置
                                for (int blockY = y; blockY >= getMinY(); blockY--) {
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
        return chunkAccess;
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
        return OptionalInt.of((int) getActuallyHeight(i.orElse(0), x, z));
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

    public float getActuallyHeight(int noiseHeight, int x, int z) {
        return offsetYAt(x, z) + noiseHeight * routerScaleFactor;
    }
}
