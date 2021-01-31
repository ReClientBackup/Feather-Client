package com.murengezi.feather.Module;

import com.darkmagician6.eventapi.EventManager;
import com.darkmagician6.eventapi.EventTarget;
import com.murengezi.feather.Event.KeyboardPressEvent;
import com.murengezi.feather.Event.RenderOverlayEvent;
import com.murengezi.feather.Feather;
import com.murengezi.feather.Module.Modules.*;
import com.murengezi.feather.Module.Modules.BetterHat;

import java.util.LinkedList;
import java.util.List;

/**
 * @author Tobias Sjöblom
 * Created on 2021-01-10 at 00:06
 */
public class ModuleManager {

    private List<Module> modules = new LinkedList<>();

    public ModuleManager() {
        load(new ToggleSprint());
        load(new Direction());
        load(new MouseCircle());
        load(new Blur());
        load(new Keystrokes());
        //load(new BetterHat());
        load(new Lefty());
        load(new FPS());
        load(new Scoreboard());

        EventManager.register(this);
    }

    public List<Module> getModules() {
        return modules;
    }

    public Module get(Class clazz) {
        return getModules().stream().filter(module -> module.getClass() == clazz).findFirst().orElse(null);
    }

    public void load(Module module) {
        getModules().add(module);
    }

    public void unload(Module module) {
        getModules().remove(module);
    }

    @EventTarget
    public void onKeyPress(KeyboardPressEvent event) {
        getModules().stream().filter(module -> module.getKeyBind() == event.getKey()).forEach(module -> module.Toggle(true));
    }

    @EventTarget
    public void onRender(RenderOverlayEvent event) {
        getModules().stream().filter(module -> module instanceof Adjustable).forEach(module -> ((Adjustable) module).render(event.getScaledResolution()));
    }
}
