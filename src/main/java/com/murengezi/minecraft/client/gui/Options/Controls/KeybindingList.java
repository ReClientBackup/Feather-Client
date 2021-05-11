package com.murengezi.minecraft.client.gui.Options.Controls;

import com.murengezi.minecraft.client.gui.GuiButton;
import com.murengezi.minecraft.client.gui.GuiListExtended;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.EnumChatFormatting;
import org.apache.commons.lang3.ArrayUtils;

import java.util.Arrays;

/**
 * @author Tobias Sjöblom
 * Created on 2021-01-24 at 19:06
 */
public class KeybindingList extends GuiListExtended {
	
	private final ControlsScreen controlsScreen;
	private final GuiListExtended.IGuiListEntry[] listEntries;
	private int maxListLabelWidth = 0;

	public KeybindingList(ControlsScreen controlsScreen) {
		super(controlsScreen.width, controlsScreen.height, 63, controlsScreen.height - 32, 20);
		this.controlsScreen = controlsScreen;
		KeyBinding[] akeybinding = ArrayUtils.clone(getMc().gameSettings.keyBindings);
		this.listEntries = new GuiListExtended.IGuiListEntry[akeybinding.length + KeyBinding.getKeybinds().size()];
		Arrays.sort(akeybinding);
		int i = 0;
		String s = null;

		for (KeyBinding keybinding : akeybinding) {
			String s1 = keybinding.getCategory();

			if (!s1.equals(s)) {
				s = s1;
				this.listEntries[i++] = new KeybindingList.CategoryEntry(s1);
			}

			int j = getFr().getStringWidth(I18n.format(keybinding.getDescription()));

			if (j > this.maxListLabelWidth) {
				this.maxListLabelWidth = j;
			}

			this.listEntries[i++] = new KeybindingList.KeyEntry(keybinding);
		}
	}

	protected int getSize()
	{
		return this.listEntries.length;
	}

	public GuiListExtended.IGuiListEntry getListEntry(int index) {
		return this.listEntries[index];
	}

	protected int getScrollBarX()
	{
		return super.getScrollBarX() + 15;
	}

	public int getListWidth()
	{
		return super.getListWidth() + 32;
	}

	public class CategoryEntry implements GuiListExtended.IGuiListEntry {
		private final String labelText;

		public CategoryEntry(String label) {
			this.labelText = I18n.format(label);
		}

		@Override
		public void drawEntry(int slotIndex, int x, int y, int listWidth, int slotHeight, int mouseX, int mouseY, boolean isSelected) {
			getFr().drawCenteredString(this.labelText, getMc().currentScreen.width / 2, y + slotHeight - getFr().FONT_HEIGHT - 1, 16777215);
		}

		@Override
		public boolean mousePressed(int slotIndex, int p_148278_2_, int p_148278_3_, int p_148278_4_, int p_148278_5_, int p_148278_6_) {
			return false;
		}

		@Override
		public void mouseReleased(int slotIndex, int x, int y, int mouseEvent, int relativeX, int relativeY) {}

		@Override
		public void setSelected(int p_178011_1_, int p_178011_2_, int p_178011_3_) { }
	}

	public class KeyEntry implements GuiListExtended.IGuiListEntry {

		private final KeyBinding keyBinding;
		private final String keyDesc;
		private final GuiButton btnChangeKeyBinding;
		private final GuiButton btnReset;

		private KeyEntry(KeyBinding keyBinding) {
			this.keyBinding = keyBinding;
			this.keyDesc = I18n.format(keyBinding.getDescription());
			this.btnChangeKeyBinding = new GuiButton(0, 0, 0, 75, 20, I18n.format(keyBinding.getDescription()));
			this.btnReset = new GuiButton(0, 0, 0, 50, 20, I18n.format("controls.reset"));
		}

		public void drawEntry(int slotIndex, int x, int y, int listWidth, int slotHeight, int mouseX, int mouseY, boolean isSelected) {
			boolean flag = KeybindingList.this.controlsScreen.keyBinding == this.keyBinding;
			getFr().drawStringWithShadow(this.keyDesc, x + 90 - KeybindingList.this.maxListLabelWidth, y + slotHeight / 2 - getFr().FONT_HEIGHT / 2, 16777215);
			this.btnReset.setX(x + 190);
			this.btnReset.setY(y);
			this.btnReset.setEnabled(this.keyBinding.getKeyCode() != this.keyBinding.getKeyCodeDefault());
			this.btnReset.drawButton(mouseX, mouseY);
			this.btnChangeKeyBinding.setX(x + 105);
			this.btnChangeKeyBinding.setY(y);
			this.btnChangeKeyBinding.displayString = GameSettings.getKeyDisplayString(this.keyBinding.getKeyCode());
			boolean flag1 = false;

			if (this.keyBinding.getKeyCode() != 0)
			{
				for (KeyBinding keybinding : getMc().gameSettings.keyBindings)
				{
					if (keybinding != this.keyBinding && keybinding.getKeyCode() == this.keyBinding.getKeyCode())
					{
						flag1 = true;
						break;
					}
				}
			}

			if (flag)
			{
				this.btnChangeKeyBinding.displayString = EnumChatFormatting.WHITE + "> " + EnumChatFormatting.YELLOW + this.btnChangeKeyBinding.displayString + EnumChatFormatting.WHITE + " <";
			}
			else if (flag1)
			{
				this.btnChangeKeyBinding.displayString = EnumChatFormatting.RED + this.btnChangeKeyBinding.displayString;
			}

			this.btnChangeKeyBinding.drawButton(mouseX, mouseY);
		}

		public boolean mousePressed(int slotIndex, int p_148278_2_, int p_148278_3_, int p_148278_4_, int p_148278_5_, int p_148278_6_)
		{
			if (this.btnChangeKeyBinding.mousePressed(getMc(), p_148278_2_, p_148278_3_))
			{
				KeybindingList.this.controlsScreen.keyBinding = this.keyBinding;
				return true;
			}
			else if (this.btnReset.mousePressed(getMc(), p_148278_2_, p_148278_3_))
			{
				getMc().gameSettings.setOptionKeyBinding(this.keyBinding, this.keyBinding.getKeyCodeDefault());
				KeyBinding.resetKeyBindingArrayAndHash();
				return true;
			}
			else
			{
				return false;
			}
		}

		public void mouseReleased(int slotIndex, int x, int y, int mouseEvent, int relativeX, int relativeY)
		{
			this.btnChangeKeyBinding.mouseReleased(x, y);
			this.btnReset.mouseReleased(x, y);
		}

		public void setSelected(int p_178011_1_, int p_178011_2_, int p_178011_3_)
		{
		}
	}
}
