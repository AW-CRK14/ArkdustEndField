package com.arkdust.system.industry.thermodynamics;

import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Range;

/**IHeatBackground热力背景
 * 热力背景只应为BlockEntity提供，用于确认方块实体的一些属性<br>
 * 包括最大热力存储，热力传导，热力衰减等<br>
 * 不提供热力存储的具体数值，数值方面请查看{@link com.arkdust.registry.DataAttachmentRegistry#HEAT}
 * */
public interface IHeatBackground {
    /**温度决定了热的流动，最大温度乘以比热容可以获取到最大热力存储量<br>
     * 对于温度不同的两个物件，存在以下热力流动：Q/t ∝ k*∆T<br>
     * 即单位时间(每tick)内，将会转移 {@link IHeatBackground#getTC()}*两方块温度差 的热量<br>
     * 同时对于每个方块，降低的温度∆T = ∆Q / {@link IHeatBackground#getSHC()}<br>
     * 环境温度与默认温度将被记作0
     * */
    int maxTemperature();

    default int maxHeatStore(){
        return maxTemperature() * getSHC();
    }

    /**SHC为Specific Heat Capacity，比热容*/
    int getSHC();

    /**TC为Thermal Conductivity，导热率<br>
     * 空气与不具有{@link IHeatBackground}的方块导热率恒为0.1F，温度由环境决定*/
    @Range(from = 0L,to = 1L)
    default float getTC(){
        return 0.25F;
    };

    /**允许主动拉取热量形成热量聚集点。注：主动拉取功能应自己实现*/
    default boolean allowActivePull(){
        return false;
    }

    /**强制限制热量在上限以下（阻止过热）*/
    default boolean forceLimit(){
        return false;
    }

    /**与另一个实现了此接口的方块交换。原则上，这两个方块应当相邻。当目标温度高于此物块的温度，交互不会发生。
     * @param target 与其交互的目标方块的接口。如果目标为null，则将被视作空气方块。
     * @param simulate 是否为模拟。如果是，则不会实际调用更改热量存储
     * @return 逸散的热量。
     * */
    default int interactWith(@Nullable IHeatBackground target, boolean simulate){
        if(target == null){
            int q = (int) (getHeat() * Math.sqrt(getTC() * tcForAir));
            if(!simulate) q = extractHeat(q,false);
            return q;
        }else if(target.getTemperature() >= getTemperature()){
            return 0;
        }else {
            int q = (int) (
                    ((float)(getHeat() + target.getHeat()) / (getSHC() + target.getSHC()) * target.getSHC() - target.getHeat())
                            * Math.sqrt(getTC() * target.getTC())
            );
            q = target.insertHeat(q,simulate);
            q = extractHeat(q,simulate);
            return q;
        }
    }

    void onOverheating();

    default int extractHeat(int count,boolean simulate){
        int cost = Math.min(count,getHeat());
        if(!simulate) setHeat(getHeat() - cost);
        return cost;
    };

    default int insertHeat(int count,boolean simulate){
        int cost = forceLimit() ? Math.min(count,maxHeatStore() - getHeat()) : count;
        if(!simulate) setHeat(getHeat() + cost);
        if(getHeat() > maxHeatStore()) onOverheating();
        return cost;
    };

    int getHeat();

    void setHeat(int heat);

    default float getTemperature(){
        return (float) getHeat() / getSHC();
    }

    float tcForAir = 0.005F;

    int heatInteractionCycle = 50;
}
