package com.murengezi.minecraft.client.gui.BrowseServers;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.murengezi.minecraft.client.gui.GuiButton;
import com.murengezi.minecraft.client.gui.GuiListExtended;
import com.murengezi.minecraft.client.gui.Multiplayer.ConnectingScreen;
import com.murengezi.minecraft.client.gui.Screen;
import com.murengezi.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.multiplayer.ServerList;
import net.minecraft.client.network.OldServerPinger;
import net.minecraft.client.resources.I18n;
import org.lwjgl.opengl.GL11;

import java.io.IOException;

/**
 * @author Tobias Sjöblom
 * Created on 2021-01-25 at 10:35
 */
public class BrowseScreen extends Screen {

	private final Screen previousScreen;
	private final OldServerPinger oldServerPinger = new OldServerPinger();
	private ServerData selectedServer;
	private ServerSelectionList serverListSelector;
	private ServerList serverList;
	private boolean initialized;
	private String hoveringText;

	private static final int SELECT = 0;
	private static final int REFRESH = 1;
	private static final int CANCEL = 2;

	public BrowseScreen(Screen previousScreen) {
		this.previousScreen = previousScreen;
	}

	@Override
	public void initGui() {
		if (!this.initialized) {
			this.initialized = true;
			this.serverList = new ServerList(getMc());
			this.serverList.loadServerList();
			this.serverListSelector = new ServerSelectionList(this, this.width, this.height, 32, this.height - 64, 36);
			this.serverListSelector.loadNormalEntries(serverList);
		} else {
			this.serverListSelector.setDimensions(this.width, this.height, 32, this.height - 64);
		}

		addButton(new GuiButton(SELECT, this.width / 2 - 154, this.height - 52, 100, 20, I18n.format("selectServer.select")));
		addButton(new GuiButton(REFRESH, this.width / 2 + 4, this.height - 28, 70, 20, I18n.format("selectServer.refresh")));
		addButton(new GuiButton(CANCEL, this.width / 2 + 84, this.height - 28, 70, 20, I18n.format("gui.cancel")));
		super.initGui();
	}

	@Override
	protected void actionPerformed(GuiButton button) throws IOException {
		if (button.isEnabled()) {
			switch (button.getId()) {
				case SELECT:
					break;
				case REFRESH:
					refreshServerList();
					break;
				case CANCEL:
					changeScreen(previousScreen);
					break;
			}
		}
		super.actionPerformed(button);
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
		changeScreen(new BrowseScreen(this.previousScreen));
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		this.hoveringText = null;
		this.drawDefaultBackground(mouseX, mouseY, 120);
		scissorBox(serverListSelector.getLeft(), serverListSelector.getTop(), serverListSelector.getRight(), serverListSelector.getBottom(), new ScaledResolution());
		GL11.glEnable(GL11.GL_SCISSOR_TEST);
		this.serverListSelector.drawScreen(mouseX, mouseY, partialTicks);
		GL11.glDisable(GL11.GL_SCISSOR_TEST);
		drawRect(0, 0, serverListSelector.getWidth(), serverListSelector.getTop(), Integer.MIN_VALUE);
		drawRect(0, serverListSelector.getBottom(), serverListSelector.getWidth(), this.height, Integer.MIN_VALUE);
		getFr().drawCenteredString(I18n.format("multiplayer.title"), (float)this.width / 2, 20, 0xffffff);

		if (this.hoveringText != null) {
			drawHoveringText(Lists.newArrayList(Splitter.on("\n").split(this.hoveringText)), mouseX, mouseY);
		}

		super.drawScreen(mouseX, mouseY, partialTicks);
	}

	public void connectToSelected() {
		GuiListExtended.IGuiListEntry guiListEntry = this.serverListSelector.getSelectedSlotIndex() < 0 ? null : this.serverListSelector.getListEntry(this.serverListSelector.getSelectedSlotIndex());

		if (guiListEntry instanceof com.murengezi.minecraft.client.gui.Multiplayer.ServerListEntryNormal) {
			this.connectToServer(((com.murengezi.minecraft.client.gui.Multiplayer.ServerListEntryNormal)guiListEntry).getServerData());
		}
	}

	public void connectToServer(ServerData serverData) {
		changeScreen(new ConnectingScreen(this, serverData));
	}

	public void selectServer(int index) {
		this.serverListSelector.setSelectedSlotIndex(index);
		GuiListExtended.IGuiListEntry guiListEntry = index < 0 ? null : this.serverListSelector.getListEntry(index);
		getButton(SELECT).setEnabled(false);

		if (guiListEntry != null) {
			getButton(SELECT).setEnabled(true);
		}
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
