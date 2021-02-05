package com.murengezi.feather.Module;

import com.darkmagician6.eventapi.EventManager;
import com.murengezi.feather.Event.ModuleDisableEvent;
import com.murengezi.feather.Event.ModuleEnableEvent;
import com.murengezi.feather.Module.Setting.Setting;
import com.murengezi.feather.Module.Setting.SettingType;
import com.murengezi.feather.Module.Setting.Settings.BooleanSetting;
import com.murengezi.feather.Module.Setting.Settings.ModeSetting;
import com.murengezi.feather.Module.Setting.Settings.NumberSetting;
import com.murengezi.feather.Util.MinecraftUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Tobias Sjöblom
 * Created on 2021-01-10 at 00:06
 */
public class Module extends MinecraftUtils {

    private String name = getClass().getAnnotation(ModuleInfo.class).name(),
            description = getClass().getAnnotation(ModuleInfo.class).description(),
            version = getClass().getAnnotation(ModuleInfo.class).version();
    private int keyBind = getClass().getAnnotation(ModuleInfo.class).keyBind();
    private boolean enabled = getClass().getAnnotation(ModuleInfo.class).enabled();

    private final List<Setting> settings;

    public Module() {
        if (isEnabled()) {
            EventManager.register(this);
        }
        settings = new ArrayList<>();
        Init();
    }

    protected void Init() {}

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public int getKeyBind() {
        return keyBind;
    }

    public void setKeyBind(int keyBind) {
        this.keyBind = keyBind;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public List<Setting> getSettings() {
        return settings;
    }

    public void addSetting(Setting setting) {
        getSettings().add(setting);
    }

    public Setting getSetting(String name) {
        return getSettings().stream().filter(setting -> setting.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
    }

    public ModeSetting getModeSetting(String name) {
        return (ModeSetting)getSettings().stream().filter(setting -> setting.getType() == SettingType.MODE).filter(setting -> setting.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
    }

    public NumberSetting getNumberSetting(String name) {
        return (NumberSetting)getSettings().stream().filter(setting -> setting.getType() == SettingType.NUMBER).filter(setting -> setting.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
    }

    public BooleanSetting getBooleanSetting(String name) {
        return (BooleanSetting)getSettings().stream().filter(setting -> setting.getType() == SettingType.BOOLEAN).filter(setting -> setting.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
    }

    public void Toggle(boolean save) {
        if (isEnabled()) {
            setEnabled(false);
            ModuleDisableEvent moduleDisableEvent = new ModuleDisableEvent(this, save);
            EventManager.call(moduleDisableEvent);
            EventManager.unregister(this);
        } else {
            setEnabled(true);
            EventManager.register(this);
            ModuleEnableEvent moduleEnableEvent = new ModuleEnableEvent(this, save);
            EventManager.call(moduleEnableEvent);
        }
    }
}
