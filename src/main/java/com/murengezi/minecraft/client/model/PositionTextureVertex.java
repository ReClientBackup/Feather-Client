package com.murengezi.minecraft.client.model;

import net.minecraft.util.Vec3;

public class PositionTextureVertex {

    public Vec3 vector3D;
    public float texturePositionX, texturePositionY;

    public PositionTextureVertex(float p_i1158_1_, float p_i1158_2_, float p_i1158_3_, float p_i1158_4_, float p_i1158_5_) {
        this(new Vec3(p_i1158_1_, p_i1158_2_, p_i1158_3_), p_i1158_4_, p_i1158_5_);
    }

    public PositionTextureVertex setTexturePosition(float p_78240_1_, float p_78240_2_) {
        return new PositionTextureVertex(this, p_78240_1_, p_78240_2_);
    }

    public PositionTextureVertex(PositionTextureVertex textureVertex, float texturePositionX, float texturePositionY) {
        this.vector3D = textureVertex.vector3D;
        this.texturePositionX = texturePositionX;
        this.texturePositionY = texturePositionY;
    }

    public PositionTextureVertex(Vec3 vector3DIn, float texturePositionX, float texturePositionY) {
        this.vector3D = vector3DIn;
        this.texturePositionX = texturePositionX;
        this.texturePositionY = texturePositionY;
    }

}
