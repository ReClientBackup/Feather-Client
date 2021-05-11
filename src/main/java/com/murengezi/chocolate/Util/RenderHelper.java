package com.murengezi.chocolate.Util;

import com.murengezi.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import com.murengezi.minecraft.client.gui.ScaledResolution;
import com.murengezi.minecraft.client.renderer.OpenGlHelper;
import com.murengezi.minecraft.client.renderer.Tessellator;
import com.murengezi.minecraft.client.renderer.WorldRenderer;
import com.murengezi.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.MathHelper;
import org.lwjgl.opengl.GL11;

import java.nio.FloatBuffer;

import static org.lwjgl.opengl.GL11.*;

/**
 * @author Tobias Sjöblom
 * Created on 2021-01-12 at 14:15
 */
public class RenderHelper {
    private static Tessellator t;

    private static WorldRenderer wr;

    private static final float DEG2RAD = 0.017453277F;

    private static int r, g, b, a;

    private static float lineWidth;

    public static void start() {
        t = Tessellator.getInstance();
        wr = t.getWorldRenderer();
    }

    public static void color(int red, int green, int blue, int alpha) {
        r = MathHelper.clamp_int(red, 0, 255);
        g = MathHelper.clamp_int(green, 0, 255);
        b = MathHelper.clamp_int(blue, 0, 255);
        a = alpha;
    }

    public static void points() {
        wr.begin(0, DefaultVertexFormats.POSITION_COLOR);
    }

    public static void lines() {
        wr.begin(1, DefaultVertexFormats.POSITION_COLOR);
    }

    public static void quads() {
        wr.begin(7, DefaultVertexFormats.POSITION_COLOR);
    }

    public static void poly() {
        wr.begin(9, DefaultVertexFormats.POSITION_COLOR);
    }

    public static void circle(boolean filled) {
        wr.begin(filled ? 6 : 2, DefaultVertexFormats.POSITION_COLOR);
    }

    public static void drawMode(int drawMode) {
        wr.begin(drawMode, DefaultVertexFormats.POSITION_COLOR);
    }

    public static void drawPoint(int x, int y) {
        wr.pos(x, y, 0.0D).color(r, g, b, a).endVertex();
    }

    public static void drawPoint(double x, double y) {
        wr.pos(x, y, 0.0D).color(r, g, b, a).endVertex();
    }

    public static void drawLine(int x1, int y1, int x2, int y2) {
        wr.pos(x1, y1, 0.0D).color(r, g, b, a).endVertex();
        wr.pos(x2, y2, 0.0D).color(r, g, b, a).endVertex();
    }

    public static void drawLine(double x1, double y1, double x2, double y2) {
        wr.pos(x1, y1, 0.0D).color(r, g, b, a).endVertex();
        wr.pos(x2, y2, 0.0D).color(r, g, b, a).endVertex();
    }

    public static void drawQuad(int topLeftX, int topLeftY, int width, int height) {
        wr.pos(topLeftX, topLeftY, 0.0D).color(r, g, b, a).endVertex();
        wr.pos(topLeftX, (topLeftY + height), 0.0D).color(r, g, b, a).endVertex();
        wr.pos((topLeftX + width), (topLeftY + height), 0.0D).color(r, g, b, a).endVertex();
        wr.pos((topLeftX + width), topLeftY, 0.0D).color(r, g, b, a).endVertex();
    }

    public static void drawQuad(double topLeftX, double topLeftY, double width, double height) {
        wr.pos(topLeftX, topLeftY, 0.0D).color(r, g, b, a).endVertex();
        wr.pos(topLeftX, topLeftY + height, 0.0D).color(r, g, b, a).endVertex();
        wr.pos(topLeftX + width, topLeftY + height, 0.0D).color(r, g, b, a).endVertex();
        wr.pos(topLeftX + width, topLeftY, 0.0D).color(r, g, b, a).endVertex();
    }

    public static void drawPolygon(int... points) {
        for (int i = 0; i < points.length; i += 2)
            wr.pos(points[i], points[i + 1], 0.0D).color(r, g, b, a).endVertex();
    }

    public static void drawPolygon(double... points) {
        for (int i = 0; i < points.length / 2; i += 2)
            wr.pos(points[i], points[i + 1], 0.0D).color(r, g, b, a).endVertex();
    }

    public static void drawCircle(float x, float y, float radius, boolean filled) {
        if (filled) {
            float angle;
            for (angle = 1.0F; angle < 361.0F; angle = (float)(angle + 0.2D)) {
                float x2 = x + MathHelper.sin(angle) * radius;
                float y2 = y + MathHelper.cos(angle) * radius;
                wr.pos(x2, y2, 0.0D).color(r, g, b, a).endVertex();
            }
        } else {
            for (int i = 0; i < 360; i++) {
                float degInRad = i * DEG2RAD;
                wr.pos((x + MathHelper.cos(degInRad) * radius), (y + MathHelper.sin(degInRad) * radius), 0.0D).color(r, g, b, a).endVertex();
            }
        }
    }

    public static void finish() {
        t.draw();
    }

    public static ScaledResolution getScaledResolution() {
        return new ScaledResolution();
    }

    public static FontRenderer getFontRenderer() {
        return (Minecraft.getMinecraft()).fontRenderer;
    }

    public static void prepare() {
        disableTexture();
        enableBlend();
        OpenGlHelper.glBlendFunc(770, 771, 1, 0);
        disableLighting();
        saveLineWidth();
    }

    public static void restore() {
        resetLineWidth();
        disableBlend();
        enableTexture();
    }

    public static void enableBlend() {
        GL11.glEnable(GL_BLEND);
    }

    public static void disableBlend() {
        GL11.glDisable(GL_BLEND);
    }

    public static void enableLighting() {
        GL11.glEnable(GL_LIGHTING);
    }

    public static void disableLighting() {
        GL11.glDisable(GL_LIGHTING);
    }

    public static void enableTexture() {
        GL11.glEnable(GL_TEXTURE_2D);
    }

    public static void disableTexture() {
        GL11.glDisable(GL_TEXTURE_2D);
    }

    public static void loadIdentity() {
        GL11.glLoadIdentity();
    }

    public static void pushMatrix() {
        GL11.glPushMatrix();
    }

    public static void popMatrix() {
        GL11.glPopMatrix();
    }

    public static void getFloat(int pname, FloatBuffer params) {
        GL11.glGetFloat(pname, params);
    }

    public static void ortho(double left, double right, double bottom, double top, double zNear, double zFar) {
        GL11.glOrtho(left, right, bottom, top, zNear, zFar);
    }

    public static void rotate(float angle, float x, float y, float z) {
        GL11.glRotatef(angle, x, y, z);
    }

    public static void scale(float x, float y, float z) {
        GL11.glScalef(x, y, z);
    }

    public static void scale(double x, double y, double z) {
        GL11.glScaled(x, y, z);
    }

    public static void translate(float x, float y, float z) {
        GL11.glTranslatef(x, y, z);
    }

    public static void translate(double x, double y, double z) {
        GL11.glTranslated(x, y, z);
    }

    public static void saveLineWidth() {
        lineWidth = GL11.glGetFloat(2849);
    }

    public static void lineWidth(float width) {
        GL11.glLineWidth(width);
    }

    public static void resetLineWidth() {
        lineWidth(lineWidth);
    }
}
