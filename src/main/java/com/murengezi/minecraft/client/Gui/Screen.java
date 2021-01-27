package com.murengezi.minecraft.client.Gui;

import com.murengezi.feather.Feather;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import org.lwjgl.opengl.GL11;

/**
 * @author Tobias Sjöblom
 * Created on 2021-01-14 at 15:34
 */
public abstract class Screen extends GuiScreen {

    public void drawDefaultBackground(int mouseX, int mouseY, int delta) {
        if (mc.theWorld == null) {
            //drawGradientRect(0, 0, this.width, this.height, 0xfff4f1d6, 0xff333333);
            drawGradientRect(0, 0, this.width, this.height, Feather.getThemeManager().getActiveTheme().getColor().getRGB(), 0xff333333);
            Feather.getParticleManager().render(mouseX, mouseY);
            Feather.getParticleManager().tick(delta);
        }
    }

    public void addButton(GuiButton guiButton) {
        this.buttonList.add(guiButton);
    }

    public GuiButton getButton(int id) {
        return this.buttonList.get(id);
    }

    public void changeScreen(GuiScreen guiScreen) {
        mc.displayGuiScreen(guiScreen);
    }

    public void saveSettings() {
        mc.gameSettings.saveOptions();
    }

    protected void scissorBox(float x, float y, float x2, float y2, ScaledResolution sr) {
        final int factor = sr.getScaleFactor();
        GL11.glScissor((int) (x * factor), (int) ((sr.getScaledHeight() - y2) * factor), (int) ((x2 - x) * factor), (int) ((y2 -y) * factor));
    }

}
