package com.landis.breakdowncore.system.material;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.util.Optional;

/**TypedMaterialInfo类型材料信息<br>
 * 类型材料信息是一个record类，使你可以在不修改一个物品/方块或其它内容的情况下创建其对应的材料信息。<br>
 * 这些内容可以在相应事件中注册。在获取一个物品成分时将优先检查注册的物品-信息再尝试直接从接口获取。
 * */
public record TypedMaterialInfo(Material material, long content, float purity,MaterialItemType mit) implements ITypedMaterialObj{
    public TypedMaterialInfo(Material material,long content){
        this(material,content,1F,null);
    }
    @Override
    public ResourceLocation getMaterialId(ItemStack stack) {
        return material.id;
    }

    @Override
    public Optional<Material> getMaterial(ItemStack stack) {
        return Optional.of(material);
    }

    @Override
    public MaterialItemType getMIType() {
        return mit;
    }

    @Override
    public long getContent() {
        return content;
    }
}
