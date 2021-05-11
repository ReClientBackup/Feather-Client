package com.murengezi.minecraft.client.gui;

import com.murengezi.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.client.resources.I18n;
import net.minecraft.network.play.client.C00PacketKeepAlive;

import java.io.IOException;

/**
 * @author Tobias Sjöblom
 * Created on 2021-02-02 at 11:13
 */
public class DownloadTerrainScreen extends Screen {

	private final NetHandlerPlayClient netHandlerPlayClient;
	private int progress;


	public DownloadTerrainScreen(NetHandlerPlayClient netHandlerPlayClient) {
		this.netHandlerPlayClient = netHandlerPlayClient;
	}

	@Override
	protected void keyTyped(char typedChar, int keyCode) throws IOException {}

	@Override
	public void initGui() {}

	@Override
	public void updateScreen() {
		setProgress(getProgress() + 1);

		if (getProgress() % 20 == 0) {
			getNetHandlerPlayClient().addToSendQueue(new C00PacketKeepAlive());
		}
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		drawWorldBackground(mouseX, mouseY, 60);
		getFr().drawCenteredString(I18n.format("multiplayer.downloadingTerrain"), this.width / 2, this.height / 2 - 50, 16777215);
		super.drawScreen(mouseX, mouseY, partialTicks);
	}

	@Override
	public boolean doesGuiPauseGame() {
		return false;
	}

	public NetHandlerPlayClient getNetHandlerPlayClient() {
		return netHandlerPlayClient;
	}

	public int getProgress() {
		return progress;
	}

	public void setProgress(int progress) {
		this.progress = progress;
	}
}
