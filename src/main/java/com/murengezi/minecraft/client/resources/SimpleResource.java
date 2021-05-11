package com.murengezi.minecraft.client.resources;

import com.google.common.collect.Maps;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.murengezi.minecraft.client.resources.data.IMetadataSection;
import com.murengezi.minecraft.client.resources.data.IMetadataSerializer;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.io.IOUtils;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;

public class SimpleResource implements IResource {

	private final Map<String, IMetadataSection> mapMetadataSections = Maps.newHashMap();
	private final String resourcePackName;
	private final ResourceLocation srResourceLocation;
	private final InputStream resourceInputStream, mcmetaInputStream;
	private final IMetadataSerializer srMetadataSerializer;
	private boolean mcmetaJsonChecked;
	private JsonObject mcmetaJson;

	public SimpleResource(String resourcePackName, ResourceLocation srResourceLocation, InputStream resourceInputStream, InputStream mcmetaInputStream, IMetadataSerializer srMetadataSerializer) {
		this.resourcePackName = resourcePackName;
		this.srResourceLocation = srResourceLocation;
		this.resourceInputStream = resourceInputStream;
		this.mcmetaInputStream = mcmetaInputStream;
		this.srMetadataSerializer = srMetadataSerializer;
	}

	public ResourceLocation getResourceLocation() {
		return this.srResourceLocation;
	}

	public InputStream getInputStream() {
		return this.resourceInputStream;
	}

	public boolean hasMetadata() {
		return this.mcmetaInputStream != null;
	}

	public <T extends IMetadataSection> T getMetadata(String p_110526_1_) {
		if (!this.hasMetadata()) {
			return null;
		} else {
			if (this.mcmetaJson == null && !this.mcmetaJsonChecked) {
				this.mcmetaJsonChecked = true;
				BufferedReader bufferedreader = null;

				try {
					bufferedreader = new BufferedReader(new InputStreamReader(this.mcmetaInputStream));
					this.mcmetaJson = (new JsonParser()).parse(bufferedreader).getAsJsonObject();
				} finally {
					IOUtils.closeQuietly(bufferedreader);
				}
			}

			T t = (T) this.mapMetadataSections.get(p_110526_1_);

			if (t == null) {
				t = this.srMetadataSerializer.parseMetadataSection(p_110526_1_, this.mcmetaJson);
			}

			return t;
		}
	}

	public String getResourcePackName() {
		return this.resourcePackName;
	}

	public boolean equals(Object p_equals_1_) {
		if (this == p_equals_1_) {
			return true;
		} else if (!(p_equals_1_ instanceof SimpleResource)) {
			return false;
		} else {
			SimpleResource simpleresource = (SimpleResource) p_equals_1_;

			if (this.srResourceLocation != null) {
				if (!this.srResourceLocation.equals(simpleresource.srResourceLocation)) {
					return false;
				}
			} else if (simpleresource.srResourceLocation != null) {
				return false;
			}

			if (this.resourcePackName != null) {
				return this.resourcePackName.equals(simpleresource.resourcePackName);
			} else return simpleresource.resourcePackName == null;
		}
	}

	public int hashCode() {
		int i = this.resourcePackName != null ? this.resourcePackName.hashCode() : 0;
		i = 31 * i + (this.srResourceLocation != null ? this.srResourceLocation.hashCode() : 0);
		return i;
	}

}
