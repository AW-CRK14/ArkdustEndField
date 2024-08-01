package com.landis.breakdowncore.system.material;

import com.landis.breakdowncore.BreaRegistries;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Function;

/**MaterialFeatureType材料特征类型<br><p>
 * 材料特征类型是对{@link IMaterialFeature 材料特征(MaterialFeature)}的类型的包装。<br>
 * 这一包装在注册中被使用，同时也用于快速的将特征类型与特征实例进行相互对应。
 * @see BreaRegistries.MaterialReg#feature(String, Class)  在Registries中查看MF的注册方法
 * @see IMaterialFeature 继续浏览。查看MaterialFeature的详细信息
 * */
public record MaterialFeatureType<I extends IMaterialFeature<I>>(ResourceLocation id, Class<I> clazz){ }