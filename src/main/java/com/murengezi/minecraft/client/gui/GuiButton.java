package com.murengezi.minecraft.client.gui;

import com.murengezi.chocolate.Chocolate;
import com.murengezi.minecraft.client.Minecraft;
import com.murengezi.minecraft.client.audio.PositionedSoundRecord;
import com.murengezi.minecraft.client.audio.SoundHandler;
import net.minecraft.util.ResourceLocation;

import java.awt.*;

public class GuiButton extends GUI {

    protected static final ResourceLocation buttonTextures = new ResourceLocation("textures/gui/widgets.png");
    private int width;
    private int height;
    private int x;
    private int y;
    public String displayString;
    private int id;
    private boolean enabled, visible, hovered, wasHovered;


    public int boxColorRed = 0, boxColorGreen = 0, boxColorBlue = 0;
    private long lastHover;

    public GuiButton(int buttonId, int x, int y, String buttonText) {
        this(buttonId, x, y, 200, 20, buttonText);
    }

    public GuiButton(int buttonId, int x, int y, int widthIn, int heightIn, String buttonText) {
        this.enabled = true;
        this.visible = true;
        this.id = buttonId;
        this.x = x;
        this.y = y;
        this.width = widthIn;
        this.height = heightIn;
        this.displayString = buttonText;
        this.lastHover = 0l;
    }

    public void drawButton(int mouseX, int mouseY) {
        if (isVisible()) {
            setHovered((mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height) && isEnabled());

            if (isHovered() != this.wasHovered) {
                this.wasHovered = isHovered();
                this.lastHover = System.currentTimeMillis();
            }

            Color color = Chocolate.getThemeManager().getActiveTheme().getColor();

            if (isHovered()) {
                boxColorRed = Math.min(color.getRed(), (int)((System.currentTimeMillis() - this.lastHover)));
                boxColorGreen = Math.min(color.getGreen(), (int)((System.currentTimeMillis() - this.lastHover)));
                boxColorBlue = Math.min(color.getBlue(), (int)((System.currentTimeMillis() - this.lastHover)));
            } else {
                boxColorRed = Math.max(0, color.getRed() - (int)((System.currentTimeMillis() - this.lastHover)));
                boxColorGreen = Math.max(0, color.getGreen() - (int)((System.currentTimeMillis() - this.lastHover)));
                boxColorBlue = Math.max(0, color.getBlue() - (int)((System.currentTimeMillis() - this.lastHover)));
            }

            drawRect(this.x, this.y, this.x + this.width, this.y + this.height, Integer.MIN_VALUE + (boxColorRed << 16) + (boxColorGreen << 8) + boxColorBlue);
            int colorInt = 0xffffff;

            if (!this.enabled) {
                colorInt = 0xA0A0A0;
            } else if (this.hovered) {
                colorInt = color.getRGB();
            }

            this.drawCenteredString(getFr(), this.displayString, this.x + this.width / 2, this.y + (this.height - 8) / 2, colorInt);
            this.mouseDragged(getMc(), mouseX, mouseY);
        }
    }

    protected void mouseDragged(Minecraft mc, int mouseX, int mouseY) {}

    public void mouseReleased(int mouseX, int mouseY) {}

    public boolean mousePressed(Minecraft mc, int mouseX, int mouseY) {
        return this.enabled && this.visible && mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
    }

    public void drawButtonForegroundLayer(int mouseX, int mouseY) {}

    public void playPressSound(SoundHandler soundHandlerIn) {
        soundHandlerIn.playSound(PositionedSoundRecord.create(new ResourceLocation("gui.button.press"), 1.0F));
    }

    public int getButtonWidth()
    {
        return this.width;
    }

    public void setWidth(int width)
    {
        this.width = width;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public boolean isHovered() {
        return hovered;
    }

    public void setHovered(boolean hovered) {
        this.hovered = hovered;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
