package com.murengezi.feather.Gui.Click;

import com.darkmagician6.eventapi.EventManager;
import com.darkmagician6.eventapi.EventTarget;
import com.murengezi.feather.Event.KeyboardPressEvent;
import com.murengezi.feather.Feather;
import com.murengezi.feather.Gui.Click.Frames.ModuleFrame;
import com.murengezi.feather.Gui.Click.Frames.ThemesFrame;
import com.murengezi.feather.Gui.Click.Items.ModuleSettingsItem;
import com.murengezi.feather.Gui.Click.Items.ModuleStateItem;
import com.murengezi.feather.Gui.Click.Items.ThemeItem;
import com.murengezi.feather.Util.MinecraftUtils;
import com.murengezi.minecraft.client.gui.Screen;
import com.murengezi.minecraft.client.gui.ScaledResolution;
import org.lwjgl.input.Keyboard;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Tobias Sjöblom
 * Created on 2021-01-12 at 19:23
 */
public class ClickGui extends Screen {

    public List<Frame> frames;

    public ClickGui() {
        frames = new ArrayList<>();

        Feather.getModuleManager().getModules().forEach(module -> {
            ModuleFrame moduleFrame = new ModuleFrame(module, 10 + (frames.size() * 90), 10);
            moduleFrame.addItem(new ModuleStateItem(module));
            module.getSettings().forEach(setting -> moduleFrame.addItem(new ModuleSettingsItem(module, setting)));
            frames.add(moduleFrame);
        });
        ThemesFrame themesFrame = new ThemesFrame(10 + (frames.size() * 90), 10);
        Feather.getThemeManager().getThemes().forEach(theme -> themesFrame.addItem(new ThemeItem(theme)));
        frames.add(themesFrame);

        EventManager.register(this);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        ScaledResolution resolution = new ScaledResolution();
        frames.forEach(frame -> frame.render(mouseX, mouseY, resolution));
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        frames.forEach(frame -> frame.mouseClicked(mouseX, mouseY, mouseButton));
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int mouseButton) {
        frames.forEach(frame -> frame.mouseReleased(mouseX, mouseY, mouseButton));
        super.mouseReleased(mouseX, mouseY, mouseButton);
    }

    @Override
    public void onGuiClosed() {
        Feather.getConfigManager().saveAllConfigs();
        super.onGuiClosed();
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

    @EventTarget
    public void onKeyPress(KeyboardPressEvent e) {
        if (e.getKey() == Keyboard.KEY_RMENU) {
            MinecraftUtils.getMc().displayGuiScreen(this);
        }
    }
}
