package com.crazydev.graphbuilder.io;

import android.content.Context;

import com.crazydev.graphbuilder.rendering.Texture;
import com.crazydev.graphbuilder.rendering.TextureRegion;

public class Assets {

    public static Texture digits;
    public static TextureRegion digitsRegion_m_b;
    public static TextureRegion digitsRegion_0_b;
    public static TextureRegion digitsRegion_1_b;
    public static TextureRegion digitsRegion_2_b;
    public static TextureRegion digitsRegion_3_b;
    public static TextureRegion digitsRegion_4_b;
    public static TextureRegion digitsRegion_5_b;
    public static TextureRegion digitsRegion_6_b;
    public static TextureRegion digitsRegion_7_b;
    public static TextureRegion digitsRegion_8_b;
    public static TextureRegion digitsRegion_9_b;
    public static TextureRegion digitsRegion_p_b;

    public static void load(Context context) {
        IOManager ioManager = new IOManager(context);

        digits = new Texture(ioManager, "digits.png");
        digitsRegion_m_b = new TextureRegion(digits, 503, 105, 92, 14);
        digitsRegion_0_b = new TextureRegion(digits, 8  , 0, 105, 169);
        digitsRegion_1_b = new TextureRegion(digits, 130, 0, 105, 169);
        digitsRegion_2_b = new TextureRegion(digits, 248, 0, 105, 169);
        digitsRegion_3_b = new TextureRegion(digits, 367, 0, 105, 169);
        digitsRegion_4_b = new TextureRegion(digits, 485, 0, 113, 169);
        digitsRegion_5_b = new TextureRegion(digits, 608, 0, 105, 169);
        digitsRegion_6_b = new TextureRegion(digits, 732, 0, 105, 169);
        digitsRegion_7_b = new TextureRegion(digits, 852, 0, 105, 169);
        digitsRegion_8_b = new TextureRegion(digits, 972, 0, 105, 169);
        digitsRegion_9_b = new TextureRegion(digits, 1091, 0, 105, 169);
        digitsRegion_p_b = new TextureRegion(digits, 1204, 0, 125, 163);
    }
}
