package com.murengezi.feather.Module.Setting.Settings;

import com.murengezi.feather.Module.Module;
import com.murengezi.feather.Module.Setting.Setting;
import com.murengezi.feather.Module.Setting.SettingType;

/**
 * @author Tobias Sjöblom
 * Created on 2020-05-07 at 19:11
 */
public class NumberSetting extends Setting {

    private float min, max, value, step;

    public NumberSetting(String name, Module parent, float min, float max, float value, float step) {
        super(name, parent, SettingType.NUMBER);
        this.min = min;
        this.max = max;
        this.value = value;
        this.step = step;
    }

    public float getMin() {
        return min;
    }

    public void setMin(float min) {
        this.min = min;
    }

    public float getMax() {
        return max;
    }

    public void setMax(float max) {
        this.max = max;
    }

    public float getValue() {
        return value;
    }

    public void setValue(float value) {
        this.value = value;
    }

    public float getStep() {
        return step;
    }

    public void setStep(float step) {
        this.step = step;
    }
}
