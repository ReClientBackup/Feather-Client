package com.murengezi.feather.Module.Modules;

import com.darkmagician6.eventapi.EventTarget;
import com.murengezi.feather.Event.KeyboardEvent;
import com.murengezi.feather.Event.ModuleDisableEvent;
import com.murengezi.feather.Module.Module;
import com.murengezi.feather.Module.ModuleInfo;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;

import java.util.Properties;

/**
 * @author Tobias Sjöblom
 * Created on 2021-02-03 at 16:46
 */
@ModuleInfo(name = "Perspective", description = "", version = "1.0.0", enabled = true)
public class Perspective extends Module {

	private static boolean perspectiveToggled = false;
	private static float cameraYaw = 0.0f, cameraPitch = 0.0f;
	private static int previousPerspective;

	@EventTarget
	public void onKey(KeyboardEvent event) {
		if (event.getKey() == getGs().keyPerspective.getKeyCode()) {
			if (Keyboard.getEventKeyState()) {
				perspectiveToggled = !perspectiveToggled;

				cameraYaw = getPlayer().rotationYaw;
				cameraPitch = getPlayer().rotationPitch;

				if (perspectiveToggled) {
					previousPerspective = getGs().thirdPersonView;
					getGs().thirdPersonView = 1;
				} else {
					getGs().thirdPersonView = previousPerspective;
				}
			} else {
				perspectiveToggled = false;
				getGs().thirdPersonView = previousPerspective;
			}
		}

		if (event.getKey() == getGs().keyBindTogglePerspective.getKeyCode()) {
			perspectiveToggled = false;
		}
	}

	@EventTarget
	public void onDisable(ModuleDisableEvent event) {
		if (event.getModule() == this) {
			if (perspectiveToggled) {
				perspectiveToggled = false;
				getGs().thirdPersonView = previousPerspective;
			}
		}
	}

	public static float getCameraYaw() {
		return perspectiveToggled ? cameraYaw : getPlayer().rotationYaw;
	}

	public static float getCameraPitch() {
		return perspectiveToggled ? cameraPitch : getPlayer().rotationPitch;
	}

	public static boolean overrideMouse() {
		if (getMc().inGameHasFocus && Display.isActive()) {
			if (!perspectiveToggled) {
				return true;
			}
			getMc().mouseHelper.mouseXYChange();
			float f1 = getGs().mouseSensitivity * 0.6f + 0.2f;
			float f2 = (f1 * f1 * 8.0f);
			float f3 = (float) getMc().mouseHelper.deltaX * f2;
			float f4 = (float) getMc().mouseHelper.deltaY * f2;

			cameraYaw += f3 * 0.15f;
			cameraPitch += f4 * 0.15f;

			if (cameraPitch > 90.0f) {
				cameraPitch = 90.0f;
			} else if (cameraPitch < -90.0f) {
				cameraPitch = -90.0f;
			}

		}
		return false;
	}
}
