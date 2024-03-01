package com.arkdust.helper;

import com.arkdust.gui.widget.button.CloseButton;
import icyllis.modernui.core.Context;
import icyllis.modernui.fragment.Fragment;
import icyllis.modernui.graphics.Image;
import icyllis.modernui.text.SpannableString;
import icyllis.modernui.text.Spanned;
import icyllis.modernui.text.style.ForegroundColorSpan;
import icyllis.modernui.view.Gravity;
import icyllis.modernui.view.ViewGroup;
import icyllis.modernui.widget.ImageView;
import icyllis.modernui.widget.RelativeLayout;
import icyllis.modernui.widget.TextView;
import it.unimi.dsi.fastutil.Pair;
import net.minecraft.resources.ResourceLocation;

import java.awt.*;

public class MUIHelper {
    public static Pair<TextView, ViewGroup.LayoutParams> drawStringAt(Context context, ViewGroup group, String text, int color, int size, int x,int y){
        TextView title = new TextView(context);
        title.setText(text);
        title.setTextColor(color);
        title.setTextSize(size);
        title.setGravity(Gravity.TOP | Gravity.LEFT);

        RelativeLayout.LayoutParams titlePara = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        titlePara.setMargins(group.dp(x),group.dp(y),0,0);
        group.addView(title,titlePara);

        return Pair.of(title,titlePara);
    }

    /**@param left 组件的左侧位置相对于父组件左边框的位置
     * @param top 组件的顶部位置相对于父组件的顶部位置
     * */
    public static Pair<TextView, RelativeLayout.LayoutParams> drawStringAt(Context context, ViewGroup group, String text, int color, int size, int gravity, int left, int top, int right, int bottom){
        TextView title = new TextView(context);
        title.setText(text);
        title.setTextColor(color);
        title.setTextSize(size);
        title.setGravity(gravity);

        RelativeLayout.LayoutParams titlePara = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        titlePara.setMargins(group.dp(left),group.dp(top),group.dp(right),group.dp(bottom));

        group.addView(title,titlePara);

        return Pair.of(title,titlePara);
    }

    public static Pair<ImageView, ViewGroup.LayoutParams> drawImage(Context context, ViewGroup group, ResourceLocation resource, int x,int y,int width, int height){
        ImageView imageView = new ImageView(context);
        Image image = Image.create(resource.getNamespace(),resource.getPath());

        imageView.setImage(image);

        RelativeLayout.LayoutParams titlePara = new RelativeLayout.LayoutParams(width,height);
        titlePara.topMargin = group.dp(y);
        titlePara.leftMargin = group.dp(x);
        titlePara.width = group.dp(width);
        titlePara.height = group.dp(height);

        group.addView(imageView,titlePara);
        return Pair.of(imageView,titlePara);
    }



    public static class Widgets {
        public static Pair<CloseButton, ViewGroup.LayoutParams> addCloseButton(Context context, ViewGroup group, Fragment fragment){
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(group.dp(20),group.dp(20));
            params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            params.addRule(RelativeLayout.ALIGN_PARENT_TOP);
            params.setMarginsRelative(0,group.dp(20),group.dp(20),0);
//            params.height = 16;
//            params.width = 16;

            CloseButton button = new CloseButton(context,group,fragment);
//            button.setText("this is a test close button");

            group.addView(button,params);
            return Pair.of(button,params);
        }
    }


    public record TextSpan(int from, int length, Color color){
        public void setSpan(SpannableString s){
            s.setSpan(new ForegroundColorSpan(color.getRGB()),from,from + length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
    }
}
