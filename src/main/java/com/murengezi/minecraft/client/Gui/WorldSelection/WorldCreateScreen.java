package com.murengezi.minecraft.client.Gui.WorldSelection;

import com.murengezi.minecraft.client.Gui.GuiButton;
import com.murengezi.minecraft.client.Gui.Screen;
import net.minecraft.client.gui.GuiCreateFlatWorld;
import net.minecraft.client.gui.GuiCustomizeWorldScreen;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ChatAllowedCharacters;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.WorldType;
import net.minecraft.world.storage.ISaveFormat;
import net.minecraft.world.storage.WorldInfo;
import org.apache.commons.lang3.StringUtils;
import org.lwjgl.input.Keyboard;

import java.io.IOException;
import java.util.Random;

/**
 * @author Tobias Sjöblom
 * Created on 2021-01-25 at 11:48
 */
public class WorldCreateScreen extends Screen {

	private final GuiScreen previousScreen;
	private GuiTextField field_146333_g;
	private GuiTextField field_146335_h;
	private String field_146336_i;
	private String gameMode = "survival";
	private String field_175300_s;
	private boolean field_146341_s = true;

	/** If cheats are allowed */
	private boolean allowCheats;
	private boolean field_146339_u;
	private boolean field_146338_v;
	private boolean field_146337_w;
	private boolean field_146345_x;
	private boolean field_146344_y;
	private GuiButton btnGameMode;
	private GuiButton btnMoreOptions;
	private GuiButton btnMapFeatures;
	private GuiButton btnBonusItems;
	private GuiButton btnMapType;
	private GuiButton btnAllowCommands;
	private GuiButton btnCustomizeType;
	private String field_146323_G;
	private String field_146328_H;
	private String field_146329_I;
	private String field_146330_J;
	private int selectedIndex;
	public String chunkProviderSettingsJson = "";

	/** These filenames are known to be restricted on one or more OS's. */
	private static final String[] disallowedFilenames = new String[] {"CON", "COM", "PRN", "AUX", "CLOCK$", "NUL", "COM1", "COM2", "COM3", "COM4", "COM5", "COM6", "COM7", "COM8", "COM9", "LPT1", "LPT2", "LPT3", "LPT4", "LPT5", "LPT6", "LPT7", "LPT8", "LPT9"};

	public WorldCreateScreen(GuiScreen previousScreen)
	{
		this.previousScreen = previousScreen;
		this.field_146329_I = "";
		this.field_146330_J = I18n.format("selectWorld.newWorld");
	}

	@Override
	public void updateScreen() {
		this.field_146333_g.updateCursorCounter();
		this.field_146335_h.updateCursorCounter();
		super.updateScreen();
	}

	@Override
	public void initGui() {
		Keyboard.enableRepeatEvents(true);
		this.buttonList.clear();
		this.buttonList.add(new GuiButton(0, this.width / 2 - 155, this.height - 28, 150, 20, I18n.format("selectWorld.create")));
		this.buttonList.add(new GuiButton(1, this.width / 2 + 5, this.height - 28, 150, 20, I18n.format("gui.cancel")));
		this.buttonList.add(this.btnGameMode = new GuiButton(2, this.width / 2 - 75, 115, 150, 20, I18n.format("selectWorld.gameMode")));
		this.buttonList.add(this.btnMoreOptions = new GuiButton(3, this.width / 2 - 75, 187, 150, 20, I18n.format("selectWorld.moreWorldOptions")));
		this.buttonList.add(this.btnMapFeatures = new GuiButton(4, this.width / 2 - 155, 100, 150, 20, I18n.format("selectWorld.mapFeatures")));
		this.btnMapFeatures.setVisible(false);
		this.buttonList.add(this.btnBonusItems = new GuiButton(7, this.width / 2 + 5, 151, 150, 20, I18n.format("selectWorld.bonusItems")));
		this.btnBonusItems.setVisible(false);
		this.buttonList.add(this.btnMapType = new GuiButton(5, this.width / 2 + 5, 100, 150, 20, I18n.format("selectWorld.mapType")));
		this.btnMapType.setVisible(false);
		this.buttonList.add(this.btnAllowCommands = new GuiButton(6, this.width / 2 - 155, 151, 150, 20, I18n.format("selectWorld.allowCommands")));
		this.btnAllowCommands.setVisible(false);
		this.buttonList.add(this.btnCustomizeType = new GuiButton(8, this.width / 2 + 5, 120, 150, 20, I18n.format("selectWorld.customizeType")));
		this.btnCustomizeType.setVisible(false);
		this.field_146333_g = new GuiTextField(9, this.fontRendererObj, this.width / 2 - 100, 60, 200, 20);
		this.field_146333_g.setFocused(true);
		this.field_146333_g.setText(this.field_146330_J);
		this.field_146335_h = new GuiTextField(10, this.fontRendererObj, this.width / 2 - 100, 60, 200, 20);
		this.field_146335_h.setText(this.field_146329_I);
		this.func_146316_a(this.field_146344_y);
		this.func_146314_g();
		this.func_146319_h();
		super.initGui();
	}

	private void func_146314_g() {
		this.field_146336_i = this.field_146333_g.getText().trim();

		for (char c0 : ChatAllowedCharacters.allowedCharactersArray) {
			this.field_146336_i = this.field_146336_i.replace(c0, '_');
		}

		if (StringUtils.isEmpty(this.field_146336_i))
		{
			this.field_146336_i = "World";
		}

		this.field_146336_i = getValidName(this.mc.getSaveLoader(), this.field_146336_i);
	}

	private void func_146319_h() {
		this.btnGameMode.displayString = I18n.format("selectWorld.gameMode", new Object[0]) + ": " + I18n.format("selectWorld.gameMode." + this.gameMode);
		this.field_146323_G = I18n.format("selectWorld.gameMode." + this.gameMode + ".line1", new Object[0]);
		this.field_146328_H = I18n.format("selectWorld.gameMode." + this.gameMode + ".line2");
		this.btnMapFeatures.displayString = I18n.format("selectWorld.mapFeatures") + " ";

		if (this.field_146341_s) {
			this.btnMapFeatures.displayString = this.btnMapFeatures.displayString + I18n.format("options.on");
		} else {
			this.btnMapFeatures.displayString = this.btnMapFeatures.displayString + I18n.format("options.off");
		}

		this.btnBonusItems.displayString = I18n.format("selectWorld.bonusItems") + " ";

		if (this.field_146338_v && !this.field_146337_w) {
			this.btnBonusItems.displayString = this.btnBonusItems.displayString + I18n.format("options.on");
		} else {
			this.btnBonusItems.displayString = this.btnBonusItems.displayString + I18n.format("options.off");
		}

		this.btnMapType.displayString = I18n.format("selectWorld.mapType") + " " + I18n.format(WorldType.worldTypes[this.selectedIndex].getTranslateName());
		this.btnAllowCommands.displayString = I18n.format("selectWorld.allowCommands") + " ";

		if (this.allowCheats && !this.field_146337_w){
			this.btnAllowCommands.displayString = this.btnAllowCommands.displayString + I18n.format("options.on");
		} else {
			this.btnAllowCommands.displayString = this.btnAllowCommands.displayString + I18n.format("options.off");
		}
	}

	public static String getValidName(ISaveFormat saveFormat, String fileName) {
		fileName = fileName.replaceAll("[\\./\"]", "_");

		for (String s : disallowedFilenames) {
			if (fileName.equalsIgnoreCase(s)) {
				fileName = "_" + fileName + "_";
			}
		}

		while (saveFormat.getWorldInfo(fileName) != null) {
			fileName = fileName + "-";
		}

		return fileName;
	}

	@Override
	public void onGuiClosed() {
		Keyboard.enableRepeatEvents(false);
		super.onGuiClosed();
	}

	@Override
	protected void actionPerformed(GuiButton button) throws IOException {
		if (button.isEnabled()) {
			if (button.getId() == 1) {
				this.mc.displayGuiScreen(this.previousScreen);
			} else if (button.getId() == 0) {
				this.mc.displayGuiScreen(null);

				if (this.field_146345_x) {
					return;
				}

				this.field_146345_x = true;
				long i = (new Random()).nextLong();
				String s = this.field_146335_h.getText();

				if (!StringUtils.isEmpty(s)) {
					try {
						long j = Long.parseLong(s);

						if (j != 0L) {
							i = j;
						}
					} catch (NumberFormatException var7) {
						i = s.hashCode();
					}
				}

				WorldSettings.GameType worldsettings$gametype = WorldSettings.GameType.getByName(this.gameMode);
				WorldSettings worldsettings = new WorldSettings(i, worldsettings$gametype, this.field_146341_s, this.field_146337_w, WorldType.worldTypes[this.selectedIndex]);
				worldsettings.setWorldName(this.chunkProviderSettingsJson);

				if (this.field_146338_v && !this.field_146337_w) {
					worldsettings.enableBonusChest();
				}

				if (this.allowCheats && !this.field_146337_w) {
					worldsettings.enableCommands();
				}

				this.mc.launchIntegratedServer(this.field_146336_i, this.field_146333_g.getText().trim(), worldsettings);
			} else if (button.getId() == 3) {
				this.func_146315_i();
			} else if (button.getId() == 2) {
				if (this.gameMode.equals("survival")) {
					if (!this.field_146339_u) {
						this.allowCheats = false;
					}

					this.field_146337_w = false;
					this.gameMode = "hardcore";
					this.field_146337_w = true;
					this.btnAllowCommands.setEnabled(false);
					this.btnBonusItems.setEnabled(false);
					this.func_146319_h();
				} else if (this.gameMode.equals("hardcore")) {
					if (!this.field_146339_u) {
						this.allowCheats = true;
					}

					this.field_146337_w = false;
					this.gameMode = "creative";
					this.func_146319_h();
					this.field_146337_w = false;
					this.btnAllowCommands.setEnabled(true);
					this.btnBonusItems.setEnabled(true);
				}
				else {
					if (!this.field_146339_u) {
						this.allowCheats = false;
					}

					this.gameMode = "survival";
					this.func_146319_h();
					this.btnAllowCommands.setEnabled(true);
					this.btnBonusItems.setEnabled(true);
					this.field_146337_w = false;
				}

				this.func_146319_h();
			} else if (button.getId() == 4) {
				this.field_146341_s = !this.field_146341_s;
				this.func_146319_h();
			} else if (button.getId() == 7) {
				this.field_146338_v = !this.field_146338_v;
				this.func_146319_h();
			} else if (button.getId() == 5) {
				++this.selectedIndex;

				if (this.selectedIndex >= WorldType.worldTypes.length) {
					this.selectedIndex = 0;
				}

				while (!this.func_175299_g()) {
					++this.selectedIndex;

					if (this.selectedIndex >= WorldType.worldTypes.length) {
						this.selectedIndex = 0;
					}
				}

				this.chunkProviderSettingsJson = "";
				this.func_146319_h();
				this.func_146316_a(this.field_146344_y);
			} else if (button.getId() == 6) {
				this.field_146339_u = true;
				this.allowCheats = !this.allowCheats;
				this.func_146319_h();
			} else if (button.getId() == 8) {
				if (WorldType.worldTypes[this.selectedIndex] == WorldType.FLAT) {
					this.mc.displayGuiScreen(new WorldCreateFlatScreen(this, this.chunkProviderSettingsJson));
				} else {
					this.mc.displayGuiScreen(new GuiCustomizeWorldScreen(this, this.chunkProviderSettingsJson));
				}
			}
		}
		super.actionPerformed(button);
	}

	private boolean func_175299_g() {
		WorldType worldtype = WorldType.worldTypes[this.selectedIndex];
		return worldtype != null && worldtype.getCanBeCreated() && (worldtype != WorldType.DEBUG_WORLD || isShiftKeyDown());
	}

	private void func_146315_i() {
		this.func_146316_a(!this.field_146344_y);
	}

	private void func_146316_a(boolean p_146316_1_) {
		this.field_146344_y = p_146316_1_;

		if (WorldType.worldTypes[this.selectedIndex] == WorldType.DEBUG_WORLD) {
			this.btnGameMode.setVisible(!this.field_146344_y);
			this.btnGameMode.setEnabled(false);

			if (this.field_175300_s == null) {
				this.field_175300_s = this.gameMode;
			}

			this.gameMode = "spectator";
			this.btnMapFeatures.setVisible(false);
			this.btnBonusItems.setVisible(false);
			this.btnMapType.setVisible(this.field_146344_y);
			this.btnAllowCommands.setVisible(false);
			this.btnCustomizeType.setVisible(false);
		} else {
			this.btnGameMode.setVisible(!this.field_146344_y);
			this.btnGameMode.setEnabled(true);

			if (this.field_175300_s != null) {
				this.gameMode = this.field_175300_s;
				this.field_175300_s = null;
			}

			this.btnMapFeatures.setVisible(this.field_146344_y && WorldType.worldTypes[this.selectedIndex] != WorldType.CUSTOMIZED);
			this.btnBonusItems.setVisible(this.field_146344_y);
			this.btnMapType.setVisible(this.field_146344_y);
			this.btnAllowCommands.setVisible(this.field_146344_y);
			this.btnCustomizeType.setVisible(this.field_146344_y && (WorldType.worldTypes[this.selectedIndex] == WorldType.FLAT || WorldType.worldTypes[this.selectedIndex] == WorldType.CUSTOMIZED));
		}

		this.func_146319_h();

		if (this.field_146344_y) {
			this.btnMoreOptions.displayString = I18n.format("gui.done");
		} else {
			this.btnMoreOptions.displayString = I18n.format("selectWorld.moreWorldOptions");
		}
	}

	protected void keyTyped(char typedChar, int keyCode) throws IOException {
		if (this.field_146333_g.isFocused() && !this.field_146344_y) {
			this.field_146333_g.textBoxKeyTyped(typedChar, keyCode);
			this.field_146330_J = this.field_146333_g.getText();
		} else if (this.field_146335_h.isFocused() && this.field_146344_y) {
			this.field_146335_h.textBoxKeyTyped(typedChar, keyCode);
			this.field_146329_I = this.field_146335_h.getText();
		}

		if (keyCode == 28 || keyCode == 156) {
			this.actionPerformed(this.buttonList.get(0));
		}

		this.buttonList.get(0).setEnabled(this.field_146333_g.getText().length() > 0);
		this.func_146314_g();
	}

	@Override
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) {
		if (this.field_146344_y) {
			this.field_146335_h.mouseClicked(mouseX, mouseY, mouseButton);
		} else {
			this.field_146333_g.mouseClicked(mouseX, mouseY, mouseButton);
		}
		super.mouseClicked(mouseX, mouseY, mouseButton);
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		this.drawDefaultBackground(mouseX, mouseY, 60);
		this.drawCenteredString(this.fontRendererObj, I18n.format("selectWorld.create"), this.width / 2, 20, -1);

		if (this.field_146344_y) {
			this.drawString(this.fontRendererObj, I18n.format("selectWorld.enterSeed"), this.width / 2 - 100, 47, -6250336);
			this.drawString(this.fontRendererObj, I18n.format("selectWorld.seedInfo"), this.width / 2 - 100, 85, -6250336);

			if (this.btnMapFeatures.isVisible()) {
				this.drawString(this.fontRendererObj, I18n.format("selectWorld.mapFeatures.info"), this.width / 2 - 150, 122, -6250336);
			}

			if (this.btnAllowCommands.isVisible()) {
				this.drawString(this.fontRendererObj, I18n.format("selectWorld.allowCommands.info"), this.width / 2 - 150, 172, -6250336);
			}

			this.field_146335_h.drawTextBox();

			if (WorldType.worldTypes[this.selectedIndex].showWorldInfoNotice()) {
				this.fontRendererObj.drawSplitString(I18n.format(WorldType.worldTypes[this.selectedIndex].func_151359_c(), new Object[0]), this.btnMapType.getX() + 2, this.btnMapType.getY() + 22, this.btnMapType.getButtonWidth(), 10526880);
			}
		} else {
			this.drawString(this.fontRendererObj, I18n.format("selectWorld.enterName"), this.width / 2 - 100, 47, -6250336);
			this.drawString(this.fontRendererObj, I18n.format("selectWorld.resultFolder") + " " + this.field_146336_i, this.width / 2 - 100, 85, -6250336);
			this.field_146333_g.drawTextBox();
			this.drawString(this.fontRendererObj, this.field_146323_G, this.width / 2 - 100, 137, -6250336);
			this.drawString(this.fontRendererObj, this.field_146328_H, this.width / 2 - 100, 149, -6250336);
		}

		super.drawScreen(mouseX, mouseY, partialTicks);
	}

	public void func_146318_a(WorldInfo p_146318_1_) {
		this.field_146330_J = I18n.format("selectWorld.newWorld.copyOf", p_146318_1_.getWorldName());
		this.field_146329_I = p_146318_1_.getSeed() + "";
		this.selectedIndex = p_146318_1_.getTerrainType().getWorldTypeID();
		this.chunkProviderSettingsJson = p_146318_1_.getGeneratorOptions();
		this.field_146341_s = p_146318_1_.isMapFeaturesEnabled();
		this.allowCheats = p_146318_1_.areCommandsAllowed();

		if (p_146318_1_.isHardcoreModeEnabled()) {
			this.gameMode = "hardcore";
		} else if (p_146318_1_.getGameType().isSurvivalOrAdventure()) {
			this.gameMode = "survival";
		} else if (p_146318_1_.getGameType().isCreative()) {
			this.gameMode = "creative";
		}
	}

}
