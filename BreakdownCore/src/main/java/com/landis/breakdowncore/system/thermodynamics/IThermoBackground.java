package com.landis.breakdowncore.system.thermodynamics;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

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
     * 即每个热力刻(5tick)内，将会转移 k(m) * ΔT 的热量<br>
     * 同时对于每个方块，降低的温度∆T = ∆Q / {@link IThermoBackground#getMC()}<br>
     * 环境温度与默认温度将被记作0
     */
    int maxT();

    /**
     * SpecificHeatCapacity，比热容  与质量的乘积，即提高方块一度需要的热量
     */
    int getMC();

    /**
     * HeatTransferRate，热导率
     */
    float getK();

    long getQ();

    void setQ(long heat);

    default long maxQ() {
        return (long) maxT() * getMC();
    }

    /**
     * 热交互面积。在计算热传导时，将会乘以这个数值。
     */
    default float interactQuad(@Nullable IThermoBackground background, boolean extra, BlockState state, Direction direction, BlockPos targetPos, @Nullable Level level) {
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
     * @param itbg     与其交互的目标方块的接口。
     * @param simulate 是否为模拟。如果是，则不会实际调用更改热量存储
     * @return 逸散的热量。
     */
    default long interactWith(@Nullable IThermoBackground itbg, boolean simulate, BlockState target, Direction direction, BlockPos targetPos, @Nullable Level level) {
        if (itbg == null) {//如果目标为空气或其它无热力特性目标
            //TODO 气体和其它材料的区分处理
            long q = thermalCycle(getK(), (int) (KAir * (target.isAir() ? 1 : 2.5F)), interactQuad(null, true, target, direction, targetPos, level), getT() - getEnvironmentTemperature());//计算流动热
            if (!simulate) q = extractHeat(q, false);
            return q;
        } else if (itbg.getT() >= getT()) {//如果目标温度更高，不进行活动
            return 0;
        } else {
            long q = thermalCycle(getK(), itbg.getK(), interactQuad(itbg, true, target, direction, targetPos, level), equT(itbg.getQ(), itbg.getMC()));
            q = itbg.insertHeat(q, simulate);
            q = extractHeat(q, simulate);
            return q;
        }
    }

    void onOverheating();

    /**提取热量，下方为存入热量
     * @param count 热力点数，单位为焦耳(J)
     * @param simulate 模拟，若为true则不会造成实际的数据变动*/
    default long extractHeat(long count, boolean simulate) {
        long cost = Math.min(count, getQ() + 273L * getMC());
        if (!simulate) setQ(getQ() - cost);
        return cost;
    }

    ;

    default long insertHeat(long count, boolean simulate) {
        long cost = forceLimit() ? Math.min(count, maxQ() - getQ()) : count;
        if (!simulate) setQ(getQ() + cost);
        if (getQ() > maxQ()) onOverheating();
        return cost;
    }

    default float getT() {
        return ((float) getQ() / getMC());
    }

    default int getEnvironmentTemperature() {//TODO 预留的环境温度处理
        return TEnvironment;
    }


    static long thermalCycle(float providerK, float consumerK, float heatLock, float deltaT) {
        if (heatLock == 0 || deltaT <= 0) return 0;
        return (int) (Math.min(providerK * heatLock, consumerK) * deltaT);
    }

    default int equT(long consumerQ, int consumerMC) {
        return equT(getQ(), getMC(), consumerQ, consumerMC);
    }

    default void init(Level level,BlockPos pos,BlockState state){
        setQ((long) TEnvironment * getMC());
    }

    static int equT(long providerQ, int providerMC, long consumerQ, int consumerMC) {
        return (int) ((providerQ + consumerQ) / (providerMC + consumerMC));
    }

    int KAir = 8;//TODO
    int TEnvironment = 27;

    int heatInteractionCycle = 50;

}
