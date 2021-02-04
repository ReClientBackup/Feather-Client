package com.murengezi.minecraft.client.gui.Options.Controls;

import com.murengezi.minecraft.client.gui.GUI;
import com.murengezi.minecraft.client.gui.GuiButton;
import com.murengezi.minecraft.client.gui.ScaledResolution;
import com.murengezi.minecraft.client.gui.Screen;
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

	private final Screen previousScreen;
	public KeyBinding keyBinding = null;
	private KeybindingList keyBindingList;

	private static final int DONE = 0, RESET = 1, INVERT_MOUSE = 2, SENSITIVITY = 3;

	public ControlsScreen(Screen previousScreen) {
		this.previousScreen = previousScreen;
	}

	@Override
	public void initGui() {
		this.keyBindingList = new KeybindingList(this);
		addButton(new GuiButton(DONE, this.width / 2 - 155, this.height - 26, 150, 20, I18n.format("gui.done")));
		addButton(new GuiButton(RESET, this.width / 2 - 155 + 160, this.height - 26, 150, 20, I18n.format("controls.resetAll")));
		addButton(new GuiOptionButton(INVERT_MOUSE, this.width / 2 - 155, 18, GameSettings.Options.INVERT_MOUSE, getGs().getKeyBinding(GameSettings.Options.INVERT_MOUSE)));
		addButton(new GuiOptionSlider(SENSITIVITY, this.width / 2 - 155 + 160, 18, GameSettings.Options.SENSITIVITY));
		super.initGui();
	}

	@Override
	public void handleMouseInput() throws IOException {
		this.keyBindingList.handleMouseInput();
		super.handleMouseInput();
	}

	@Override
	protected void actionPerformed(GuiButton button) throws IOException {

		switch (button.getId()) {
			case DONE:
				changeScreen(previousScreen);
				break;
			case RESET:
				for (KeyBinding keybinding : getMc().gameSettings.keyBindings) {
					keybinding.setKeyCode(keybinding.getKeyCodeDefault());
				}
				KeyBinding.resetKeyBindingArrayAndHash();
				break;
			case INVERT_MOUSE:
				getGs().setOptionValue(((GuiOptionButton)button).getOptions(), 1);
				button.displayString = getGs().getKeyBinding(GameSettings.Options.getEnumOptions(0));
				break;
		}
		super.actionPerformed(button);
	}

	@Override
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) {
		if (this.keyBinding != null) {
			getGs().setOptionKeyBinding(this.keyBinding, -100 + mouseButton);
			this.keyBinding = null;
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
		if (this.keyBinding != null) {
			if (keyCode == 1) {
				getGs().setOptionKeyBinding(this.keyBinding, 0);
			} else if (keyCode != 0) {
				getGs().setOptionKeyBinding(this.keyBinding, keyCode);
			} else if (typedChar > 0) {
				getGs().setOptionKeyBinding(this.keyBinding, typedChar + 256);
			}

			this.keyBinding = null;
			KeyBinding.resetKeyBindingArrayAndHash();
		} else {
			super.keyTyped(typedChar, keyCode);
		}
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		this.drawDefaultBackground(mouseX, mouseY, 60);

		GlStateManager.pushMatrix();
		scissorBox(keyBindingList.getLeft(), keyBindingList.getTop(), keyBindingList.getRight(), keyBindingList.getBottom(), new ScaledResolution());
		GL11.glEnable(GL11.GL_SCISSOR_TEST);
		keyBindingList.drawScreen(mouseX, mouseY, partialTicks);
		GL11.glDisable(GL11.GL_SCISSOR_TEST);
		GlStateManager.popMatrix();

		GUI.drawRect(0, 0, keyBindingList.getWidth(), keyBindingList.getTop(), Integer.MIN_VALUE);
		GUI.drawRect(0, keyBindingList.getBottom(), keyBindingList.getWidth(), this.height, Integer.MIN_VALUE);

		getFr().drawCenteredString(I18n.format("controls.title"), this.width / 2, 8, 16777215);

		boolean edited = false;

		for (KeyBinding keybinding : getGs().keyBindings) {
			if (keybinding.getKeyCode() != keybinding.getKeyCodeDefault()) {
				edited = true;
				break;
			}
		}

		getButton(RESET).setEnabled(edited);
		super.drawScreen(mouseX, mouseY, partialTicks);
	}
}
