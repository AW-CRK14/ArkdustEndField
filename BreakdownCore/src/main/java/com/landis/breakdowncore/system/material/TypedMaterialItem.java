package com.landis.breakdowncore.system.material;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

/**TypedMaterialItem材料类型物品<br>
 * 材料类型物品是一个独立的物品，其内部包含的nbt数据将使其在不同材料条件下变成不同的样式。也就是说，所有这个形态的材料物品都是同一个物品。<br>
 * 因此请不要直接通过物品判断来决定它是否为你需要的物品。这有可能导致，比如说，你需要下届合金齿轮，但匹配到一个地狱岩齿轮。
 * */
public class TypedMaterialItem extends Item implements ITypedMaterialObj{
    public final Supplier<? extends MaterialItemType> type;

    public TypedMaterialItem(Supplier<? extends MaterialItemType> type) {
        super(new Properties().fireResistant());
        this.type = type;
    }

    public void setMaterial(ItemStack itemStack,Material material){
        if(itemStack.is(this)){
            itemStack.getOrCreateTag().putString("brea_mat",material.id.toString());
        }
    }


    @Override
    public ResourceLocation getMaterialId(ItemStack stack) {
        if(stack.is(this)){
            return new ResourceLocation(stack.getOrCreateTag().getString("brea_mat"));
        }
        return null;
    }

    @Override
    public MaterialItemType getMIType() {
        return type.get();
    }

    @Override
    public @NotNull Component getName(@NotNull ItemStack pStack) {
        return materialId2Component(getMaterialId(pStack)).append(" ").append(mitId2Component(getMIType().id));
    }

    public static MutableComponent materialId2Component(ResourceLocation id){
        if(Registry$Material.MATERIAL.containsKey(id)){
            return Component.translatable("brea.material." + id.getNamespace() + "." + id.getPath());
        }
        return Component.translatable("brea.material.missing");
    }

    public static MutableComponent mitId2Component(ResourceLocation id){
        return Component.translatable("brea.mit." + id.getNamespace() + "." + id.getPath());
    }
}
