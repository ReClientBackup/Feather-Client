package com.murengezi.minecraft.client.resources;

import com.murengezi.minecraft.client.resources.data.IMetadataSection;
import net.minecraft.util.ResourceLocation;

import java.io.InputStream;

public interface IResource {

	ResourceLocation getResourceLocation();

	InputStream getInputStream();

	boolean hasMetadata();

	<T extends IMetadataSection> T getMetadata(String p_110526_1_);

	String getResourcePackName();

}
