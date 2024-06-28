package com.landis.breakdowncore.system.animation;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.landis.breakdowncore.system.animation.model.ModelPartController;
import com.landis.breakdowncore.system.animation.model.data.ModelPoseData;
import com.landis.breakdowncore.system.animation.model.data.ScaleData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

public class Animation{
    public static final Logger LOGGER = LogManager.getLogger("BREA:Animation/");
    private final String name;
    private final int duration;
    private final List<AnimationFrame> keyFrames;
    private boolean cycle = false;
    protected float startTime = 0;
    // 当前动画播放的时间
    protected float currentTime = 0;
    // 当前帧索引
    protected int currentFrameIndex = 0;
    // 帧率，即每秒播放的帧数
    protected float frameRate = 20;
    // 每个帧的持续时间
    protected float frameDuration;
    // 是否开始播放
    public boolean isStarted = false;

    // 构造函数，初始化动画名称、持续时间和关键帧列表
    public Animation(String name, int duration, List<AnimationFrame> keyFrames) {
        this.name = name;
        this.duration = duration;
        this.keyFrames = arrangeKeyframes(keyFrames);
        this.frameDuration = 1.0f / frameRate * duration / keyFrames.size(); // 总持续时间除以帧数
    }

    public Animation(String name, int duration, List<AnimationFrame> keyFrames, boolean cycle) {
        this.name = name;
        this.duration = duration;
        this.keyFrames = arrangeKeyframes(keyFrames);
        this.frameDuration = 1.0f / frameRate * duration / keyFrames.size(); // 总持续时间除以帧数
        this.cycle = cycle;
    }

    public static List<AnimationFrame> arrangeKeyframes(List<AnimationFrame> keyFrames){
        List<AnimationFrame> sortedKeyFrames = new ArrayList<>();
        int minKey = keyFrames.stream().mapToInt(AnimationFrame::getTick).min().orElse(0);
        int maxKey = keyFrames.stream().mapToInt(AnimationFrame::getTick).max().orElse(0);
        for (int i = minKey; i <= maxKey; i++) {
            AnimationFrame frame = keyFrames.get(i);
            if (frame != null) {
                sortedKeyFrames.set(i, frame);
            }
        }
        return sortedKeyFrames;
    }

    // 获取下一帧的姿态数据
    public List<ModelPoseData> getNextFramePoseData(){
        int nextIndex = (currentFrameIndex + 1) % keyFrames.size();
        return keyFrames.get(nextIndex).getPoseData();
    }

    // 获取动画名称
    public String getName() {
        return name;
    }

    // 获取动画持续时间
    public int getDuration() {
        return duration;
    }

    // 获取动画帧列表
    public List<AnimationFrame> getFrames() {
        return keyFrames;
    }

    // 播放动画
    public void play(ModelPartController controller){
        float partialTick = Minecraft.getInstance().getPartialTick();
        this.isStarted = true;
        if(startTime == 0){
            startTime = partialTick;
        }
        update(controller,startTime - partialTick);
    }

    // 更新动画状态
    public void update(ModelPartController controller, float tickTime) {
        // 更新当前时间
        currentTime = tickTime;
        if (currentTime > duration) {
            currentTime %= duration; // 如果超过动画总时长，循环到开始
        }

        // 计算当前帧和下一帧的索引
        int nextFrameIndex = (int) (currentTime / frameDuration);
        if (nextFrameIndex != currentFrameIndex) {
            currentFrameIndex = nextFrameIndex;
            // 插值逻辑
            interpolateFrame(controller);
        }
    }

    // 插值当前帧和下一帧
    private void interpolateFrame(ModelPartController controller) {
        // 获取当前帧和下一帧
        AnimationFrame currentFrames = keyFrames.get(currentFrameIndex);
        int nextIndex = (currentFrameIndex + 1) % keyFrames.size();
        if(nextIndex == 0 && !this.cycle) return;
        List<ModelPoseData> nextFramePoseDataMap = getNextFramePoseData();

        // 计算插值比例
        float progress = calculateInterpolationProgress();
        interpolatePartPose(currentFrames, nextFramePoseDataMap, progress, controller);
    }


    // 计算插值进度
    private float calculateInterpolationProgress() {
        return (currentTime - (currentFrameIndex * frameDuration)) / frameDuration;
    }

    // 插值模型部分的姿态
    private void interpolatePartPose(AnimationFrame frame, List<ModelPoseData> nextFramePoseData, float progress, ModelPartController controller) {
        List<ModelPoseData> currentPose = frame.getPoseData();
        currentPose.forEach((pose) -> {
            int modelPartNum = pose.modelPartNum();
            ModelPoseData nextPose = getNextPoseData(modelPartNum, nextFramePoseData, pose);
            ModelPart part = controller.getModelPartByNum(modelPartNum);
            if (part == null) {
                LOGGER.error("{} play fail: part is null by num {}", this.name, modelPartNum);
                Iterable<ModelPart> parts = controller.getModelParts();
                if(parts != null){
                    parts.forEach((ModelPart::resetPose));
                }else{
                    LOGGER.error("Reset of the model parts has failed because they are null.");
                }
                return;
            }

            interpolatePoseAttributes(part, pose.partPose(), nextPose.partPose(), progress);
            interpolateScaleAttributes(part, pose.scaleData(), nextPose.scaleData(), progress);
        });
    }

    // 获取下一帧的姿态数据
    private ModelPoseData getNextPoseData(int partNum, List<ModelPoseData> nextFramePoseData, ModelPoseData defaultPose) {
        AtomicReference<ModelPoseData> data = new AtomicReference<>(defaultPose);
        nextFramePoseData.forEach((pose) -> {
            if(pose.modelPartNum() == partNum){
                data.set(pose);
            }
        });

        if(data.get() == defaultPose){
            LOGGER.warn("No pose data found for part {} in next frame pose data list.", partNum);
        }

        return data.get();
    }

    // 插值姿态属性
    private void interpolatePoseAttributes(ModelPart part, PartPose currentPose, PartPose nextPose, float progress) {
        part.x = currentPose.x + (nextPose.x - currentPose.x) * progress;
        part.y = currentPose.y + (nextPose.y - currentPose.y) * progress;
        part.z = currentPose.z + (nextPose.z - currentPose.z) * progress;
        part.xRot = currentPose.xRot + (nextPose.xRot - currentPose.xRot) * progress;
        part.yRot = currentPose.yRot + (nextPose.yRot - currentPose.yRot) * progress;
        part.zRot = currentPose.zRot + (nextPose.zRot - currentPose.zRot) * progress;
    }

    // 插值缩放属性
    private void interpolateScaleAttributes(ModelPart part, ScaleData currentScale, ScaleData nextScale, float progress) {
        part.xScale = currentScale.xScale() + (nextScale.xScale() - currentScale.xScale()) * progress;
        part.yScale = currentScale.yScale() + (nextScale.yScale() - currentScale.yScale()) * progress;
        part.zScale = currentScale.zScale() + (nextScale.zScale() - currentScale.zScale()) * progress;
    }

    public JsonObject toJson(){
        JsonObject json = new JsonObject();
        json.addProperty("name", name);
        json.addProperty("duration", duration);
        json.addProperty("cycle", cycle);
        JsonArray keyFramesJson = new JsonArray();
        keyFrames.forEach(frame -> keyFramesJson.add(frame.toJson()));
        json.add("keyFrames", keyFramesJson);
        return json;
    }

    public static Animation fromJson(JsonObject json){
        String name = json.get("name").getAsString();
        int duration = json.get("duration").getAsInt();
        boolean cycle = json.get("cycle").getAsBoolean();
        List<AnimationFrame> keyFrames = new ArrayList<>();
        json.get("keyFrames").getAsJsonArray().forEach(frame -> keyFrames.add(AnimationFrame.fromJson(frame.getAsJsonObject())));
        return new Animation(name, duration, keyFrames,cycle);
    }

}
