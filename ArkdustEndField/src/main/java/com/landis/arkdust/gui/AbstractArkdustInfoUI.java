package com.landis.arkdust.gui;

import com.landis.arkdust.Arkdust;
import com.landis.arkdust.helper.MUIHelper;
import icyllis.arc3d.core.Color;
import icyllis.modernui.fragment.Fragment;
import icyllis.modernui.graphics.Canvas;
import icyllis.modernui.graphics.Image;
import icyllis.modernui.graphics.Paint;
import icyllis.modernui.graphics.Rect;
import icyllis.modernui.graphics.drawable.ShapeDrawable;
import icyllis.modernui.util.DataSet;
import icyllis.modernui.view.LayoutInflater;
import icyllis.modernui.view.View;
import icyllis.modernui.view.ViewGroup;
import icyllis.modernui.widget.RelativeLayout;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public abstract class AbstractArkdustInfoUI extends Fragment {
    public AbstractArkdustInfoUI() {
    }

//    @Deprecated
//    public static void main(String[] args) {
//        System.setProperty("java.awt.headless", "true");
//        Configurator.setRootLevel(Level.DEBUG);
//
//        try (ModernUI app = new ModernUI()) {
//            app.run(new AbstractArkdustInfoUI());
//        }
//        AudioManager.getInstance().close();
//        System.gc();
//    }

    public static Image BACKGROUND;
    public static Rect BACKGROUND_RECT;


    @Override
    public View onCreateView(LayoutInflater flater, ViewGroup container, DataSet savedInstanceState) {
        BACKGROUND = Objects.requireNonNullElse(BACKGROUND,Image.create(getBackground().getNamespace(),getBackground().getPath()));
        BACKGROUND_RECT = Objects.requireNonNullElse(BACKGROUND_RECT,new Rect(0,0, BACKGROUND.getWidth(), BACKGROUND.getHeight()));

        RelativeLayout base = new RelativeLayout(getContext()){
            @Override
            protected void onDraw(@NotNull Canvas canvas) {
                int top = (int) (getHeight() * 0.5F * ( 1 - (float) getWidth() / BACKGROUND.getWidth() ));
                Paint p = Paint.obtain();
                p.setAlpha(32);
                canvas.drawImage(BACKGROUND,BACKGROUND_RECT,new Rect(0,top,getWidth(),getHeight() - top),p);
                p.recycle();
                super.onDraw(canvas);
            }
        };

        {//顶部组件部分
            RelativeLayout topLayout = new RelativeLayout(getContext());
            MUIHelper.drawStringAt(getContext(), topLayout, "//" + I18n.get(getTitle()), Color.WHITE, 20, 35, 10);
            MUIHelper.drawImage(getContext(), topLayout, new ResourceLocation(Arkdust.MODID, "gui/element/endfield.png"), 20, -20, 120, 106).left();
            MUIHelper.Widgets.addCloseButton(getContext(), topLayout, this);
            base.addView(topLayout);
        }

        addView(flater,base,savedInstanceState);

        return base;
    }

    public abstract String getTitle();

    public ResourceLocation getBackground(){
        return new ResourceLocation(Arkdust.MODID,"gui/element/background.png");
    }

    public void addView(LayoutInflater inflater,RelativeLayout layout,DataSet dataSet){

    }

    public static ShapeDrawable withBorder(){
        ShapeDrawable shape = new ShapeDrawable();
        shape.setShape(ShapeDrawable.RECTANGLE);//形状为矩形
        shape.setCornerRadius(10);//圆角半径
        shape.setStroke(2,Color.BLUE);//设置边框
        return shape;
    }
}
