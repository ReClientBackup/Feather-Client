package com.murengezi.chocolate.Util;

import com.murengezi.chocolate.API.MurengeziAPI;
import com.murengezi.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.ThreadDownloadImageData;
import net.minecraft.client.renderer.texture.ITextureObject;
import net.minecraft.client.renderer.texture.SimpleTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.util.ResourceLocation;
import net.optifine.player.CapeImageBuffer;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.regex.Pattern;

/**
 * @author Tobias Sjöblom
 * Created on 2021-01-22 at 11:37
 */
public class CapeUtils extends MinecraftUtils{

	private static final Pattern PATTERN_USERNAME = Pattern.compile("[a-zA-Z0-9_]+");

	public static void downloadCape(AbstractClientPlayer player) {
		String name = player.getNameClear();
		String uuid = player.getUniqueID().toString();

		/*if (name != null && !name.isEmpty() && !name.contains("\u0000") && PATTERN_USERNAME.matcher(name).matches()) {
			String murengeziCape = MurengeziAPI.getPlayerCape(uuid);
			String url = murengeziCape != null ? murengeziCape : "http://s.optifine.net/capes/" + name + ".png";
			ResourceLocation resourcelocation = new ResourceLocation("chocolate", "capes/" + name);
			TextureManager textureManager = getMc().getTextureManager();
			ITextureObject iTextureObject = textureManager.getTexture(resourcelocation);

			if (iTextureObject instanceof ThreadDownloadImageData) {
				ThreadDownloadImageData threadDownloadImageData = (ThreadDownloadImageData)iTextureObject;

				if (threadDownloadImageData.imageFound != null) {
					if (threadDownloadImageData.imageFound) {
						player.setCapeLocation(resourcelocation);
					}
					return;
				}
			}

			CapeImageBuffer capeImageBuffer = new CapeImageBuffer(player, resourcelocation);
			ThreadDownloadImageData threadDownloadImageData = new ThreadDownloadImageData(null, url, null, capeImageBuffer);
			threadDownloadImageData.pipeline = true;
			textureManager.loadTexture(resourcelocation, threadDownloadImageData);
		}*/
	}

	public static BufferedImage parseCape(BufferedImage image) {
		int width = 64;
		int height = 32;
		int imageWidth = image.getWidth();

		for (int imageHeight = image.getHeight(); width < imageWidth || height < imageHeight; height *= 2) {
			width *= 2;
		}

		BufferedImage bufferedimage = new BufferedImage(width, height, 2);
		Graphics graphics = bufferedimage.getGraphics();
		graphics.drawImage(image, 0, 0, null);
		graphics.dispose();
		return bufferedimage;
	}

	public static void reloadCape(AbstractClientPlayer player) {
		String name = player.getNameClear();
		ResourceLocation resourceLocation = new ResourceLocation("chocolate", "capes/" + name);
		TextureManager textureManager = getMc().getTextureManager();
		ITextureObject iTextureObject = textureManager.getTexture(resourceLocation);

		if (iTextureObject instanceof SimpleTexture) {
			SimpleTexture simpleTexture = (SimpleTexture)iTextureObject;
			simpleTexture.deleteGlTexture();
			textureManager.deleteTexture(resourceLocation);
		}

		player.setCapeLocation(null);
		//downloadCape(player);
	}
}
