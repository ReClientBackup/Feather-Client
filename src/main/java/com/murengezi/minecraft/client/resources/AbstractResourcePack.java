package com.murengezi.minecraft.client.resources;

import com.google.common.base.Charsets;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.murengezi.minecraft.client.renderer.texture.TextureUtil;
import com.murengezi.minecraft.client.resources.data.IMetadataSection;
import com.murengezi.minecraft.client.resources.data.IMetadataSerializer;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;

public abstract class AbstractResourcePack implements IResourcePack {

	private static final Logger resourceLog = LogManager.getLogger();
	public final File resourcePackFile;

	public AbstractResourcePack(File resourcePackFileIn) {
		this.resourcePackFile = resourcePackFileIn;
	}

	private static String locationToName(ResourceLocation location) {
		return String.format("%s/%s/%s", "assets", location.getResourceDomain(), location.getResourcePath());
	}

	protected static String getRelativeName(File p_110595_0_, File p_110595_1_) {
		return p_110595_0_.toURI().relativize(p_110595_1_.toURI()).getPath();
	}

	public InputStream getInputStream(ResourceLocation location) throws IOException {
		return this.getInputStreamByName(locationToName(location));
	}

	public boolean resourceExists(ResourceLocation location) {
		return this.hasResourceName(locationToName(location));
	}

	protected abstract InputStream getInputStreamByName(String name) throws IOException;

	protected abstract boolean hasResourceName(String name);

	protected void logNameNotLowercase(String p_110594_1_) {
		resourceLog.warn("ResourcePack: ignored non-lowercase namespace: {} in {}", p_110594_1_, this.resourcePackFile);
	}

	public <T extends IMetadataSection> T getPackMetadata(IMetadataSerializer serializer, String p_135058_2_) throws IOException {
		return readMetadata(serializer, this.getInputStreamByName("pack.mcmeta"), p_135058_2_);
	}

	static <T extends IMetadataSection> T readMetadata(IMetadataSerializer p_110596_0_, InputStream p_110596_1_, String p_110596_2_) {
		JsonObject jsonobject = null;
		BufferedReader bufferedreader = null;

		try {
			bufferedreader = new BufferedReader(new InputStreamReader(p_110596_1_, Charsets.UTF_8));
			jsonobject = (new JsonParser()).parse(bufferedreader).getAsJsonObject();
		} catch (RuntimeException runtimeexception) {
			throw new JsonParseException(runtimeexception);
		} finally {
			IOUtils.closeQuietly(bufferedreader);
		}

		return p_110596_0_.parseMetadataSection(p_110596_2_, jsonobject);
	}

	public BufferedImage getPackImage() throws IOException {
		BufferedImage image = TextureUtil.readBufferedImage(this.getInputStreamByName("pack.png"));
		if (image == null)
			return null;
		System.out.println("Scaling resource pack icon from " + image.getWidth() + " to 64");
		BufferedImage smallImage = new BufferedImage(64, 64, 2);
		Graphics graphics = smallImage.getGraphics();
		graphics.drawImage(image, 0, 0, 64, 64, null);
		graphics.dispose();
		return smallImage;
	}

	public String getPackName() {
		return this.resourcePackFile.getName();
	}

}
