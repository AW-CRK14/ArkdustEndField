package com.landis.arkdust.mui.widget.viewgroup;

import com.landis.arkdust.Arkdust;
import com.landis.arkdust.helper.MUIHelper;
import icyllis.modernui.animation.ObjectAnimator;
import icyllis.modernui.animation.TimeInterpolator;
import icyllis.modernui.core.Context;
import icyllis.modernui.graphics.Canvas;
import icyllis.modernui.graphics.Image;
import icyllis.modernui.graphics.Paint;
import icyllis.modernui.graphics.Rect;
import icyllis.modernui.graphics.drawable.Drawable;
import icyllis.modernui.graphics.drawable.ImageDrawable;
import icyllis.modernui.util.FloatProperty;
import icyllis.modernui.view.Gravity;
import icyllis.modernui.view.MotionEvent;
import icyllis.modernui.view.View;
import icyllis.modernui.widget.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.resources.ResourceLocation;

public class IndsGroup extends RelativeLayout {
    public static final int TOP_H = 40;

    public final ResourceLocation id;
    public final int lv;
    public final int thickness = dp(0.5F);
    public final RelativeLayout child;


    public float renderNodeA;
    public float renderNodeB;


    private boolean leftDecEnable = true;

    private boolean closeButtonEnable = true;

    public IndsGroup(Context context, ResourceLocation id, int lv, boolean enableCloseButton) {
        this(context, id, lv, enableCloseButton, 0, 0.4F);
    }

    public IndsGroup(Context context, ResourceLocation id, int lv, boolean enableCloseButton, float renderNodeA, float renderNodeB) {
        super(context);
        this.id = id;
        this.lv = lv;
        this.setBackground(new Background());
//        this.setBackground(MUIHelper.withBorder());//test

        this.renderNodeA = renderNodeA;
        this.renderNodeB = renderNodeB;

        ImageView logo = new ImageView(context);
        logo.setImage(Image.create(id.getNamespace(), "ind_mac/" + id.getPath() + ".png"));
//        logo.setBackground(MUIHelper.withBorder());//test
        LayoutParams paraLogo = new LayoutParams(dp(TOP_H * 0.8F), dp(TOP_H * 0.8F));
        paraLogo.setMargins(dp(TOP_H * 0.1F), dp(TOP_H * 0.1F), dp(TOP_H * 0.1F), dp(TOP_H * 0.1F));
        this.addView(logo, paraLogo);

        RelativeLayout topBar = new RelativeLayout(context);
        topBar.setGravity(RelativeLayout.CENTER_VERTICAL);
        topBar.setId(200001);
//        topBar.setBackground(MUIHelper.withBorder());//test
        LayoutParams paraTopBar = new LayoutParams(-2, dp(TOP_H));
        paraTopBar.setMarginsRelative(dp(TOP_H), 0, 0, dp(getHeight() - TOP_H));
        {
            LinearLayout titles = new LinearLayout(context);
            titles.setOrientation(LinearLayout.VERTICAL);
            titles.setGravity(Gravity.LEFT);
            titles.setId(200011);
//            titles.setBackground(MUIHelper.withBorder());//test
            LayoutParams paraTitles = new LayoutParams(-2, -2);
            paraTitles.setMarginsRelative(dp(TOP_H * 0.1F), 0, 0, 0);
            paraTitles.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
            {
                ImageView nameDec = new ImageView(context);
                nameDec.setImage(Image.create(Arkdust.MODID, "gui/element/ind/name_dec.png"));
                LayoutParams paraNameDec = new LayoutParams(dp(TOP_H * 1.25F), dp(TOP_H * 0.25F));
                paraNameDec.setMargins(dp(TOP_H * 0.15F), dp(TOP_H / 8F), 0, 0);
                titles.addView(nameDec, paraNameDec);

                LinearLayout nameGroup = new LinearLayout(context);
                nameGroup.setGravity(Gravity.BOTTOM);
                nameGroup.setShowDividers(LinearLayout.SHOW_DIVIDER_MIDDLE);
                nameGroup.setOrientation(LinearLayout.HORIZONTAL);
//                nameGroup.setBackground(MUIHelper.withBorder());//test
                nameGroup.setDividerDrawable(new Divider());
                {
                    TextView name = new TextView(context);
                    name.setText(I18n.get(nameKey()));
                    name.setTextSize(dp(TOP_H * 0.135F));//TODO
                    name.setTextColor(CTM);
                    LayoutParams paraName = new LayoutParams(-2, -2);
                    paraName.setMargins(dp(TOP_H * 0.15F), 0, dp(TOP_H * 0.15F), 0);
                    nameGroup.addView(name, paraName);

                    if (lv >= 0) {
                        TextView lvText = new TextView(context);
                        lvText.setText("Lv." + lv);
                        lvText.setTextSize(dp(TOP_H * 0.09F));
                        lvText.setTextColor(CTS);
                        LayoutParams paraLv = new LayoutParams(-2, -2);
                        paraLv.setMargins(dp(TOP_H * 0.15F), 0, dp(TOP_H * 0.2F), 0);
                        nameGroup.addView(lvText, paraLv);
                    }
                }
                titles.addView(nameGroup, new LayoutParams(-2, -1));

            }
            topBar.addView(titles, paraTitles);

            LinearLayout buttons = new LinearLayout(context);
            buttons.setOrientation(LinearLayout.HORIZONTAL);
            buttons.setGravity(Gravity.RIGHT);
            buttons.setShowDividers(LinearLayout.SHOW_DIVIDER_MIDDLE);
            buttons.setDividerDrawable(new Divider());
//            buttons.setBackground(MUIHelper.withBorder());//test
            LayoutParams paraButtons = new LayoutParams(-2, -2);
            paraButtons.setMarginsRelative(dp(TOP_H * 0.15F), 0, dp(TOP_H * 0.15F), 0);
//            paraButtons.addRule(RelativeLayout.ALIGN_RIGHT);
            paraButtons.addRule(RelativeLayout.CENTER_VERTICAL);
            paraButtons.addRule(RelativeLayout.ALIGN_RIGHT, topBar.getId());
            paraButtons.addRule(RelativeLayout.RIGHT_OF, titles.getId());
//            paraButtons.addRule(Gravity.CENTER);
            {
                if (enableCloseButton) {
                    buttons.addView(new TopBarButton(context, 1001, new ResourceLocation(Arkdust.MODID, "gui/element/ind/close.png"), v -> Minecraft.getInstance().execute(() -> Minecraft.getInstance().setScreen(null))), new LayoutParams(dp(TOP_H * 0.6F), dp(TOP_H * 0.6F)));
                }
            }
            topBar.addView(buttons, paraButtons);

        }
        this.addView(topBar, paraTopBar);

        //TODO RecipeBar
        this.child = new RelativeLayout(getContext());
        this.child.setId(210000);
        LayoutParams paraChild = new LayoutParams(-2, -2);
        paraChild.setMarginsRelative(0, dp(TOP_H), 0, 0);
        this.addView(child, paraChild);

    }


    public static final int CTM = 0xFF2E2E2E;
    public static final int CTS = 0xFF8C8C8C;

    public String nameKey() {
        return "arkdust.industry." + id.getNamespace() + "." + id.getPath() + ".name";
    }

    public class Background extends Drawable {
        public static final int CA = 0xFFE6E6E6;
        public static final int CB = 0x9F1E1E1E;
        private final Paint PAINT = new Paint();
        private Image left;

        @Override
        public void draw(Canvas canvas) {
            Rect b = getBounds();
            int x0 = b.left;
            int x1 = b.right;
            int y0 = b.top;
            float y1 = y0 + dp(TOP_H) + b.height() * renderNodeA;
            float y2 = Math.min(b.bottom, y1 + b.height() * renderNodeB);
            boolean flag = y2 < b.bottom;
            PAINT.setColor(CA);
            canvas.drawRoundRect(x0, y0, x1, y1, dp(4), Gravity.TOP, PAINT);
            if (y1 < y2)
                canvas.drawRectGradient(x0, y1, x1, y2, CA, CA, CB, CB, PAINT);
            if (flag) {
                PAINT.setColor(CB);
                canvas.drawRoundRect(x0, y2, x1, b.bottom, dp(TOP_H * 0.2F), Gravity.BOTTOM, PAINT);
            }

            PAINT.setColor(CTS);
            canvas.drawLine(x0, y0 + dp(TOP_H), x1, y0 + dp(TOP_H), thickness, PAINT);
            canvas.drawLine(x0 + dp(TOP_H), y0, x0 + dp(TOP_H), y0 + dp(TOP_H), thickness, PAINT);

            if (leftDecEnable) {
                left = Image.create(Arkdust.MODID, "gui/element/ind/left_net.png");
                //left_net左侧渲染
                MUIHelper.repeatedGridImage(canvas, new Rect(0, dp(TOP_H), dp(TOP_H * 0.75F), b.bottom), left, false, true, 1, PAINT);
            }
        }
    }

    public class Divider extends Drawable {
        private final Paint PAINT = new Paint();

        {
            PAINT.setColor(CTS);
        }

        @Override
        public void draw(Canvas canvas) {
            canvas.drawLine(getBounds().left, getBounds().top + getBounds().height() * 0.3F, getBounds().left, getBounds().bottom - getBounds().height() * 0.2F, thickness, PAINT);
        }

    }


    public static class TopBarButton extends CompoundButton {
        ObjectAnimator animator;

        public TopBarButton(Context context, int id, ResourceLocation image, View.OnClickListener listener) {
            super(context);
            setWidth(dp(16));
            setHeight(dp(16));
            setId(id);
            setAlpha(0.7F);

            setBackground(new ImageDrawable(image.getNamespace(), image.getPath()));
            animator = ObjectAnimator.ofFloat(this, animProperty, 0.7F, 1);
            animator.setDuration(200);
            animator.setInterpolator(TimeInterpolator.DECELERATE);

            setOnHoverListener((v, event) -> {
                switch (event.getAction()) {
                    case (MotionEvent.ACTION_HOVER_ENTER):
                        post(() -> animator.start());
                        break;
                    case (MotionEvent.ACTION_HOVER_EXIT):
                        post(() -> animator.reverse());
                        break;
                }
                return false;
            });

            setOnClickListener(listener);
        }


        private static final FloatProperty<TopBarButton> animProperty = new FloatProperty<>("hover"
        ) {
            @Override
            public void setValue(TopBarButton object, float value) {
                object.setAlpha(value);
            }

            @Override
            public Float get(TopBarButton object) {
                return object.getAlpha();
            }
        };
    }


    public void setLeftDecEnable(boolean leftDecEnable) {
        this.leftDecEnable = leftDecEnable;
    }

    public void disableLeftDec() {
        this.leftDecEnable = false;
    }

}
