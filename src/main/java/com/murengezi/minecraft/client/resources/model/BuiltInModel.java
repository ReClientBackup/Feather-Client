package com.murengezi.minecraft.client.resources.model;

import com.murengezi.minecraft.client.renderer.block.model.BakedQuad;
import com.murengezi.minecraft.client.renderer.block.model.ItemCameraTransforms;
import com.murengezi.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.EnumFacing;

import java.util.List;

public class BuiltInModel implements IBakedModel {

	private final ItemCameraTransforms cameraTransforms;

	public BuiltInModel(ItemCameraTransforms cameraTransforms) {
		this.cameraTransforms = cameraTransforms;
	}

	public List<BakedQuad> getFaceQuads(EnumFacing facing) {
		return null;
	}

	public List<BakedQuad> getGeneralQuads() {
		return null;
	}

	public boolean isAmbientOcclusion() {
		return false;
	}

	public boolean isGui3d() {
		return true;
	}

	public boolean isBuiltInRenderer() {
		return true;
	}

	public TextureAtlasSprite getTexture() {
		return null;
	}

	public ItemCameraTransforms getItemCameraTransforms() {
		return this.cameraTransforms;
	}

}
