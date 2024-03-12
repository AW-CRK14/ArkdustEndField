package com.landis.breakdowncore.material;

import com.landis.breakdowncore.unsafe.SkippedRegister;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.registries.DeferredHolder;

import java.util.List;

/**IMaterialFeature材料特征接口<br>
 * 这一接口主要要求实现两个方法:<br>
 * {@link IMaterialFeature#getInstance() getInstance方法}返回这一接口的实现对象本身<br>
 * {@link IMaterialFeature#getType() getType方法}返回这一接口的实现对象本身<br>
 * 因此，为了确保正常运作，请将泛型I设置为您的实现的类。<br>
 * */
//TODO 正在考虑有关Capability的部分
public interface IMaterialFeature<I extends IMaterialFeature<I>> {

    default I getInstance(){return (I) this;}

    //请不要把没有注册的传进去……
    DeferredHolder<MaterialFeatureHandle<?>,MaterialFeatureHandle<I>> getType();
}
