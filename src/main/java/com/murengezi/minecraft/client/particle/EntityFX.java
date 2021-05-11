package com.murengezi.minecraft.client.particle;

import com.murengezi.minecraft.client.Minecraft;
import com.murengezi.minecraft.client.renderer.WorldRenderer;
import com.murengezi.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class EntityFX extends Entity {

    protected int particleTextureIndexX, particleTextureIndexY, particleAge, particleMaxAge;
    protected float particleTextureJitterX, particleTextureJitterY, particleScale, particleGravity, particleRed, particleGreen, particleBlue, particleAlpha;
    protected TextureAtlasSprite particleIcon;
    public static double interpPosX, interpPosY, interpPosZ;

    protected EntityFX(World world, double posX, double posY, double posZ) {
        super(world);
        this.particleAlpha = 1.0F;
        this.setSize(0.2F, 0.2F);
        this.setPosition(posX, posY, posZ);
        this.lastTickPosX = this.prevPosX = posX;
        this.lastTickPosY = this.prevPosY = posY;
        this.lastTickPosZ = this.prevPosZ = posZ;
        this.particleRed = this.particleGreen = this.particleBlue = 1.0F;
        this.particleTextureJitterX = this.rand.nextFloat() * 3.0F;
        this.particleTextureJitterY = this.rand.nextFloat() * 3.0F;
        this.particleScale = (this.rand.nextFloat() * 0.5F + 0.5F) * 2.0F;
        this.particleMaxAge = (int)(4.0F / (this.rand.nextFloat() * 0.9F + 0.1F));
        this.particleAge = 0;
    }

    public EntityFX(World world, double xCoord, double yCoord, double zCoord, double xSpeed, double ySpeed, double zSpeed) {
        this(world, xCoord, yCoord, zCoord);
        this.motionX = xSpeed + (Math.random() * 2.0D - 1.0D) * 0.4000000059604645D;
        this.motionY = ySpeed + (Math.random() * 2.0D - 1.0D) * 0.4000000059604645D;
        this.motionZ = zSpeed + (Math.random() * 2.0D - 1.0D) * 0.4000000059604645D;
        float f = (float)(Math.random() + Math.random() + 1.0D) * 0.15F;
        float f1 = MathHelper.sqrt_double(this.motionX * this.motionX + this.motionY * this.motionY + this.motionZ * this.motionZ);
        this.motionX = this.motionX / (double)f1 * (double)f * 0.4000000059604645D;
        this.motionY = this.motionY / (double)f1 * (double)f * 0.4000000059604645D + 0.10000000149011612D;
        this.motionZ = this.motionZ / (double)f1 * (double)f * 0.4000000059604645D;
    }

    public EntityFX multiplyVelocity(float multiplier) {
        this.motionX *= multiplier;
        this.motionY = (this.motionY - 0.10000000149011612D) * (double)multiplier + 0.10000000149011612D;
        this.motionZ *= multiplier;
        return this;
    }

    public EntityFX multipleParticleScaleBy(float p_70541_1_) {
        this.setSize(0.2F * p_70541_1_, 0.2F * p_70541_1_);
        this.particleScale *= p_70541_1_;
        return this;
    }

    public void setRBGColorF(float particleRed, float particleGreen, float particleBlue) {
        this.particleRed = particleRed;
        this.particleGreen = particleGreen;
        this.particleBlue = particleBlue;
    }

    public void setAlphaF(float alpha) {
        if (this.particleAlpha == 1.0F && alpha < 1.0F) {
            Minecraft.getMinecraft().effectRenderer.moveToAlphaLayer(this);
        } else if (this.particleAlpha < 1.0F && alpha == 1.0F) {
            Minecraft.getMinecraft().effectRenderer.moveToNoAlphaLayer(this);
        }

        this.particleAlpha = alpha;
    }

    public float getRedColorF() {
        return this.particleRed;
    }

    public float getGreenColorF() {
        return this.particleGreen;
    }

    public float getBlueColorF() {
        return this.particleBlue;
    }

    public float getAlpha() {
        return this.particleAlpha;
    }

    protected boolean canTriggerWalking() {
        return false;
    }

    protected void entityInit() {}

    public void onUpdate() {
        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;

        if (this.particleAge++ >= this.particleMaxAge) {
            this.setDead();
        }

        this.motionY -= 0.04D * (double)this.particleGravity;
        this.moveEntity(this.motionX, this.motionY, this.motionZ);
        this.motionX *= 0.9800000190734863D;
        this.motionY *= 0.9800000190734863D;
        this.motionZ *= 0.9800000190734863D;

        if (this.onGround) {
            this.motionX *= 0.699999988079071D;
            this.motionZ *= 0.699999988079071D;
        }
    }

    public void renderParticle(WorldRenderer worldRenderer, Entity entityIn, float partialTicks, float p_180434_4_, float p_180434_5_, float p_180434_6_, float p_180434_7_, float p_180434_8_) {
        float f = (float)this.particleTextureIndexX / 16.0F;
        float f1 = f + 0.0624375F;
        float f2 = (float)this.particleTextureIndexY / 16.0F;
        float f3 = f2 + 0.0624375F;
        float f4 = 0.1F * this.particleScale;

        if (this.particleIcon != null) {
            f = this.particleIcon.getMinU();
            f1 = this.particleIcon.getMaxU();
            f2 = this.particleIcon.getMinV();
            f3 = this.particleIcon.getMaxV();
        }

        float f5 = (float)(this.prevPosX + (this.posX - this.prevPosX) * (double)partialTicks - interpPosX);
        float f6 = (float)(this.prevPosY + (this.posY - this.prevPosY) * (double)partialTicks - interpPosY);
        float f7 = (float)(this.prevPosZ + (this.posZ - this.prevPosZ) * (double)partialTicks - interpPosZ);
        int i = this.getBrightnessForRender(partialTicks);
        int j = i >> 16 & 65535;
        int k = i & 65535;
        worldRenderer.pos(f5 - p_180434_4_ * f4 - p_180434_7_ * f4, f6 - p_180434_5_ * f4, f7 - p_180434_6_ * f4 - p_180434_8_ * f4).tex(f1, f3).color(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha).lightmap(j, k).endVertex();
        worldRenderer.pos(f5 - p_180434_4_ * f4 + p_180434_7_ * f4, f6 + p_180434_5_ * f4, f7 - p_180434_6_ * f4 + p_180434_8_ * f4).tex(f1, f2).color(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha).lightmap(j, k).endVertex();
        worldRenderer.pos(f5 + p_180434_4_ * f4 + p_180434_7_ * f4, f6 + p_180434_5_ * f4, f7 + p_180434_6_ * f4 + p_180434_8_ * f4).tex(f, f2).color(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha).lightmap(j, k).endVertex();
        worldRenderer.pos(f5 + p_180434_4_ * f4 - p_180434_7_ * f4, f6 - p_180434_5_ * f4, f7 + p_180434_6_ * f4 - p_180434_8_ * f4).tex(f, f3).color(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha).lightmap(j, k).endVertex();
    }

    public int getFXLayer() {
        return 0;
    }

    public void writeEntityToNBT(NBTTagCompound tagCompound) {}

    public void readEntityFromNBT(NBTTagCompound tagCompund) {}

    public void setParticleIcon(TextureAtlasSprite icon) {
        int i = this.getFXLayer();

        if (i == 1) {
            this.particleIcon = icon;
        } else {
            throw new RuntimeException("Invalid call to Particle.setTex, use coordinate methods");
        }
    }
    public void setParticleTextureIndex(int particleTextureIndex) {
        if (this.getFXLayer() != 0) {
            throw new RuntimeException("Invalid call to Particle.setMiscTex");
        } else {
            this.particleTextureIndexX = particleTextureIndex % 16;
            this.particleTextureIndexY = particleTextureIndex / 16;
        }
    }

    public void nextTextureIndexX() {
        ++this.particleTextureIndexX;
    }

    public boolean canAttackWithItem() {
        return false;
    }

    public String toString() {
        return this.getClass().getSimpleName() + ", Pos (" + this.posX + "," + this.posY + "," + this.posZ + "), RGBA (" + this.particleRed + "," + this.particleGreen + "," + this.particleBlue + "," + this.particleAlpha + "), Age " + this.particleAge;
    }

}
