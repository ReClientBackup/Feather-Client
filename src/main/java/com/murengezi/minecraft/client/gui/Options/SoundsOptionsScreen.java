package com.murengezi.minecraft.client.gui.Options;

import com.murengezi.minecraft.client.gui.GuiButton;
import com.murengezi.minecraft.client.gui.Screen;
import com.murengezi.minecraft.client.Minecraft;
import com.murengezi.minecraft.client.audio.PositionedSoundRecord;
import com.murengezi.minecraft.client.audio.SoundCategory;
import com.murengezi.minecraft.client.audio.SoundHandler;
import com.murengezi.minecraft.client.renderer.GlStateManager;
import com.murengezi.minecraft.client.resources.I18n;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;

import java.io.IOException;
import java.util.Arrays;

/**
 * @author Tobias Sjöblom
 * Created on 2021-01-14 at 17:45
 */
public class SoundsOptionsScreen extends Screen {

    private final Screen previousScreen;

    public static final int DONE = 0;

    public SoundsOptionsScreen(Screen previousScreen) {
        this.previousScreen = previousScreen;
    }

    @Override
    public void initGui() {
        addButton(new Button(SoundCategory.MASTER.getCategoryId() + 1, this.width / 2 - 155, this.height / 6 + 42, SoundCategory.MASTER, this));

        Arrays.stream(SoundCategory.values()).forEach(soundCategory -> {
            if (soundCategory != SoundCategory.MASTER) {
                addButton(new Button(soundCategory.getCategoryId() + 1, this.width / 2 - 155 + ((soundCategory.getCategoryId() + 1) % 2 * 160),this.height / 6 + 42 + 24 * ((soundCategory.getCategoryId() + 1) >> 1), soundCategory, this));
            }
        });

        addButton(new GuiButton(DONE, this.width / 2 - 75, this.height / 6 + 168, 150, 20, I18n.format("gui.done")));
        super.initGui();
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        if (button.isEnabled()) {
            switch (button.getId()) {
                case DONE:
                    saveSettings();
                    changeScreen(previousScreen);
                    break;
                default:
                    break;
            }
        }
        super.actionPerformed(button);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawWorldBackground(mouseX, mouseY, 60);
        drawRect(this.width / 2 - 160, this.height / 6 + 15, this.width / 2 + 160, this.height / 6 + 193, Integer.MIN_VALUE);
        drawCenteredString(getFr(), EnumChatFormatting.UNDERLINE + I18n.format("options.sounds.title"), this.width / 2, this.height / 6 + 20, 0xffffffff);
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    protected String getSoundVolume(SoundCategory soundCategory) {
        float soundLevel = getGs().getSoundLevel(soundCategory);
        return soundLevel == 0.0f ? I18n.format("options.off") : (int)(soundLevel * 100.0f) + "%";
    }

    class Button extends GuiButton {

        private final SoundsOptionsScreen soundsOptionsScreen;
        private final SoundCategory soundCategory;
        private final String label;
        private float soundLevel;
        private boolean b;

        public Button(int id, int x, int y, SoundCategory soundCategory, SoundsOptionsScreen soundsOptionsScreen) {
            super(id, x, y, soundCategory == SoundCategory.MASTER ? 310 : 150, 20, "");
            this.soundsOptionsScreen = soundsOptionsScreen;
            this.soundCategory = soundCategory;
            this.label = I18n.format("soundCategory." + soundCategory.getCategoryName());
            this.displayString = this.label + ": " + soundsOptionsScreen.getSoundVolume(soundCategory);
            this.soundLevel = getGs().getSoundLevel(soundCategory);
        }

        @Override
        protected void mouseDragged(Minecraft mc, int mouseX, int mouseY) {
            if (this.isVisible()) {
                if (this.b) {
                    setSoundLevelFromMouseX(mouseX);
                }

                GlStateManager.colorAllMax();
                drawRect(getX() + (int) (this.soundLevel * (float)(this.getWidth() - 8)), this.getY(), getX() + (int) (this.soundLevel * (float)(this.getWidth() - 8)) + 8, this.getY() + this.getHeight(), Integer.MIN_VALUE + (boxColorRed << 16) + (boxColorGreen << 8) + boxColorBlue);
            }
        }

        @Override
        public boolean mousePressed(Minecraft mc, int mouseX, int mouseY) {
            if (super.mousePressed(mc, mouseX, mouseY)) {
                setSoundLevelFromMouseX(mouseX);
                this.b = true;
                return true;
            }
            return false;
        }

        public void setSoundLevelFromMouseX(int mouseX) {
            this.soundLevel = (float) (mouseX - (this.getX() + 4)) / (float) (this.getWidth() - 8);
            this.soundLevel = MathHelper.clamp_float(this.soundLevel, 0.0f, 1.0f);
            getMc().gameSettings.setSoundLevel(soundCategory, this.soundLevel);
            this.displayString = this.label + ": " + soundsOptionsScreen.getSoundVolume(soundCategory);
        }

        @Override
        public void playPressSound(SoundHandler soundHandlerIn) {}

        @Override
        public void mouseReleased(int mouseX, int mouseY) {
            if (this.b) {
                getMc().getSoundHandler().playSound(PositionedSoundRecord.create(new ResourceLocation("gui.button.press"), 1.0F));
                saveSettings();
                this.b = false;
            }
        }
    }
}
