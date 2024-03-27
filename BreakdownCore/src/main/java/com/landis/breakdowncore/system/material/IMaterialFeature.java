package com.landis.breakdowncore.system.material;

import net.neoforged.neoforge.registries.DeferredHolder;

import java.util.HashSet;

/**IMaterialFeature材料特征接口<br>
 * 此接口的实例在被创建后其内容不应被以任何形式进行任何更改，这有可能导致出现不可预料的严重问题。<br>
 * 为了确保正常运作，请将泛型I设置为您的实现的类。
 * */
//TODO 正在考虑有关Capability的部分
public interface IMaterialFeature<I extends IMaterialFeature<I>> {

    /**返回实现的实例*/
    default I getInstance(){return (I) this;}

    /**返回一个注册了的材料特征类型*/
    DeferredHolder<MaterialFeatureType<?>,MaterialFeatureType<I>> getType();

    /**获取该特征所包含的物品类型。原则上允许根据材料特性的不同内容返回不同的物品类型组。*/
    HashSet<? extends MaterialItemType> forItemTypes();
}
