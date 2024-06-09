package com.landis.breakdowncore.system.material;

import com.landis.breakdowncore.BreakdownCore;
import com.landis.breakdowncore.ModBusConsumer;
import com.landis.breakdowncore.system.material.client.TMIModel;
import com.landis.breakdowncore.system.material.datagen.MitModelGen;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.StringTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.client.event.ModelEvent;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.registries.RegisterEvent;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.List;

/**
 * MaterialItemType材料物品类型(MIT)<br><p>
 * 材料物品类型用于创建一个材料物品模板。比如说，一个“板”类型将可以被使用作铁板，铜板，金板等。<br><p>
 * 材料物品内包含三个必须设定的参数与一个会自动配置的物品容器。此容器用来获取其自动额外注册的物品，即使它们可能不被使用。<br>
 * 另外的三个参数分别是：<br>
 * --- {@link MaterialItemType#content} 指定该种物品包含多少单位的对应材料。<br>
 * --- {@link MaterialItemType#purity}  指定该种物品的纯度。纯度将与content数值相乘获得直接产量。<br>
 * --- {@link MaterialItemType#id} 为该物品的id，也将会被用于获取材料的透明度图。<br>
 * 在默认提供的{@link TypedMaterialItem}中，最终材质的获取会先检查有无用json额外配置的材质，在不存在时再进行自动处理。<br><p>
 * 在材质方面，{@link MaterialItemType}的id指向该物品在使用material系统的模型时，其将会在:<br>
 *      [NameSpace]/textures/brea/material/mit/[Path].png<br>
 * 下寻找形状层材质文件。mit_cover则是可选的，在与材料颜色组装完成后的叠加层文件。<br>
 * @see com.landis.breakdowncore.system.material.expansion.IngotType IngotType中有关于一些预备方法的特别使用方法
 * @see MaterialFeatureType 继续浏览。查看MaterialFeatureType的详细信息
 */
public class MaterialItemType {
    public final long content;
    public final float purity;
    public final ResourceLocation id;
    private boolean regFlag = false;

    protected ResourceKey<Item> autoRegKey;

    public MaterialItemType(long content, float purity, ResourceLocation id) {
        this.content = content;
        this.purity = purity;
        this.id = id;
    }

    //创建该物品类型的物品实例
    public ItemStack getHolder() {
        return new ItemStack(BuiltInRegistries.ITEM.get(autoRegKey));
    }

    /**
     * WARN:<br>
     * 如果您设置了自定义的全局注册或材料特性接口，您可能需要覆写此方法来保证获取的正确运行。
     */
    public @NonNull ItemStack createItem(Material material) {
        ItemStack stack = getHolder();
        StringTag tag = StringTag.valueOf(material.id.toString());
        stack.addTagElement("brea_material", tag);
        return stack;
    }


    //bug:当向事件中注册item时，item已经完成了注册，这导致进行的额外自动注册无法成功。
    //因此……现在我们换用了一种更加奇怪的方式。

    /**
     * 初级注册<br>
     * 这一阶段，请求材料物品类型对该类型进行物品注册。在游戏中即为由nbt控制材料属性的对应id物品
     */
    public void primaryRegister(@Nullable RegisterEvent event) {
        Registry<Item> registry = (Registry<Item>) ModBusConsumer.REGS_MAP.get(Registries.ITEM);
        ResourceLocation reg = new ResourceLocation(BreakdownCore.MODID, id.getNamespace() + "_" + id.getPath());
        autoRegKey = ResourceKey.create(Registries.ITEM, reg);
        Registry.register(registry, reg, new TypedMaterialItem(() -> this));
        regFlag = true;
    }

    /**
     * 次级注册<br>
     * 这一阶段，每一种拥有该物品类型的材料将会相应的调用一次此方法，以此来实现根据特殊材料进行额外的注册。<br>
     * 这一过程您可能需要相应的配置自定义的物品材料特征映射表。
     */
    public void secondaryRegistry(@Nullable RegisterEvent event, Material material) {
    }

    //收集数据生成信息，用于自动生成模型
    public void gatherKeyForDatagen(MitModelGen ins) {

        //basic model
        ins.getBuilder(id.withPath(s -> "mit_basic/" + s) + "")
                .parent(new ModelFile.UncheckedModelFile("item/generated"))
                .texture("layer0", id.withPath(s -> "brea/material/mit/" + s));
    }

    //在模型事件中，将对应物品的模型重定向为material系统的材质动态生成模型。
    public void consumeModelReg(ModelEvent.ModifyBakingResult event) {
        ModelBakery bakery = event.getModelBakery();
        event.getModels().put(System$Material.trans2ModelLocation(autoRegKey.location()), new TMIModel(bakery, this));
    }

    //将物品添加至创造模式物品栏
    public void attachToCreativeTab(BuildCreativeModeTabContentsEvent event) {
        event.accept(BuiltInRegistries.ITEM.get(autoRegKey.location()));
    }


    @Override
    public String toString() {
        return super.toString() + "{id=" + id + "}";
    }
}
