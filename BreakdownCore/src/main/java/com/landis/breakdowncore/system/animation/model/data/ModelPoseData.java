package com.landis.breakdowncore.system.animation.model.data;

import com.google.gson.JsonArray;
import net.minecraft.client.model.geom.PartPose;

import java.util.ArrayList;
import java.util.List;

public class ModelPoseData{
    private final int modelPartNum;
    private final PartPose partPose;
    private final ScaleData scaleData;
    public ModelPoseData(int modelPartNum, PartPose partPose, ScaleData scaleData) {
        this.modelPartNum = modelPartNum;
        this.partPose = partPose;
        this.scaleData = scaleData;
    }

    public static List<ModelPoseData> fromJsonArray(JsonArray poseData) {
        List<ModelPoseData> modelPoseDataList = new ArrayList<>();
        for (int i = 0; i < poseData.size(); i++) {
            JsonArray pose = poseData.get(i).getAsJsonArray();
            int modelPartNum = pose.get(0).getAsInt();
            float x = pose.get(1).getAsFloat();
            float y = pose.get(2).getAsFloat();
            float z = pose.get(3).getAsFloat();
            float xRot = pose.get(4).getAsFloat();
            float yRot = pose.get(5).getAsFloat();
            float zRot = pose.get(6).getAsFloat();
            ScaleData scaleData = new ScaleData(1, 1, 1);
            if (pose.size() == 9) {
                scaleData = new ScaleData(pose.get(7).getAsFloat(), pose.get(8).getAsFloat(), pose.get(9).getAsFloat());
            }
            modelPoseDataList.add(new ModelPoseData(modelPartNum, PartPose.offsetAndRotation(x, y, z, xRot, yRot, zRot), scaleData));
        }
        return modelPoseDataList;
    }

    public int modelPartNum(){
        return modelPartNum;
    }
    public PartPose partPose(){
        return partPose;
    }
    public ScaleData scaleData(){
        return scaleData;
    }

    public ModelPoseData(int modelPartNum, PartPose partPose) {
        this.modelPartNum = modelPartNum;
        this.partPose = partPose;
        this.scaleData = ScaleData.DEFAULT;
    }

    public char[] toJson() {
        String sb = "{\"modelPartNum\":" + modelPartNum + "," +
                "\"partPose\":{" +
                "\"x\":" + partPose.x + "," +
                "\"y\":" + partPose.y + "," +
                "\"z\":" + partPose.z + "," +
                "\"xRot\":" + partPose.xRot + "," +
                "\"yRot\":" + partPose.yRot + "," +
                "\"zRot\":" + partPose.zRot + "}," +
                "\"scaleData\":{\"xScale\":" + scaleData.xScale() + "," +
                "\"yScale\":" + scaleData.yScale() + "," +
                "\"zScale\":" + scaleData.zScale() + "}}";
        return sb.toCharArray();
    }
}
