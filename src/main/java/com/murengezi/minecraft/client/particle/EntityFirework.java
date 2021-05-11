package com.murengezi.minecraft.client.particle;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemDye;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class EntityFirework {

    public static class Factory implements IParticleFactory {
        public EntityFX getEntityFX(int particleID, World world, double xCoord, double yCoord, double zCoord, double xSpeed, double ySpeed, double zSpeed, int... p_178902_15_) {
            EntityFirework.SparkFX sparkFX = new EntityFirework.SparkFX(world, xCoord, yCoord, zCoord, xSpeed, ySpeed, zSpeed, Minecraft.getMinecraft().effectRenderer);
            sparkFX.setAlphaF(0.99F);
            return sparkFX;
        }
    }

    public static class OverlayFX extends EntityFX {
        protected OverlayFX(World world, double p_i46466_2_, double p_i46466_4_, double p_i46466_6_) {
            super(world, p_i46466_2_, p_i46466_4_, p_i46466_6_);
            this.particleMaxAge = 4;
        }

        public void renderParticle(WorldRenderer worldRenderer, Entity entityIn, float partialTicks, float p_180434_4_, float p_180434_5_, float p_180434_6_, float p_180434_7_, float p_180434_8_) {
            float f4 = 7.1F * MathHelper.sin(((float)this.particleAge + partialTicks - 1.0F) * 0.25F * (float)Math.PI);
            this.particleAlpha = 0.6F - ((float)this.particleAge + partialTicks - 1.0F) * 0.25F * 0.5F;
            float f5 = (float)(this.prevPosX + (this.posX - this.prevPosX) * (double)partialTicks - interpPosX);
            float f6 = (float)(this.prevPosY + (this.posY - this.prevPosY) * (double)partialTicks - interpPosY);
            float f7 = (float)(this.prevPosZ + (this.posZ - this.prevPosZ) * (double)partialTicks - interpPosZ);
            int i = this.getBrightnessForRender(partialTicks);
            int j = i >> 16 & 65535;
            int k = i & 65535;
            worldRenderer.pos(f5 - p_180434_4_ * f4 - p_180434_7_ * f4, f6 - p_180434_5_ * f4, f7 - p_180434_6_ * f4 - p_180434_8_ * f4).tex(0.5D, 0.375D).color(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha).lightmap(j, k).endVertex();
            worldRenderer.pos(f5 - p_180434_4_ * f4 + p_180434_7_ * f4, f6 + p_180434_5_ * f4, f7 - p_180434_6_ * f4 + p_180434_8_ * f4).tex(0.5D, 0.125D).color(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha).lightmap(j, k).endVertex();
            worldRenderer.pos(f5 + p_180434_4_ * f4 + p_180434_7_ * f4, f6 + p_180434_5_ * f4, f7 + p_180434_6_ * f4 + p_180434_8_ * f4).tex(0.25D, 0.125D).color(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha).lightmap(j, k).endVertex();
            worldRenderer.pos(f5 + p_180434_4_ * f4 - p_180434_7_ * f4, f6 - p_180434_5_ * f4, f7 + p_180434_6_ * f4 - p_180434_8_ * f4).tex(0.25D, 0.375D).color(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha).lightmap(j, k).endVertex();
        }
    }

    public static class SparkFX extends EntityFX {
        private final int baseTextureIndex = 160;
        private boolean trail;
        private boolean twinkle;
        private final EffectRenderer renderer;
        private float fadeColourRed, fadeColourGreen, fadeColourBlue;
        private boolean hasFadeColour;

        public SparkFX(World world, double p_i46465_2_, double p_i46465_4_, double p_i46465_6_, double motionX, double motionY, double motionZ, EffectRenderer renderer) {
            super(world, p_i46465_2_, p_i46465_4_, p_i46465_6_);
            this.motionX = motionX;
            this.motionY = motionY;
            this.motionZ = motionZ;
            this.renderer = renderer;
            this.particleScale *= 0.75F;
            this.particleMaxAge = 48 + this.rand.nextInt(12);
            this.noClip = false;
        }

        public void setTrail(boolean trail) {
            this.trail = trail;
        }

        public void setTwinkle(boolean twinkle) {
            this.twinkle = twinkle;
        }

        public void setColour(int colour) {
            float f = (float)((colour & 16711680) >> 16) / 255.0F;
            float f1 = (float)((colour & 65280) >> 8) / 255.0F;
            float f2 = (float)((colour & 255)) / 255.0F;
            float f3 = 1.0F;
            this.setRBGColorF(f * f3, f1 * f3, f2 * f3);
        }

        public void setFadeColour(int faceColour) {
            this.fadeColourRed = (float)((faceColour & 16711680) >> 16) / 255.0F;
            this.fadeColourGreen = (float)((faceColour & 65280) >> 8) / 255.0F;
            this.fadeColourBlue = (float)((faceColour & 255)) / 255.0F;
            this.hasFadeColour = true;
        }

        public AxisAlignedBB getCollisionBoundingBox() {
            return null;
        }

        public boolean canBePushed() {
            return false;
        }

        public void renderParticle(WorldRenderer worldRenderer, Entity entityIn, float partialTicks, float p_180434_4_, float p_180434_5_, float p_180434_6_, float p_180434_7_, float p_180434_8_) {
            if (!this.twinkle || this.particleAge < this.particleMaxAge / 3 || (this.particleAge + this.particleMaxAge) / 3 % 2 == 0) {
                super.renderParticle(worldRenderer, entityIn, partialTicks, p_180434_4_, p_180434_5_, p_180434_6_, p_180434_7_, p_180434_8_);
            }
        }

        public void onUpdate() {
            this.prevPosX = this.posX;
            this.prevPosY = this.posY;
            this.prevPosZ = this.posZ;

            if (this.particleAge++ >= this.particleMaxAge) {
                this.setDead();
            }

            if (this.particleAge > this.particleMaxAge / 2) {
                this.setAlphaF(1.0F - ((float)this.particleAge - (float)(this.particleMaxAge / 2)) / (float)this.particleMaxAge);

                if (this.hasFadeColour) {
                    this.particleRed += (this.fadeColourRed - this.particleRed) * 0.2F;
                    this.particleGreen += (this.fadeColourGreen - this.particleGreen) * 0.2F;
                    this.particleBlue += (this.fadeColourBlue - this.particleBlue) * 0.2F;
                }
            }

            this.setParticleTextureIndex(this.baseTextureIndex + (7 - this.particleAge * 8 / this.particleMaxAge));
            this.motionY -= 0.004D;
            this.moveEntity(this.motionX, this.motionY, this.motionZ);
            this.motionX *= 0.9100000262260437D;
            this.motionY *= 0.9100000262260437D;
            this.motionZ *= 0.9100000262260437D;

            if (this.onGround) {
                this.motionX *= 0.699999988079071D;
                this.motionZ *= 0.699999988079071D;
            }

            if (this.trail && this.particleAge < this.particleMaxAge / 2 && (this.particleAge + this.particleMaxAge) % 2 == 0) {
                EntityFirework.SparkFX sparkFX = new EntityFirework.SparkFX(this.worldObj, this.posX, this.posY, this.posZ, 0.0D, 0.0D, 0.0D, this.renderer);
                sparkFX.setAlphaF(0.99F);
                sparkFX.setRBGColorF(this.particleRed, this.particleGreen, this.particleBlue);
                sparkFX.particleAge = sparkFX.particleMaxAge / 2;

                if (this.hasFadeColour) {
                    sparkFX.hasFadeColour = true;
                    sparkFX.fadeColourRed = this.fadeColourRed;
                    sparkFX.fadeColourGreen = this.fadeColourGreen;
                    sparkFX.fadeColourBlue = this.fadeColourBlue;
                }

                sparkFX.twinkle = this.twinkle;
                this.renderer.addEffect(sparkFX);
            }
        }

        public int getBrightnessForRender(float partialTicks) {
            return 15728880;
        }

        public float getBrightness(float partialTicks) {
            return 1.0F;
        }
    }

    public static class StarterFX extends EntityFX {

        private int fireworkAge;
        private final EffectRenderer renderer;
        private NBTTagList fireworkExplosions;
        boolean twinkle;

        public StarterFX(World world, double p_i46464_2_, double p_i46464_4_, double p_i46464_6_, double motionX, double motionY, double motionZ, EffectRenderer renderer, NBTTagCompound p_i46464_15_) {
            super(world, p_i46464_2_, p_i46464_4_, p_i46464_6_, 0.0D, 0.0D, 0.0D);
            this.motionX = motionX;
            this.motionY = motionY;
            this.motionZ = motionZ;
            this.renderer = renderer;
            this.particleMaxAge = 8;

            if (p_i46464_15_ != null) {
                this.fireworkExplosions = p_i46464_15_.getTagList("Explosions", 10);

                if (this.fireworkExplosions.tagCount() == 0) {
                    this.fireworkExplosions = null;
                } else {
                    this.particleMaxAge = this.fireworkExplosions.tagCount() * 2 - 1;

                    for (int i = 0; i < this.fireworkExplosions.tagCount(); ++i) {
                        NBTTagCompound nbttagcompound = this.fireworkExplosions.getCompoundTagAt(i);

                        if (nbttagcompound.getBoolean("Flicker")) {
                            this.twinkle = true;
                            this.particleMaxAge += 15;
                            break;
                        }
                    }
                }
            }
        }

        public void renderParticle(WorldRenderer worldRenderer, Entity entityIn, float partialTicks, float p_180434_4_, float p_180434_5_, float p_180434_6_, float p_180434_7_, float p_180434_8_) {}

        public void onUpdate() {
            if (this.fireworkAge == 0 && this.fireworkExplosions != null) {
                boolean flag = this.func_92037_i();
                boolean flag1 = false;

                if (this.fireworkExplosions.tagCount() >= 3) {
                    flag1 = true;
                } else {
                    for (int i = 0; i < this.fireworkExplosions.tagCount(); ++i) {
                        NBTTagCompound nbttagcompound = this.fireworkExplosions.getCompoundTagAt(i);

                        if (nbttagcompound.getByte("Type") == 1) {
                            flag1 = true;
                            break;
                        }
                    }
                }

                String s1 = "fireworks." + (flag1 ? "largeBlast" : "blast") + (flag ? "_far" : "");
                this.worldObj.playSound(this.posX, this.posY, this.posZ, s1, 20.0F, 0.95F + this.rand.nextFloat() * 0.1F, true);
            }

            if (this.fireworkAge % 2 == 0 && this.fireworkExplosions != null && this.fireworkAge / 2 < this.fireworkExplosions.tagCount()) {
                int k = this.fireworkAge / 2;
                NBTTagCompound nbttagcompound1 = this.fireworkExplosions.getCompoundTagAt(k);
                int l = nbttagcompound1.getByte("Type");
                boolean flag4 = nbttagcompound1.getBoolean("Trail");
                boolean flag2 = nbttagcompound1.getBoolean("Flicker");
                int[] aint = nbttagcompound1.getIntArray("Colors");
                int[] aint1 = nbttagcompound1.getIntArray("FadeColors");

                if (aint.length == 0) {
                    aint = new int[] {ItemDye.dyeColors[0]};
                }

                if (l == 1) {
                    this.createBall(0.5D, 4, aint, aint1, flag4, flag2);
                } else if (l == 2) {
                    this.createShaped(0.5D, new double[][] {{0.0D, 1.0D}, {0.3455D, 0.309D}, {0.9511D, 0.309D}, {0.3795918367346939D, -0.12653061224489795D}, {0.6122448979591837D, -0.8040816326530612D}, {0.0D, -0.35918367346938773D}}, aint, aint1, flag4, flag2, false);
                } else if (l == 3) {
                    this.createShaped(0.5D, new double[][] {{0.0D, 0.2D}, {0.2D, 0.2D}, {0.2D, 0.6D}, {0.6D, 0.6D}, {0.6D, 0.2D}, {0.2D, 0.2D}, {0.2D, 0.0D}, {0.4D, 0.0D}, {0.4D, -0.6D}, {0.2D, -0.6D}, {0.2D, -0.4D}, {0.0D, -0.4D}}, aint, aint1, flag4, flag2, true);
                } else if (l == 4) {
                    this.createBurst(aint, aint1, flag4, flag2);
                } else {
                    this.createBall(0.25D, 2, aint, aint1, flag4, flag2);
                }

                int j = aint[0];
                float f = (float)((j & 16711680) >> 16) / 255.0F;
                float f1 = (float)((j & 65280) >> 8) / 255.0F;
                float f2 = (float)((j & 255)) / 255.0F;
                EntityFirework.OverlayFX entityfirework$overlayfx = new EntityFirework.OverlayFX(this.worldObj, this.posX, this.posY, this.posZ);
                entityfirework$overlayfx.setRBGColorF(f, f1, f2);
                this.renderer.addEffect(entityfirework$overlayfx);
            }

            ++this.fireworkAge;

            if (this.fireworkAge > this.particleMaxAge) {
                if (this.twinkle) {
                    boolean flag3 = this.func_92037_i();
                    String s = "fireworks." + (flag3 ? "twinkle_far" : "twinkle");
                    this.worldObj.playSound(this.posX, this.posY, this.posZ, s, 20.0F, 0.9F + this.rand.nextFloat() * 0.15F, true);
                }

                this.setDead();
            }
        }

        private boolean func_92037_i() {
            Minecraft minecraft = Minecraft.getMinecraft();
            return minecraft == null || minecraft.getRenderViewEntity() == null || minecraft.getRenderViewEntity().getDistanceSq(this.posX, this.posY, this.posZ) >= 256.0D;
        }

        private void createParticle(double p_92034_1_, double p_92034_3_, double p_92034_5_, double p_92034_7_, double p_92034_9_, double p_92034_11_, int[] p_92034_13_, int[] p_92034_14_, boolean p_92034_15_, boolean p_92034_16_) {
            EntityFirework.SparkFX sparkFX = new EntityFirework.SparkFX(this.worldObj, p_92034_1_, p_92034_3_, p_92034_5_, p_92034_7_, p_92034_9_, p_92034_11_, this.renderer);
            sparkFX.setAlphaF(0.99F);
            sparkFX.setTrail(p_92034_15_);
            sparkFX.setTwinkle(p_92034_16_);
            int i = this.rand.nextInt(p_92034_13_.length);
            sparkFX.setColour(p_92034_13_[i]);

            if (p_92034_14_ != null && p_92034_14_.length > 0) {
                sparkFX.setFadeColour(p_92034_14_[this.rand.nextInt(p_92034_14_.length)]);
            }

            this.renderer.addEffect(sparkFX);
        }

        private void createBall(double speed, int size, int[] colours, int[] fadeColours, boolean trail, boolean twinkleIn) {
            double d0 = this.posX;
            double d1 = this.posY;
            double d2 = this.posZ;

            for (int i = -size; i <= size; ++i) {
                for (int j = -size; j <= size; ++j) {
                    for (int k = -size; k <= size; ++k) {
                        double d3 = (double)j + (this.rand.nextDouble() - this.rand.nextDouble()) * 0.5D;
                        double d4 = (double)i + (this.rand.nextDouble() - this.rand.nextDouble()) * 0.5D;
                        double d5 = (double)k + (this.rand.nextDouble() - this.rand.nextDouble()) * 0.5D;
                        double d6 = (double)MathHelper.sqrt_double(d3 * d3 + d4 * d4 + d5 * d5) / speed + this.rand.nextGaussian() * 0.05D;
                        this.createParticle(d0, d1, d2, d3 / d6, d4 / d6, d5 / d6, colours, fadeColours, trail, twinkleIn);

                        if (i != -size && i != size && j != -size && j != size) {
                            k += size * 2 - 1;
                        }
                    }
                }
            }
        }

        private void createShaped(double speed, double[][] shape, int[] colours, int[] fadeColours, boolean trail, boolean twinkle, boolean p_92038_8_) {
            double d0 = shape[0][0];
            double d1 = shape[0][1];
            this.createParticle(this.posX, this.posY, this.posZ, d0 * speed, d1 * speed, 0.0D, colours, fadeColours, trail, twinkle);
            float f = this.rand.nextFloat() * (float)Math.PI;
            double d2 = p_92038_8_ ? 0.034D : 0.34D;

            for (int i = 0; i < 3; ++i) {
                double d3 = (double)f + (double)((float)i * (float)Math.PI) * d2;
                double d4 = d0;
                double d5 = d1;

                for (int j = 1; j < shape.length; ++j) {
                    double d6 = shape[j][0];
                    double d7 = shape[j][1];

                    for (double d8 = 0.25D; d8 <= 1.0D; d8 += 0.25D) {
                        double d9 = (d4 + (d6 - d4) * d8) * speed;
                        double d10 = (d5 + (d7 - d5) * d8) * speed;
                        double d11 = d9 * Math.sin(d3);
                        d9 = d9 * Math.cos(d3);

                        for (double d12 = -1.0D; d12 <= 1.0D; d12 += 2.0D) {
                            this.createParticle(this.posX, this.posY, this.posZ, d9 * d12, d10, d11 * d12, colours, fadeColours, trail, twinkle);
                        }
                    }

                    d4 = d6;
                    d5 = d7;
                }
            }
        }

        private void createBurst(int[] colours, int[] fadeColours, boolean trail, boolean twinkle) {
            double d0 = this.rand.nextGaussian() * 0.05D;
            double d1 = this.rand.nextGaussian() * 0.05D;

            for (int i = 0; i < 70; ++i) {
                double d2 = this.motionX * 0.5D + this.rand.nextGaussian() * 0.15D + d0;
                double d3 = this.motionZ * 0.5D + this.rand.nextGaussian() * 0.15D + d1;
                double d4 = this.motionY * 0.5D + this.rand.nextDouble() * 0.5D;
                this.createParticle(this.posX, this.posY, this.posZ, d2, d4, d3, colours, fadeColours, trail, twinkle);
            }
        }

        public int getFXLayer() {
            return 0;
        }
    }

}
