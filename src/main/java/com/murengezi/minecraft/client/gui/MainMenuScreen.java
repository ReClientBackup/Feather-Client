package com.murengezi.minecraft.client.gui;

import com.murengezi.feather.Feather;
import com.murengezi.minecraft.client.gui.BrowseServers.BrowseScreen;
import com.murengezi.minecraft.client.gui.Multiplayer.MultiplayerScreen;
import com.murengezi.minecraft.client.gui.Options.OptionsScreen;
import com.murengezi.minecraft.client.gui.WorldSelection.WorldSelectionScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.io.FilenameUtils;

import java.io.IOException;

/**
 * @author Tobias Sjöblom
 * Created on 2021-01-12 at 22:34
 */
public class MainMenuScreen extends Screen {

    private static final int SINGLEPLAYER = 0;
    private static final int MULTIPLAYER = 1;
    private static final int BROWSE = 2;
    private static final int OPTIONS = 3;
    private static final int QUIT = 4;

    @Override
    public void initGui() {
        int yPos = this.height / 4 + 48;
        addButton(new GuiButton(SINGLEPLAYER, this.width / 2 - 100,  yPos, I18n.format("menu.singleplayer")));
        addButton(new GuiButton(MULTIPLAYER, this.width / 2 - 100,  yPos + 24, I18n.format("menu.multiplayer")));
        addButton(new GuiButton(BROWSE, this.width / 2 - 100,  yPos + 24 * 2, "Browse Servers..."));
        addButton(new GuiButton(OPTIONS, this.width / 2 - 100,  yPos + 72 + 12, 96, 20, I18n.format("menu.options")));
        addButton(new GuiButton(QUIT, this.width / 2 + 4,  yPos + 72 + 12, 96, 20, I18n.format("menu.quit")));

        getButton(BROWSE).setEnabled(false);
        super.initGui();
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        switch (button.getId()) {
            case SINGLEPLAYER:
                changeScreen(new WorldSelectionScreen(this));
                break;
            case MULTIPLAYER:
                changeScreen(new MultiplayerScreen(this));
                break;
            case BROWSE:
                changeScreen(new BrowseScreen(this));
                break;
            case OPTIONS:
                changeScreen(new OptionsScreen(this, mc.gameSettings));
                break;
            case QUIT:
                mc.shutdown();
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
        drawDefaultBackground(mouseX, mouseY, 240);

        drawRect(width / 2 - 105, this.height / 4 + 43, this.width / 2 + 105, this.height / 4 + 157, Integer.MIN_VALUE);
        GlStateManager.color(1.0f, 1.0f, 1.0f);
        mc.getTextureManager().bindTexture(new ResourceLocation("textures/gui/title/minecraft.png"));
        int x = this.width / 2 - 274 / 2;
        drawTexturedModalRect(x, 30, 0, 0, 155, 44);
        drawTexturedModalRect(x + 155, 30, 0, 45, 155, 44);

        mc.getTextureManager().bindTexture(new ResourceLocation("feather/edition.png"));
        drawModalRectWithCustomSizedTexture(x + 88, 67, 0, 0, 98, 14, 128, 16);

        String playerName = mc.getSession().getUsername();
        drawRect(0, 0, 20 + 3 + mc.fontRendererObj.getStringWidth(playerName) + 8 + 3, 20, Integer.MIN_VALUE);
        boolean premium = !mc.getSession().getToken().equals("") && !mc.getSession().getToken().equals("0");
        String uuid = premium ? mc.getSession().getPlayerID() : "606e2ff0ed7748429d6ce1d3321c7838";
        String url = "https://crafatar.com/avatars/" + uuid + "?size=8.png";
        Feather.getImageManager().drawImageFromUrl(FilenameUtils.getBaseName(url), url, 2, 2, 16, 16);
        mc.fontRendererObj.drawStringWithShadow(playerName, 21, 6, 0xffffffff);

        GlStateManager.pushMatrix();
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
        mc.getTextureManager().bindTexture(new ResourceLocation("feather/sign-in.png"));
        drawModalRectWithCustomSizedTexture(20 + 3 + mc.fontRendererObj.getStringWidth(playerName), 6, 0, 0, 8, 8, 8, 8);
        GlStateManager.popMatrix();

        fontRendererObj.drawStringWithShadow(EnumChatFormatting.BOLD + "\"" + EnumChatFormatting.RESET + Feather.getThemeManager().getActiveTheme().getName() + EnumChatFormatting.BOLD + "\"" + EnumChatFormatting.RESET + " by " + Feather.getThemeManager().getActiveTheme().getAuthor(), 2, this.height - 10, 0xffffffff);

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int mouseButton) {
        super.mouseReleased(mouseX, mouseY, mouseButton);
    }


}
