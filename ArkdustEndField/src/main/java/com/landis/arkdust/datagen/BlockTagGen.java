package com.landis.arkdust.datagen;

import com.landis.arkdust.Arkdust;
import com.landis.breakdowncore.helper.ListAndMapHelper;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.IntrinsicHolderTagsProvider;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.registries.DeferredBlock;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class BlockTagGen extends BlockTagsProvider {
    public BlockTagGen(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, Arkdust.MODID, existingFileHelper);
    }

    private static final Map<ToolType, List<DeferredBlock<Block>>> TOOL = new HashMap<>();
    private static final Map<Integer, List<DeferredBlock<Block>>> LEVEL = new HashMap<>();


    @Override
    protected void addTags(HolderLookup.Provider pProvider) {
        TOOL.forEach(((type, deferredBlocks) -> {
            IntrinsicHolderTagsProvider.IntrinsicTagAppender<Block> tag = this.tag(switch (type){
                case AXE -> BlockTags.MINEABLE_WITH_AXE;
                case PICKAXE -> BlockTags.MINEABLE_WITH_PICKAXE;
                case SHOVEL -> BlockTags.MINEABLE_WITH_SHOVEL;
                case SWORD -> BlockTags.SWORD_EFFICIENT;
                case HOE -> BlockTags.MINEABLE_WITH_HOE;
            });
            for (DeferredBlock<Block> block : deferredBlocks){
                tag.add(block.get());
            }
        }));

        LEVEL.forEach(((type, deferredBlocks) -> {
            IntrinsicHolderTagsProvider.IntrinsicTagAppender<Block> tag = this.tag(switch (type){
                case 1 -> BlockTags.NEEDS_STONE_TOOL;
                case 2 -> BlockTags.NEEDS_IRON_TOOL;
                case 3 -> BlockTags.NEEDS_DIAMOND_TOOL;
                case 4 -> Tags.Blocks.NEEDS_NETHERITE_TOOL;
                default -> throw new IllegalStateException("Unexpected value: " + type);//TODO
            });
            for (DeferredBlock<Block> block : deferredBlocks){
                tag.add(block.get());
            }
        }));
    }

    public static void add(DeferredBlock<Block> block,int level) {
        if(level > 0){
            ListAndMapHelper.tryAddElementToMapList(LEVEL,level,block);
        }
    }

    public static void add(DeferredBlock<Block> block,ToolType toolType) {
        if(toolType != null){
            ListAndMapHelper.tryAddElementToMapList(TOOL,toolType,block);
        }
    }
    public static void add(DeferredBlock<Block> block, ToolType type,int level){
        add(block, level);
        add(block, type);
    }

    public enum ToolType{
        AXE,
        PICKAXE,
        SHOVEL,
        SWORD,
        HOE
    }
}
