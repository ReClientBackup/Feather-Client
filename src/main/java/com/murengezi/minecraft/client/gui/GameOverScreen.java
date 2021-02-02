package com.murengezi.minecraft.client.gui;

import java.io.IOException;

import com.murengezi.minecraft.client.gui.GuiButton;
import com.murengezi.minecraft.client.gui.MainMenuScreen;
import com.murengezi.minecraft.client.gui.Screen;
import com.murengezi.minecraft.client.gui.Singleplayer.YesNoCallback;
import com.murengezi.minecraft.client.gui.YesNoScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.EnumChatFormatting;

public class GameOverScreen extends Screen {

    private static final int RESPAWN = 0;
    private static final int TITLE = 1;
    private static final int LEAVE = 2;

    @Override
    public void initGui() {
        if (!getWorld().getWorldInfo().isHardcoreModeEnabled()) {
            addButton(new GuiButton(RESPAWN, this.width / 2 - 100, this.height / 4 + 72, I18n.format("deathScreen.respawn")));
            addButton(new GuiButton(TITLE, this.width / 2 - 100, this.height / 4 + 96, I18n.format("deathScreen.titleScreen")));
        } else {
            if (getMc().isIntegratedServerRunning()) {
                addButton(new GuiButton(TITLE, this.width / 2 - 100, this.height / 4 + 96, I18n.format("deathScreen.titleScreen")));
            } else {
                addButton(new GuiButton(LEAVE, this.width / 2 - 100, this.height / 4 + 96, I18n.format("deathScreen.leaveServer")));
            }
        }

        super.initGui();
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        switch (button.getId()) {
            case RESPAWN:
                getPlayer().respawnPlayer();
                changeScreen(null);
                break;
            case TITLE:
                confirmClicked(true, TITLE);
                break;
            case LEAVE:
                YesNoScreen yesNoScreen = new YesNoScreen(this, I18n.format("deathScreen.quit.confirm"), "", I18n.format("deathScreen.titleScreen"), I18n.format("deathScreen.respawn"), 0);
                changeScreen(yesNoScreen);
                break;
        }
        super.actionPerformed(button);
    }

    @Override
    public void confirmClicked(boolean result, int id) {
        if (result) {
            getWorld().sendQuittingDisconnectingPacket();
            getMc().loadWorld(null);
            changeScreen(new MainMenuScreen());
        } else {
            getPlayer().respawnPlayer();
            changeScreen(null);
        }
        super.confirmClicked(result, id);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawGradientRect(0, 0, this.width, this.height, 1615855616, -1602211792);
        GlStateManager.pushMatrix();
        GlStateManager.scale(2.0F, 2.0F, 2.0F);
        boolean hardcore = getWorld().getWorldInfo().isHardcoreModeEnabled();
        String title = hardcore ? I18n.format("deathScreen.title.hardcore") : I18n.format("deathScreen.title");
        getFr().drawCenteredString(title, this.width / 2 / 2, 30, 16777215);
        GlStateManager.popMatrix();

        if (hardcore) {
            getFr().drawCenteredString(I18n.format("deathScreen.hardcoreInfo"), this.width / 2, 144, 16777215);
        }

        getFr().drawCenteredString(I18n.format("deathScreen.score") + ": " + EnumChatFormatting.YELLOW + getPlayer().getScore(), this.width / 2, 100, 16777215);
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

}
