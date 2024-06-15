package com.landis.arkdust.helper;

import com.landis.arkdust.mui.widget.button.CloseButton;
import icyllis.modernui.core.Context;
import icyllis.modernui.fragment.Fragment;
import icyllis.modernui.graphics.Canvas;
import icyllis.modernui.graphics.Image;
import icyllis.modernui.graphics.Rect;
import icyllis.modernui.graphics.drawable.ShapeDrawable;
import icyllis.modernui.text.PrecomputedText;
import icyllis.modernui.text.Spannable;
import icyllis.modernui.text.SpannableString;
import icyllis.modernui.text.Spanned;
import icyllis.modernui.text.style.BackgroundColorSpan;
import icyllis.modernui.text.style.ForegroundColorSpan;
import icyllis.modernui.view.Gravity;
import icyllis.modernui.view.ViewGroup;
import icyllis.modernui.widget.ImageView;
import icyllis.modernui.widget.RelativeLayout;
import icyllis.modernui.widget.TextView;
import icyllis.modernui.graphics.Paint;
import it.unimi.dsi.fastutil.Pair;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nullable;
import java.awt.*;
import java.util.List;
import java.util.function.Supplier;


public class MUIHelper {
    @Deprecated
    public static Pair<TextView, ViewGroup.LayoutParams> drawStringAt(Context context, ViewGroup group, String text, int color, int size, int x, int y) {
        TextView title = new TextView(context);
        title.setText(text);
        title.setTextColor(color);
        title.setTextSize(size);
        title.setGravity(Gravity.TOP | Gravity.LEFT);

        RelativeLayout.LayoutParams titlePara = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        titlePara.setMargins(group.dp(x), group.dp(y), 0, 0);
        group.addView(title, titlePara);

        return Pair.of(title, titlePara);
    }

    @Deprecated
    public static Pair<ImageView, ViewGroup.LayoutParams> drawImage(Context context, ViewGroup group, ResourceLocation resource, int x, int y, int width, int height) {
        ImageView imageView = new ImageView(context);
        Image image = Image.create(resource.getNamespace(), resource.getPath());

        imageView.setImage(image);

        RelativeLayout.LayoutParams titlePara = new RelativeLayout.LayoutParams(width, height);
        titlePara.topMargin = group.dp(y);
        titlePara.leftMargin = group.dp(x);
        titlePara.width = group.dp(width);
        titlePara.height = group.dp(height);

        group.addView(imageView, titlePara);
        return Pair.of(imageView, titlePara);
    }

    public static ShapeDrawable withBorder() {
        ShapeDrawable shape = new ShapeDrawable();
        shape.setShape(ShapeDrawable.RECTANGLE);//形状为矩形
        shape.setCornerRadius(10);//圆角半径
        shape.setStroke(2, icyllis.arc3d.core.Color.BLUE);//设置边框
        return shape;
    }

    public static void repeatedGridImage(Canvas canvas, Rect rect, Image image, boolean useImageSize, boolean horizontalLock, float zoom, Paint paint) {
        int x = rect.x();
        int y = rect.y();
        int width, height;
        if (useImageSize) {
            width = (int) (image.getWidth() * zoom);
            height = (int) (image.getHeight() * zoom);

        } else if (horizontalLock) {
            width = (int) (rect.width() * zoom);
            height = (int) ((float) image.getHeight() / image.getWidth() * width);
        } else {
            height = (int) (rect.height() * zoom);
            width = (int) ((float) image.getWidth() / image.getHeight() * height);
        }

        int xc = rect.width() / width + (rect.width() % width == 0 ? 0 : 1);
        int yc = rect.height() / height + (rect.height() % height == 0 ? 0 : 1);

        canvas.save();
        canvas.clipRect(rect);
        for (int xi = 0; xi < xc; xi++) {
            for (int yi = 0; yi < yc; yi++) {
                canvas.drawImage(image, null, new Rect(x + xi * width, y + yi * height, x + (xi + 1) * width, y + (yi + 1) * height), paint);
            }
        }
        canvas.restore();
    }

    public static Spannable digitComplement(int length, String source, char defaultChar, List<Object> complementSpan, List<Object> subjectSpan, @Nullable List<Object> reprocessSpan) {
        if (length < 0) return new SpannableString("");

        int markPoint = Math.max(0, length - source.length());
        Spannable spannable = new SpannableString(source.length() >= length ? source : String.valueOf(defaultChar).repeat(markPoint) + source);
        if (markPoint > 0) {
            complementSpan.forEach(i -> spannable.setSpan(i, 0, markPoint, Spanned.SPAN_INCLUSIVE_EXCLUSIVE));
        }
        if (!source.isEmpty()) {
            subjectSpan.forEach(i -> spannable.setSpan(i, markPoint, length, Spanned.SPAN_INCLUSIVE_EXCLUSIVE));
        }
        if (reprocessSpan != null && !reprocessSpan.isEmpty()) {
            reprocessSpan.forEach(i -> spannable.setSpan(i, 0, length, Spanned.SPAN_INCLUSIVE_EXCLUSIVE));
        }
        return spannable;
    }

    public static Spannable digitComplement(int length, String source, char defaultChar, Object complementSpan, Object subjectSpan) {
        return digitComplement(length, source, defaultChar, List.of(complementSpan), List.of(subjectSpan), null);
    }

    public static Spannable digitComplement(int length, String source, char defaultChar, Object complementSpan, Object subjectSpan, Object reprocessSpan) {
        return digitComplement(length, source, defaultChar, List.of(complementSpan), List.of(subjectSpan), List.of(reprocessSpan));
    }

    public static void digitComplementAndSet(TextView textView, int length, String source, char defaultChar, Object complementSpan, Object subjectSpan, Object reprocessSpan) {
        textView.post(() -> textView.setText(PrecomputedText.create(MUIHelper.digitComplement(length, source, defaultChar, complementSpan, subjectSpan, reprocessSpan), textView.getTextMetricsParams()), TextView.BufferType.SPANNABLE));
    }

    public static void digitComplementAndSet(TextView textView, int length, String source, char defaultChar, Object complementSpan, Object subjectSpan) {
        textView.post(() -> textView.setText(PrecomputedText.create(MUIHelper.digitComplement(length, source, defaultChar, complementSpan, subjectSpan), textView.getTextMetricsParams()), TextView.BufferType.SPANNABLE));
    }

    public static Spannable digitComplement(int length, String source, char defaultChar, int complementColor, int subjectColor) {
        return digitComplement(length, source, defaultChar, new ForegroundColorSpan(complementColor), new ForegroundColorSpan(subjectColor));
    }


    public static class Widgets {
        public static Pair<CloseButton, ViewGroup.LayoutParams> addCloseButton(Context context, ViewGroup group, Fragment fragment) {
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(group.dp(20), group.dp(20));
            params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            params.addRule(RelativeLayout.ALIGN_PARENT_TOP);
            params.setMarginsRelative(0, group.dp(20), group.dp(20), 0);
//            params.height = 16;
//            params.width = 16;

            CloseButton button = new CloseButton(context, group, fragment);
//            button.setText("this is a test close button");

            group.addView(button, params);
            return Pair.of(button, params);
        }
    }


    public record TextSpan(int from, int length, Color color) {
        public void setSpan(SpannableString s) {
            s.setSpan(new ForegroundColorSpan(color.getRGB()), from, from + length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
    }
}
