package com.murengezi.minecraft.client.particle;

import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.World;

public class EntityParticleEmitter extends EntityFX {

    private final Entity attachedEntity;
    private int age;
    private final int lifetime;
    private final EnumParticleTypes particleTypes;

    public EntityParticleEmitter(World world, Entity entity, EnumParticleTypes particleTypes) {
        super(world, entity.posX, entity.getEntityBoundingBox().minY + (double)(entity.height / 2.0F), entity.posZ, entity.motionX, entity.motionY, entity.motionZ);
        this.attachedEntity = entity;
        this.lifetime = 3;
        this.particleTypes = particleTypes;
        this.onUpdate();
    }

    public void renderParticle(WorldRenderer worldRenderer, Entity entityIn, float partialTicks, float p_180434_4_, float p_180434_5_, float p_180434_6_, float p_180434_7_, float p_180434_8_) {}

    public void onUpdate() {
        for (int i = 0; i < 16; ++i) {
            double d0 = this.rand.nextFloat() * 2.0F - 1.0F;
            double d1 = this.rand.nextFloat() * 2.0F - 1.0F;
            double d2 = this.rand.nextFloat() * 2.0F - 1.0F;

            if (d0 * d0 + d1 * d1 + d2 * d2 <= 1.0D) {
                double d3 = this.attachedEntity.posX + d0 * (double)this.attachedEntity.width / 4.0D;
                double d4 = this.attachedEntity.getEntityBoundingBox().minY + (double)(this.attachedEntity.height / 2.0F) + d1 * (double)this.attachedEntity.height / 4.0D;
                double d5 = this.attachedEntity.posZ + d2 * (double)this.attachedEntity.width / 4.0D;
                this.worldObj.spawnParticle(this.particleTypes, false, d3, d4, d5, d0, d1 + 0.2D, d2);
            }
        }

        ++this.age;

        if (this.age >= this.lifetime) {
            this.setDead();
        }
    }

    public int getFXLayer() {
        return 3;
    }

}
