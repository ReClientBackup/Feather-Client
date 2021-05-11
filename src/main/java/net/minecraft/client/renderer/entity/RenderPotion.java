package net.minecraft.client.renderer.entity;

import net.minecraft.entity.projectile.EntityPotion;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

public class RenderPotion extends RenderSnowball<EntityPotion> {

	public RenderPotion(RenderManager renderManager, RenderItem itemRenderer) {
		super(renderManager, Items.potionitem, itemRenderer);
	}

	public ItemStack func_177082_d(EntityPotion entity) {
		return new ItemStack(this.item, 1, entity.getPotionDamage());
	}

}
