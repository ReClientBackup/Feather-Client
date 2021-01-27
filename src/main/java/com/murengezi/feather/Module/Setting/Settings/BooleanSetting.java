package com.murengezi.feather.Module.Setting.Settings;

import com.murengezi.feather.Module.Module;
import com.murengezi.feather.Module.Setting.Setting;
import com.murengezi.feather.Module.Setting.SettingType;

/**
 * @author Tobias Sjöblom
 * Created on 2020-05-07 at 19:11
 */
public class BooleanSetting extends Setting {

    private boolean value;

    public BooleanSetting(String name, Module parent, Boolean value) {
        super(name, parent, SettingType.BOOLEAN);
        this.value = value;
    }

    public boolean getValue() {
        return this.value;
    }

    public void setValue(boolean value) {
        this.value = value;
    }

    public void toggleValue() {
        setValue(!getValue());
    }
}
