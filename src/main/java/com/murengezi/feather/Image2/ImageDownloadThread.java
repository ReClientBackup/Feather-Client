package com.murengezi.feather.Image2;

import com.murengezi.feather.Feather;
import com.murengezi.feather.Image.Image;
import net.minecraft.util.ResourceLocation;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Tobias Sjöblom
 * Created on 2021-01-26 at 14:43
 */
public class ImageDownloadThread extends Thread {

	private Map<String, ResourceLocation> queue;
	private boolean running = true;

	public ImageDownloadThread() {
		queue = new HashMap<>();
		start();
	}

	@Override
	public void run() {
		while (isRunning()) {
			if (!getQueue().isEmpty()) {
				getQueue().forEach((url, location) -> {
					try {
						if (url != null && !url.isEmpty()) {
							HttpURLConnection httpURLConnection = (HttpURLConnection)(new URL(url)).openConnection();
							httpURLConnection.setRequestProperty("User-Agent", "Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; en-US; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2");
							httpURLConnection.setRequestProperty("Cookie", "foo=bar");
							httpURLConnection.connect();
							if (httpURLConnection.getResponseCode() == 200) {
								BufferedImage bufferedImage = ImageIO.read(httpURLConnection.getInputStream());
								if (bufferedImage != null) {
									Feather.getImageManager().getLoaded().put(location, bufferedImage);
									Feather.getImageManager().getImages().put(url, new Image(location));
									Feather.getImageManager().getQueue().remove(url);
								}
							}
						}
					} catch (IOException e) {
						e.printStackTrace();
					}
				});
			}
		}
		super.run();
	}

	public boolean isRunning() {
		return running;
	}

	public void setRunning(boolean running) {
		this.running = running;
	}

	public Map<String, ResourceLocation> getQueue() {
		return queue;
	}
}
