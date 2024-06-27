package com.landis.breakdowncore.system.animation;

import com.google.gson.JsonObject;
import com.landis.breakdowncore.system.animation.model.data.ModelPoseData;
import java.util.List;

public class AnimationFrame{
    private final int tick;
    private final List<ModelPoseData> poseData;

    public AnimationFrame(int tick, List<ModelPoseData> poseData) {
        this.tick = tick;
        this.poseData = poseData;
    }

    public static AnimationFrame fromJson(JsonObject asJsonObject) {
        int tick = asJsonObject.get("tick").getAsInt();
        List<ModelPoseData> poseData = ModelPoseData.fromJsonArray(asJsonObject.get("poseData").getAsJsonArray());
        return new AnimationFrame(tick, poseData);
    }

    public int getTick() {
        return tick;
    }

    public List<ModelPoseData> getPoseData() {
        return poseData;
    }

    public String toJson() {
        StringBuilder sb = new StringBuilder();
        sb.append("{\"tick\":").append(tick).append(",\"poseData\":[");
        for (ModelPoseData data : poseData) {
            sb.append(data.toJson()).append(",");
        }
        sb.deleteCharAt(sb.length() - 1);
        sb.append("]}");
        return sb.toString();
    }
}