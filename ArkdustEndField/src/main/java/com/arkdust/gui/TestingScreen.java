package com.arkdust.gui;

import icyllis.modernui.ModernUI;
import icyllis.modernui.R;
import icyllis.modernui.TestFragment;
import icyllis.modernui.animation.*;
import icyllis.modernui.annotation.NonNull;
import icyllis.modernui.annotation.Nullable;
import icyllis.modernui.audio.AudioManager;
import icyllis.modernui.audio.FFT;
import icyllis.modernui.audio.Track;
import icyllis.modernui.core.Context;
import icyllis.modernui.core.Core;
import icyllis.modernui.fragment.Fragment;
import icyllis.modernui.fragment.FragmentContainerView;
import icyllis.modernui.fragment.FragmentTransaction;
import icyllis.modernui.graphics.*;
import icyllis.modernui.graphics.drawable.Drawable;
import icyllis.modernui.graphics.font.GlyphManager;
import icyllis.modernui.graphics.text.FontFamily;
import icyllis.modernui.graphics.text.LineBreakConfig;
import icyllis.modernui.material.MaterialCheckBox;
import icyllis.modernui.material.MaterialRadioButton;
import icyllis.modernui.mc.MarkdownFragment;
import icyllis.modernui.resources.SystemTheme;
import icyllis.modernui.text.*;
import icyllis.modernui.text.style.*;
import icyllis.modernui.util.DataSet;
import icyllis.modernui.util.FloatProperty;
import icyllis.modernui.view.*;
import icyllis.modernui.widget.*;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.config.Configurator;

import javax.annotation.Nonnull;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;

import static icyllis.modernui.ModernUI.LOGGER;
import static icyllis.modernui.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static icyllis.modernui.view.ViewGroup.LayoutParams.WRAP_CONTENT;

public class TestingScreen extends Fragment {

    //public static SpectrumGraph sSpectrumGraph;

    public static void main(String[] args) {
        System.setProperty("java.awt.headless", "true");
        Configurator.setRootLevel(Level.DEBUG);

        try (ModernUI app = new ModernUI()) {
            app.run(new FragmentA());
        }
        AudioManager.getInstance().close();
        System.gc();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        getParentFragmentManager().beginTransaction()
                .setPrimaryNavigationFragment(this)
                .commit();
    }

    @Override
    public void onCreate(@Nullable DataSet savedInstanceState) {
        super.onCreate(savedInstanceState);
//        getChildFragmentManager().beginTransaction()
//                .replace(660, new icyllis.modernui.TestFragment.FragmentA(), null)
//                .commit();

        /*CompletableFuture.runAsync(() -> {
            String text = "My name is van";
            var tp = new TextPaint();
            tp.setTextStyle(TextPaint.BOLD);
            var shapedText = TextShaper.shapeText(text, 1, text.length() - 2, 0, text.length(),
                    TextDirectionHeuristics.FIRSTSTRONG_LTR, tp);
            LOGGER.info("Shape \"{}\"\n{}\nMemory Usage: {} bytes", text, shapedText, shapedText.getMemoryUsage());
            text = "y";
            var adv = tp.getTypeface().getFamilies().get(0).getClosestMatch(FontPaint.BOLD)
                    .doSimpleLayout(text.toCharArray(), 0, 1, tp.getInternalPaint(), null, null, 0, 0);
            LOGGER.info("y: adv {}", adv);
        }).exceptionally(e -> {
            LOGGER.info("Shape", e);
            return null;
        });*/

//        AudioManager.getInstance().initialize();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable DataSet savedInstanceState) {
        RelativeLayout base = new RelativeLayout(getContext());
        base.setId(660);

        final int tabId = 0x200;//这地方随你搞
        getChildFragmentManager().beginTransaction()
                .replace(tabId, MarkdownFragment.class, null, "markdown")
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .setReorderingAllowed(true)
                .commit();

        FragmentContainerView tabContainer = new FragmentContainerView(getContext());
        tabContainer.setId(tabId);
        base.addView(tabContainer);



        base.setBackground(new Drawable() {
            @Override
            public void draw(@NonNull Canvas canvas) {
            }
        });
        //base.setRotation(30);
        return base;
    }

    public static class FragmentA extends Fragment {
        @Nullable
        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                                 @Nullable DataSet savedInstanceState) {
            LinearLayout content = new TestLinearLayout(getContext());
            //content.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
            content.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

            content.setOnKeyListener((v, keyCode, event) -> {
                if (keyCode == KeyEvent.KEY_E && event.getAction() == KeyEvent.ACTION_UP) {
                    Core.postOnRenderThread(() ->
                            GlyphManager.getInstance().debug());
                    /*getParentFragmentManager().beginTransaction()
                            .replace(getId(), new FragmentB())
                            .addToBackStack(null)
                            .setReorderingAllowed(true)
                            .commit();*/
                    return true;
                }
                return false;
            });

            LOGGER.info("{} onCreateView(), id={}", getClass().getSimpleName(), getId());

            return content;
        }

        @Override
        public void onDestroy() {
            super.onDestroy();
            LOGGER.info("{} onDestroy()", getClass().getSimpleName());
        }
    }

    public static class FragmentB extends Fragment {
        @Nullable
        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                                 @Nullable DataSet savedInstanceState) {
            var tv = new TextView(getContext());
            tv.setText("My name is Van, I'm an arist, a performance artist.");
            return tv;
        }

        @Override
        public void onDestroy() {
            super.onDestroy();
            LOGGER.info("FragmentB onDestroy()");
        }
    }

    private static class TestView extends View {

        public TestView(Context context) {
            super(context);
        }

        @Override
        protected void onDraw(@NonNull Canvas canvas) {
            //canvas.drawRing(100, 20, 5, 8);
            // 3


            /*//RenderHelper.setupGuiFlatDiffuseLighting();
            //GL11.glColor4d(1, 1, 1, 1);
            //GL11.glDisable(GL11.GL_CULL_FACE);
            GL11.glMatrixMode(GL11.GL_PROJECTION);
            GL11.glPushMatrix();
            GL11.glLoadIdentity();
            RenderSystem.disableDepthTest();

            MainWindow mainWindow = Minecraft.getInstance().getMainWindow();
            RenderSystem.multMatrix(Matrix4f.perspective(90.0D,
                    (float) mainWindow.getFramebufferWidth() / mainWindow.getFramebufferHeight(),
                    1.0F, 100.0F));
            //RenderSystem.viewport(0, 0, mainWindow.getFramebufferWidth(), mainWindow.getFramebufferHeight());
            GL11.glMatrixMode(GL11.GL_MODELVIEW);
            GL11.glPushMatrix();
            GL11.glLoadIdentity();
            GL11.glTranslatef(-2.8f, -1.0f, -1.8f);
            GL11.glScalef(1 / 90f, -1 / 90f, 1 / 90f);
            //GL11.glTranslatef(0, 3, 1984);
            //GL11.glRotatef((canvas.getDrawingTime() / 10f) % 360 - 180, 0, 1, 0);
            GL11.glRotatef(12, 0, 1, 0);
            *//*if ((canvas.getDrawingTime() ^ 127) % 40 == 0) {
             *//**//*float[] pj = new float[16];
                GL11.glGetFloatv(GL11.GL_PROJECTION_MATRIX, pj);
                ModernUI.LOGGER.info(Arrays.toString(pj));
                GL11.glGetFloatv(GL11.GL_MODELVIEW_MATRIX, pj);
                ModernUI.LOGGER.info(Arrays.toString(pj));*//**//*
                ModernUI.LOGGER.info(GL11.glGetBoolean(GL30.GL_RESCALE_NORMAL));
            }*//*
            ClientPlayerEntity player = Minecraft.getInstance().player;
            canvas.setColor(170, 220, 240, 128);
            if (player != null) {
                canvas.drawRoundedRect(0, 25, player.getHealth() * 140 / player.getMaxHealth(), 39, 4);
            }
            *//*canvas.setAlpha(255);
            canvas.drawRoundedFrame(1, 26, 141, 40, 4);*//*
             *//*canvas.setColor(53, 159, 210, 192);
            canvas.drawRoundedFrame(0, 25, 140, 39, 4);*//*
            if (player != null) {
                canvas.resetColor();
                canvas.setTextAlign(TextAlign.RIGHT);
                canvas.drawText(decimalFormat.format(player.getHealth()) + " / " + decimalFormat.format(player
                .getMaxHealth()), 137, 28);
            }
            RenderSystem.enableDepthTest();
            GL11.glPopMatrix();
            GL11.glMatrixMode(GL11.GL_PROJECTION);
            GL11.glPopMatrix();
            GL11.glMatrixMode(GL11.GL_MODELVIEW);
            //GL11.glEnable(GL11.GL_CULL_FACE);
            //RenderHelper.setupGui3DDiffuseLighting();

            //canvas.drawRoundedRect(0, 25, 48, 45, 6);*/
        }
    }

    public static class TestLinearLayout extends LinearLayout {

        private float c = 10;
        private float f = 0;

        /*private final Animation cAnim;

        private final Animation circleAnimation1;
        private final Animation circleAnimation2;
        private final Animation circleAnimation3;
        private final Animation circleAnimation4;
        private final Animation iconRadiusAni;
        private final Animation arcStartAni;
        private final Animation arcEndAni;*/

        private static final FloatBuffer sLinePoints = FloatBuffer.allocate(16);
        private static final IntBuffer sLineColors = IntBuffer.allocate(sLinePoints.capacity() / 2);

        private static final FloatBuffer sTrianglePoints = FloatBuffer.allocate(12);
        private static final IntBuffer sTriangleColors = IntBuffer.allocate(sTrianglePoints.capacity() / 2);

        static {
            sLinePoints
                    .put(100).put(100)
                    .put(110).put(200)
                    .put(120).put(100)
                    .put(130).put(300)
                    .put(140).put(100)
                    .put(150).put(400)
                    .put(160).put(100)
                    .put(170).put(500)
                    .flip();
            sLineColors
                    .put(0xAAFF0000)
                    .put(0xFFFF00FF)
                    .put(0xAA0000FF)
                    .put(0xFF00FF00)
                    .put(0xAA00FFFF)
                    .put(0xFF00FF00)
                    .put(0xAAFFFF00)
                    .put(0xFFFFFFFF)
                    .flip();
            sTrianglePoints
                    .put(420).put(20)
                    .put(420).put(100)
                    .put(490).put(60)
                    .put(300).put(130)
                    .put(250).put(180)
                    .put(350).put(180)
                    .flip();
            sTriangleColors
                    .put(0xAAFF0000)
                    .put(0xFFFF00FF)
                    .put(0xAA0000FF)
                    .put(0xAA00FFFF)
                    .put(0xFF00FF00)
                    .put(0xAAFFFF00)
                    .flip();
        }

        private final Animator mRoundRectLenAnim;
        //private final Animation roundRectAlphaAni;

        private float circleAcc1;
        private float circleAcc2;
        private float circleAcc3;
        private float circleAcc4;
        private float iconRadius = 40;
        private float arcStart = 0;
        private float arcEnd = 0;

        private float mRoundRectLen = 0;
        private float roundRectAlpha = 0;
        private float mSmoothRadius = 0;

        private boolean b;

        private int ticks;

        PopupWindow mPopupWindow = new PopupWindow();

        ObjectAnimator mGoodAnim;

        public TestLinearLayout(Context context) {
            super(context);
            setOrientation(VERTICAL);
            setGravity(Gravity.CENTER);
            setDividerDrawable(new Drawable() {
                @Override
                public void draw(@Nonnull Canvas canvas) {
                    Paint paint = Paint.obtain();
                    paint.setRGBA(192, 192, 192, 128);
                    canvas.drawRect(getBounds(), paint);
                    paint.recycle();
                }

                @Override
                public int getIntrinsicHeight() {
                    return 2;
                }
            });
            setShowDividers(SHOW_DIVIDER_MIDDLE | SHOW_DIVIDER_END);

            setPadding(dp(12), dp(12), dp(12), dp(12));

            setDividerPadding(dp(8));

            setClickable(true);
            setFocusable(true);
            setFocusableInTouchMode(true);


            ObjectAnimator anim;
            {
                var pvh1 = PropertyValuesHolder.ofFloat(ROTATION, 0, 2880);
                var pvh2 = PropertyValuesHolder.ofFloat(ROTATION_Y, 0, 720);
                var pvh3 = PropertyValuesHolder.ofFloat(ROTATION_X, 0, 1440);
                anim = ObjectAnimator.ofPropertyValuesHolder(this, pvh1, pvh2, pvh3);
                anim.setDuration(12000);
                anim.setInterpolator(TimeInterpolator.ACCELERATE_DECELERATE);
                //anim.setRepeatCount(ValueAnimator.INFINITE);
                //anim.start();
                mGoodAnim = anim;
            }

                anim = ObjectAnimator.ofFloat(this, sRoundRectLengthProp, 0, 80);
                anim.setDuration(400);
                anim.setInterpolator(TimeInterpolator.OVERSHOOT);
                anim.addUpdateListener(a -> invalidate());
                mRoundRectLenAnim = anim;




            setLayoutTransition(new LayoutTransition());
        }

        private static final FloatProperty<TestLinearLayout> sRoundRectLengthProp = new FloatProperty<>("roundRectLen"
        ) {
            @Override
            public void setValue(@Nonnull TestLinearLayout object, float value) {
                object.mRoundRectLen = value;
            }

            @Override
            public Float get(@Nonnull TestLinearLayout object) {
                return object.mRoundRectLen;
            }
        };

        private static final FloatProperty<TestLinearLayout> sSmoothRadiusProp = new FloatProperty<>("smoothRadius") {
            @Override
            public void setValue(@Nonnull TestLinearLayout object, float value) {
                object.mSmoothRadius = value;
            }

            @Override
            public Float get(@Nonnull TestLinearLayout object) {
                return object.mSmoothRadius;
            }
        };

        @Override
        protected void onDraw(@Nonnull Canvas canvas) {
            super.onDraw(canvas);
            /*canvas.moveTo(this);
            canvas.resetColor();
            canvas.setTextAlign(TextAlign.LEFT);
            canvas.save();
            canvas.scale(3, 3);
            canvas.drawText("A Text", 10, 0);
            canvas.drawText(ChatFormatting.BOLD + "A Text", 10, 10);
            canvas.drawText("This is اللغة " +
                    "العربية, and " +
                    "she is 海螺", 10, 20);
            canvas.restore();*/

            Paint paint = Paint.obtain();
            paint.setColor(SystemTheme.COLOR_CONTROL_ACTIVATED);
            paint.setStyle(Paint.FILL);
            canvas.drawRoundRect(6, 90, 46, 104, 7, paint);

            paint.setStyle(Paint.STROKE);
            paint.setStrokeWidth(4.0f);
            canvas.save();
            canvas.rotate(-45);
            canvas.drawRoundRect(6, 110, 86, 124, 6, paint);

            paint.setStyle(Paint.FILL);
            canvas.drawRect(6, 126, 86, 156, paint);
            canvas.restore();

            canvas.drawLine(560, 20, 600, 100, 10, paint);

            canvas.drawLineListMesh(sLinePoints, sLineColors, paint);
            //canvas.drawPointListMesh(sLinePoints, sLineColors, paint);
            canvas.drawTriangleListMesh(sTrianglePoints, sTriangleColors, paint);

            //canvas.drawRoundImage(ICON, 6, 160, 166, 320, iconRadius, paint);

            paint.setStyle(Paint.STROKE);
            canvas.drawPie(100, 200, 50, 60, 120, paint);
            float s1 = (float) Math.sin(AnimationUtils.currentAnimationTimeMillis() / 300D);
            canvas.drawPie(350, 94, 55, 180 + 20 * s1, 100 + 50 * s1 * s1, paint);

            paint.setStrokeWidth(10.0f);
            canvas.drawRect(200, 300, 500, 400, paint);
            paint.setStrokeCap(Paint.CAP_SQUARE);
            canvas.drawRect(200, 450, 500, 550, paint);

            paint.setStrokeWidth(40.0f);
            paint.setSmoothWidth(40.0f);
            //canvas.drawArc(80, 400, 60, arcStart, arcStart - arcEnd, paint);
            canvas.drawArc(80, 400, 50, 60, 240, paint);
            canvas.drawBezier(80, 400, 180, 420, 80, 600, paint);

            paint.setStyle(Paint.FILL);
            canvas.drawCircle(80, 700, 60, paint);

            paint.setSmoothWidth(0.0f);

            paint.setStyle(Paint.FILL);
            paint.setAlpha((int) (roundRectAlpha * 192));
            canvas.drawRoundRect(20, 480, 20 + mRoundRectLen * 1.6f, 480 + mRoundRectLen, 10, paint);
            paint.setAlpha(255);

            paint.recycle();

            // 1

            /*canvas.save();
            RenderSystem.depthMask(true);

            //canvas.scale(f, f, getLeft() + 10, getTop() + 10);
            RenderSystem.translatef(0, 0, 0.001f);
            RenderSystem.colorMask(false, false, false, true);
            //canvas.setColor(0, 0, 0, 128);

            paint.setStyle(Paint.Style.FILL);
            paint.setSmoothRadius(0);
            canvas.drawRoundRect(c, c, 40 - c, 40 - c, 3, paint);

            RenderSystem.translatef(0, 0, -0.001f);
            RenderSystem.colorMask(true, true, true, true);

            paint.setSmoothRadius(1);
            paint.setRGBA(80, 210, 240, 128);
            canvas.drawRoundRect(0, 0, 40, 40, 6, paint);

            canvas.restore();
            RenderSystem.depthMask(false);*/


            // 4

            /*paint.reset();

            canvas.save();
            canvas.translate((float) Math.sin(circleAcc1) * 8, (float) Math.cos(circleAcc1) * 8);
            canvas.drawCircle(40, 18, 3, paint);
            canvas.restore();

            canvas.save();
            canvas.translate((float) Math.sin(circleAcc2) * 8, (float) Math.cos(circleAcc2) * 8);
            canvas.drawCircle(40, 18, 2.5f, paint);
            canvas.restore();

            canvas.save();
            canvas.translate((float) Math.sin(circleAcc3) * 8, (float) Math.cos(circleAcc3) * 8);
            canvas.drawCircle(40, 18, 2, paint);
            canvas.restore();

            canvas.save();
            canvas.translate((float) Math.sin(circleAcc4) * 8, (float) Math.cos(circleAcc4) * 8);
            canvas.drawCircle(40, 18, 1.5f, paint);
            canvas.restore();*/


            // 5

            /*canvas.drawRect(35, 55, 45, 65);
            RenderSystem.blendFuncSeparate(GL11.GL_ONE_MINUS_DST_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11
            .GL_ONE_MINUS_DST_ALPHA, GL11.GL_ZERO);
            canvas.drawCircle(40, 60, 4);
            RenderSystem.defaultBlendFunc();*/

            // 2
            /*GL11.glEnable(GL11.GL_STENCIL_TEST);
            GL11.glClearStencil(0);
            GL11.glClear(GL11.GL_STENCIL_BUFFER_BIT);

            GL11.glStencilMask(0xff);

            GL11.glStencilFunc(GL11.GL_ALWAYS, 1, 0xff);
            GL11.glStencilOp(GL11.GL_KEEP, GL11.GL_KEEP, GL11.GL_REPLACE);

            canvas.setColor(255, 255, 255, 128);
            canvas.drawRect(5, 2, 15, 8);

            GL11.glStencilOp(GL11.GL_KEEP, GL11.GL_KEEP, GL11.GL_KEEP);
            GL11.glStencilFunc(GL11.GL_NOTEQUAL, 1, 0xff);

            canvas.setColor(0, 0, 0, 128);
            canvas.drawRect(0, 0, 20, 10);

            GL11.glDisable(GL11.GL_STENCIL_TEST);*/
        }

        protected boolean onMousePressed(double mouseX, double mouseY, int mouseButton) {
            /*if (!b) {
                cAnim.start();
                b = true;
            } else {
                cAnim.invert();
                b = false;
            }
            f = 0.95f;*/
            return true;
        }

        protected boolean onMouseReleased(double mouseX, double mouseY, int mouseButton) {
            f = 1;
            return true;
        }

        public void tick() {
            /*ticks++;
            if ((ticks & 15) == 0) {
                if (!b) {
                    cAnim.start();
                    iconRadiusAni.start();
                    b = true;
                } else {
                    cAnim.invert();
                    iconRadiusAni.invert();
                    b = false;
                }
            }
            int a = ticks % 20;
            if (a == 1) {
                circleAnimation1.startFull();
                arcStartAni.startFull();
                arcEndAni.startFull();
                mRoundRectLenAnim.start();
                roundRectAlphaAni.startFull();
            } else if (a == 3) {
                circleAnimation2.startFull();
            } else if (a == 5) {
                circleAnimation3.startFull();
            } else if (a == 7) {
                circleAnimation4.startFull();
            }*/
        }

        private static class CView extends View {

            private final String mIndex;
            TextPaint mTextPaint = new TextPaint();

            public CView(Context context, int index) {
                super(context);
                mIndex = Integer.toString(index);
            }

            @Override
            protected void onDraw(@Nonnull Canvas canvas) {
                if (isHovered()) {
                    Paint paint = Paint.obtain();
                    paint.setARGB(128, 140, 200, 240);
                    canvas.drawRoundRect(0, 1, getWidth(), getHeight() - 2, 4, paint);
                    TextUtils.drawTextRun(canvas, mIndex, 0, mIndex.length(), 0, mIndex.length(), 20,
                            getHeight() >> 1, false,
                            mTextPaint);
                    paint.recycle();
                }
            }

            @Override
            public void onHoverChanged(boolean hovered) {
                super.onHoverChanged(hovered);
                invalidate();
            }
        }

        private static class DView extends View {

            //private final Animation animation;

            private float offsetY;

            private final TextPaint mTextPaint = new TextPaint();
            private int mTicks;

            private final ObjectAnimator mAnimator;

            public DView(Context context, TimeInterpolator interpolator) {
                super(context);
                mTextPaint.setTextSize(10);
                /*animation = new Animation(200)
                        .applyTo(new Applier(0, 60, () -> offsetY, v -> {
                            offsetY = v;
                            invalidate();
                        }).setInterpolator(interpolator));
                animation.invertFull();*/
                PropertyValuesHolder pvh1 = PropertyValuesHolder.ofFloat(ROTATION, 0, 360);
                PropertyValuesHolder pvh2 = PropertyValuesHolder.ofFloat(SCALE_X, 1, 0.2f);
                PropertyValuesHolder pvh3 = PropertyValuesHolder.ofFloat(SCALE_Y, 1, 0.2f);
                PropertyValuesHolder pvh4 = PropertyValuesHolder.ofFloat(TRANSLATION_X, 0, 60);
                PropertyValuesHolder pvh5 = PropertyValuesHolder.ofFloat(TRANSLATION_Y, 0, -180);
                PropertyValuesHolder pvh6 = PropertyValuesHolder.ofFloat(ALPHA, 1, 0);
                mAnimator = ObjectAnimator.ofPropertyValuesHolder(this, pvh1, pvh2, pvh3, pvh4, pvh5, pvh6);
                mAnimator.setRepeatCount(1);
                mAnimator.setRepeatMode(ObjectAnimator.REVERSE);
                mAnimator.setDuration(3000);
                setClickable(true);
            }

            @Override
            protected void onDraw(@Nonnull Canvas canvas) {
                Paint paint = Paint.obtain();
                paint.setARGB(128, 140, 200, 240);
                canvas.drawRoundRect(0, 1, getWidth(), getHeight() - 2, 4, paint);
                TextUtils.drawTextRun(canvas, "18:52", 0, 5, 0, 5, getWidth() / 2f, offsetY + 24, false, mTextPaint);
                paint.recycle();
            }

            @Override
            public boolean performClick() {
                mAnimator.start();
                return super.performClick();
            }

            /*public void tick() {
                mTicks++;
                if (mTicks % 40 == 0) {
                    animation.invert();
                } else if (mTicks % 20 == 0) {
                    animation.start();
                }
            }*/
        }
    }

    public static class SpectrumGraph {

        private final boolean mCircular;

        private final float[] mAmplitudes = new float[60];
        private final FFT mFFT;
        private final int mHeight;

        private boolean mUpdated;

        public SpectrumGraph(Track track, View view, boolean circular, int height) {
            mFFT = FFT.create(1024, track.getSampleRate());
            mFFT.setLogAverages(250, 14);
            mFFT.setWindowFunc(FFT.HANN);
            track.setAnalyzer(mFFT, f -> {
                updateAmplitudes();
                view.postInvalidate();
            });
            mCircular = circular;
            mHeight = height;
        }

        public void updateAmplitudes() {
            int len = Math.min(mFFT.getAverageSize() - 5, mAmplitudes.length);
            long time = Core.timeMillis();
            int iOff;
            if (mCircular)
                iOff = (int) (time / 200);
            else
                iOff = 0;
            synchronized (mAmplitudes) {
                for (int i = 0; i < len; i++) {
                    float va = mFFT.getAverage(((i + iOff) % len) + 5) / mFFT.getBandSize();
                    mAmplitudes[i] = Math.max(mAmplitudes[i], va);
                }
                mUpdated = true;
            }
        }

        public boolean update(long delta) {
            int len = Math.min(mFFT.getAverageSize() - 5, mAmplitudes.length);
            synchronized (mAmplitudes) {
                for (int i = 0; i < len; i++) {
                    // 2.5e-5f * BPM
                    mAmplitudes[i] = Math.max(0, mAmplitudes[i] - delta * 2.5e-5f * 198f * (mAmplitudes[i] + 0.03f));
                }
                boolean updated = mUpdated;
                mUpdated = false;
                if (!updated) {
                    for (int i = 0; i < len; i++) {
                        if (mAmplitudes[i] > 0) {
                            return true;
                        }
                    }
                }
                return updated;
            }
        }

        public void draw(@Nonnull Canvas canvas, float cx, float cy) {
            var paint = Paint.obtain();
            if (mCircular) {
                long time = Core.timeMillis();
                float b = 1.5f + MathUtil.sin(time / 600f) / 2;
                paint.setRGBA(160, 155, 230, (int) (64 * b));
                paint.setStrokeWidth(200);
                paint.setSmoothWidth(200);
                paint.setStyle(Paint.STROKE);
                canvas.drawCircle(cx, cy, 130, paint);
                paint.reset();
                for (int i = 0; i < mAmplitudes.length; i++) {
                    float f = Math.abs((i + (int) (time / 100)) % mAmplitudes.length - (mAmplitudes.length - 1) / 2f)
                            / (mAmplitudes.length - 1) * b;
                    paint.setRGBA(100 + (int) (f * 120), 220 - (int) (f * 130), 240 - (int) (f * 20), 255);
                    canvas.rotate(-360f / mAmplitudes.length, cx, cy);
                    canvas.drawRect(cx - 6, cy - 120 - mAmplitudes[i] * mHeight, cx + 6, cy - 120, paint);
                }
            } else {
                for (int i = 0; i < mAmplitudes.length; i++) {
                    paint.setRGBA(100 + i * 2, 220 - i * 2, 240 - i * 4, 255);
                    canvas.drawRect(cx - 479 + i * 16, cy - mAmplitudes[i] * mHeight, cx - 465 + i * 16, cy, paint);
                }
            }
            paint.recycle();
        }
    }
}
