package com.landis.breakdowncore.module.render.color;

import it.unimi.dsi.fastutil.floats.FloatArrayList;
import it.unimi.dsi.fastutil.floats.FloatList;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import org.jetbrains.annotations.Range;

//使用ARGB顺序
public class GradientColors {
    public final boolean ignoreAlpha;
    private final IntList colors = new IntArrayList();
    private final FloatList points = new FloatArrayList();
    private int frontPointColor;
    private int endPointColor;

    public GradientColors(int frontPointColor, int endPointColor) {
        this.frontPointColor = frontPointColor;
        this.endPointColor = endPointColor;
        colors.add(frontPointColor);
        colors.add(endPointColor);
        points.add(0);
        points.add(1);
        ignoreAlpha = false;
    }

    public GradientColors(boolean ignoreAlpha, int frontPointColor, int endPointColor) {
        this.frontPointColor = frontPointColor;
        this.endPointColor = endPointColor;
        colors.add(frontPointColor);
        colors.add(endPointColor);
        points.add(0);
        points.add(1);
        this.ignoreAlpha = ignoreAlpha;
    }

    public static GradientColors equidistant(int... colors) {
        return equidistant(false,colors);
    }

    public static GradientColors equidistant(boolean ignoreAlpha, int... colors) {
        if (colors.length == 0) return new GradientColors(ignoreAlpha, 0xFF000000, 0xFFFFFFFF);
        if (colors.length == 1) return new GradientColors(ignoreAlpha, colors[0], colors[0]);
        GradientColors ins = new GradientColors(ignoreAlpha, colors[0], colors[colors.length - 1]);
        float interval = 1F / (colors.length - 1);
        for (int i = colors.length - 2; i >= 1; i--) {
            ins.colors.add(1,colors[i]);
            ins.points.add(1,i * interval);
        }
        return ins;
    }


    public int getFrontPointColor() {
        return frontPointColor;
    }

    public GradientColors setFrontPointColor(int frontPointColor) {
        this.frontPointColor = frontPointColor;
        colors.set(0, frontPointColor);
        return this;
    }

    public int getEndPointColor() {
        return endPointColor;
    }

    public GradientColors setEndPointColor(int endPointColor) {
        this.endPointColor = endPointColor;
        colors.set(colors.size() - 1, endPointColor);
        return this;
    }

    public GradientColors addKeyPoint(int color, float point) {
        return addKeyPoint(color, point, true);
    }

    public GradientColors addKeyPoint(int color, float point, boolean override) {
        if (point < 0 || point > 1)
            throw new IllegalArgumentException("Insert point must be in [0,1]. Currently, it's " + point);
        if (point == 0) return setFrontPointColor(color);
        if (point == 1) return setEndPointColor(color);
        if (points.size() == 2) {
            points.add(1, point);
            colors.add(1, color);
            return this;
        }
        if (points.size() > 2) {
            float now;
            for (int index = 1; index < points.size() - 1; index++) {
                now = points.getFloat(index);
                if (now == point) {
                    colors.set(index, color);
                    return this;
                } else if (now > point) {
                    points.add(index, point);
                    colors.add(index, color);
                    return this;
                }
            }
        }
        return this;
    }

    public int getColor(float point) {
        if (point <= 0) return getFrontPointColor();
        if (point >= 1) return getEndPointColor();
        float last = 0;
        float now;
        for (int index = 1; index < points.size(); index++) {
            now = points.getFloat(index);
            if (now > point) {
                int colorLast = colors.getInt(index - 1);
                int colorNext = colors.getInt(index);
                float progress = (point - last) / (now - last);
                float poor = 1 - progress;
                int alpha = (int) (((colorLast >>> 24) & 0xFF) * poor + ((colorNext >>> 24) & 0xFF) * progress);
                int red = (int) (((colorLast >>> 16) & 0xFF) * poor + ((colorNext >>> 16) & 0xFF) * progress);
                int green = (int) (((colorLast >>> 8) & 0xFF) * poor + ((colorNext >>> 8) & 0xFF) * progress);
                int blue = (int) ((colorLast & 0xFF) * poor + (colorNext & 0xFF) * progress);
                return (ignoreAlpha ? 0xFF000000 : alpha << 24) | red << 16 | green << 8 | blue;
            } else if (now == point) {
                return colors.getInt(index) | (ignoreAlpha ? 0xFF000000 : 0);
            } else if (now < point) {
                last = now;
            }
        }
        return getEndPointColor();
    }
}
