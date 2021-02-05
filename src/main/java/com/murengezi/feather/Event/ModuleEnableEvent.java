package com.murengezi.feather.Event;

import com.darkmagician6.eventapi.events.Event;
import com.murengezi.feather.Module.Module;

/**
 * @author Tobias Sjöblom
 * Created on 2021-01-09 at 23:56
 */
public class ModuleEnableEvent implements Event {

    private final Module module;
    private final boolean save;

    public ModuleEnableEvent(Module module, boolean save) {
        this.module = module;
        this.save = save;
    }

    public Module getModule() {
        return module;
    }

    public boolean doSave() {
        return save;
    }
}