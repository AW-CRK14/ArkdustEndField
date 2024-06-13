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
import icyllis.modernui.view.ViewGroup;
import icyllis.modernui.widget.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Supplier;

public class IndsGroup extends RelativeLayout {
    public static final int TOP_H = 40;
    public static final Image left = Image.create(Arkdust.MODID, "gui/element/ind/left_net.png");


    public final ResourceLocation id;
    public final int lv;
    public final int thickness = dp(0.5F);
    public final ViewGroup child;


    public float renderNodeA;
    public float renderNodeB;


    private boolean leftDecEnable = true;

    public IndsGroup(Context context, ResourceLocation id, int lv, boolean enableCloseButton, int subject) {
        this(context, id, lv, enableCloseButton, 0, 0.4F, subject);
    }

    public IndsGroup(Context context, ResourceLocation id, int lv, boolean enableCloseButton, int subject, Supplier<? extends ViewGroup> supplier) {
        this(context, id, lv, enableCloseButton, 0, 0.4F, subject, supplier);
    }

    public IndsGroup(Context context, ResourceLocation id, int lv, boolean enableCloseButton, float renderNodeA, float renderNodeB, int subject) {
        this(context, id, lv, enableCloseButton, renderNodeA, renderNodeB, subject, () -> new RelativeLayout(context));
    }

    public IndsGroup(Context context, ResourceLocation id, int lv, boolean enableCloseButton, float renderNodeA, float renderNodeB, int subject, Supplier<? extends ViewGroup> supplier) {
        super(context);
        this.id = id;
        this.lv = lv;
        this.setBackground(new Background());
        this.setId(subject);

        this.renderNodeA = renderNodeA;
        this.renderNodeB = renderNodeB;

        ImageView logo = new ImageView(context);
        logo.setId(subject + 1);
        logo.setImage(Image.create(id.getNamespace(), "ind_mac/" + id.getPath() + ".png"));
        LayoutParams paraLogo = new LayoutParams(dp(TOP_H * 0.8F), dp(TOP_H * 0.8F));
        paraLogo.setMargins(dp(TOP_H * 0.1F), dp(TOP_H * 0.1F), dp(TOP_H * 0.1F), dp(TOP_H * 0.1F));
        this.addView(logo, paraLogo);

        LinearLayout titles = new LinearLayout(context);
        titles.setOrientation(LinearLayout.VERTICAL);
        titles.setGravity(Gravity.LEFT);
        titles.setId(subject + 2);
        LayoutParams paraTitles = new LayoutParams(-2, dp(TOP_H));
        paraTitles.setMarginsRelative(dp(TOP_H * 0.1F), 0, 0, 0);
        paraTitles.addRule(RelativeLayout.RIGHT_OF, logo.getId());
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
            titles.addView(nameGroup, new LayoutParams(-2, -2));

        }
        this.addView(titles, paraTitles);

        LinearLayout buttons = new LinearLayout(context);
        buttons.setOrientation(LinearLayout.HORIZONTAL);
        buttons.setShowDividers(LinearLayout.SHOW_DIVIDER_MIDDLE);
        buttons.setDividerDrawable(new Divider());
        buttons.setId(subject + 3);
        buttons.setGravity(Gravity.CENTER_VERTICAL | Gravity.RIGHT);
        LayoutParams paraButtons = new LayoutParams(-2, dp(TOP_H));
        paraButtons.setMarginsRelative(dp(TOP_H * 0.15F), 0, dp(TOP_H * 0.15F), 0);
        paraButtons.addRule(RelativeLayout.ALIGN_RIGHT, subject + 10000);
        paraButtons.addRule(RelativeLayout.RIGHT_OF, titles.getId());
        {
            if (enableCloseButton) {
                buttons.addView(new TopBarButton(context, subject + 9901, new ResourceLocation(Arkdust.MODID, "gui/element/ind/close.png"), v -> Minecraft.getInstance().execute(() -> Minecraft.getInstance().setScreen(null))), new LayoutParams(dp(TOP_H * 0.6F), dp(TOP_H * 0.6F)));
            }
        }
        this.addView(buttons, paraButtons);

        this.child = supplier.get();
        this.child.setId(subject + 10000);
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
        public static final int CB = 0x8A1E1E1E;
        private final Paint PAINT = new Paint();
        @Override
        public void draw(Canvas canvas) {
            Rect b = getBounds();
            int x0 = b.left;
            int x1 = b.right;
            int y0 = b.top;
            float y1 = y0 + dp(TOP_H) + b.height() * renderNodeA;
            float y2 = Math.min(b.bottom, Math.max(y0 + dp(TOP_H) + b.height() * renderNodeB, y1));
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
