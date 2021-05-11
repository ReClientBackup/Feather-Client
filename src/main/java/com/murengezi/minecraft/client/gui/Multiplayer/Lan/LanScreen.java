package com.murengezi.minecraft.client.gui.Multiplayer.Lan;

import com.murengezi.minecraft.client.gui.GuiButton;
import com.murengezi.minecraft.client.gui.GuiListExtended;
import com.murengezi.minecraft.client.gui.Multiplayer.ConnectingScreen;
import com.murengezi.minecraft.client.gui.Screen;
import com.murengezi.minecraft.client.gui.ScaledResolution;
import com.murengezi.minecraft.client.multiplayer.ServerData;
import com.murengezi.minecraft.client.resources.I18n;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import java.io.IOException;
import java.util.List;

/**
 * @author Tobias Sjöblom
 * Created on 2021-01-15 at 14:12
 */
public class LanScreen extends Screen {

    private final Screen previousScreen;
    private ServerData selectedServer;
    private LanSelectionList lanSelectionList;
    private LanServerDetector.LanServerList lanServerList;
    private LanServerDetector.ThreadLanServerFind lanServerDetector;
    private boolean initialized;
    private String hoveringText;


    private static final int SELECT = 0;
    private static final int REFRESH = 1;
    private static final int CANCEL = 2;

    public LanScreen(Screen previousScreen) {
        this.previousScreen = previousScreen;
    }

    @Override
    public void initGui() {
        Keyboard.enableRepeatEvents(true);

        if (!this.initialized) {
            this.initialized = true;

            this.lanServerList = new LanServerDetector.LanServerList();

            try {
                this.lanServerDetector = new LanServerDetector.ThreadLanServerFind(this.lanServerList);
                this.lanServerDetector.start();
            } catch (Exception exception) {
                exception.printStackTrace();
            }

            this.lanSelectionList = new LanSelectionList(this, this.width, this.height, 32, this.height - 64, 36);

        } else {
            this.lanSelectionList.setDimensions(this.width, this.height, 32, this.height - 64);
        }

        addButton(new GuiButton(SELECT, this.width / 2 - 154, this.height - 52, 100, 20, I18n.format("Join LAN World")));
        addButton(new GuiButton(REFRESH, this.width / 2 - 50, this.height - 52, 100, 20, I18n.format("selectServer.refresh")));
        addButton(new GuiButton(CANCEL, this.width / 2 + 54, this.height - 52, 100, 20, I18n.format("gui.cancel")));
        this.selectServer(this.lanSelectionList.getSelectedSlotIndex());

        super.initGui();
    }

    @Override
    public void handleMouseInput() throws IOException {
        this.lanSelectionList.handleMouseInput();
        super.handleMouseInput();
    }

    @Override
    public void onGuiClosed() {
        Keyboard.enableRepeatEvents(false);
        if (this.lanServerDetector != null) {
            this.lanServerDetector.interrupt();
            this.lanServerDetector = null;
        }
        super.onGuiClosed();
    }

    @Override
    public void updateScreen() {
        if (this.lanServerList.getWasUpdated()) {
            List<LanServerDetector.LanServer> list = this.lanServerList.getLanServers();
            this.lanServerList.setWasNotUpdated();
            this.lanSelectionList.loadLanEntries(list);
        }
        super.updateScreen();
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        if (button.isEnabled()) {
            switch (button.getId()) {
                case SELECT:
                    this.connectToSelected();
                    break;
                case REFRESH:
                    this.refreshServerList();
                    break;
                case CANCEL:
                    changeScreen(this.previousScreen);
                    break;
            }
        }
        super.actionPerformed(button);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawWorldBackground(mouseX, mouseY, 60);
        scissorBox(lanSelectionList.getLeft(), lanSelectionList.getTop(), lanSelectionList.getRight(), lanSelectionList.getBottom(), new ScaledResolution());
        GL11.glEnable(GL11.GL_SCISSOR_TEST);
        this.lanSelectionList.drawScreen(mouseX, mouseY, partialTicks);
        GL11.glDisable(GL11.GL_SCISSOR_TEST);
        drawRect(0, 0, lanSelectionList.getWidth(), lanSelectionList.getTop(), Integer.MIN_VALUE);
        drawRect(0, lanSelectionList.getBottom(), lanSelectionList.getWidth(), this.height, Integer.MIN_VALUE);
        getFr().drawCenteredString(I18n.format("LAN"), (float)this.width / 2, 20, 0xffffff);
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        this.lanSelectionList.mouseClicked(mouseX, mouseY, mouseButton);
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int mouseButton) {
        this.lanSelectionList.mouseReleased(mouseX, mouseY, mouseButton);
        super.mouseReleased(mouseX, mouseY, mouseButton);
    }

    private void refreshServerList() {
        changeScreen(new LanScreen(this.previousScreen));
    }

    public void connectToSelected() {
        GuiListExtended.IGuiListEntry guiListEntry = this.lanSelectionList.getSelectedSlotIndex() < 0 ? null : this.lanSelectionList.getListEntry(this.lanSelectionList.getSelectedSlotIndex());

        if (guiListEntry instanceof ServerListEntryLanDetected) {
            LanServerDetector.LanServer lanServer = ((ServerListEntryLanDetected)guiListEntry).getLanServer();
            this.connectToServer(new ServerData(lanServer.getServerMotd(), lanServer.getServerIpPort(), true));
        }

    }

    public void connectToServer(ServerData serverData) {
        changeScreen(new ConnectingScreen(this, serverData));
    }

    public void selectServer(int index) {
        this.lanSelectionList.setSelectedSlotIndex(index);
        GuiListExtended.IGuiListEntry guiListEntry = index < 0 ? null : this.lanSelectionList.getListEntry(index);
        getButton(SELECT).setEnabled(false);

        if (guiListEntry != null) {
            getButton(SELECT).setEnabled(true);
        }
    }

    public String getHoveringText() {
        return hoveringText;
    }

    public void setHoveringText(String hoveringText) {
        this.hoveringText = hoveringText;
    }
}
