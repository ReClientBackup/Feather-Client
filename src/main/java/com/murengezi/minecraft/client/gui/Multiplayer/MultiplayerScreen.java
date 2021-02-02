package com.murengezi.minecraft.client.gui.Multiplayer;


import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.murengezi.minecraft.client.gui.*;
import com.murengezi.minecraft.client.gui.Multiplayer.Lan.LanScreen;
import net.minecraft.client.gui.GuiScreen;
import com.murengezi.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.multiplayer.ServerList;
import net.minecraft.client.network.OldServerPinger;
import net.minecraft.client.resources.I18n;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import java.io.IOException;

/**
 * @author Tobias Sjöblom
 * Created on 2021-01-15 at 14:12
 */
public class MultiplayerScreen extends Screen {

    private final GuiScreen previousScreen;
    private final OldServerPinger oldServerPinger = new OldServerPinger();
    private ServerData selectedServer;
    private ServerSelectionList serverListSelector;
    private ServerList serverList;
    private boolean initialized;
    private String hoveringText;
    private boolean deletingServer, addingServer, editingServer, directConnect;


    private static final int SELECT = 0;
    private static final int DIRECT = 1;
    private static final int ADD = 2;
    private static final int EDIT = 3;
    private static final int DELETE = 4;
    private static final int REFRESH = 5;
    private static final int CANCEL = 6;
    private static final int LAN = 7;

    public MultiplayerScreen(GuiScreen previousScreen) {
        this.previousScreen = previousScreen;
    }

    @Override
    public void initGui() {
        Keyboard.enableRepeatEvents(true);

        if (!this.initialized) {
            this.initialized = true;
            this.serverList = new ServerList(this.mc);
            this.serverList.loadServerList();
            this.serverListSelector = new ServerSelectionList(this, this.width, this.height, 32, this.height - 64, 36);
            this.serverListSelector.loadNormalEntries(serverList);
        } else {
            this.serverListSelector.setDimensions(this.width, this.height, 32, this.height - 64);
        }

        addButton(new GuiButton(SELECT, this.width / 2 - 154, this.height - 52, 100, 20, I18n.format("selectServer.select")));
        addButton(new GuiButton(DIRECT, this.width / 2 - 50, this.height - 52, 100, 20, I18n.format("selectServer.direct")));
        addButton(new GuiButton(ADD, this.width / 2 + 4 + 50, this.height - 52, 100, 20, I18n.format("selectServer.add")));
        addButton(new GuiButton(EDIT, this.width / 2 - 154, this.height - 28, 70, 20, I18n.format("selectServer.edit")));
        addButton(new GuiButton(DELETE, this.width / 2 - 74, this.height - 28, 70, 20, I18n.format("selectServer.delete")));
        addButton(new GuiButton(REFRESH, this.width / 2 + 4, this.height - 28, 70, 20, I18n.format("selectServer.refresh")));
        addButton(new GuiButton(CANCEL, this.width / 2 + 84, this.height - 28, 70, 20, I18n.format("gui.cancel")));
        addButton(new GuiButton(LAN, this.width - 56, 6, 50, 20, "LAN"));
        this.selectServer(this.serverListSelector.getSelectedSlotIndex());

        super.initGui();
    }

    @Override
    public void handleMouseInput() throws IOException {
        this.serverListSelector.handleMouseInput();
        super.handleMouseInput();
    }

    @Override
    public void onGuiClosed() {
        Keyboard.enableRepeatEvents(false);
        this.oldServerPinger.clearPendingNetworks();
        super.onGuiClosed();
    }

    @Override
    public void updateScreen() {
        this.oldServerPinger.pingPendingNetworks();
        super.updateScreen();
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        if (button.isEnabled()) {
            GuiListExtended.IGuiListEntry guiListEntry = this.serverListSelector.getSelectedSlotIndex() < 0 ? null : this.serverListSelector.getListEntry(this.serverListSelector.getSelectedSlotIndex());

            switch (button.getId()) {
                case SELECT:
                    this.connectToSelected();
                    break;
                case DIRECT:
                    this.directConnect = true;
                    changeScreen(new DirectConnectScreen(this, this.selectedServer = new ServerData(I18n.format("selectServer.defaultName"), "", false)));
                    break;
                case ADD:
                    this.addingServer = true;
                    changeScreen(new AddServerScreen(this, this.selectedServer = new ServerData(I18n.format("selectServer.defaultName"), "", false)));
                    break;
                case EDIT:
                    if (guiListEntry instanceof ServerListEntryNormal) {
                        this.editingServer = true;
                        ServerData serverdata = ((ServerListEntryNormal)guiListEntry).getServerData();
                        this.selectedServer = new ServerData(serverdata.serverName, serverdata.serverIP, false);
                        this.selectedServer.copyFrom(serverdata);
                        this.mc.displayGuiScreen(new AddServerScreen(this, this.selectedServer));
                    }
                    break;
                case DELETE:
                    if (guiListEntry instanceof ServerListEntryNormal) {
                        String serverName = ((ServerListEntryNormal)guiListEntry).getServerData().serverName;
                        if (serverName != null) {
                            this.deletingServer = true;
                            changeScreen(new YesNoScreen(this, I18n.format("selectServer.deleteQuestion"), "'" + serverName + "' " + I18n.format("selectServer.deleteWarning"), I18n.format("selectServer.deleteButton"), I18n.format("gui.cancel"), this.serverListSelector.getSelectedSlotIndex()));
                        }
                    }
                    break;
                case REFRESH:
                    this.refreshServerList();
                    break;
                case CANCEL:
                    if (this.previousScreen instanceof InGameMenuScreen && mc.theWorld == null) {
                        changeScreen(new MainMenuScreen());
                    } else {
                        changeScreen(this.previousScreen);
                    }
                    break;
                case LAN:
                    changeScreen(new LanScreen(this));
            }
        }
        super.actionPerformed(button);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.hoveringText = null;
        drawDefaultBackground(mouseX, mouseY, 120);
        scissorBox(serverListSelector.getLeft(), serverListSelector.getTop(), serverListSelector.getRight(), serverListSelector.getBottom(), new ScaledResolution());
        GL11.glEnable(GL11.GL_SCISSOR_TEST);
        this.serverListSelector.drawScreen(mouseX, mouseY, partialTicks);
        GL11.glDisable(GL11.GL_SCISSOR_TEST);
        drawRect(0, 0, serverListSelector.getWidth(), serverListSelector.getTop(), Integer.MIN_VALUE);
        drawRect(0, serverListSelector.getBottom(), serverListSelector.getWidth(), this.height, Integer.MIN_VALUE);
        mc.fontRendererObj.drawCenteredString(I18n.format("multiplayer.title"), (float)this.width / 2, 20, 0xffffff);

        if (this.hoveringText != null) {
            drawHoveringText(Lists.newArrayList(Splitter.on("\n").split(this.hoveringText)), mouseX, mouseY);
        }
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        this.serverListSelector.mouseClicked(mouseX, mouseY, mouseButton);
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int mouseButton) {
        this.serverListSelector.mouseReleased(mouseX, mouseY, mouseButton);
        super.mouseReleased(mouseX, mouseY, mouseButton);
    }

    private void refreshServerList() {
        changeScreen(new MultiplayerScreen(this.previousScreen));
    }

    @Override
    public void confirmClicked(boolean result, int id) {
        GuiListExtended.IGuiListEntry guiListEntry = this.serverListSelector.getSelectedSlotIndex() < 0 ? null : this.serverListSelector.getListEntry(this.serverListSelector.getSelectedSlotIndex());

        if (this.directConnect) {
            this.directConnect = false;

            if (result) {
                this.connectToServer(this.selectedServer);
            } else {
                changeScreen(this);
            }
        } else if (this.addingServer) {
            this.addingServer = false;

            if (result) {
                this.serverList.addServerData(this.selectedServer);
                this.serverList.saveServerList();
                this.serverListSelector.setSelectedSlotIndex(-1);
                this.serverListSelector.loadNormalEntries(this.serverList);
            }

            changeScreen(this);
        } else if (this.editingServer) {
            this.editingServer = false;

            if (result && guiListEntry instanceof ServerListEntryNormal) {
                ServerData serverdata = ((ServerListEntryNormal)guiListEntry).getServerData();
                serverdata.serverName = this.selectedServer.serverName;
                serverdata.serverIP = this.selectedServer.serverIP;
                serverdata.copyFrom(this.selectedServer);
                this.serverList.saveServerList();
                this.serverListSelector.loadNormalEntries(this.serverList);
            }

            changeScreen(this);

        } else if (this.deletingServer) {
            this.deletingServer = false;

            if (result && guiListEntry instanceof ServerListEntryNormal) {
                this.serverList.removeServerData(this.serverListSelector.getSelectedSlotIndex());
                this.serverList.saveServerList();
                this.serverListSelector.setSelectedSlotIndex(-1);
                this.serverListSelector.loadNormalEntries(this.serverList);
            }

            changeScreen(this);

        }

        super.confirmClicked(result, id);
    }

    public void connectToSelected() {
        GuiListExtended.IGuiListEntry guiListEntry = this.serverListSelector.getSelectedSlotIndex() < 0 ? null : this.serverListSelector.getListEntry(this.serverListSelector.getSelectedSlotIndex());

        if (guiListEntry instanceof ServerListEntryNormal) {
            this.connectToServer(((ServerListEntryNormal)guiListEntry).getServerData());
        }
    }

    public void connectToServer(ServerData serverData) {
        changeScreen(new ConnectingScreen(this, serverData));
    }

    public void selectServer(int index) {
        this.serverListSelector.setSelectedSlotIndex(index);
        GuiListExtended.IGuiListEntry guiListEntry = index < 0 ? null : this.serverListSelector.getListEntry(index);
        getButton(SELECT).setEnabled(false);
        getButton(EDIT).setEnabled(false);
        getButton(DELETE).setEnabled(false);

        if (guiListEntry != null) {
            getButton(SELECT).setEnabled(true);

            if (guiListEntry instanceof ServerListEntryNormal) {
                getButton(EDIT).setEnabled(true);
                getButton(DELETE).setEnabled(true);
            }
        }
    }

    public void swapDown(ServerListEntryNormal entryNormal, int p_175391_2_, boolean shift) {
        int i = shift ? 0 : p_175391_2_ - 1;
        this.serverList.swapServers(p_175391_2_, i);

        if (this.serverListSelector.getSelectedSlotIndex() == p_175391_2_) {
            this.selectServer(i);
        }

        this.serverListSelector.loadNormalEntries(this.serverList);
    }

    public void swapUp(ServerListEntryNormal entryNormal, int p_175393_2_, boolean shift) {
        int i = shift ? this.serverList.countServers() - 1 : p_175393_2_ + 1;
        this.serverList.swapServers(p_175393_2_, i);

        if (this.serverListSelector.getSelectedSlotIndex() == p_175393_2_) {
            this.selectServer(i);
        }

        this.serverListSelector.loadNormalEntries(this.serverList);
    }


    public OldServerPinger getOldServerPinger() {
        return oldServerPinger;
    }

    public ServerList getServerList() {
        return serverList;
    }

    public String getHoveringText() {
        return hoveringText;
    }

    public void setHoveringText(String hoveringText) {
        this.hoveringText = hoveringText;
    }
}
