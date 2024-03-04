package com.landis.breakdowncore.material;

import com.landis.breakdowncore.BreakdownCore;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

/**TypedMaterialItem材料类型物品<br>
 * 材料类型物品是一个独立的物品，其内部包含的nbt数据将使其在不同材料条件下变成不同的样式。也就是说，所有这个形态的材料物品都是同一个物品。<br>
 * 因此请不要直接通过物品判断来决定它是否为你需要的物品。这有可能导致，比如说，你需要下届合金齿轮，但匹配到一个地狱岩齿轮。
 * //TODO
 * */
public class TypedMaterialItem extends Item implements ITypedMaterialObj{
    public final MaterialItemType type;

    public TypedMaterialItem(MaterialItemType type) {
        super(new Properties().fireResistant());
        this.type = type;
    }

    public static void setMaterial(ItemStack itemStack){

    }


    @Override
    public Material getMaterial(ItemStack stack) {
        if(stack.is(this)){
//            return stack.getOrCreateTag().getString("brea_material");
        }
        return null;
    }

    @Override
    public MaterialItemType getMIType() {
        return type;
    }
}
