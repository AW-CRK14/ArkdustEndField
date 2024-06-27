package com.landis.breakdowncore.mixin;

import com.landis.breakdowncore.system.animation.model.ModelPartController;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(HumanoidModel.class)
public class SetupAnimationMixin implements ModelPartController {
    @Nullable
    @Override
    public ModelPart getModelPartByNum(int num) {
        return switch (num) {
            case 0 -> ((HumanoidModel<?>) (Object) this).head;
            case 1 -> ((HumanoidModel<?>) (Object) this).body;
            case 2 -> ((HumanoidModel<?>) (Object) this).rightArm;
            case 3 -> ((HumanoidModel<?>) (Object) this).leftArm;
            case 4 -> ((HumanoidModel<?>) (Object) this).rightLeg;
            case 5 -> ((HumanoidModel<?>) (Object) this).leftLeg;
            case 6 -> ((HumanoidModel<?>) (Object) this).hat;
            default -> null;
        };
    }

    @Nullable
    @Override
    public Iterable<ModelPart> getModelParts() {
        return List.of(((HumanoidModel<?>) (Object) this).head, ((HumanoidModel<?>) (Object) this).body, ((HumanoidModel<?>) (Object) this).rightArm, ((HumanoidModel<?>) (Object) this).leftArm, ((HumanoidModel<?>) (Object) this).rightLeg, ((HumanoidModel<?>) (Object) this).leftLeg, ((HumanoidModel<?>) (Object) this).hat);
    }

    @Inject(method = "setupAnim*", at = @At("HEAD"))
    public void setupAnim(float f, float g, float h, float i, float j, float k, CallbackInfo ci) {
        // TODO: Implement custom animations here
    }
}
