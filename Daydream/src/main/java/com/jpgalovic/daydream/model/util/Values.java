package com.jpgalovic.daydream.model.util;

public class Values {
    public static final float ANGLE_THRESHOLD = 0.2f;
    public static final float ANGLE_THRESHOLD_FINE = 0.1f;

    public static final float Z_NEAR = 0.01f;
    public static final float Z_FAR = 20.0f;

    public static final float MAX_YAW = 100.0f;
    public static final float MAX_PITCH = 25.0f;

    public static final float MIN_TARGET_DISTANCE = 6.0f;
    public static final float MAX_TARGET_DISTANCE = 8.0f;

    public static final float ALPHANUMERIC_OFFSET_H = 1.0f;
    public static final float APLHANUMERIC_OFFSET_HC = 0.4f;
    public static final float ALPHANUMERIC_OFFSET_V = 1.4f;
    public static final float ALPHANUMERIC_OFFSET_VC = 0.6f;

    public static final float SCORE_DISPLAY_CENTER_OFFSET = 2.0f;

    public static final float[] POS_MATRIX_MULTIPLY_VEC = {0.0f, 0.0f, 0.0f, 1.0f};
    public static final float[] FORWARD_VEC = {0.0f, 0.0f, -1.0f, 1.f};

    public static final String[] OBJECT_VERTEX_SHADER =
            new String[] {
                "uniform mat4 u_MVP;",
                "attribute vec4 a_Position;",
                "attribute vec2 a_UV;",
                "varying vec2 v_UV;",
                "",
                "void main() {",
                "  v_UV = a_UV;",
                "  gl_Position = u_MVP * a_Position;",
                "}",
            };

    public static final String[] OBJECT_FRAGMENT_SHADER =
            new String[] {
                "precision mediump float;",
                "varying vec2 v_UV;",
                "uniform sampler2D u_Texture;",
                "",
                "void main() {",
                "  // The y coordinate of this sample's textures is reversed compared to",
                "  // what OpenGL expects, so we invert the y coordinate.",
                "  gl_FragColor = texture2D(u_Texture, vec2(v_UV.x, 1.0 - v_UV.y));",
                "}",
            };
}
