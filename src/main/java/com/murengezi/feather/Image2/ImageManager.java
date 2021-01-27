package com.murengezi.feather.Image2;

import net.minecraft.util.ResourceLocation;

import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Tobias Sjöblom
 * Created on 2021-01-26 at 14:42
 */
public class ImageManager {

	private ImageDownloadThread downloadThread;
	private final Map<ResourceLocation, BufferedImage> loaded;

	public ImageManager() {
		downloadThread = new ImageDownloadThread();
		loaded = new HashMap<>();
	}

	public void addToQueue(String url, ResourceLocation location) {
		downloadThread.getQueue().put(url, location);
	}

}
