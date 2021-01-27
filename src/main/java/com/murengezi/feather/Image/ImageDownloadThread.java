package com.murengezi.feather.Image;

import com.murengezi.feather.Feather;
import net.minecraft.util.ResourceLocation;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * @author Tobias Sjöblom
 * Created on 2021-01-13 at 16:50
 */
public class ImageDownloadThread extends Thread {

    private String location;
    private String url;

    public ImageDownloadThread(String location, String url) {
        this.location = location;
        this.url = url;
        start();
    }

    @Override
    public void run() {
        try {
            ResourceLocation resourceLocation = new ResourceLocation("images/" + getLocation());
            if (getUrl() != null && !getUrl().isEmpty()) {
                HttpURLConnection httpURLConnection = (HttpURLConnection)(new URL(getUrl())).openConnection();
                httpURLConnection.setRequestProperty("User-Agent", "Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; en-US; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2");
                httpURLConnection.setRequestProperty("Cookie", "foo=bar");
                httpURLConnection.connect();
                if (httpURLConnection.getResponseCode() == 200) {
                    BufferedImage bufferedImage = ImageIO.read(httpURLConnection.getInputStream());
                    if (bufferedImage != null) {
                        Feather.getImageManager().getLoaded().put(resourceLocation, bufferedImage);
                        Feather.getImageManager().getImages().put(getUrl(), new Image(resourceLocation));
                        Feather.getImageManager().getQueue().remove(getUrl());
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getLocation() {
        return location;
    }

    public String getUrl() {
        return url;
    }
}
