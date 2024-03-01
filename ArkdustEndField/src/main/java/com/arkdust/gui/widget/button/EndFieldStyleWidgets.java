package com.arkdust.gui.widget.button;

import icyllis.modernui.animation.Animator;
import icyllis.modernui.animation.AnimatorListener;
import icyllis.modernui.animation.ObjectAnimator;
import icyllis.modernui.animation.TimeInterpolator;
import icyllis.modernui.core.Context;
import icyllis.modernui.graphics.BlendMode;
import icyllis.modernui.graphics.Canvas;
import icyllis.modernui.graphics.Color;
import icyllis.modernui.graphics.Paint;
import icyllis.modernui.mc.neoforge.UIManagerForge;
import icyllis.modernui.util.FloatProperty;
import icyllis.modernui.view.Gravity;
import icyllis.modernui.view.MotionEvent;
import icyllis.modernui.view.ViewGroup;
import icyllis.modernui.widget.RadioButton;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public class EndFieldStyleWidgets {
    public static class RadioButtonA extends RadioButton {

        ObjectAnimator hoverAnimator;

        public RadioButtonA(Context context, ViewGroup base, String text, int id) {
            super(context);
            setText(text);
            setTextSize(dp(7));
            setWidth(dp(160));
            setId(id);
            setTextAlignment(TEXT_ALIGNMENT_CENTER);
            setHeight(base.dp(50));


            hoverAnimator = ObjectAnimator.ofFloat(this,hoverPro,0.5F,0);
            hoverAnimator.setDuration(400);
            hoverAnimator.setInterpolator(TimeInterpolator.DECELERATE);
            hoverAnimator.addUpdateListener(i->invalidate());

            clickedAnimator = ObjectAnimator.ofFloat(this,clickPro,0,8.5F);
            clickedAnimator.setDuration(250);
            clickedAnimator.setInterpolator(TimeInterpolator.ACCELERATE_DECELERATE);
            clickedAnimator.addUpdateListener(i->{
                int c = 255 - (int)(255 * i.getAnimatedFraction());
                setTextColor(Color.rgb(c,c,c));
                invalidate();
            });
            clickedAnimator.addListener(new AnimatorListener() {
                public void onAnimationEnd(@NotNull Animator animation, boolean isReverse) {
                    AnimatorListener.super.onAnimationEnd(animation, isReverse);
                    if(isReverse && !hoverAnimator.isRunning()){
                        post(()-> hoverAnimator.reverse());
                    }
                }
            });

            setOnHoverListener((v, event) -> {
                if(!isChecked()) {
                    switch (event.getAction()) {
                        case (MotionEvent.ACTION_HOVER_ENTER):
                            post(() -> hoverAnimator.start());
                            break;
                        case (MotionEvent.ACTION_HOVER_EXIT):
                            post(() -> hoverAnimator.reverse());
                            break;
                    }
                }
                return false;
            });
        }

        @Override
        protected void onDraw(@NotNull Canvas canvas) {
            Paint paint = Paint.obtain();
            //渲染底部区域
            int color = (int) (72 - 72 * hover);
            paint.setRGBA(color,color,color,127);
            canvas.drawRect(0,getHeight()*0.15F,getWidth(),getHeight(),paint);

            //渲染顶部条纹。先是下层。
            paint.setColor(0xFF4C4C4C);
            canvas.drawRect(0,0,getWidth(),getHeight() * 0.15F,paint);
            //然后是叠加的黄色部分
            float c = click / 10F;
            paint.setColor(0xFFFCED15);
            canvas.drawRect(getWidth() * hover,0,getWidth() * (1 - hover),getHeight() * (0.15F + c),paint);

            paint.setRGBA(255,255,255,(int)(click * 40));
            paint.setBlendMode(BlendMode.LIGHTEN);

            float a = dp(8);
            canvas.drawRect(a,a,2 * a,2 * a,paint);

            paint.recycle();
            super.onDraw(canvas);
        }

        @Override
        public void setChecked(boolean checked) {
            super.setChecked(checked);
            if(checked){
                post(() -> {
                    if(hover == 0.5F){
                        hoverAnimator.start();
                    }
                    clickedAnimator.start();
                });
            }else {
                post(() -> clickedAnimator.reverse());
            }
        }

        ObjectAnimator clickedAnimator;
        private float click;
        private static final FloatProperty<RadioButtonA> clickPro = new FloatProperty<>("click"
        ) {
            @Override
            public void setValue(RadioButtonA object, float value) {
                object.click = value;
            }

            @Override
            public Float get(RadioButtonA object) {
                return object.click;
            }
        };


        private float hover = 0.5F;
        private static final FloatProperty<RadioButtonA> hoverPro = new FloatProperty<>("click"
        ) {
            @Override
            public void setValue(RadioButtonA object, float value) {
                object.hover = value;
            }

            @Override
            public Float get(RadioButtonA object) {
                return object.hover;
            }
        };
    }
}
