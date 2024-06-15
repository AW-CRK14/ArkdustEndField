package com.landis.breakdowncore.system.material;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.landis.breakdowncore.BREARegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.*;
import java.util.stream.Collectors;

/**Material材料<br><p>
 * 材料是材料系统的最前端对象，也是MF、MIT与ItemStack交互的中枢层级。<br>
 * 创建一个材料时，您可以为其添加材料特征实例，它们将被统一存储且内容不可变。<br><p>
 * 在创建实例后的初始化方法调用后，将自动生成一系列对应的表，包括材料包含的物品类型，特征类型对特征实例的映射等。您可以在适当的时机调用它们以获取信息<br><p>
 * 材质方面，物品模型的组装中，除了来自MIT的物品形状与叠加层外，就是来自材料的材质。其路径也与id绑定，对应为<br>
 *      [NameSpace]/textures/brea/material/material/[Path].png<br>
 * 的材质。另外，材料系统可以将该种材料指定为中间产物(isIntermediateProduct)，这样它将不会尝试拉取材料材质，而是仅使用MIT的材质，并使用设定的颜色(x16color)进行着色。<br>
 * 注意：我们建议您在使用后一种方法时<font color="yellow">尽量使用少种类的颜色</font>，因为同种颜色动态生成的材质会被统一编入并调用，更多的颜色意味着更多的材质，更多的内存负担。
 * @see BREARegistries.MaterialReg 在Registries中查看系统的注册方法
 * @see System$Material 查看Material系统的核心中控
 *
 * */
public class Material {
    public static final Logger LOGGER = LogManager.getLogger("BREA:Material/M");
    public final ResourceLocation id;
    public final int x16color;
    public final boolean intermediateProduct;
    public final ImmutableList<IMaterialFeature<?>> fIns;//材料特征数组。材料的所有MF都被存储在这里
    public final ImmutableSet<ResourceLocation> featureIDs;//材料特征的id列表
    private ImmutableMap<MaterialFeatureType<? extends IMaterialFeature<?>>,IMaterialFeature<?>> toFeature;//由特征类型向特征实例的映射表
    private ImmutableSet<MaterialItemType> toTypes;//材料具有的所有物品类型

    public Material(ResourceLocation id, int x16color, boolean isIntermediateProduct, IMaterialFeature<?>... fIns){
        this.id = id;
        this.x16color = x16color;
        List<IMaterialFeature<?>> l = new ArrayList<>();
        l.addAll(Arrays.asList(fIns));
        l.addAll(System$Material.MF4M_ADDITION.get(id));

        HashSet<ResourceLocation> fids = (HashSet<ResourceLocation>) l.stream().map(i -> i.getType().getId()).collect(Collectors.toSet());
        List<Integer> indexes = new ArrayList<>();

        for(int i = 0 ; i < l.size() ; i++){
            IMaterialFeature<?> m = l.get(i);
            if(m.dependencies() != null){
                boolean flag = false;
                for(ResourceLocation d : m.dependencies()){
                    if(!fids.contains(d)){
                        LOGGER.warn("Unable to find depended MaterialFeature(id={}) in material(id={}), this feature will be skipped. Required by MaterialFeature(id={}).",d,this.id,m.getType().getId());
                        flag = true;
                    }
                };
                if(flag){
                    fids.remove(m.getType().getId());
                    indexes.add(i);
                }
            }
        }

        for(int index : indexes){
            l.set(index,null);
        }
        this.featureIDs = ImmutableSet.copyOf(fids.iterator());
        this.fIns = ImmutableList.copyOf(l.stream().filter(Objects::nonNull).toList());
        this.intermediateProduct = isIntermediateProduct;
    }

    public Material(ResourceLocation id, int x16color, IMaterialFeature<?>... fIns){
        this(id,x16color,false,fIns);
    }

    public Material(ResourceLocation id, boolean isIntermediateProduct, IMaterialFeature<?>... fIns){
        this(id,0xEBEEF0,isIntermediateProduct,fIns);
    }

    public ImmutableMap<MaterialFeatureType<? extends IMaterialFeature<?>>,IMaterialFeature<?>> getOrCreateFeatures(){
        if(toFeature == null){
            if(!System$Material.release){
                LOGGER.warn("Can't get CLASS2MFH map as it's not exist right now. It will be automatically created in method System$Material#init()");
                LOGGER.warn("Details: called by Material(id={})",id);
                return null;
            }
            ImmutableMap.Builder<MaterialFeatureType<?>,IMaterialFeature<?>> builder = new ImmutableMap.Builder<>();
            for(IMaterialFeature<?> feature : fIns){
                builder.put(System$Material.getMFType(feature.getClass()),feature);
            }
            toFeature = builder.build();
        }
        return toFeature;
    }

    public <I extends IMaterialFeature<I>> IMaterialFeature<I> getFeature(MaterialFeatureType<I> handle){
        return (IMaterialFeature<I>) getOrCreateFeatures().get(handle);
    }

    public ImmutableSet<MaterialItemType> getOrCreateTypes(){
        if(toTypes == null){
            ImmutableSet.Builder<MaterialItemType> builder = new ImmutableSet.Builder<>();
            for(IMaterialFeature<?> feature : fIns){
                builder.addAll(feature.forItemTypes());
            }
            toTypes = builder.build();
        }
        return toTypes;
    }

    //当返回值为null时，将会继续尝试使用对应的ItemType获取结果。
    public @Nullable ItemStack createItem(MaterialItemType type){
        if(getOrCreateTypes().contains(type)){
            return null;
        }
        return new ItemStack(Items.AIR);
    }

    @Override
    public String toString() {
        return super.toString() + "{id=" + id + "}";
    }
}
