package com.murengezi.minecraft.client.util;

import com.google.gson.JsonObject;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.JsonUtils;
import org.lwjgl.opengl.GL14;

public class JSONBlendingMode {

    private static JSONBlendingMode lastApplied = null;
    private final int srcColorFactor, srcAlphaFactor, destColorFactor, destAlphaFactor, blendFunction;
    private final boolean separateBlend, opaque;

    private JSONBlendingMode(boolean separateBlend, boolean opaque, int srcColorFactor, int destColorFactor, int srcAlphaFactor, int destAlphaFactor, int blendFunction) {
        this.separateBlend = separateBlend;
        this.srcColorFactor = srcColorFactor;
        this.destColorFactor = destColorFactor;
        this.srcAlphaFactor = srcAlphaFactor;
        this.destAlphaFactor = destAlphaFactor;
        this.opaque = opaque;
        this.blendFunction = blendFunction;
    }

    public JSONBlendingMode() {
        this(false, true, 1, 0, 1, 0, 32774);
    }

    public JSONBlendingMode(int srcFactor, int destFactor, int blendFunction) {
        this(false, false, srcFactor, destFactor, srcFactor, destFactor, blendFunction);
    }

    public JSONBlendingMode(int srcColorFactor, int destColorFactor, int srcAlphaFactor, int destAlphaFactor, int blendFunction) {
        this(true, false, srcColorFactor, destColorFactor, srcAlphaFactor, destAlphaFactor, blendFunction);
    }

    public void apply() {
        if (!this.equals(lastApplied)) {
            if (lastApplied == null || this.opaque != lastApplied.isOpaque()) {
                lastApplied = this;

                if (this.opaque) {
                    GlStateManager.disableBlend();
                    return;
                }

                GlStateManager.enableBlend();
            }

            GL14.glBlendEquation(this.blendFunction);

            if (this.separateBlend) {
                GlStateManager.tryBlendFuncSeparate(this.srcColorFactor, this.destColorFactor, this.srcAlphaFactor, this.destAlphaFactor);
            } else {
                GlStateManager.blendFunc(this.srcColorFactor, this.destColorFactor);
            }
        }
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        } else if (!(obj instanceof JSONBlendingMode)) {
            return false;
        } else {
            JSONBlendingMode blendingMode = (JSONBlendingMode)obj;
            return this.blendFunction == blendingMode.blendFunction && (this.destAlphaFactor == blendingMode.destAlphaFactor && (this.destColorFactor == blendingMode.destColorFactor && (this.opaque == blendingMode.opaque && (this.separateBlend == blendingMode.separateBlend && (this.srcAlphaFactor == blendingMode.srcAlphaFactor && this.srcColorFactor == blendingMode.srcColorFactor)))));
        }
    }

    public int hashCode() {
        int i = this.srcColorFactor;
        i = 31 * i + this.srcAlphaFactor;
        i = 31 * i + this.destColorFactor;
        i = 31 * i + this.destAlphaFactor;
        i = 31 * i + this.blendFunction;
        i = 31 * i + (this.separateBlend ? 1 : 0);
        i = 31 * i + (this.opaque ? 1 : 0);
        return i;
    }

    public boolean isOpaque() {
        return this.opaque;
    }

    public static JSONBlendingMode getBlendingMode(JsonObject jsonObject) {
        if (jsonObject == null) {
            return new JSONBlendingMode();
        } else {
            int i = 32774;
            int j = 1;
            int k = 0;
            int l = 1;
            int i1 = 0;
            boolean flag = true;
            boolean flag1 = false;

            if (JsonUtils.isString(jsonObject, "func")) {
                i = stringToBlendFunction(jsonObject.get("func").getAsString());

                if (i != 32774) {
                    flag = false;
                }
            }

            if (JsonUtils.isString(jsonObject, "srcrgb")) {
                j = stringToBlendFactor(jsonObject.get("srcrgb").getAsString());

                if (j != 1) {
                    flag = false;
                }
            }

            if (JsonUtils.isString(jsonObject, "dstrgb")) {
                k = stringToBlendFactor(jsonObject.get("dstrgb").getAsString());

                if (k != 0) {
                    flag = false;
                }
            }

            if (JsonUtils.isString(jsonObject, "srcalpha")) {
                l = stringToBlendFactor(jsonObject.get("srcalpha").getAsString());

                if (l != 1) {
                    flag = false;
                }

                flag1 = true;
            }

            if (JsonUtils.isString(jsonObject, "dstalpha")) {
                i1 = stringToBlendFactor(jsonObject.get("dstalpha").getAsString());

                if (i1 != 0) {
                    flag = false;
                }

                flag1 = true;
            }

            return flag ? new JSONBlendingMode() : (flag1 ? new JSONBlendingMode(j, k, l, i1, i) : new JSONBlendingMode(j, k, i));
        }
    }

    private static int stringToBlendFunction(String funcName) {
        String s = funcName.trim().toLowerCase();
        return s.equals("add") ? 32774 : (s.equals("subtract") ? 32778 : (s.equals("reversesubtract") ? 32779 : (s.equals("reverse_subtract") ? 32779 : (s.equals("min") ? 32775 : (s.equals("max") ? 32776 : 32774)))));
    }

    private static int stringToBlendFactor(String factorName) {
        String s = factorName.trim().toLowerCase();
        s = s.replaceAll("_", "");
        s = s.replaceAll("one", "1");
        s = s.replaceAll("zero", "0");
        s = s.replaceAll("minus", "-");
        return s.equals("0") ? 0 : (s.equals("1") ? 1 : (s.equals("srccolor") ? 768 : (s.equals("1-srccolor") ? 769 : (s.equals("dstcolor") ? 774 : (s.equals("1-dstcolor") ? 775 : (s.equals("srcalpha") ? 770 : (s.equals("1-srcalpha") ? 771 : (s.equals("dstalpha") ? 772 : (s.equals("1-dstalpha") ? 773 : -1)))))))));
    }
}
