package com.landis.breakdowncore.material;

import net.minecraft.core.Holder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.checkerframework.checker.nullness.qual.NonNull;

/**MaterialItemType材料物品类型<br>
 * 材料物品类型用于创建一个材料物品模板。比如说，一个“板”类型将可以被使用作铁板，铜板，金板等。<br>
 * 材料物品内包含三个必须设定的参数与一个会自动配置的物品容器。此容器用来获取其自动额外注册的物品，即使它们可能不被使用。<br>
 * 另外的三个参数分别是：<br>
 * {@link MaterialItemType#content} 指定该种物品包含多少单位的对应材料。<br>
 * {@link MaterialItemType#purity}  指定该种物品的纯度。纯度将与content数值相乘获得直接产量。<br>
 * {@link MaterialItemType#alphaMapPosition} 指定该种物品的贴图特征，将按照此特征进行叠加获得最终材质。<br>
 * 在默认提供的{@link TypedMaterialItem}中，最终材质的获取会先检查有无用json额外配置的材质，在不存在时再进行自动处理。
 * */
public class MaterialItemType {
    public final long content;
    public final float purity;
    public final ResourceLocation alphaMapPosition;

    private Holder<Item> insItemHolder;

    public MaterialItemType(long content, float purity, ResourceLocation alphaMapPosition) {
        this.content = content;
        this.purity = purity;
        this.alphaMapPosition = alphaMapPosition;
    }

    protected final void setHolder(Holder<Item> itemHolder){
        this.insItemHolder = itemHolder;
    }

    public final Item getHolder(){
        return insItemHolder.value();
    }

    /**WARN:<br>
     * 如果您设置了自定义的全局注册或材料特性接口，您可能需要覆写此方法来保证获取的正确运行。
     * */
    public @NonNull ItemStack createItem(Material material){
        ItemStack stack = new ItemStack(insItemHolder);
        StringTag tag = StringTag.valueOf(material.getId().toString());
        stack.addTagElement("brea_material",tag);
        return stack;
    }
}
