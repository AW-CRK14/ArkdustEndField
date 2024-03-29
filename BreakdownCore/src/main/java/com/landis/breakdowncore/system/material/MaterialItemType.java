package com.landis.breakdowncore.system.material;

import com.landis.breakdowncore.BreakdownCore;
import com.landis.breakdowncore.system.material.datagen.MitModelGen;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.StringTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.registries.RegisterEvent;
import org.checkerframework.checker.nullness.qual.NonNull;

/**MaterialItemType材料物品类型<br>
 * 材料物品类型用于创建一个材料物品模板。比如说，一个“板”类型将可以被使用作铁板，铜板，金板等。<br>
 * 材料物品内包含三个必须设定的参数与一个会自动配置的物品容器。此容器用来获取其自动额外注册的物品，即使它们可能不被使用。<br>
 * 另外的三个参数分别是：<br>
 * {@link MaterialItemType#content} 指定该种物品包含多少单位的对应材料。<br>
 * {@link MaterialItemType#purity}  指定该种物品的纯度。纯度将与content数值相乘获得直接产量。<br>
 * {@link MaterialItemType#id} 为该物品的id，也将会被用于获取材料的透明度图。<br>
 * 在默认提供的{@link TypedMaterialItem}中，最终材质的获取会先检查有无用json额外配置的材质，在不存在时再进行自动处理。
 * */
public class MaterialItemType {
    public final long content;
    public final float purity;
    public final ResourceLocation id;

    private ResourceKey<Item> autoRegKey;

    public MaterialItemType(long content, float purity, ResourceLocation id) {
        this.content = content;
        this.purity = purity;
        this.id = id;
    }

    public ItemStack getHolder(){
        return new ItemStack(BuiltInRegistries.ITEM.get(autoRegKey));
    }

    /**WARN:<br>
     * 如果您设置了自定义的全局注册或材料特性接口，您可能需要覆写此方法来保证获取的正确运行。
     * */
    public @NonNull ItemStack createItem(Material material){
        ItemStack stack = getHolder();
        StringTag tag = StringTag.valueOf(material.id.toString());
        stack.addTagElement("brea_material",tag);
        return stack;
    }


    public void primaryRegister(RegisterEvent event){
        ResourceLocation reg = new ResourceLocation(BreakdownCore.MODID,id.getNamespace() + "_" + id.getPath());
        autoRegKey = ResourceKey.create(Registries.ITEM,reg);
        event.register(Registries.ITEM,reg,()->new TypedMaterialItem(()->this));
    }

    public void secondaryRegistry(RegisterEvent event,Material material){
    }

    public void gatherKeyForDatagen(MitModelGen ins){
        ins.getBuilder(autoRegKey.location().toString())
                .parent(new ModelFile.UncheckedModelFile("item/generated"))
                .texture("layer0",id.withPath(s -> "brea/mit/" + s));
    }
}
