package com.murengezi.minecraft.client.renderer.culling;

import net.minecraft.util.AxisAlignedBB;

public class Frustum implements ICamera {

    private final ClippingHelper clippingHelper;
    private double xPosition, yPosition, zPosition;

    public Frustum(ClippingHelper clippingHelper) {
        this.clippingHelper = clippingHelper;
    }

    public void setPosition(double x, double y, double z) {
        this.xPosition = x;
        this.yPosition = y;
        this.zPosition = z;
    }

    public boolean isBoxInFrustum(double p_78548_1_, double p_78548_3_, double p_78548_5_, double p_78548_7_, double p_78548_9_, double p_78548_11_) {
        return this.clippingHelper.isBoxInFrustum(p_78548_1_ - this.xPosition, p_78548_3_ - this.yPosition, p_78548_5_ - this.zPosition, p_78548_7_ - this.xPosition, p_78548_9_ - this.yPosition, p_78548_11_ - this.zPosition);
    }

    public boolean isBoundingBoxInFrustum(AxisAlignedBB axisAlignedBB) {
        return this.isBoxInFrustum(axisAlignedBB.minX, axisAlignedBB.minY, axisAlignedBB.minZ, axisAlignedBB.maxX, axisAlignedBB.maxY, axisAlignedBB.maxZ);
    }

    public boolean isBoxInFrustumFully(double p_isBoxInFrustumFully_1_, double p_isBoxInFrustumFully_3_, double p_isBoxInFrustumFully_5_, double p_isBoxInFrustumFully_7_, double p_isBoxInFrustumFully_9_, double p_isBoxInFrustumFully_11_) {
        return this.clippingHelper.isBoxInFrustumFully(p_isBoxInFrustumFully_1_ - this.xPosition, p_isBoxInFrustumFully_3_ - this.yPosition, p_isBoxInFrustumFully_5_ - this.zPosition, p_isBoxInFrustumFully_7_ - this.xPosition, p_isBoxInFrustumFully_9_ - this.yPosition, p_isBoxInFrustumFully_11_ - this.zPosition);
    }

}
