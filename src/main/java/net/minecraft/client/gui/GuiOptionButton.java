package net.minecraft.client.gui;

import com.murengezi.minecraft.client.gui.GuiButton;
import com.murengezi.minecraft.client.settings.GameSettings;

public class GuiOptionButton extends GuiButton {

    private final GameSettings.Options options;

    public GuiOptionButton(int buttonId, int x, int y, String buttonText) {
        this(buttonId, x, y, null, buttonText);
    }

    public GuiOptionButton(int buttonId, int x, int y, int width, int height, String buttonText) {
        super(buttonId, x, y, width, height, buttonText);
        this.options = null;
    }

    public GuiOptionButton(int buttonId, int x, int y, GameSettings.Options options, String buttonText) {
        super(buttonId, x, y, 150, 20, buttonText);
        this.options = options;
    }

    public GameSettings.Options getOptions() {
        return this.options;
    }
}
