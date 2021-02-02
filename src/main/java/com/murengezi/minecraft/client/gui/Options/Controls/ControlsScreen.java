package com.murengezi.minecraft.client.gui.Options.Controls;

import com.murengezi.minecraft.client.gui.GuiButton;
import com.murengezi.minecraft.client.gui.ScaledResolution;
import com.murengezi.minecraft.client.gui.Screen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.*;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.settings.KeyBinding;
import org.lwjgl.opengl.GL11;

import java.io.IOException;

/**
 * @author Tobias Sjöblom
 * Created on 2021-01-24 at 18:59
 */
public class ControlsScreen extends Screen {

	private static final GameSettings.Options[] optionsArr = new GameSettings.Options[] {GameSettings.Options.INVERT_MOUSE, GameSettings.Options.SENSITIVITY, GameSettings.Options.TOUCHSCREEN};

	private GuiScreen previousScreen;
	private GameSettings gameSettings;

	/** The ID of the button that has been pressed. */
	public KeyBinding buttonId = null;
	public long time;
	private KeybindingList keyBindingList;
	private GuiButton buttonReset;

	public ControlsScreen(GuiScreen previousScreen, GameSettings gameSettings)
	{
		this.previousScreen = previousScreen;
		this.gameSettings = gameSettings;
	}

	@Override
	public void initGui() {
		this.keyBindingList = new KeybindingList(this, mc);
		this.buttonList.add(new GuiButton(200, this.width / 2 - 155, this.height - 29, 150, 20, I18n.format("gui.done")));
		this.buttonList.add(this.buttonReset = new GuiButton(201, this.width / 2 - 155 + 160, this.height - 29, 150, 20, I18n.format("controls.resetAll")));
		int i = 0;

		for (GameSettings.Options gamesettings$options : optionsArr)
		{
			if (gamesettings$options.getEnumFloat())
			{
				this.buttonList.add(new GuiOptionSlider(gamesettings$options.returnEnumOrdinal(), this.width / 2 - 155 + i % 2 * 160, 18 + 24 * (i >> 1), gamesettings$options));
			}
			else
			{
				this.buttonList.add(new GuiOptionButton(gamesettings$options.returnEnumOrdinal(), this.width / 2 - 155 + i % 2 * 160, 18 + 24 * (i >> 1), gamesettings$options, this.gameSettings.getKeyBinding(gamesettings$options)));
			}

			++i;
		}
	}

	@Override
	public void handleMouseInput() throws IOException {
		this.keyBindingList.handleMouseInput();
		super.handleMouseInput();
	}

	/**
	 * Called by the controls from the buttonList when activated. (Mouse pressed for buttons)
	 */
	@Override
	protected void actionPerformed(GuiButton button) throws IOException {

		if (button.getId() == 200) {
			changeScreen(previousScreen);
		} else if (button.getId() == 201) {
			for (KeyBinding keybinding : this.mc.gameSettings.keyBindings) {
				keybinding.setKeyCode(keybinding.getKeyCodeDefault());
			}

			KeyBinding.resetKeyBindingArrayAndHash();
		} else if (button.getId() < 100 && button instanceof GuiOptionButton) {
			this.gameSettings.setOptionValue(((GuiOptionButton)button).returnEnumOptions(), 1);
			button.displayString = this.gameSettings.getKeyBinding(GameSettings.Options.getEnumOptions(button.getId()));
		}
	}

	@Override
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) {
		if (this.buttonId != null) {
			this.gameSettings.setOptionKeyBinding(this.buttonId, -100 + mouseButton);
			this.buttonId = null;
			KeyBinding.resetKeyBindingArrayAndHash();
		} else if (mouseButton != 0 || !this.keyBindingList.mouseClicked(mouseX, mouseY, mouseButton)) {
			super.mouseClicked(mouseX, mouseY, mouseButton);
		}
	}

	@Override
	protected void mouseReleased(int mouseX, int mouseY, int mouseButton) {
		if (mouseButton != 0 || !this.keyBindingList.mouseReleased(mouseX, mouseY, mouseButton)) {
			super.mouseReleased(mouseX, mouseY, mouseButton);
		}
	}

	@Override
	protected void keyTyped(char typedChar, int keyCode) throws IOException {
		if (this.buttonId != null) {
			if (keyCode == 1) {
				this.gameSettings.setOptionKeyBinding(this.buttonId, 0);
			} else if (keyCode != 0) {
				this.gameSettings.setOptionKeyBinding(this.buttonId, keyCode);
			} else if (typedChar > 0) {
				this.gameSettings.setOptionKeyBinding(this.buttonId, typedChar + 256);
			}

			this.buttonId = null;
			this.time = Minecraft.getSystemTime();
			KeyBinding.resetKeyBindingArrayAndHash();
		} else {
			super.keyTyped(typedChar, keyCode);
		}
	}

	/**
	 * Draws the screen and all the components in it. Args : mouseX, mouseY, renderPartialTicks
	 */
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		this.drawDefaultBackground(mouseX, mouseY, 60);

		GlStateManager.pushMatrix();
		scissorBox(keyBindingList.getLeft(), keyBindingList.getTop(), keyBindingList.getRight(), keyBindingList.getBottom(), new ScaledResolution());
		GL11.glEnable(GL11.GL_SCISSOR_TEST);
		keyBindingList.drawScreen(mouseX, mouseY, partialTicks);
		GL11.glDisable(GL11.GL_SCISSOR_TEST);
		GlStateManager.popMatrix();

		Gui.drawRect(0, 0, keyBindingList.getWidth(), keyBindingList.getTop(), Integer.MIN_VALUE);
		Gui.drawRect(0, keyBindingList.getBottom(), keyBindingList.getWidth(), this.height, Integer.MIN_VALUE);

		this.drawCenteredString(this.fontRendererObj, I18n.format("controls.title"), this.width / 2, 8, 16777215);
		boolean flag = true;

		for (KeyBinding keybinding : this.gameSettings.keyBindings)
		{
			if (keybinding.getKeyCode() != keybinding.getKeyCodeDefault())
			{
				flag = false;
				break;
			}
		}

		this.buttonReset.setEnabled(!flag);
		super.drawScreen(mouseX, mouseY, partialTicks);
	}
}
