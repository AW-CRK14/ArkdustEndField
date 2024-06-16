package com.landis.arkdust.deprecated;

@Deprecated
public class MUIAbout {
    //旋转的图片

//                ImageView backgroundThermoTestA = new ImageView(getContext());
//                backgroundThermoTestA.setImage(ResourceQuote$Thermo.THERMO_TES);
//                rightPart.addView(backgroundThermoTestA, new RelativeLayout.LayoutParams(-1, -1));

//                ValueAnimator rotationAnimator = ValueAnimator.ofFloat(0f,360f);
//                rotationAnimator.setDuration(3000); // 设置动画持续时间
//                rotationAnimator.setRepeatCount(ValueAnimator.INFINITE); // 设置无限重复
//                rotationAnimator.setInterpolator(v -> v);
//                rotationAnimator.addUpdateListener(animation -> {
//                    float animatedValue = (float) animation.getAnimatedValue();
//                    backgroundThermoTestA.setRotation(animatedValue);
//                });
//
//                ValueAnimator breathEffectAnimator = ValueAnimator.ofFloat(0.75f,0.65f,0.75f);
//                breathEffectAnimator.setDuration(3000);
//                breathEffectAnimator.setRepeatCount(ValueAnimator.INFINITE);
//                breathEffectAnimator.setInterpolator(TimeInterpolator.SINE);
//                breathEffectAnimator.addUpdateListener(animation -> {
//                    float animatedValue = (float) animation.getAnimatedValue();
//                    backgroundThermoTestA.setScaleX(animatedValue);
//                    backgroundThermoTestA.setScaleY(animatedValue);
//                    backgroundThermoTestA.setAlpha(1f - ((animatedValue - 1f) * 0.75f));
//                });
//
//                AnimatorSet animatorSet = new AnimatorSet();
//                animatorSet.playTogether(
//                        breathEffectAnimator,
//                        rotationAnimator
//                );
//                animatorSet.start();
}
