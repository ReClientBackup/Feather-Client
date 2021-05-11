package net.minecraft.client.renderer.entity;

import com.google.common.collect.Maps;
import com.murengezi.minecraft.block.Block;
import com.murengezi.minecraft.block.BlockBed;
import com.murengezi.minecraft.block.state.IBlockState;
import com.murengezi.minecraft.client.entity.AbstractClientPlayer;
import com.murengezi.minecraft.client.model.*;
import com.murengezi.minecraft.crash.CrashReport;
import com.murengezi.minecraft.crash.CrashReportCategory;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.culling.ICamera;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.tileentity.RenderEnderCrystal;
import net.minecraft.client.renderer.tileentity.RenderItemFrame;
import net.minecraft.client.renderer.tileentity.RenderWitherSkull;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLeashKnot;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityMinecartMobSpawner;
import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.entity.boss.EntityWither;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.item.*;
import net.minecraft.entity.monster.*;
import net.minecraft.entity.passive.*;
import net.minecraft.entity.projectile.*;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.util.*;
import net.minecraft.world.World;
import net.optifine.entity.model.CustomEntityModels;
import net.optifine.player.PlayerItemsLayer;
import net.optifine.reflect.Reflector;

import java.util.Map;

public class RenderManager {

	private Map<Class, Render> entityRenderMap = Maps.newHashMap();
	private final Map<String, RenderPlayer> skinMap = Maps.newHashMap();
	private final RenderPlayer playerRenderer;

	private FontRenderer textRenderer;
	private double renderPosX, renderPosY, renderPosZ;
	public TextureManager renderEngine;

	public World worldObj;

	public Entity livingPlayer, pointedEntity;
	public float playerViewY, playerViewX;

	public GameSettings options;
	public double viewerPosX, viewerPosY, viewerPosZ;
	private boolean renderOutlines = false, renderShadow = true, debugBoundingBox = false;
	public Render renderRender = null;

	public RenderManager(TextureManager renderEngine, RenderItem itemRenderer) {
		this.renderEngine = renderEngine;
		this.entityRenderMap.put(EntityCaveSpider.class, new RenderCaveSpider(this));
		this.entityRenderMap.put(EntitySpider.class, new RenderSpider(this));
		this.entityRenderMap.put(EntityPig.class, new RenderPig(this, new ModelPig(), 0.7F));
		this.entityRenderMap.put(EntitySheep.class, new RenderSheep(this, new ModelSheep2(), 0.7F));
		this.entityRenderMap.put(EntityCow.class, new RenderCow(this, new ModelCow(), 0.7F));
		this.entityRenderMap.put(EntityMooshroom.class, new RenderMooshroom(this, new ModelCow(), 0.7F));
		this.entityRenderMap.put(EntityWolf.class, new RenderWolf(this, new ModelWolf(), 0.5F));
		this.entityRenderMap.put(EntityChicken.class, new RenderChicken(this, new ModelChicken(), 0.3F));
		this.entityRenderMap.put(EntityOcelot.class, new RenderOcelot(this, new ModelOcelot(), 0.4F));
		this.entityRenderMap.put(EntityRabbit.class, new RenderRabbit(this, new ModelRabbit(), 0.3F));
		this.entityRenderMap.put(EntitySilverfish.class, new RenderSilverfish(this));
		this.entityRenderMap.put(EntityEndermite.class, new RenderEndermite(this));
		this.entityRenderMap.put(EntityCreeper.class, new RenderCreeper(this));
		this.entityRenderMap.put(EntityEnderman.class, new RenderEnderman(this));
		this.entityRenderMap.put(EntitySnowman.class, new RenderSnowMan(this));
		this.entityRenderMap.put(EntitySkeleton.class, new RenderSkeleton(this));
		this.entityRenderMap.put(EntityWitch.class, new RenderWitch(this));
		this.entityRenderMap.put(EntityBlaze.class, new RenderBlaze(this));
		this.entityRenderMap.put(EntityPigZombie.class, new RenderPigZombie(this));
		this.entityRenderMap.put(EntityZombie.class, new RenderZombie(this));
		this.entityRenderMap.put(EntitySlime.class, new RenderSlime(this, new ModelSlime(16), 0.25F));
		this.entityRenderMap.put(EntityMagmaCube.class, new RenderMagmaCube(this));
		this.entityRenderMap.put(EntityGiantZombie.class, new RenderGiantZombie(this, new ModelZombie(), 0.5F, 6.0F));
		this.entityRenderMap.put(EntityGhast.class, new RenderGhast(this));
		this.entityRenderMap.put(EntitySquid.class, new RenderSquid(this, new ModelSquid(), 0.7F));
		this.entityRenderMap.put(EntityVillager.class, new RenderVillager(this));
		this.entityRenderMap.put(EntityIronGolem.class, new RenderIronGolem(this));
		this.entityRenderMap.put(EntityBat.class, new RenderBat(this));
		this.entityRenderMap.put(EntityGuardian.class, new RenderGuardian(this));
		this.entityRenderMap.put(EntityDragon.class, new RenderDragon(this));
		this.entityRenderMap.put(EntityEnderCrystal.class, new RenderEnderCrystal(this));
		this.entityRenderMap.put(EntityWither.class, new RenderWither(this));
		this.entityRenderMap.put(Entity.class, new RenderEntity(this));
		this.entityRenderMap.put(EntityPainting.class, new RenderPainting(this));
		this.entityRenderMap.put(EntityItemFrame.class, new RenderItemFrame(this, itemRenderer));
		this.entityRenderMap.put(EntityLeashKnot.class, new RenderLeashKnot(this));
		this.entityRenderMap.put(EntityArrow.class, new RenderArrow(this));
		this.entityRenderMap.put(EntitySnowball.class, new RenderSnowball(this, Items.snowball, itemRenderer));
		this.entityRenderMap.put(EntityEnderPearl.class, new RenderSnowball(this, Items.ender_pearl, itemRenderer));
		this.entityRenderMap.put(EntityEnderEye.class, new RenderSnowball(this, Items.ender_eye, itemRenderer));
		this.entityRenderMap.put(EntityEgg.class, new RenderSnowball(this, Items.egg, itemRenderer));
		this.entityRenderMap.put(EntityPotion.class, new RenderPotion(this, itemRenderer));
		this.entityRenderMap.put(EntityExpBottle.class, new RenderSnowball(this, Items.experience_bottle, itemRenderer));
		this.entityRenderMap.put(EntityFireworkRocket.class, new RenderSnowball(this, Items.fireworks, itemRenderer));
		this.entityRenderMap.put(EntityLargeFireball.class, new RenderFireball(this, 2.0F));
		this.entityRenderMap.put(EntitySmallFireball.class, new RenderFireball(this, 0.5F));
		this.entityRenderMap.put(EntityWitherSkull.class, new RenderWitherSkull(this));
		this.entityRenderMap.put(EntityItem.class, new RenderEntityItem(this, itemRenderer));
		this.entityRenderMap.put(EntityXPOrb.class, new RenderXPOrb(this));
		this.entityRenderMap.put(EntityTNTPrimed.class, new RenderTNTPrimed(this));
		this.entityRenderMap.put(EntityFallingBlock.class, new RenderFallingBlock(this));
		this.entityRenderMap.put(EntityArmorStand.class, new ArmorStandRenderer(this));
		this.entityRenderMap.put(EntityMinecartTNT.class, new RenderTntMinecart(this));
		this.entityRenderMap.put(EntityMinecartMobSpawner.class, new RenderMinecartMobSpawner(this));
		this.entityRenderMap.put(EntityMinecart.class, new RenderMinecart(this));
		this.entityRenderMap.put(EntityBoat.class, new RenderBoat(this));
		this.entityRenderMap.put(EntityFishHook.class, new RenderFish(this));
		this.entityRenderMap.put(EntityHorse.class, new RenderHorse(this, new ModelHorse(), 0.75F));
		this.entityRenderMap.put(EntityLightningBolt.class, new RenderLightningBolt(this));
		this.playerRenderer = new RenderPlayer(this);
		this.skinMap.put("default", this.playerRenderer);
		this.skinMap.put("slim", new RenderPlayer(this, true));
		PlayerItemsLayer.register(this.skinMap);

		if (Reflector.RenderingRegistry_loadEntityRenderers.exists()) {
			Reflector.call(Reflector.RenderingRegistry_loadEntityRenderers, this, this.entityRenderMap);
		}
	}

	public void setRenderPosition(double renderPosXIn, double renderPosYIn, double renderPosZIn) {
		this.renderPosX = renderPosXIn;
		this.renderPosY = renderPosYIn;
		this.renderPosZ = renderPosZIn;
	}

	public <T extends Entity> Render<T> getEntityClassRenderObject(Class<? extends Entity> p_78715_1_) {
		Render render = this.entityRenderMap.get(p_78715_1_);

		if (render == null && p_78715_1_ != Entity.class) {
			render = this.getEntityClassRenderObject((Class<? extends Entity>) p_78715_1_.getSuperclass());
			this.entityRenderMap.put(p_78715_1_, render);
		}

		return (Render<T>) render;
	}

	public <T extends Entity> Render<T> getEntityRenderObject(Entity entityIn) {
		if (entityIn instanceof AbstractClientPlayer) {
			String s = ((AbstractClientPlayer) entityIn).getSkinType();
			RenderPlayer renderplayer = this.skinMap.get(s);
			return (Render<T>) (renderplayer != null ? renderplayer : this.playerRenderer);
		} else {
			return this.getEntityClassRenderObject(entityIn.getClass());
		}
	}

	public void cacheActiveRenderInfo(World world, FontRenderer textRendererIn, Entity livingPlayerIn, Entity pointedEntityIn, GameSettings optionsIn, float partialTicks) {
		this.worldObj = world;
		this.options = optionsIn;
		this.livingPlayer = livingPlayerIn;
		this.pointedEntity = pointedEntityIn;
		this.textRenderer = textRendererIn;

		if (livingPlayerIn instanceof EntityLivingBase && ((EntityLivingBase) livingPlayerIn).isPlayerSleeping()) {
			IBlockState iblockstate = world.getBlockState(new BlockPos(livingPlayerIn));
			Block block = iblockstate.getBlock();

			if (Reflector.callBoolean(block, Reflector.ForgeBlock_isBed, iblockstate, world, new BlockPos(livingPlayerIn), livingPlayerIn)) {
				EnumFacing enumfacing = (EnumFacing) Reflector.call(block, Reflector.ForgeBlock_getBedDirection, new Object[]{iblockstate, world, new BlockPos(livingPlayerIn)});
				assert enumfacing != null;
				int i = enumfacing.getHorizontalIndex();
				this.playerViewY = (float) (i * 90 + 180);
				this.playerViewX = 0.0F;
			} else if (block == Blocks.bed) {
				int j = iblockstate.getValue(BlockBed.FACING).getHorizontalIndex();
				this.playerViewY = (float) (j * 90 + 180);
				this.playerViewX = 0.0F;
			}
		} else {
			this.playerViewY = livingPlayerIn.prevRotationYaw + (livingPlayerIn.rotationYaw - livingPlayerIn.prevRotationYaw) * partialTicks;
			this.playerViewX = livingPlayerIn.prevRotationPitch + (livingPlayerIn.rotationPitch - livingPlayerIn.prevRotationPitch) * partialTicks;
		}

		if (optionsIn.thirdPersonView == 2) {
			this.playerViewY += 180.0F;
		}

		this.viewerPosX = livingPlayerIn.lastTickPosX + (livingPlayerIn.posX - livingPlayerIn.lastTickPosX) * (double) partialTicks;
		this.viewerPosY = livingPlayerIn.lastTickPosY + (livingPlayerIn.posY - livingPlayerIn.lastTickPosY) * (double) partialTicks;
		this.viewerPosZ = livingPlayerIn.lastTickPosZ + (livingPlayerIn.posZ - livingPlayerIn.lastTickPosZ) * (double) partialTicks;
	}

	public void setPlayerViewY(float playerViewYIn) {
		this.playerViewY = playerViewYIn;
	}

	public boolean isRenderShadow() {
		return this.renderShadow;
	}

	public void setRenderShadow(boolean renderShadowIn) {
		this.renderShadow = renderShadowIn;
	}

	public void setDebugBoundingBox(boolean debugBoundingBox) {
		this.debugBoundingBox = debugBoundingBox;
	}

	public boolean isDebugBoundingBox() {
		return this.debugBoundingBox;
	}

	public void renderEntitySimple(Entity entity, float partialTicks) {
        this.renderEntityStatic(entity, partialTicks, false);
    }

	public boolean shouldRender(Entity entity, ICamera camera, double camX, double camY, double camZ) {
		Render<Entity> render = this.getEntityRenderObject(entity);
		return render != null && render.shouldRender(entity, camera, camX, camY, camZ);
	}

	public boolean renderEntityStatic(Entity entity, float partialTicks, boolean p_147936_3_) {
		if (entity.ticksExisted == 0) {
			entity.lastTickPosX = entity.posX;
			entity.lastTickPosY = entity.posY;
			entity.lastTickPosZ = entity.posZ;
		}

		double d0 = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * (double) partialTicks;
		double d1 = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * (double) partialTicks;
		double d2 = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * (double) partialTicks;
		float f = entity.prevRotationYaw + (entity.rotationYaw - entity.prevRotationYaw) * partialTicks;
		int i = entity.getBrightnessForRender(partialTicks);

		if (entity.isBurning()) {
			i = 15728880;
		}

		int j = i % 65536;
		int k = i / 65536;
		OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, (float) j, (float) k);
		GlStateManager.colorAllMax();
		return this.doRenderEntity(entity, d0 - this.renderPosX, d1 - this.renderPosY, d2 - this.renderPosZ, f, partialTicks, p_147936_3_);
	}

	public void renderWitherSkull(Entity entityIn, float partialTicks) {
		double d0 = entityIn.lastTickPosX + (entityIn.posX - entityIn.lastTickPosX) * (double) partialTicks;
		double d1 = entityIn.lastTickPosY + (entityIn.posY - entityIn.lastTickPosY) * (double) partialTicks;
		double d2 = entityIn.lastTickPosZ + (entityIn.posZ - entityIn.lastTickPosZ) * (double) partialTicks;
		Render<Entity> render = this.getEntityRenderObject(entityIn);

		if (render != null && this.renderEngine != null) {
			int i = entityIn.getBrightnessForRender(partialTicks);
			int j = i % 65536;
			int k = i / 65536;
			OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, (float) j, (float) k);
			GlStateManager.colorAllMax();
			render.renderName(entityIn, d0 - this.renderPosX, d1 - this.renderPosY, d2 - this.renderPosZ);
		}
	}

	public void renderEntityWithPosYaw(Entity entity, double x, double y, double z, float entityYaw, float partialTicks) {
        this.doRenderEntity(entity, x, y, z, entityYaw, partialTicks, false);
    }

	public boolean doRenderEntity(Entity entity, double x, double y, double z, float entityYaw, float partialTicks, boolean p_147939_10_) {
		Render<Entity> render = null;

		try {
			render = this.getEntityRenderObject(entity);

			if (render != null && this.renderEngine != null) {
				try {
					if (render instanceof RendererLivingEntity) {
						((RendererLivingEntity) render).setRenderOutlines(this.renderOutlines);
					}

					if (CustomEntityModels.isActive()) {
						this.renderRender = render;
					}

					render.doRender(entity, x, y, z, entityYaw, partialTicks);
				} catch (Throwable throwable2) {
					throw new ReportedException(CrashReport.makeCrashReport(throwable2, "Rendering entity in world"));
				}

				try {
					if (!this.renderOutlines) {
						render.doRenderShadowAndFire(entity, x, y, z, entityYaw, partialTicks);
					}
				} catch (Throwable throwable1) {
					throw new ReportedException(CrashReport.makeCrashReport(throwable1, "Post-rendering entity in world"));
				}

				if (this.debugBoundingBox && !entity.isInvisible() && !p_147939_10_) {
					try {
						this.renderDebugBoundingBox(entity, x, y, z, partialTicks);
					} catch (Throwable throwable) {
						throw new ReportedException(CrashReport.makeCrashReport(throwable, "Rendering entity hitbox in world"));
					}
				}
			} else return this.renderEngine == null;

			return true;
		} catch (Throwable throwable3) {
			CrashReport report = CrashReport.makeCrashReport(throwable3, "Rendering entity in world");
			CrashReportCategory crashreportcategory = report.makeCategory("Entity being rendered");
			entity.addEntityCrashInfo(crashreportcategory);
			CrashReportCategory category = report.makeCategory("Renderer details");
			category.addCrashSection("Assigned renderer", render);
			category.addCrashSection("Location", CrashReportCategory.getCoordinateInfo(x, y, z));
			category.addCrashSection("Rotation", entityYaw);
			category.addCrashSection("Delta", partialTicks);
			throw new ReportedException(report);
		}
	}

	private void renderDebugBoundingBox(Entity entity, double p_85094_2_, double p_85094_4_, double p_85094_6_, float p_85094_9_) {
		GlStateManager.depthMask(false);
		GlStateManager.disableTexture2D();
		GlStateManager.disableLighting();
		GlStateManager.disableCull();
		GlStateManager.disableBlend();
		float f = entity.width / 2.0F;
		AxisAlignedBB axisalignedbb = entity.getEntityBoundingBox();
		AxisAlignedBB axisalignedbb1 = new AxisAlignedBB(axisalignedbb.minX - entity.posX + p_85094_2_, axisalignedbb.minY - entity.posY + p_85094_4_, axisalignedbb.minZ - entity.posZ + p_85094_6_, axisalignedbb.maxX - entity.posX + p_85094_2_, axisalignedbb.maxY - entity.posY + p_85094_4_, axisalignedbb.maxZ - entity.posZ + p_85094_6_);
		RenderGlobal.func_181563_a(axisalignedbb1, 255, 255, 255, 255);

		if (entity instanceof EntityLivingBase) {
			RenderGlobal.func_181563_a(new AxisAlignedBB(p_85094_2_ - (double) f, p_85094_4_ + (double) entity.getEyeHeight() - 0.009999999776482582D, p_85094_6_ - (double) f, p_85094_2_ + (double) f, p_85094_4_ + (double) entity.getEyeHeight() + 0.009999999776482582D, p_85094_6_ + (double) f), 255, 0, 0, 255);
		}

		Tessellator tessellator = Tessellator.getInstance();
		WorldRenderer worldrenderer = tessellator.getWorldRenderer();
		Vec3 vec3 = entity.getLook(p_85094_9_);
		worldrenderer.begin(3, DefaultVertexFormats.POSITION_COLOR);
		worldrenderer.pos(p_85094_2_, p_85094_4_ + (double) entity.getEyeHeight(), p_85094_6_).color(0, 0, 255, 255).endVertex();
		worldrenderer.pos(p_85094_2_ + vec3.xCoord * 2.0D, p_85094_4_ + (double) entity.getEyeHeight() + vec3.yCoord * 2.0D, p_85094_6_ + vec3.zCoord * 2.0D).color(0, 0, 255, 255).endVertex();
		tessellator.draw();
		GlStateManager.enableTexture2D();
		GlStateManager.enableLightning();
		GlStateManager.enableCull();
		GlStateManager.disableBlend();
		GlStateManager.depthMask(true);
	}

	public void set(World world) {
		this.worldObj = world;
	}

	public double getDistanceToCamera(double p_78714_1_, double p_78714_3_, double p_78714_5_) {
		double d0 = p_78714_1_ - this.viewerPosX;
		double d1 = p_78714_3_ - this.viewerPosY;
		double d2 = p_78714_5_ - this.viewerPosZ;
		return d0 * d0 + d1 * d1 + d2 * d2;
	}

	public FontRenderer getFontRenderer() {
		return this.textRenderer;
	}

	public void setRenderOutlines(boolean renderOutlinesIn) {
		this.renderOutlines = renderOutlinesIn;
	}

	public Map<Class, Render> getEntityRenderMap() {
		return this.entityRenderMap;
	}

}
