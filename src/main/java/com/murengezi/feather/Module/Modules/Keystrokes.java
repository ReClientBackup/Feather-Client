package com.murengezi.feather.Module.Modules;

import com.darkmagician6.eventapi.EventTarget;
import com.murengezi.feather.Event.*;
import com.murengezi.feather.Feather;
import com.murengezi.feather.Module.Adjustable;
import com.murengezi.feather.Module.ModuleInfo;
import com.murengezi.minecraft.client.gui.GUI;
import com.murengezi.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.EnumChatFormatting;
import org.lwjgl.input.Keyboard;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Tobias Sjöblom
 * Created on 2021-01-16 at 15:32
 */
@ModuleInfo(name = "Keystrokes", description = "", version = "1.0.0", enabled = true)
public class Keystrokes extends Adjustable {

	LetterKey keyW = new LetterKey("W", Keyboard.KEY_W);
	LetterKey keyA = new LetterKey("A", Keyboard.KEY_A);
	LetterKey keyS = new LetterKey("S", Keyboard.KEY_S);
	LetterKey keyD = new LetterKey("D", Keyboard.KEY_D);

	MouseKey mouseLeft = new MouseKey("LMB", 0);
	MouseKey mouseRight = new MouseKey("RMB", 1);

	SpaceKey spaceKey = new SpaceKey(EnumChatFormatting.STRIKETHROUGH + "----", Keyboard.KEY_SPACE);

	@EventTarget
	public void Render(RenderOverlayEvent event) {
		ScaledResolution resolution = event.getScaledResolution();
		float x = getX();
		float y = getY();

		int offset = 2;

		keyW.render(x + keyW.getWidth() + offset, y);
		keyA.render(x, y + keyA.getHeight() + offset);
		keyS.render(x + keyA.getWidth() + offset, y + keyS.getHeight() + offset);
		keyD.render(x + keyA.getWidth() + offset + keyS.getWidth() + offset, y + keyD.getHeight() + offset);

		mouseLeft.render(x, y + keyW.getHeight() + offset + keyA.getHeight() + offset);
		mouseRight.render(x + mouseLeft.getWidth() + offset, y + keyW.getHeight() + offset + keyD.getHeight() + offset);

		spaceKey.render(x, y + keyW.getHeight() + offset + keyA.getHeight() + offset + mouseLeft.getHeight() + offset);

		setWidth(KeyType.LETTER.getWidth() * 3 + 2 + 2);
		setHeight(KeyType.LETTER.getHeight() * 2 + 2 + 2 + KeyType.MOUSE.getHeight() + 2 + KeyType.SPACE.getHeight());
	}

	@EventTarget
	public void KeyboardPress(KeyboardPressEvent event) {
		if (event.getKey() == keyW.getKeyCode()) {
			keyW.press();
		}
		if (event.getKey() == keyA.getKeyCode()) {
			keyA.press();
		}
		if (event.getKey() == keyS.getKeyCode()) {
			keyS.press();
		}
		if (event.getKey() == keyD.getKeyCode()) {
			keyD.press();
		}

		if (event.getKey() == spaceKey.getKeyCode()) {
			spaceKey.press();
		}
	}

	@EventTarget
	public void KeyboardRelease(KeyboardReleaseEvent event) {
		if (event.getKey() == keyW.getKeyCode()) {
			keyW.release();
		}
		if (event.getKey() == keyA.getKeyCode()) {
			keyA.release();
		}
		if (event.getKey() == keyS.getKeyCode()) {
			keyS.release();
		}
		if (event.getKey() == keyD.getKeyCode()) {
			keyD.release();
		}

		if (event.getKey() == spaceKey.getKeyCode()) {
			spaceKey.release();
		}
	}

	@EventTarget
	public void MousePress(MousePressEvent event) {
		if (event.getMouseButton() == mouseLeft.mouseButton) {
			mouseLeft.press();
		}

		if (event.getMouseButton() == mouseRight.mouseButton) {
			mouseRight.press();
		}
	}

	@EventTarget
	public void MouseRelease(MouseReleaseEvent event) {
		if (event.getMouseButton() == mouseLeft.mouseButton) {
			mouseLeft.release();
		}

		if (event.getMouseButton() == mouseRight.mouseButton) {
			mouseRight.release();
		}
	}

	private enum KeyType {
		LETTER(20, 20), MOUSE(31, 20), SPACE(64, 10);

		private final int width, height;

		KeyType(int width, int height) {
			this.width = width;
			this.height = height;
		}

		public int getWidth() {
			return width;
		}

		public int getHeight() {
			return height;
		}
	}

	private abstract static class Key {

		private final KeyType keyType;
		private final int width, height;
		private int textColor, boxColor;
		private boolean pressed;
		private long lastPress;

		public Key(KeyType keyType) {
			this.keyType = keyType;
			this.width = keyType.getWidth();
			this.height = keyType.getHeight();
			this.textColor = 0xffffffff;
			this.boxColor = Integer.MIN_VALUE;
		}

		public void render(float x, float y) {
			Color color = Feather.getThemeManager().getActiveTheme().getColor();
			int red, green, blue;

			if (this.pressed) {
				red = Math.min(color.getRed(), (int)((System.currentTimeMillis() - this.lastPress)));
				green = Math.min(color.getGreen(), (int)((System.currentTimeMillis() - this.lastPress)));
				blue = Math.min(color.getBlue(), (int)((System.currentTimeMillis() - this.lastPress)));
			} else {
				red = Math.max(0, color.getRed() - (int)((System.currentTimeMillis() - this.lastPress)));
				green = Math.max(0, color.getGreen() - (int)((System.currentTimeMillis() - this.lastPress)));
				blue = Math.max(0, color.getBlue() - (int)((System.currentTimeMillis() - this.lastPress)));
			}
			setBoxColor(Integer.MIN_VALUE + (red << 16) + (green << 8) + blue);
			setTextColor(this.pressed ? color.getRGB() : 0xffffffff);
			GUI.drawRect(x, y, x + getWidth(), y + getHeight(), getBoxColor());
		}

		public void press() {
			if (!pressed) {
				pressed = true;
				lastPress = System.currentTimeMillis();
			}
		}

		public void release() {
			if (pressed) {
				pressed = false;
				lastPress = System.currentTimeMillis();
			}
		}

		public int getTextColor() {
			return textColor;
		}

		public void setTextColor(int textColor) {
			this.textColor = textColor;
		}

		public int getBoxColor() {
			return boxColor;
		}

		public void setBoxColor(int boxColor) {
			this.boxColor = boxColor;
		}

		public KeyType getKeyType() {
			return keyType;
		}

		public int getWidth() {
			return width;
		}

		public int getHeight() {
			return height;
		}

		public boolean isPressed() {
			return pressed;
		}
	}

	private static class LetterKey extends Key {

		private final String letter;
		private final int keyCode;

		public LetterKey(String letter, int keyCode) {
			super(KeyType.LETTER);
			this.letter = letter;
			this.keyCode = keyCode;
		}

		@Override
		public void render(float x, float y) {
			super.render(x, y);
			getFr().drawCenteredString(letter, x + (float)(getWidth() / 2) - 0.5f, (float)((y + getHeight() / 2) - (getFr().FONT_HEIGHT / 2)) + 0.5f, getTextColor());
		}

		public int getKeyCode() {
			return keyCode;
		}
	}

	private static class MouseKey extends Key {

		private final String display;
		private final int mouseButton;
		private List<Long> clicks = new ArrayList<>();


		public MouseKey(String display, int mouseButton) {
			super(KeyType.MOUSE);
			this.display = display;
			this.mouseButton = mouseButton;
		}

		@Override
		public void render(float x, float y) {
			super.render(x, y);
			getFr().drawCenteredString(display, x + (float)(getWidth() / 2) - 0.5f, (float)((y + getHeight() / 2) - (getFr().FONT_HEIGHT / 2)) - 2f, getTextColor());
			GlStateManager.pushMatrix();
			GlStateManager.scale(0.5f, 0.5f, 0.5f);
			getFr().drawCenteredString(getClicks() + " CPS", (x + (float)(getWidth() / 2) - 0.5f) * 2, ((float)((y + getHeight() / 2) - (getFr().FONT_HEIGHT / 2)) + 7.5f) * 2, getTextColor());
			GlStateManager.popMatrix();
		}

		@Override
		public void press() {
			super.press();
			if (this.isPressed()) {
				addClick();
			}
		}

		public void addClick() {
			clicks.add(System.currentTimeMillis());
		}

		public int getClicks() {
			clicks.removeIf(aLong -> aLong < System.currentTimeMillis() - 1000L);
			return clicks.size();
		}
	}

	private static class SpaceKey extends Key {

		private final String text;
		private final int keyCode;

		public SpaceKey(String text, int keyCode) {
			super(KeyType.SPACE);
			this.text = text;
			this.keyCode = keyCode;
		}

		@Override
		public void render(float x, float y) {
			super.render(x, y);
			getFr().drawCenteredString(text, x + getWidth() / 2, y + getHeight() / 2 - getFr().FONT_HEIGHT / 2 + 0.5f, getTextColor());
		}

		public int getKeyCode() {
			return keyCode;
		}
	}

}
