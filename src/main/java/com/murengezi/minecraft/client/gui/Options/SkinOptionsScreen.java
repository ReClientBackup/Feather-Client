package com.murengezi.minecraft.client.gui.Options;

import com.murengezi.minecraft.client.gui.GuiButton;
import com.murengezi.minecraft.client.gui.Screen;
import com.murengezi.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EnumPlayerModelParts;
import net.minecraft.util.EnumChatFormatting;
import net.optifine.gui.GuiButtonOF;
import net.optifine.gui.GuiScreenCapeOF;

import java.io.IOException;
import java.util.Arrays;

/**
 * @author Tobias Sjöblom
 * Created on 2021-01-14 at 14:42
 */
public class SkinOptionsScreen extends Screen {

    private final Screen previousScreen;

    private static final int CAPE = 0, DONE = 1;

    public SkinOptionsScreen(Screen previousScreen) {
        this.previousScreen = previousScreen;
    }

    @Override
    public void initGui() {
        Arrays.stream(EnumPlayerModelParts.values()).forEach(playerModelParts -> {
            addButton(new Button( 2 + playerModelParts.getPartId(), this.width / 2 - 155 + playerModelParts.getPartId() % 2 * 160, this.height / 6 + 42 + 24 * (playerModelParts.getPartId() >> 1), 150, 20, this, playerModelParts));
        });

        this.buttonList.add(new GuiButtonOF(CAPE, this.width / 2 + 5, this.height / 6 + 42 + 24 * (EnumPlayerModelParts.values().length >> 1), 150, 20, I18n.format("of.options.skinCustomisation.ofCape")));
        addButton(new GuiButton(DONE, this.width / 2 - 75, this.height / 6 + 24 + 24 * (EnumPlayerModelParts.values().length + 4 >> 1), 150, 20, I18n.format("gui.done")));

        super.initGui();
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        switch (button.getId()) {
            case CAPE:
                changeScreen(new GuiScreenCapeOF(this));
                break;
            case DONE:
                saveSettings();
                changeScreen(getPreviousScreen());
                break;
            default:
                if (button instanceof Button) {
                    EnumPlayerModelParts playerModelParts = ((Button)button).getPlayerModelParts();
                    getMc().gameSettings.switchModelPartEnabled(playerModelParts);
                    button.displayString = getLabel(playerModelParts);
                }
                break;
        }
        super.actionPerformed(button);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawWorldBackground(mouseX, mouseY, 60);

        drawRect(this.width / 2 - 160, this.height / 6 + 15, this.width / 2 + 160, this.height / 6 + 169, Integer.MIN_VALUE);
        getFr().drawCenteredString(EnumChatFormatting.UNDERLINE + I18n.format("options.skinCustomisation.title"), this.width / 2, this.height / 6 + 20, 0xffffff);
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    private String getLabel(EnumPlayerModelParts playerModelParts) {
        return playerModelParts.getChatComponent().getFormattedText() + ": " + I18n.format("options." + (getMc().gameSettings.getModelParts().contains(playerModelParts) ? "on" : "off"));
    }

    public Screen getPreviousScreen() {
        return previousScreen;
    }

    class Button extends GuiButton {

        private final EnumPlayerModelParts playerModelParts;

        public Button(int id, int x, int y, int width, int height, SkinOptionsScreen customizeSkinScreen, EnumPlayerModelParts playerModelParts) {
            super(id, x, y, width, height, customizeSkinScreen.getLabel(playerModelParts));
            this.playerModelParts = playerModelParts;
        }

        public EnumPlayerModelParts getPlayerModelParts() {
            return playerModelParts;
        }
    }

}
