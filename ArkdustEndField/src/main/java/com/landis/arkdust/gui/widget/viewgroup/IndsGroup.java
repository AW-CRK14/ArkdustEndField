package com.landis.arkdust.gui.widget.viewgroup;

import com.landis.arkdust.Arkdust;
import com.landis.arkdust.gui.AbstractArkdustInfoUI;
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
import org.jetbrains.annotations.NotNull;

public class IndsGroup extends RelativeLayout {
    public static final int MIN_WIDTH = 0;
    public static final int MIN_HEIGHT = 40;
    private static final int TOP_H = 20;

    public final ResourceLocation id;
    public final int lv;
    public final int thickness = dp(0.5F);

    public IndsGroup(Context context, ResourceLocation id, int lv) {
        super(context);
        this.id = id;
        this.lv = lv;
        this.setBackground(new Background());
//        this.setBackground(AbstractArkdustInfoUI.withBorder());


        ImageView logo = new ImageView(context);
        logo.setImage(Image.create(id.getNamespace(), "ind_mac/" + id.getPath() + ".png"));
//        logo.setX(4);
//        logo.setY(4);
//        this.addView(logo,new LayoutParams(12,12));
        this.addView(logo, new LayoutParams(dp(20), dp(20)));

        RelativeLayout topBar = new RelativeLayout(context);
        topBar.setGravity(RelativeLayout.CENTER_VERTICAL);
//        topBar.setBackground(AbstractArkdustInfoUI.withBorder());//test
        LayoutParams paraTopBar = new LayoutParams(-1, dp(20));
        paraTopBar.setMarginsRelative(dp(20), 0, 0, dp(getHeight() - 20));
        {
            LinearLayout titles = new LinearLayout(context);
            titles.setOrientation(LinearLayout.VERTICAL);
            titles.setGravity(Gravity.LEFT);
//            titles.setBackground(AbstractArkdustInfoUI.withBorder());//test
            LayoutParams paraTitles = new LayoutParams(-1, -2);
            paraTitles.setMarginsRelative(dp(2), 0, 0, 0);
            paraTitles.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
            {
                ImageView nameDec = new ImageView(context);
                nameDec.setImage(Image.create(Arkdust.MODID, "gui/element/ind/name_dec.png"));
                LayoutParams paraNameDec = new LayoutParams(dp(25), dp(5));
                paraNameDec.setMargins(dp(3), dp(2.5F), 0, 0);
                titles.addView(nameDec, paraNameDec);

                LinearLayout nameGroup = new LinearLayout(context);
                nameGroup.setGravity(Gravity.BOTTOM);
                nameGroup.setShowDividers(LinearLayout.SHOW_DIVIDER_MIDDLE);
                nameGroup.setOrientation(LinearLayout.HORIZONTAL);
                nameGroup.setDividerDrawable(new Divider());
                {
                    TextView name = new TextView(context);
                    name.setText(I18n.get(nameKey()));
                    name.setTextSize(dp(1.05F));//TODO
                    name.setTextColor(CTM);
                    LayoutParams paraName = new LayoutParams(-2, -2);
                    paraName.setMargins(dp(3), 0, dp(3), 0);
                    nameGroup.addView(name, paraName);

                    TextView lvText = new TextView(context);
                    lvText.setText("Lv." + lv);
                    lvText.setTextSize(dp(0.7F));
                    lvText.setTextColor(CTS);
                    LayoutParams paraLv = new LayoutParams(-2, -2);
                    paraLv.setMargins(dp(3), 0, dp(4), 0);
                    nameGroup.addView(lvText, paraLv);
                }
                titles.addView(nameGroup);
            }
            topBar.addView(titles, paraTitles);

            LinearLayout buttons = new LinearLayout(context);
            buttons.setOrientation(LinearLayout.HORIZONTAL);
            buttons.setGravity(Gravity.RIGHT);
            buttons.setShowDividers(LinearLayout.SHOW_DIVIDER_MIDDLE);
            buttons.setDividerDrawable(new Divider());
//            buttons.setBackground(AbstractArkdustInfoUI.withBorder());//test
            LayoutParams paraButtons = new LayoutParams(-2, -2);
            paraButtons.setMarginsRelative(dp(3), 0, dp(3), 0);
            paraButtons.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            paraButtons.addRule(RelativeLayout.CENTER_VERTICAL);
//            paraButtons.addRule(Gravity.CENTER);
            {
                buttons.addView(new TopBarButton(context, 1001, new ResourceLocation(Arkdust.MODID, "gui/element/ind/close.png"), v -> Minecraft.getInstance().execute(() -> Minecraft.getInstance().setScreen(null))), new LayoutParams(dp(12),dp(12)));
            }
            topBar.addView(buttons,paraButtons);
        }
        this.addView(topBar, paraTopBar);


    }

    public static final int CTM = 0xFF2E2E2E;
    public static final int CTS = 0xFF8C8C8C;

    public String nameKey() {
        return "arkdust.industry." + id.getNamespace() + "." + id.getPath() + ".name";
    }

    public class Background extends Drawable {
        public static final int CA = 0xFFE6E6E6;
        public static final int CB = 0xDA1E1E1E;
        private final Paint PAINT = new Paint();

        @Override
        public void draw(Canvas canvas) {
            Rect b = getBounds();
            int x0 = b.left;
            int x1 = b.right;
            int y0 = b.top;
            int y1 = y0 + dp(TOP_H);
            int y2 = Math.min(b.bottom, y1 + dp(30));
            boolean flag = y2 < b.bottom;
            PAINT.setColor(CA);
            canvas.drawRoundRect(x0, y0, x1, y1, dp(4), Gravity.TOP, PAINT);
            canvas.drawRectGradient(x0, y1, x1, y2, CA, CA, CB, CB, PAINT);
            if (flag) {
                PAINT.setColor(CB);
                canvas.drawRoundRect(x0, y2, x1, b.bottom, dp(4), Gravity.BOTTOM, PAINT);
            }

            PAINT.setColor(CTS);
            canvas.drawLine(x0, y1, x1, y1, thickness, PAINT);
            canvas.drawLine(x0 + dp(20), y0, x0 + dp(20), y1, thickness, PAINT);
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

}
