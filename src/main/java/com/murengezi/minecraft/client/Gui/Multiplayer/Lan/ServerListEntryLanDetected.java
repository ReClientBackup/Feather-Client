package com.murengezi.minecraft.client.Gui.Multiplayer.Lan;

import com.murengezi.feather.Feather;
import com.murengezi.feather.Util.MinecraftUtils;
import com.murengezi.minecraft.client.Gui.GuiListExtended;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import org.apache.commons.io.FilenameUtils;

/**
 * @author Tobias Sjöblom
 * Created on 2021-01-15 at 14:40
 */
public class ServerListEntryLanDetected extends MinecraftUtils implements GuiListExtended.IGuiListEntry {

    private final LanScreen parentScreen;
    protected final LanServerDetector.LanServer lanServer;
    private long field_148290_d = 0L;

    protected ServerListEntryLanDetected(LanScreen parentScreen, LanServerDetector.LanServer lanServer) {
        this.parentScreen = parentScreen;
        this.lanServer = lanServer;
    }

    public void drawEntry(int slotIndex, int x, int y, int listWidth, int slotHeight, int mouseX, int mouseY, boolean isSelected) {
        //getFr().drawString(I18n.format("lanServer.title"), x + 32 + 3, y + 1, 16777215);
        getFr().drawString(this.lanServer.getServerMotd(), x + 32 + 3, y + 1, 16777215);

        getFr().drawString(getMc().gameSettings.hideServerAddress ? I18n.format("selectServer.hiddenAddress") : this.lanServer.getServerIpPort(), x + 32 + 3, y + 12, 8421504);


        String url = "https://crafatar.com/avatars/" + this.lanServer.getLanServerUUID() + "?size=8.png";
        Feather.getImageManager().drawImageFromUrl(FilenameUtils.getBaseName(url), url, x, y, 32, 32);
    }

    /**
     * Returns true if the mouse has been pressed on this control.
     */
    public boolean mousePressed(int slotIndex, int p_148278_2_, int p_148278_3_, int p_148278_4_, int p_148278_5_, int p_148278_6_) {
        this.parentScreen.selectServer(slotIndex);

        if (Minecraft.getSystemTime() - this.field_148290_d < 250L) {
            this.parentScreen.connectToSelected();
        }

        this.field_148290_d = Minecraft.getSystemTime();
        return false;
    }

    public void setSelected(int p_178011_1_, int p_178011_2_, int p_178011_3_) {}

    public void mouseReleased(int slotIndex, int x, int y, int mouseEvent, int relativeX, int relativeY) {}

    public LanServerDetector.LanServer getLanServer()
    {
        return this.lanServer;
    }
}
