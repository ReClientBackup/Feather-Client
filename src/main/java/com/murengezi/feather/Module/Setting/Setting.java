package com.murengezi.feather.Module.Setting;

import com.murengezi.feather.Module.Module;

/**
 * @author Tobias Sjöblom
 * Created on 2020-05-07 at 19:04
 */
public class Setting {

    private String name;
    private Module parent;
    private SettingType type;

    public Setting(String name, Module parent, SettingType type) {
        this.name = name;
        this.parent = parent;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public Module getParent() {
        return parent;
    }

    public SettingType getType() {
        return type;
    }
}
