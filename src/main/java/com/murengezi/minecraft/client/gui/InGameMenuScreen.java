package com.murengezi.minecraft.client.gui;

import com.murengezi.minecraft.client.gui.Multiplayer.MultiplayerScreen;
import com.murengezi.minecraft.client.gui.Options.OptionsScreen;
import net.minecraft.client.gui.achievement.GuiAchievements;
import net.minecraft.client.gui.achievement.GuiStats;
import net.minecraft.client.resources.I18n;

import java.io.IOException;

/**
 * @author Tobias Sjöblom
 * Created on 2021-01-15 at 09:58
 */
public class InGameMenuScreen extends Screen {

    private static final int RETURN = 0;
    private static final int STATS = 1;
    private static final int ACHIEVEMENTS = 2;
    private static final int MULTIPLAYER = 3;
    private static final int OPTIONS = 4;
    private static final int LAN = 5;
    private static final int DISCONNECT = 6;

    @Override
    public void initGui() {
        addButton(new GuiButton(RETURN, this.width / 2 - 100, this.height / 4 + 8, I18n.format("menu.returnToGame")));
        addButton(new GuiButton(STATS, this.width / 2 - 100, this.height / 4 + 32, 98, 20, I18n.format("gui.achievements")));
        addButton(new GuiButton(ACHIEVEMENTS, this.width / 2 + 2, this.height / 4 + 32, 98, 20, I18n.format("gui.stats")));
        addButton(new GuiButton(MULTIPLAYER, this.width / 2 - 100, this.height / 4 + 56, I18n.format("menu.multiplayer")));
        addButton(new GuiButton(OPTIONS, this.width / 2 - 100, this.height / 4 + 80, 98, 20, I18n.format("menu.options")));
        addButton(new GuiButton(LAN, this.width / 2 + 2, this.height / 4 + 80, 98, 20, I18n.format("menu.shareToLan")));
        addButton(new GuiButton(DISCONNECT, this.width / 2 - 100, this.height / 4 + 104, I18n.format("menu.disconnect")));

        getButton(LAN).setEnabled(getMc().isSingleplayer() && !getMc().getIntegratedServer().getPublic());
        super.initGui();
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        switch (button.getId()) {
            case RETURN:
                changeScreen(null);
                getMc().setIngameFocus();
                break;
            case STATS:
                changeScreen(new GuiStats(this, getPlayer().getStatFileWriter()));
                break;
            case ACHIEVEMENTS:
                changeScreen(new GuiAchievements(this, getPlayer().getStatFileWriter()));
                break;
            case MULTIPLAYER:
                changeScreen(new MultiplayerScreen(this));
                break;
            case OPTIONS:
                changeScreen(new OptionsScreen(this, getMc().gameSettings));
                break;
            case LAN:
                changeScreen(new ShareToLanScreen(this));
                break;
            case DISCONNECT:
                button.setEnabled(false);
                getWorld().sendQuittingDisconnectingPacket();
                getMc().loadWorld(null);
                changeScreen(new MainMenuScreen());
                break;
        }
        super.actionPerformed(button);
    }

    @Override
    public void updateScreen() {
        super.updateScreen();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground(mouseX, mouseY, 120);
        super.drawScreen(mouseX, mouseY, partialTicks);
    }
}
