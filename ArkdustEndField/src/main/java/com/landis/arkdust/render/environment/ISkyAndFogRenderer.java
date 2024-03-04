package com.landis.arkdust.render.environment;

import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.ints.IntIntPair;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.OptionalInt;

public interface ISkyAndFogRenderer {
    OptionalInt getSkyColor();
    OptionalInt getFogColor();
    @Nullable
    IntIntPair getFogDistance();
    float priority();

    //blendPara决定渲染强度。在值较小时，将会保有一部分原有的颜色。
    //事件中提供优先级遍历，也就是说如果多个实例的blendPara的叠加没有达到0.99，将会继续尝试下一个实例的颜色
    default float blendPara(){return 1;}

    default int paraHash() {
        //使用一个质数作为基数
        int base = 31;
        //使用一个初始值作为哈希值
        int hash = 17;
        //将每个属性的值乘以基数后加到哈希值上
        hash = hash * base + getSkyColor().orElse(0);
        hash = hash * base + getFogColor().orElse(0);
        hash = hash * base + (getFogDistance() == null ? -2 : getFogDistance().leftInt());
        hash = hash * base + (getFogDistance() == null ? -3 : getFogDistance().rightInt());
        hash = hash * base + Float.floatToIntBits(priority());
        hash = hash * base + Float.floatToIntBits(blendPara());
        //返回哈希值
        return hash;
    }
}
