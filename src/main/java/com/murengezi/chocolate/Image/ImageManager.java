package com.murengezi.chocolate.Image;

import com.murengezi.chocolate.Util.MinecraftUtils;
import com.murengezi.chocolate.Util.TimerUtil;
import com.murengezi.minecraft.client.gui.GUI;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.util.ResourceLocation;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Tobias Sjöblom
 * Created on 2021-01-13 at 16:23
 */
public class ImageManager extends MinecraftUtils {

    private final Map<String, Image> images;
    private final Map<ResourceLocation, BufferedImage> loaded;
    private final List<String> queue;
    private final TimerUtil timer;

    public ImageManager() {
        images = new HashMap<>();
        loaded = new HashMap<>();
        queue = new ArrayList<>();
        timer = new TimerUtil();
    }

    public void drawImageFromUrl(String location, String url, float x, float y, int width, int height) {
        try {
            checkTextures();
            if (getImages().containsKey(url)) {
                getQueue().remove(url);

                Image image = getImages().get(url);
                ResourceLocation resourceLocation = image.getLocation();
                if (resourceLocation != null && !getLoaded().containsKey(resourceLocation)) {
                    GlStateManager.pushMatrix();
                    GlStateManager.color(1f, 1f, 1f);
                    getMc().getTextureManager().bindTexture(resourceLocation);
                    GUI.drawModalRectWithCustomSizedTexture(x, y, 0, 0, width, height, width, height);
                    GlStateManager.popMatrix();
                }
            } else if (!getQueue().contains(url)) {
                getQueue().add(url);
                new ImageDownloadThread(location, url);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void checkTextures() {
        ArrayList<ResourceLocation> locations = new ArrayList<>(this.getLoaded().keySet());

        locations.forEach(location -> {
            DynamicTexture dynamictexture = new DynamicTexture(this.getLoaded().get(location));
            getMc().getTextureManager().loadTexture(location, dynamictexture);
            this.getLoaded().remove(location);
        });

        if (timer.hasPassed(60000)) {
            timer.reset();
            List<String> arraylist1 = new ArrayList<>();
            List<String> arraylist2 = new ArrayList<>(getImages().keySet());

            for (String s : arraylist2) {
                Image image = getImages().get(s);

                if (image.getTimer().hasPassed(60000)) {
                    arraylist1.add(s);
                    getMc().getTextureManager().deleteTexture(image.getLocation());
                }
            }

            for (String s1 : arraylist1) {
                getImages().remove(s1);
            }
        }
    }

    public Map<String, Image> getImages() {
        return images;
    }

    public Map<ResourceLocation, BufferedImage> getLoaded() {
        return loaded;
    }

    public List<String> getQueue() {
        return queue;
    }

    public TimerUtil getTimer() {
        return timer;
    }
}
