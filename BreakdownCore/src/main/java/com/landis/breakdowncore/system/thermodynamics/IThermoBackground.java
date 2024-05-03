package com.landis.breakdowncore.system.thermodynamics;

import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Range;

/**
 * IHeatBackground热力背景
 * 热力背景只应为BlockEntity提供，用于确认方块实体的一些属性<br>
 * 包括最大热力存储，热力传导，热力衰减等。这通常可以由对应的材料直接拉取。<br>
 * 注:由于数值处理设计，获取到的所有直接数据**均为*100处理后的结果**。
 */
public interface IThermoBackground {
    /**
     * 温度决定了热的流动，最大温度乘以比热容可以获取到最大热力存储量<br>
     * 对于温度不同的两个物件，存在以下热力流动：Q/t ∝ k*∆T<br>
     * 即单位时间(每20tick)内，将会转移 k(m) * ΔT 的热量<br>
     * 同时对于每个方块，降低的温度∆T = ∆Q / {@link IThermoBackground#getCx100()}<br>
     * 环境温度与默认温度将被记作0
     */
    int maxTx100();

    default long maxQx10000() {
        return maxTx100() * getCx100();
    }

    /**
     * Specific Heat Capacity，比热容
     */
    int getCx100();

    /**
     * TC为HeatTransferRate，热导率
     */
    default int getKx100() {
        return 2500;
    }

    /**
     * 用于处理特殊的热聚集。在发散热量时，k将被乘以这个数值，0代表不散失热量
     */
    @Range(from = 0L, to = 1L)
    default float heatLock() {
        return 1;
    }

    /**
     * 强制限制热量在上限以下（阻止过热）。按照设计，若过热将导致熔毁
     */
    default boolean forceLimit() {
        return false;
    }

    /**
     * 与另一个实现了此接口的方块交换。原则上，这两个方块应当相邻。当目标温度高于此物块的温度，交互不会发生。
     *
     * @param target   与其交互的目标方块的接口。如果目标为null，则将被视作空气方块。
     * @param simulate 是否为模拟。如果是，则不会实际调用更改热量存储
     * @return 逸散的热量。
     */
    default long interactWith(@Nullable IThermoBackground target, boolean simulate) {
        if (target == null) {//如果目标为空气或其它无热力特性目标
            long qx10000 = thermalCycle(KAir, getEnvironmentTemperature());//计算流动热
            if (!simulate) qx10000 = extractHeat(qx10000, false, true);
            return qx10000;
        } else if (target.getTx100() >= getTx100()) {//如果目标温度更高，不进行活动
            return 0;
        } else {
            long q = thermalCycle(target.getKx100(), equT(target.getQx10000(), target.getCx100()));
            q = target.insertHeat(q, simulate, true);
            q = extractHeat(q, simulate, true);
            return q;
        }
    }

    void onOverheating();

    default long extractHeat(long count, boolean simulate, boolean x10000) {
        if (!x10000) count *= 10000;
        long cost = Math.min(count, getQx10000());
        if (!simulate) setQx10000(getQx10000() - cost);
        return x10000 ? cost : cost / 10000;
    }

    ;

    default long insertHeat(long count, boolean simulate, boolean x10000) {
        if (!x10000) count *= 10000;
        long cost = forceLimit() ? Math.min(count, maxQx10000() - getQx10000()) : count;
        if (!simulate) setQx10000(getQx10000() + cost);
        if (getQx10000() > maxQx10000()) onOverheating();
        return x10000 ? cost : cost / 10000;
    }

    ;

    long getQx10000();

    void setQx10000(long heat);

    default int getTx100() {
        return (int) (getQx10000() / getCx100());
    }

    default int getEnvironmentTemperature() {//TODO 预留的环境温度处理
        return TEnvironment;
    }

    default long thermalCycle(int consumerKx100, int endTx100) {
        return thermalCycle(getKx100(), consumerKx100, heatLock(), getTx100() - endTx100);
    }

    static long thermalCycle(int providerKx100, int consumerKx100, float heatLock, int deltaTx100) {
        if (heatLock == 0 || deltaTx100 <= 0) return 0;
        return (int) (Math.min(providerKx100 * heatLock, consumerKx100) * deltaTx100);
    }

    default int equT(long consumerQ, int consumerC) {
        return equT(getQx10000(),getCx100(),consumerQ,consumerC);
    }

    static int equT(long providerQ, int providerC, long consumerQ, int consumerC){
        return (int) ((providerQ + consumerQ) / (providerC + consumerC));
    }

    int KAir = 50;//TODO
    int TEnvironment = 27;

    int heatInteractionCycle = 50;

    default float getC(){
        return (float)getCx100() / 100;
    }

    default float getQ(){
        return (float)getQx10000() / 10000;
    }

    default float getK(){
        return (float)getKx100() / 100;
    }

    default float getT(){
        return (float)getTx100() / 100;
    }
}
