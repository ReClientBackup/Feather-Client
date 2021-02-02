package com.murengezi.feather.Gui.Adjust;

import com.darkmagician6.eventapi.EventManager;
import com.darkmagician6.eventapi.EventTarget;
import com.murengezi.feather.Event.KeyboardPressEvent;
import com.murengezi.feather.Feather;
import com.murengezi.feather.Module.Adjustable;
import com.murengezi.feather.Module.Module;
import com.murengezi.feather.Util.MinecraftUtils;
import com.murengezi.minecraft.client.gui.Screen;
import com.murengezi.minecraft.client.gui.GUI;
import com.murengezi.minecraft.client.gui.ScaledResolution;
import org.lwjgl.input.Keyboard;

import java.util.Arrays;
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
			if (isMouseOver(mouseX, mouseY, adjustable) && getSelected() == null) {
				GUI.drawBox(adjustable.getX(), adjustable.getY(), adjustable.getWidth(), adjustable.getHeight(), 0xffffffff);
			}

			if (getSelected() == adjustable) {
				GUI.drawBox(adjustable.getX(), adjustable.getY(), adjustable.getWidth(), adjustable.getHeight(), 0xfffaf884);

			}
		});

		if (getSelected() != null) {
			getSelected().setPosition(mouseX - getDragX(), mouseY - getDragY());

			float x = getSelected().getX() + getSelected().getWidth() / 2;
			float y = getSelected().getY() + getSelected().getHeight() / 2;
			float resolutionWidth = resolution.getScaledWidth();
			float resolutionHeight = resolution.getScaledHeight();
			Adjustable.Region selectedRegion = getSelected().getRegion();
			GUI.drawRect((resolutionWidth / 3) * Math.max(selectedRegion.getWidthThirds() - 1, 0), (resolutionHeight / 3) * Math.max(selectedRegion.getHeightThirds() - 1, 0),
					(resolutionWidth / 3) * selectedRegion.getWidthThirds(), (resolutionHeight / 3) * selectedRegion.getHeightThirds(), Integer.MAX_VALUE);

			boolean adjustX = false, adjustY = false;
			for (Adjustable.Region region : Adjustable.Region.values()) {
				float regionWidthHalf = (resolutionWidth / 3) * region.getWidthThirds() - (resolutionWidth / 6);
				float regionHeightHalf = (resolutionHeight / 3) * region.getHeightThirds() - (resolutionHeight / 6);

				if (x >= regionWidthHalf - 10 && x <= regionWidthHalf + 10) {
					drawVerticalLine(regionWidthHalf, 0, resolutionHeight, 0xfffaf884);
					if (!adjustX) {
						getSelected().setAlignX(Adjustable.Align.CENTER);
						adjustX = true;
					}
				} else if (!adjustX) {
					getSelected().setAlignX(Adjustable.Align.NONE);
				}

				if (y >= regionHeightHalf - 10 && y <= regionHeightHalf + 10) {
					drawHorizontalLine(0, resolutionWidth, regionHeightHalf, 0xfffaf884);
					if (!adjustY) {
						getSelected().setAlignY(Adjustable.Align.CENTER);
						adjustY = true;
					}
				} else if (!adjustY) {
					getSelected().setAlignY(Adjustable.Align.NONE);
				}
			}
		}

		GUI.drawBox(0, 0, resolution.getScaledWidth(), resolution.getScaledHeight(), 0xfffaf884);
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
			getSelected().mouseReleased();
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
