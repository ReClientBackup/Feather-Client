package com.murengezi.feather.Module.Setting.Settings;

import com.murengezi.feather.Module.Module;
import com.murengezi.feather.Module.Setting.Setting;
import com.murengezi.feather.Module.Setting.SettingType;

import java.util.List;

/**
 * @author Tobias Sjöblom
 * Created on 2020-05-07 at 19:16
 */
public class ModeSetting extends Setting {

    private String value;
    private List<String> modes;

    public ModeSetting(String name, Module parent, List<String> modes, String mode) {
        super(name, parent, SettingType.MODE);
        this.modes = modes;
        this.value = mode;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public List<String> getModes() {
        return modes;
    }
}
