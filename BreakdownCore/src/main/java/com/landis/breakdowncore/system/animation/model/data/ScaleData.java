package com.landis.breakdowncore.system.animation.model.data;

public record ScaleData(float xScale, float yScale, float zScale) {
    public static final ScaleData DEFAULT = new ScaleData(1,1,1);
}
