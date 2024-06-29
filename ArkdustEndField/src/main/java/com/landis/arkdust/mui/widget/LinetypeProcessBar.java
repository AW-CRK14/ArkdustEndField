package com.landis.arkdust.mui.widget;

import icyllis.modernui.core.Context;
import icyllis.modernui.graphics.Canvas;
import icyllis.modernui.graphics.Paint;
import icyllis.modernui.view.View;
import it.unimi.dsi.fastutil.floats.Float2IntFunction;

import java.util.function.Supplier;

public class LinetypeProcessBar extends View {
    public final Float2IntFunction color;
    public final Supplier<Float> processProvider;

    public LinetypeProcessBar(Context context, int color, Supplier<Float> processProvider) {
        this(context, f -> color, processProvider);
    }

    public LinetypeProcessBar(Context context, Float2IntFunction color, Supplier<Float> processProvider) {
        super(context);
        this.color = color;
        this.processProvider = processProvider;
    }

    public int getColorAt(float progress) {
        return color.get(progress);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        float process = processProvider.get();
        Paint paint = new Paint();
        paint.setColor(color.get(process));
        canvas.drawRect(0, 0, getWidth() * process, getHeight(), paint);
    }
}
