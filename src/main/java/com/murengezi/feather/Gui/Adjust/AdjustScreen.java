package com.murengezi.feather.Gui.Adjust;

import com.darkmagician6.eventapi.EventManager;
import com.darkmagician6.eventapi.EventTarget;
import com.murengezi.feather.Event.KeyboardPressEvent;
import com.murengezi.feather.Feather;
import com.murengezi.feather.Module.Adjustable;
import com.murengezi.feather.Module.Module;
import com.murengezi.feather.Util.MinecraftUtils;
import com.murengezi.minecraft.client.gui.Screen;
import net.minecraft.client.gui.Gui;
import com.murengezi.minecraft.client.gui.ScaledResolution;
import org.lwjgl.input.Keyboard;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Tobias Sjöblom
 * Created on 2021-01-29 at 10:47
 */
public class AdjustScreen extends Screen {

	private final List<Adjustable> mods;
	private Adjustable selected;

	private float dragX, dragY;

	public AdjustScreen() {
		mods = Feather.getModuleManager().getModules().stream().filter(module -> module instanceof Adjustable).map(module -> (Adjustable)module).collect(Collectors.toList());
		selected = null;
		EventManager.register(this);
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		ScaledResolution resolution = new ScaledResolution();

		getMods().stream().filter(Module::isEnabled).forEach(adjustable -> {
			if (isMouseOver(mouseX, mouseY, adjustable) || getSelected() == adjustable) {
				Gui.drawBox(adjustable.getX(), adjustable.getY(), adjustable.getWidth(), adjustable.getHeight(), 0xffffffff);
			}
		});

		if (getSelected() != null) {
			getSelected().setPosition(mouseX - getDragX(), mouseY - getDragY());
		}

		Gui.drawBox(0, 0, resolution.getScaledWidth(), resolution.getScaledHeight(), 0xfffea3aa);
		super.drawScreen(mouseX, mouseY, partialTicks);
	}

	@Override
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) {
		getMods().stream().filter(Module::isEnabled).forEach(adjustable -> {
			if (isMouseOver(mouseX, mouseY, adjustable)) {
				adjustable.setDragging(true);
				setSelected(adjustable);
				setDragX(mouseX - adjustable.getX());
				setDragY(mouseY - adjustable.getY());
			}
		});
		super.mouseClicked(mouseX, mouseY, mouseButton);
	}

	@Override
	protected void mouseReleased(int mouseX, int mouseY, int mouseButton) {
		if (getSelected() != null) {
			getSelected().setDragging(false);
			setSelected(null);
		}
		super.mouseReleased(mouseX, mouseY, mouseButton);
	}

	@Override
	public boolean doesGuiPauseGame() {
		return false;
	}

	@EventTarget
	public void onKeyPress(KeyboardPressEvent e) {
		if (e.getKey() == Keyboard.KEY_RSHIFT) {
			MinecraftUtils.getMc().displayGuiScreen(this);
		}
	}

	public boolean isMouseOver(int mouseX, int mouseY, Adjustable adjustable) {
		return mouseX >= adjustable.getX() && mouseX <= adjustable.getX() + adjustable.getWidth() && mouseY >= adjustable.getY() && mouseY <= adjustable.getY() + adjustable.getHeight();
	}

	public List<Adjustable> getMods() {
		return mods;
	}

	public Adjustable getSelected() {
		return selected;
	}

	public void setSelected(Adjustable selected) {
		this.selected = selected;
	}

	public float getDragX() {
		return dragX;
	}

	public void setDragX(float dragX) {
		this.dragX = dragX;
	}

	public float getDragY() {
		return dragY;
	}

	public void setDragY(float dragY) {
		this.dragY = dragY;
	}
}
