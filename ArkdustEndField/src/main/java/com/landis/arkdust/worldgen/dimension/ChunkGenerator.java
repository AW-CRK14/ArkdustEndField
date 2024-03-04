//package com.arkdust.worldgen.dimension;
//
//import com.google.common.collect.Lists;
//import com.mojang.serialization.Codec;
//import java.util.BitSet;
//import java.util.List;
//import java.util.ListIterator;
//import java.util.Random;
//import java.util.function.Function;
//import java.util.function.Supplier;
//import javax.annotation.Nullable;
//import net.minecraft.crash.CrashReport;
//import net.minecraft.crash.CrashReportCategory;
//import net.minecraft.crash.ReportedException;
//import net.minecraft.entity.EntityClassification;
//import net.minecraft.network.DebugPacketSender;
//import net.minecraft.util.SharedSeedRandom;
//import net.minecraft.util.math.BlockPos;
//import net.minecraft.util.math.ChunkPos;
//import net.minecraft.util.math.SectionPos;
//import net.minecraft.util.registry.DynamicRegistries;
//import net.minecraft.util.registry.Registry;
//import net.minecraft.world.IBlockReader;
//import net.minecraft.world.ISeedReader;
//import net.minecraft.world.IWorld;
//import net.minecraft.world.biome.Biome;
//import net.minecraft.world.biome.BiomeContainer;
//import net.minecraft.world.biome.BiomeGenerationSettings;
//import net.minecraft.world.biome.BiomeManager;
//import net.minecraft.world.biome.MobSpawnInfo;
//import net.minecraft.world.biome.provider.BiomeProvider;
//import net.minecraft.world.chunk.ChunkPrimer;
//import net.minecraft.world.chunk.IChunk;
//import net.minecraft.world.gen.carver.ConfiguredCarver;
//import net.minecraft.world.gen.feature.StructureFeature;
//import net.minecraft.world.gen.feature.structure.Structure;
//import net.minecraft.world.gen.feature.structure.StructureFeatures;
//import net.minecraft.world.gen.feature.structure.StructureManager;
//import net.minecraft.world.gen.feature.structure.StructureStart;
//import net.minecraft.world.gen.feature.template.TemplateManager;
//import net.minecraft.world.gen.settings.DimensionStructuresSettings;
//import net.minecraft.world.gen.settings.StructureSeparationSettings;
//import net.minecraft.world.gen.settings.StructureSpreadSettings;
//import net.minecraft.world.server.ServerWorld;
//import net.minecraftforge.api.distmarker.Dist;
//import net.minecraftforge.api.distmarker.OnlyIn;
//
//public abstract class ChunkGenerator {
//    public static final Codec<ChunkGenerator> CODEC = Registry.CHUNK_GENERATOR.dispatchStable(ChunkGenerator::codec, Function.identity());
//    protected final BiomeProvider biomeSource;
//    protected final BiomeProvider runtimeBiomeSource;
//    private final DimensionStructuresSettings settings;
//    private final long strongholdSeed;
//    private final List<ChunkPos> strongholdPositions = Lists.newArrayList();
//
//    public ChunkGenerator(BiomeProvider p_i231888_1_, DimensionStructuresSettings p_i231888_2_) {
//        this(p_i231888_1_, p_i231888_1_, p_i231888_2_, 0L);
//    }
//
//    public ChunkGenerator(BiomeProvider p_i231887_1_, BiomeProvider p_i231887_2_, DimensionStructuresSettings p_i231887_3_, long p_i231887_4_) {
//        this.biomeSource = p_i231887_1_;
//        this.runtimeBiomeSource = p_i231887_2_;
//        this.settings = p_i231887_3_;
//        this.strongholdSeed = p_i231887_4_;
//    }
//
//    private void generateStrongholds() {
//        if (this.strongholdPositions.isEmpty()) {
//            StructureSpreadSettings structurespreadsettings = this.settings.stronghold();
//            if (structurespreadsettings != null && structurespreadsettings.count() != 0) {
//                List<Biome> list = Lists.newArrayList();
//
//                for(Biome biome : this.biomeSource.possibleBiomes()) {
//                    if (biome.getGenerationSettings().isValidStart(Structure.STRONGHOLD)) {
//                        list.add(biome);
//                    }
//                }
//
//                int k1 = structurespreadsettings.distance();
//                int l1 = structurespreadsettings.count();
//                int i = structurespreadsettings.spread();
//                Random random = new Random();
//                random.setSeed(this.strongholdSeed);
//                double d0 = random.nextDouble() * Math.PI * 2.0D;
//                int j = 0;
//                int k = 0;
//
//                for(int l = 0; l < l1; ++l) {
//                    double d1 = (double)(4 * k1 + k1 * k * 6) + (random.nextDouble() - 0.5D) * (double)k1 * 2.5D;
//                    int i1 = (int)Math.round(Math.cos(d0) * d1);
//                    int j1 = (int)Math.round(Math.sin(d0) * d1);
//                    BlockPos blockpos = this.biomeSource.findBiomeHorizontal((i1 << 4) + 8, 0, (j1 << 4) + 8, 112, list::contains, random);
//                    if (blockpos != null) {
//                        i1 = blockpos.getX() >> 4;
//                        j1 = blockpos.getZ() >> 4;
//                    }
//
//                    this.strongholdPositions.add(new ChunkPos(i1, j1));
//                    d0 += (Math.PI * 2D) / (double)i;
//                    ++j;
//                    if (j == i) {
//                        ++k;
//                        j = 0;
//                        i = i + 2 * i / (k + 1);
//                        i = Math.min(i, l1 - l);
//                        d0 += random.nextDouble() * Math.PI * 2.0D;
//                    }
//                }
//
//            }
//        }
//    }
//
//    protected abstract Codec<? extends ChunkGenerator> codec();
//
//    @OnlyIn(Dist.CLIENT)
//    public abstract ChunkGenerator withSeed(long p_230349_1_);
//
//    public void createBiomes(Registry<Biome> p_242706_1_, IChunk p_242706_2_) {
//        ChunkPos chunkpos = p_242706_2_.getPos();
//        ((ChunkPrimer)p_242706_2_).setBiomes(new BiomeContainer(p_242706_1_, chunkpos, this.runtimeBiomeSource));
//    }
//
//    public void applyCarvers(long p_230350_1_, BiomeManager p_230350_3_, IChunk p_230350_4_, GenerationStage.Carving p_230350_5_) {
//        BiomeManager biomemanager = p_230350_3_.withDifferentSource(this.biomeSource);
//        SharedSeedRandom sharedseedrandom = new SharedSeedRandom();
//        int i = 8;
//        ChunkPos chunkpos = p_230350_4_.getPos();
//        int j = chunkpos.x;
//        int k = chunkpos.z;
//        BiomeGenerationSettings biomegenerationsettings = this.biomeSource.getNoiseBiome(chunkpos.x << 2, 0, chunkpos.z << 2).getGenerationSettings();
//        BitSet bitset = ((ChunkPrimer)p_230350_4_).getOrCreateCarvingMask(p_230350_5_);
//
//        for(int l = j - 8; l <= j + 8; ++l) {
//            for(int i1 = k - 8; i1 <= k + 8; ++i1) {
//                List<Supplier<ConfiguredCarver<?>>> list = biomegenerationsettings.getCarvers(p_230350_5_);
//                ListIterator<Supplier<ConfiguredCarver<?>>> listiterator = list.listIterator();
//
//                while(listiterator.hasNext()) {
//                    int j1 = listiterator.nextIndex();
//                    ConfiguredCarver<?> configuredcarver = listiterator.next().get();
//                    sharedseedrandom.setLargeFeatureSeed(p_230350_1_ + (long)j1, l, i1);
//                    if (configuredcarver.isStartChunk(sharedseedrandom, l, i1)) {
//                        configuredcarver.carve(p_230350_4_, biomemanager::getBiome, sharedseedrandom, this.getSeaLevel(), l, i1, j, k, bitset);
//                    }
//                }
//            }
//        }
//
//    }
//
//    @Nullable
//    public BlockPos findNearestMapFeature(ServerWorld p_235956_1_, Structure<?> p_235956_2_, BlockPos p_235956_3_, int p_235956_4_, boolean p_235956_5_) {
//        if (!this.biomeSource.canGenerateStructure(p_235956_2_)) {
//            return null;
//        } else if (p_235956_2_ == Structure.STRONGHOLD) {
//            this.generateStrongholds();
//            BlockPos blockpos = null;
//            double d0 = Double.MAX_VALUE;
//            BlockPos.Mutable blockpos$mutable = new BlockPos.Mutable();
//
//            for(ChunkPos chunkpos : this.strongholdPositions) {
//                blockpos$mutable.set((chunkpos.x << 4) + 8, 32, (chunkpos.z << 4) + 8);
//                double d1 = blockpos$mutable.distSqr(p_235956_3_);
//                if (blockpos == null) {
//                    blockpos = new BlockPos(blockpos$mutable);
//                    d0 = d1;
//                } else if (d1 < d0) {
//                    blockpos = new BlockPos(blockpos$mutable);
//                    d0 = d1;
//                }
//            }
//
//            return blockpos;
//        } else {
//            StructureSeparationSettings structureseparationsettings = this.settings.getConfig(p_235956_2_);
//            return structureseparationsettings == null ? null : p_235956_2_.getNearestGeneratedFeature(p_235956_1_, p_235956_1_.structureFeatureManager(), p_235956_3_, p_235956_4_, p_235956_5_, p_235956_1_.getSeed(), structureseparationsettings);
//        }
//    }
//
//    public void applyBiomeDecoration(WorldGenRegion p_230351_1_, StructureManager p_230351_2_) {
//        int i = p_230351_1_.getCenterX();
//        int j = p_230351_1_.getCenterZ();
//        int k = i * 16;
//        int l = j * 16;
//        BlockPos blockpos = new BlockPos(k, 0, l);
//        Biome biome = this.biomeSource.getNoiseBiome((i << 2) + 2, 2, (j << 2) + 2);
//        SharedSeedRandom sharedseedrandom = new SharedSeedRandom();
//        long i1 = sharedseedrandom.setDecorationSeed(p_230351_1_.getSeed(), k, l);
//
//        try {
//            biome.generate(p_230351_2_, this, p_230351_1_, i1, sharedseedrandom, blockpos);
//        } catch (Exception exception) {
//            CrashReport crashreport = CrashReport.forThrowable(exception, "Biome decoration");
//            crashreport.addCategory("Generation").setDetail("CenterX", i).setDetail("CenterZ", j).setDetail("Seed", i1).setDetail("Biome", biome);
//            throw new ReportedException(crashreport);
//        }
//    }
//
//    public abstract void buildSurfaceAndBedrock(WorldGenRegion p_225551_1_, IChunk p_225551_2_);
//
//    public void spawnOriginalMobs(WorldGenRegion p_230354_1_) {
//    }
//
//    public DimensionStructuresSettings getSettings() {
//        return this.settings;
//    }
//
//    public int getSpawnHeight() {
//        return 64;
//    }
//
//    public BiomeProvider getBiomeSource() {
//        return this.runtimeBiomeSource;
//    }
//
//    public int getGenDepth() {
//        return 256;
//    }
//
//    public List<MobSpawnInfo.Spawners> getMobsAt(Biome p_230353_1_, StructureManager p_230353_2_, EntityClassification p_230353_3_, BlockPos p_230353_4_) {
//        return p_230353_1_.getMobSettings().getMobs(p_230353_3_);
//    }
//
//    public void createStructures(DynamicRegistries p_242707_1_, StructureManager p_242707_2_, IChunk p_242707_3_, TemplateManager p_242707_4_, long p_242707_5_) {
//        ChunkPos chunkpos = p_242707_3_.getPos();
//        Biome biome = this.biomeSource.getNoiseBiome((chunkpos.x << 2) + 2, 0, (chunkpos.z << 2) + 2);
//        this.createStructure(StructureFeatures.STRONGHOLD, p_242707_1_, p_242707_2_, p_242707_3_, p_242707_4_, p_242707_5_, chunkpos, biome);
//
//        for(Supplier<StructureFeature<?, ?>> supplier : biome.getGenerationSettings().structures()) {
//            this.createStructure(supplier.get(), p_242707_1_, p_242707_2_, p_242707_3_, p_242707_4_, p_242707_5_, chunkpos, biome);
//        }
//
//    }
//
//    private void createStructure(StructureFeature<?, ?> p_242705_1_, DynamicRegistries p_242705_2_, StructureManager p_242705_3_, IChunk p_242705_4_, TemplateManager p_242705_5_, long p_242705_6_, ChunkPos p_242705_8_, Biome p_242705_9_) {
//        StructureStart<?> structurestart = p_242705_3_.getStartForFeature(SectionPos.of(p_242705_4_.getPos(), 0), p_242705_1_.feature, p_242705_4_);
//        int i = structurestart != null ? structurestart.getReferences() : 0;
//        StructureSeparationSettings structureseparationsettings = this.settings.getConfig(p_242705_1_.feature);
//        if (structureseparationsettings != null) {
//            StructureStart<?> structurestart1 = p_242705_1_.generate(p_242705_2_, this, this.biomeSource, p_242705_5_, p_242705_6_, p_242705_8_, p_242705_9_, i, structureseparationsettings);
//            p_242705_3_.setStartForFeature(SectionPos.of(p_242705_4_.getPos(), 0), p_242705_1_.feature, structurestart1, p_242705_4_);
//        }
//
//    }
//
//    public void createReferences(ISeedReader p_235953_1_, StructureManager p_235953_2_, IChunk p_235953_3_) {
//        int i = 8;
//        int j = p_235953_3_.getPos().x;
//        int k = p_235953_3_.getPos().z;
//        int l = j << 4;
//        int i1 = k << 4;
//        SectionPos sectionpos = SectionPos.of(p_235953_3_.getPos(), 0);
//
//        for(int j1 = j - 8; j1 <= j + 8; ++j1) {
//            for(int k1 = k - 8; k1 <= k + 8; ++k1) {
//                long l1 = ChunkPos.asLong(j1, k1);
//
//                for(StructureStart<?> structurestart : p_235953_1_.getChunk(j1, k1).getAllStarts().values()) {
//                    try {
//                        if (structurestart != StructureStart.INVALID_START && structurestart.getBoundingBox().intersects(l, i1, l + 15, i1 + 15)) {
//                            p_235953_2_.addReferenceForFeature(sectionpos, structurestart.getFeature(), l1, p_235953_3_);
//                            DebugPacketSender.sendStructurePacket(p_235953_1_, structurestart);
//                        }
//                    } catch (Exception exception) {
//                        CrashReport crashreport = CrashReport.forThrowable(exception, "Generating structure reference");
//                        CrashReportCategory crashreportcategory = crashreport.addCategory("Structure");
//                        crashreportcategory.setDetail("Id", () -> {
//                            return Registry.STRUCTURE_FEATURE.getKey(structurestart.getFeature()).toString();
//                        });
//                        crashreportcategory.setDetail("Name", () -> {
//                            return structurestart.getFeature().getFeatureName();
//                        });
//                        crashreportcategory.setDetail("Class", () -> {
//                            return structurestart.getFeature().getClass().getCanonicalName();
//                        });
//                        throw new ReportedException(crashreport);
//                    }
//                }
//            }
//        }
//
//    }
//
//    public abstract void fillFromNoise(IWorld p_230352_1_, StructureManager p_230352_2_, IChunk p_230352_3_);
//
//    public int getSeaLevel() {
//        return 63;
//    }
//
//    public abstract int getBaseHeight(int p_222529_1_, int p_222529_2_, Heightmap.Type p_222529_3_);
//
//    public abstract IBlockReader getBaseColumn(int p_230348_1_, int p_230348_2_);
//
//    public int getFirstFreeHeight(int p_222532_1_, int p_222532_2_, Heightmap.Type p_222532_3_) {
//        return this.getBaseHeight(p_222532_1_, p_222532_2_, p_222532_3_);
//    }
//
//    public int getFirstOccupiedHeight(int p_222531_1_, int p_222531_2_, Heightmap.Type p_222531_3_) {
//        return this.getBaseHeight(p_222531_1_, p_222531_2_, p_222531_3_) - 1;
//    }
//
//    public boolean hasStronghold(ChunkPos p_235952_1_) {
//        this.generateStrongholds();
//        return this.strongholdPositions.contains(p_235952_1_);
//    }
//
//    static {
//        Registry.register(Registry.CHUNK_GENERATOR, "noise", NoiseChunkGenerator.CODEC);
//        Registry.register(Registry.CHUNK_GENERATOR, "flat", FlatChunkGenerator.CODEC);
//        Registry.register(Registry.CHUNK_GENERATOR, "debug", DebugChunkGenerator.CODEC);
//    }
//}