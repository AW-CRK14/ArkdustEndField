package com.landis.breakdowncore.helper;

import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.client.renderer.texture.SpriteContents;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

//在NativeImage中的RGBA颜色，所有你能看到的，不管是获取到的还是要设置的
//都是他妈的ABGR
//我很怀疑写这东西的时候的人的精神状态
public class SpriteHelper {
    public static NativeImage firstFrame(SpriteContents contents){
        NativeImage image = new NativeImage(contents.width,contents.height,false);
        boolean animate = contents.animatedTexture != null;
        contents.originalImage.copyRect(image,animate ? contents.animatedTexture.getFrameX(0) : 0,animate ? contents.animatedTexture.getFrameY(0) : 0,0,0,contents.width,contents.height,false,false);
        return image;
    }

    public static List<NativeImage> scaleToSame(List<NativeImage> images, boolean alignX){
        int clm = MathHelper.lcm(images.stream().map(alignX ? NativeImage::getWidth : NativeImage::getHeight).toList());
        List<NativeImage> imgs = new ArrayList<>();
        for(NativeImage image : images){
            int s = clm / (alignX ? image.getWidth() : image.getHeight());
            imgs.add(scale(image,s));
        }
        return imgs;
    }

    public static NativeImage scale(NativeImage image,float xScale,float yScale){
        return scale(image,(int)(image.getWidth() * xScale),(int)(image.getHeight() * yScale));
    }

    public static NativeImage scale(NativeImage image,int width,int height){
        NativeImage i = new NativeImage(width, height,false);
        image.resizeSubRectTo(0,0,image.getWidth(),image.getHeight(),i);
        return i;
    }

    public static NativeImage scale(NativeImage image,float scale){
        if(scale == 1) {
            return copy(image);
        }
        return scale(image, scale, scale);
    }

    public static NativeImage copy(NativeImage image){
        NativeImage i = new NativeImage(image.getWidth(), image.getHeight(), false);
        i.copyFrom(image);
        return i;
    }


    public static NativeImage colorCombineHandle(NativeImage a, NativeImage b, Color.ColorHandle handle){
        NativeImage copied = copy(a);
        for(int x = 0; x < a.getWidth(); x++){
            for(int y = 0; y < a.getHeight(); y++){
                int color = copied.getPixelRGBA(x,y);
                copied.setPixelRGBA(x,y,b.isOutsideBounds(x,y) ? color : handle.handle(color,b.getPixelRGBA(x,y)));
            }
        }
        return copied;
    }

    public static NativeImage alphaFilter(NativeImage original,NativeImage alpha){
        return colorCombineHandle(original,alpha, Color::alphaFilter);
    }

    public static NativeImage blend(NativeImage background,NativeImage foreground){
        return colorCombineHandle(background,foreground, Color::blend);
    }

    public static class Color{
        public static int blend(int backgroundColor, int foregroundColor) {

            // 将Alpha值标准化到[0, 1]范围
            float alphaBackground = getColorByte(backgroundColor,0) / 255.0f;
            float alphaForeground = getColorByte(foregroundColor,0) / 255.0f;

            // 对每个颜色分量执行Alpha混合
            int finalRed = (int)((alphaForeground * getColorByte(foregroundColor,3)) + ((1 - alphaForeground) * alphaBackground * getColorByte(backgroundColor,3)));
            int finalGreen = (int)((alphaForeground * getColorByte(foregroundColor,2)) + ((1 - alphaForeground) * alphaBackground * getColorByte(backgroundColor,2)));
            int finalBlue = (int)((alphaForeground * getColorByte(foregroundColor,1)) + ((1 - alphaForeground) * alphaBackground * getColorByte(backgroundColor,1)));
            int finalAlpha = (int)(255 * (alphaForeground + (1 - alphaForeground) * alphaBackground));

            // 将混合后的分量重新组合成一个整数
            return (finalAlpha << 24) | (finalBlue << 16) | (finalGreen << 8) | finalRed;
        }

        public static int alphaFilter(int original,int alpha){
            alpha = Math.min((getColorByte(alpha,3) + getColorByte(alpha, 2) + getColorByte(alpha, 1)) / 3,getColorByte(alpha,0));
            return original & 0x00FFFFFF | alpha << 24;
        }

        //A=0 B=1 G=2 R=3
        public static int getColorByte(int color,int index){
            if(index < 0 || index > 3){
                throw new IllegalArgumentException("index must in range[0,3].");
            }
            return color >>> 24 - 8 * index & 0xFF;
        }

        public interface ColorHandle{
            int handle(int a,int b);
        }
    }


}
