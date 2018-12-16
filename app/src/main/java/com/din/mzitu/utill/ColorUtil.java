package com.din.mzitu.utill;

import android.graphics.Color;

/**
 * @author: dinzhenyan
 * @time: 2018/12/16 上午10:19
 */
public class ColorUtil {

    public static int rgb;

    public static int changeColor(int color) {
        int red = color >> 16 & 0xFF;
        int green = color >> 8 & 0xFF;
        int blue = color & 0xFF;
        red = (int) Math.floor(red * (1 - 0.2));
        green = (int) Math.floor(green * (1 - 0.2));
        blue = (int) Math.floor(blue * (1 - 0.2));
        return Color.rgb(red, green, blue);
    }
}
