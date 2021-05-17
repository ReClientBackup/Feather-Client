package com.murengezi.minecraft.client.renderer.entity;

import com.murengezi.minecraft.block.state.IBlockState;
import com.murengezi.minecraft.client.renderer.tileentity.TileEntityMobSpawnerRenderer;
import net.minecraft.entity.ai.EntityMinecartMobSpawner;
import com.murengezi.minecraft.init.Blocks;

public class RenderMinecartMobSpawner extends RenderMinecart<EntityMinecartMobSpawner> {

	public RenderMinecartMobSpawner(RenderManager renderManager) {
		super(renderManager);
	}

	protected void func_180560_a(EntityMinecartMobSpawner minecart, float partialTicks, IBlockState state) {
		super.func_180560_a(minecart, partialTicks, state);

		if (state.getBlock() == Blocks.mob_spawner) {
			TileEntityMobSpawnerRenderer.renderMob(minecart.func_98039_d(), minecart.posX, minecart.posY, minecart.posZ, partialTicks);
		}
	}

}
