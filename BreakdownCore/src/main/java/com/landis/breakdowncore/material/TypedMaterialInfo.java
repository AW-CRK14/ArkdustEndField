package com.landis.breakdowncore.material;

/**TypedMaterialInfo类型材料信息
 * 类型材料信息是一个record类，使你可以在不修改一个物品/方块或其它内容的情况下创建其对应的材料信息。
 * 这些内容可以在相应事件中注册。在获取一个物品成分时将优先检查注册的物品-信息再尝试直接从接口获取。
 * //TODO
 * */
public record TypedMaterialInfo(Material material, long content, float purity) implements ITypedMaterialObj{
    public TypedMaterialInfo(Material material,long content){
        this(material,content,1F);
    }
    @Override
    public Material getMaterial() {
        return material;
    }

    @Override
    public MaterialItemType getMIType() {
        return null;
    }

    @Override
    public long getContent() {
        return content;
    }
}
