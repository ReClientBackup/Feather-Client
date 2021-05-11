package com.murengezi.minecraft.client.renderer;

import com.murengezi.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.item.ItemStack;

public interface ItemMeshDefinition {

	ModelResourceLocation getModelLocation(ItemStack stack);

}
