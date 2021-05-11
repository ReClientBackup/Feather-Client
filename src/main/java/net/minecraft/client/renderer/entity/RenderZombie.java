package net.minecraft.client.renderer.entity;

import com.google.common.collect.Lists;
import com.murengezi.minecraft.client.model.ModelBiped;
import com.murengezi.minecraft.client.model.ModelZombie;
import com.murengezi.minecraft.client.model.ModelZombieVillager;
import net.minecraft.client.renderer.entity.layers.*;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.util.ResourceLocation;

import java.util.List;

public class RenderZombie extends RenderBiped<EntityZombie> {

	private static final ResourceLocation zombieTextures = new ResourceLocation("textures/entity/zombie/zombie.png"), zombieVillagerTextures = new ResourceLocation("textures/entity/zombie/zombie_villager.png");
	private final ModelBiped field_82434_o;
	private final ModelZombieVillager zombieVillagerModel;
	private final List<LayerRenderer<EntityZombie>> field_177121_n, field_177122_o;

	public RenderZombie(RenderManager renderManager) {
		super(renderManager, new ModelZombie(), 0.5F, 1.0F);
		LayerRenderer layerrenderer = this.layerRenderers.get(0);
		this.field_82434_o = this.modelBipedMain;
		this.zombieVillagerModel = new ModelZombieVillager();
		this.addLayer(new LayerHeldItem(this));
		LayerBipedArmor layerbipedarmor = new LayerBipedArmor(this) {
			protected void initArmor() {
				this.field_177189_c = new ModelZombie(0.5F, true);
				this.field_177186_d = new ModelZombie(1.0F, true);
			}
		};
		this.addLayer(layerbipedarmor);
		this.field_177122_o = Lists.newArrayList(this.layerRenderers);

		if (layerrenderer instanceof LayerCustomHead) {
			this.removeLayer(layerrenderer);
			this.addLayer(new LayerCustomHead(this.zombieVillagerModel.bipedHead));
		}

		this.removeLayer(layerbipedarmor);
		this.addLayer(new LayerVillagerArmor(this));
		this.field_177121_n = Lists.newArrayList(this.layerRenderers);
	}

	public void doRender(EntityZombie entity, double x, double y, double z, float entityYaw, float partialTicks) {
		this.func_82427_a(entity);
		super.doRender(entity, x, y, z, entityYaw, partialTicks);
	}

	protected ResourceLocation getEntityTexture(EntityZombie entity) {
		return entity.isVillager() ? zombieVillagerTextures : zombieTextures;
	}

	private void func_82427_a(EntityZombie zombie) {
		if (zombie.isVillager()) {
			this.mainModel = this.zombieVillagerModel;
			this.layerRenderers = this.field_177121_n;
		} else {
			this.mainModel = this.field_82434_o;
			this.layerRenderers = this.field_177122_o;
		}

		this.modelBipedMain = (ModelBiped) this.mainModel;
	}

	protected void rotateCorpse(EntityZombie bat, float p_77043_2_, float p_77043_3_, float partialTicks) {
		if (bat.isConverting()) {
			p_77043_3_ += (float) (Math.cos((double) bat.ticksExisted * 3.25D) * Math.PI * 0.25D);
		}

		super.rotateCorpse(bat, p_77043_2_, p_77043_3_, partialTicks);
	}

}
