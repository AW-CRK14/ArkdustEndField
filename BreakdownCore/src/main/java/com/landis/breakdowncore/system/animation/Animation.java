package com.landis.breakdowncore.system.animation;

import com.landis.breakdowncore.system.animation.model.ModelPartController;
import com.landis.breakdowncore.system.animation.model.data.ModelPoseData;
import com.landis.breakdowncore.system.animation.model.data.ScaleData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Animation{
    public static final Logger LOGGER = LogManager.getLogger("BREA:Animation/");
    private final String name;
    private final int duration;
    private final Map<Integer,List<AnimationFrame>> keyFrames;
    private final boolean cycle = false;
    protected float startTime = 0;
    // 当前动画播放的时间
    protected float currentTime = 0;
    // 当前帧索引
    protected int currentFrameIndex = 0;
    // 帧率，即每秒播放的帧数
    protected float frameRate = 20;
    // 每个帧的持续时间
    protected float frameDuration;

    public Animation(String name, int duration, Map<Integer,List<AnimationFrame>> keyFrames) {
        this.name = name;
        this.duration = duration;
        this.keyFrames = keyFrames;
        this.frameDuration = 1.0f / frameRate * duration / keyFrames.size(); // 总持续时间除以帧数
    }

    public static Map<Integer,ModelPoseData> getNextFramePoseData(List<AnimationFrame> nextFrames){
        Map<Integer,ModelPoseData> map = new HashMap<>();
        for(AnimationFrame frame : nextFrames){
            map.put(frame.getPoseData().modelPartNum(),frame.getPoseData());
        }
        return map;
    }

    public String getName() {
        return name;
    }

    public int getDuration() {
        return duration;
    }

    public Map<Integer,List<AnimationFrame>> getFrames() {
        return keyFrames;
    }

    public void play(ModelPartController controller){
        float partialTick = Minecraft.getInstance().getPartialTick();
        if(startTime == 0){
            startTime = partialTick;
        }
        update(controller,startTime - partialTick);
    }

    public void update(ModelPartController controller, float tickTime) {
        boolean bl = true;
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
            bl = interpolateFrame(controller);
        }

        if(!bl){

        }
    }

    // 插值当前帧和下一帧
    private boolean interpolateFrame(ModelPartController controller) {
        boolean bl = true;
        // 获取当前帧和下一帧
        List<AnimationFrame> currentFrames = keyFrames.get(currentFrameIndex);
        int nextIndex = (currentFrameIndex + 1) % keyFrames.size();
        if(nextIndex == 0 && !this.cycle) return false;
        Map<Integer, ModelPoseData> nextFramePoseDataMap = getNextFramePoseData(keyFrames.get(nextIndex));

        // 计算插值比例
        float progress = calculateInterpolationProgress();

        // 对每个模型部分进行插值
        for (AnimationFrame frame : currentFrames) {
           bl = interpolatePartPose(frame, nextFramePoseDataMap, progress, controller);
           if (!bl){
               return bl;
           }
        }
        return bl;
    }


    private float calculateInterpolationProgress() {
        return (currentTime - (currentFrameIndex * frameDuration)) / frameDuration;
    }

    private boolean interpolatePartPose(AnimationFrame frame, Map<Integer, ModelPoseData> nextFramePoseDataMap, float progress, ModelPartController controller) {
        ModelPoseData currentPose = frame.getPoseData();
        int num = currentPose.modelPartNum();
        ModelPoseData nextPose = getNextPoseData(num, nextFramePoseDataMap, currentPose);

        ModelPart part = controller.getModelPartByNum(num);
        if (part == null) {
            LOGGER.error("{} play fail: part is null by num {}", this.name, num);
            Iterable<ModelPart> parts = controller.ModelParts();
            if(parts != null){
                parts.forEach((ModelPart::resetPose));
            }else{
                LOGGER.error("Reset of the model parts has failed because they are null.");
            }

            return false;
        }

        interpolatePoseAttributes(part, currentPose.partPose(), nextPose.partPose(), progress);
        interpolateScaleAttributes(part, currentPose.scaleData(), nextPose.scaleData(), progress);
        return true;
    }

    private ModelPoseData getNextPoseData(int partNum, Map<Integer, ModelPoseData> nextFramePoseDataMap, ModelPoseData defaultPose) {
        return nextFramePoseDataMap.getOrDefault(partNum, defaultPose);
    }

    private void interpolatePoseAttributes(ModelPart part, PartPose currentPose, PartPose nextPose, float progress) {
        part.x = currentPose.x + (nextPose.x - currentPose.x) * progress;
        part.y = currentPose.y + (nextPose.y - currentPose.y) * progress;
        part.z = currentPose.z + (nextPose.z - currentPose.z) * progress;
        part.xRot = currentPose.xRot + (nextPose.xRot - currentPose.xRot) * progress;
        part.yRot = currentPose.yRot + (nextPose.yRot - currentPose.yRot) * progress;
        part.zRot = currentPose.zRot + (nextPose.zRot - currentPose.zRot) * progress;
    }

    private void interpolateScaleAttributes(ModelPart part, ScaleData currentScale, ScaleData nextScale, float progress) {
        part.xScale = currentScale.xScale() + (nextScale.xScale() - currentScale.xScale()) * progress;
        part.yScale = currentScale.yScale() + (nextScale.yScale() - currentScale.yScale()) * progress;
        part.zScale = currentScale.zScale() + (nextScale.zScale() - currentScale.zScale()) * progress;
    }
}