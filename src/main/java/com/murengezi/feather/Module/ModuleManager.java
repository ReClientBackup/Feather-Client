package com.murengezi.feather.Module;

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
}
