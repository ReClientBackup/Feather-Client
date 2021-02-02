package com.murengezi.minecraft.client.gui.Options;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.murengezi.minecraft.client.gui.GuiButton;
import com.murengezi.minecraft.client.gui.Screen;
import com.murengezi.minecraft.client.gui.fGuiSlot;
import com.murengezi.minecraft.client.gui.GUI;
import net.minecraft.client.gui.GuiOptionButton;
import com.murengezi.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.resources.Language;
import net.minecraft.client.resources.LanguageManager;
import net.minecraft.client.settings.GameSettings;
import org.lwjgl.opengl.GL11;

import java.io.IOException;
import java.util.Map;

/**
 * @author Tobias Sjöblom
 * Created on 2021-01-24 at 18:45
 */
public class LanguageScreen extends Screen {
	
	private final Screen previousScreen;
	private LanguageSlots languageSlots;
	private final GameSettings gameSettings;
	private final LanguageManager languageManager;

	public static final int UNICODE = 0;
	public static final int DONE = 1;

	public LanguageScreen(Screen previousScreen, GameSettings gameSettings, LanguageManager manager) {
		this.previousScreen = previousScreen;
		this.gameSettings = gameSettings;
		this.languageManager = manager;
	}

	@Override
	public void initGui() {
		addButton(new GuiOptionButton(UNICODE, this.width / 2 - 155, this.height - 38, GameSettings.Options.FORCE_UNICODE_FONT, this.gameSettings.getKeyBinding(GameSettings.Options.FORCE_UNICODE_FONT)));
		addButton(new GuiOptionButton(DONE, this.width / 2 - 155 + 160, this.height - 38, I18n.format("gui.done")));
		this.languageSlots = new LanguageSlots();
		this.languageSlots.registerScrollButtons(7, 8);
	}

	@Override
	public void handleMouseInput() throws IOException {
		this.languageSlots.handleMouseInput();
		super.handleMouseInput();
	}

	@Override
	protected void actionPerformed(GuiButton button) throws IOException {
		if (button.isEnabled()) {
			switch (button.getId()) {

				case UNICODE:
					this.gameSettings.setOptionValue(((GuiOptionButton)button).returnEnumOptions(), 1);
					button.displayString = this.gameSettings.getKeyBinding(GameSettings.Options.FORCE_UNICODE_FONT);
					ScaledResolution resolution = new ScaledResolution();
					this.setWorldAndResolution(resolution.getScaledWidth(), resolution.getScaledHeight());
					break;
				case DONE:
					changeScreen(previousScreen);
					break;
				default:
					this.languageSlots.actionPerformed(button);
					break;
			}
		}
		super.actionPerformed(button);
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		drawDefaultBackground(mouseX, mouseY, 60);

		GlStateManager.pushMatrix();
		scissorBox(languageSlots.getLeft(), languageSlots.getTop(), languageSlots.getRight(), languageSlots.getBottom(), new ScaledResolution());
		GL11.glEnable(GL11.GL_SCISSOR_TEST);
		languageSlots.drawScreen(mouseX, mouseY, partialTicks);
		GL11.glDisable(GL11.GL_SCISSOR_TEST);
		GlStateManager.popMatrix();
		GUI.drawRect(0, 0, languageSlots.getWidth(), languageSlots.getTop(), Integer.MIN_VALUE);
		GUI.drawRect(0, languageSlots.getBottom(), languageSlots.getWidth(), this.height, Integer.MIN_VALUE);


		getFr().drawCenteredString(I18n.format("options.language"), this.width / 2, 16, 16777215);
		getFr().drawCenteredString("(" + I18n.format("options.languageWarning") + ")", this.width / 2, this.height - 56, 8421504);
		super.drawScreen(mouseX, mouseY, partialTicks);
	}

	class LanguageSlots extends fGuiSlot {

		private final java.util.List<String> langCodeList = Lists.newArrayList();
		private final Map<String, Language> languageMap = Maps.newHashMap();

		public LanguageSlots() {
			super(LanguageScreen.this.width, LanguageScreen.this.height, 32, LanguageScreen.this.height - 65 + 4, 18);

			for (Language language : LanguageScreen.this.languageManager.getLanguages()) {
				this.languageMap.put(language.getLanguageCode(), language);
				this.langCodeList.add(language.getLanguageCode());
			}
		}

		@Override
		protected int getSize() {
			return this.langCodeList.size();
		}

		@Override
		protected void elementClicked(int slotIndex, boolean isDoubleClick, int mouseX, int mouseY) {
			Language language = this.languageMap.get(this.langCodeList.get(slotIndex));
			LanguageScreen.this.languageManager.setCurrentLanguage(language);
			LanguageScreen.this.gameSettings.language = language.getLanguageCode();
			getMc().refreshResources();
			getFr().setUnicodeFlag(LanguageScreen.this.languageManager.isCurrentLocaleUnicode() || LanguageScreen.this.gameSettings.forceUnicodeFont);
			getFr().setBidiFlag(LanguageScreen.this.languageManager.isCurrentLanguageBidirectional());
			LanguageScreen.this.getButton(DONE).displayString = I18n.format("gui.done");
			LanguageScreen.this.getButton(UNICODE).displayString = LanguageScreen.this.gameSettings.getKeyBinding(GameSettings.Options.FORCE_UNICODE_FONT);
			saveSettings();
		}

		@Override
		protected boolean isSelected(int slotIndex) {
			return this.langCodeList.get(slotIndex).equals(LanguageScreen.this.languageManager.getCurrentLanguage().getLanguageCode());
		}

		@Override
		protected int getContentHeight() {
			return this.getSize() * 18;
		}

		@Override
		protected void drawBackground() {}

		@Override
		protected void drawSlot(int entryID, int x, int y, int p_180791_4_, int mouseXIn, int mouseYIn) {
			getFr().setBidiFlag(true);
			getFr().drawCenteredString(this.languageMap.get(this.langCodeList.get(entryID)).toString(), this.width / 2, y + 1, 16777215);
			getFr().setBidiFlag(LanguageScreen.this.languageManager.getCurrentLanguage().isBidirectional());
		}
	}
	
}
