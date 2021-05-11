package com.murengezi.minecraft.client.particle;

import com.murengezi.minecraft.client.Minecraft;
import com.murengezi.minecraft.client.renderer.GlStateManager;
import com.murengezi.minecraft.client.renderer.OpenGlHelper;
import com.murengezi.minecraft.client.renderer.WorldRenderer;
import com.murengezi.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.optifine.config.Config;
import net.minecraft.world.World;
import net.optifine.shaders.Program;
import net.optifine.shaders.Shaders;

public class EntityPickupFX extends EntityFX {

    private final Entity entity1, entity2;
    private int age;
    private final int maxAge;
    private final float field_174841_aA;
    private final RenderManager field_174842_aB = Minecraft.getMinecraft().getRenderManager();

    public EntityPickupFX(World world, Entity entity1, Entity entity2, float p_i1233_4_) {
        super(world, entity1.posX, entity1.posY, entity1.posZ, entity1.motionX, entity1.motionY, entity1.motionZ);
        this.entity1 = entity1;
        this.entity2 = entity2;
        this.maxAge = 3;
        this.field_174841_aA = p_i1233_4_;
    }

    public void renderParticle(WorldRenderer worldRenderer, Entity entityIn, float partialTicks, float p_180434_4_, float p_180434_5_, float p_180434_6_, float p_180434_7_, float p_180434_8_) {
        Program program = null;

        if (Config.isShaders()) {
            program = Shaders.activeProgram;
            Shaders.nextEntity(this.entity1);
        }

        float f = ((float)this.age + partialTicks) / (float)this.maxAge;
        f = f * f;
        double d0 = this.entity1.posX;
        double d1 = this.entity1.posY;
        double d2 = this.entity1.posZ;
        double d3 = this.entity2.lastTickPosX + (this.entity2.posX - this.entity2.lastTickPosX) * (double)partialTicks;
        double d4 = this.entity2.lastTickPosY + (this.entity2.posY - this.entity2.lastTickPosY) * (double)partialTicks + (double)this.field_174841_aA;
        double d5 = this.entity2.lastTickPosZ + (this.entity2.posZ - this.entity2.lastTickPosZ) * (double)partialTicks;
        double d6 = d0 + (d3 - d0) * (double)f;
        double d7 = d1 + (d4 - d1) * (double)f;
        double d8 = d2 + (d5 - d2) * (double)f;
        int i = this.getBrightnessForRender(partialTicks);
        int j = i % 65536;
        int k = i / 65536;
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, (float)j / 1.0F, (float)k / 1.0F);
        GlStateManager.colorAllMax();
        d6 = d6 - interpPosX;
        d7 = d7 - interpPosY;
        d8 = d8 - interpPosZ;
        this.field_174842_aB.renderEntityWithPosYaw(this.entity1, (float)d6, (float)d7, (float)d8, this.entity1.rotationYaw, partialTicks);

        if (Config.isShaders()) {
            Shaders.setEntityId(null);
            Shaders.useProgram(program);
        }
    }

    public void onUpdate() {
        ++this.age;

        if (this.age == this.maxAge) {
            this.setDead();
        }
    }

    public int getFXLayer() {
        return 3;
    }

}
