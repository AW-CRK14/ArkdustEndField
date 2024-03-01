package com.arkdust.blocks;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ExplainableBlock extends Block {
    private final boolean e;
    public ExplainableBlock(Properties properties,boolean explain) {
        super(properties);
        this.e = explain;
    }

    @Override
    public void appendHoverText(ItemStack pStack, @Nullable BlockGetter pLevel, List<Component> tips, TooltipFlag pFlag) {
        super.appendHoverText(pStack, pLevel, tips, pFlag);
        if(e){
            ResourceLocation name = BuiltInRegistries.BLOCK.getKey(this);
            String pre = "explain.block.arkdust." + name.getPath();
            if(Screen.hasShiftDown()){
                tips.add(Component.translatable("explain.arkdust.util.way"));
                tips.add(Component.translatable(pre +".way"));
            }else {
                tips.add(Component.translatable("explain.arkdust.util.explain"));
                tips.add(Component.translatable(pre));
            }
        }
    }
}
