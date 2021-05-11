package com.murengezi.minecraft.client.gui.InGame;

import com.murengezi.minecraft.client.gui.GuiButton;
import com.murengezi.minecraft.client.gui.Screen;
import com.murengezi.minecraft.client.resources.I18n;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.IChatComponent;
import net.minecraft.world.WorldSettings;

import java.io.IOException;

/**
 * @author Tobias Sjöblom
 * Created on 2021-01-24 at 16:44
 */
public class ShareToLanScreen extends Screen {

	private final Screen previousScreen;
	private boolean allowCommands;
	private int lanGamemode = 0;

	private static final int START = 0;
	private static final int CANCEL = 1;
	private static final int GAMEMODE = 2;
	private static final int ALLOWCOMMANDS = 3;

	public ShareToLanScreen(Screen previousScreen) {
		this.previousScreen = previousScreen;
	}

	@Override
	public void initGui() {
		addButton(new GuiButton(START, this.width / 2 - 155, this.height / 2 + 25, 150, 20, I18n.format("lanServer.start")));
		addButton(new GuiButton(CANCEL, this.width / 2 + 5, this.height / 2 + 25, 150, 20, I18n.format("gui.cancel")));
		addButton(new GuiButton(GAMEMODE, this.width / 2 - 155, this.height / 2 - 25, 150, 20, I18n.format("selectWorld.gameMode")));
		addButton(new GuiButton(ALLOWCOMMANDS, this.width / 2 + 5, this.height / 2 - 25, 150, 20, I18n.format("selectWorld.allowCommands")));
		updateDisplayStrings();
		super.initGui();
	}

	private void updateDisplayStrings() {
		getButton(GAMEMODE).displayString = I18n.format("selectWorld.gameMode") + " " + I18n.format("selectWorld.gameMode." + WorldSettings.GameType.getByID(lanGamemode).getName());
		getButton(ALLOWCOMMANDS).displayString = I18n.format("selectWorld.allowCommands") + " " + I18n.format("options." + (this.allowCommands ? "on" : "off"));
	}

	@Override
	protected void actionPerformed(GuiButton button) throws IOException {
		switch (button.getId()) {
			case START:
				changeScreen(null);
				String s = getMc().getIntegratedServer().shareToLAN(WorldSettings.GameType.getByID(lanGamemode), this.allowCommands);
				IChatComponent ichatcomponent;

				if (s != null) {
					ichatcomponent = new ChatComponentTranslation("commands.publish.started", s);
				} else {
					ichatcomponent = new ChatComponentText("commands.publish.failed");
				}

				getMc().inGameScreen.getChatGUI().printChatMessage(ichatcomponent);
				break;
			case CANCEL:
				changeScreen(previousScreen);
				break;
			case GAMEMODE:
				lanGamemode++;
				if (lanGamemode >= WorldSettings.GameType.values().length - 1) {
					lanGamemode = 0;
				}
				updateDisplayStrings();
				break;
			case ALLOWCOMMANDS:
				allowCommands = !allowCommands;
				updateDisplayStrings();
				break;
		}
		super.actionPerformed(button);
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		getFr().drawCenteredString(I18n.format("lanServer.title"), this.width / 2, this.height / 2 - 82, 16777215);
		getFr().drawCenteredString(I18n.format("lanServer.otherPlayers"), this.width / 2, this.height / 2 - 50, 16777215);
		super.drawScreen(mouseX, mouseY, partialTicks);
	}
}
