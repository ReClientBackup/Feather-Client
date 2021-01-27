package com.murengezi.feather.Theme;

import java.awt.*;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Tobias Sjöblom
 * Created on 2021-01-15 at 11:30
 */
public class ThemeManager {

    private final List<Theme> themes;
    private Theme activeTheme;

    public ThemeManager() {
        themes = new LinkedList<>();
        load(new Theme("Feather", "Default theme", "Tobias Sjöblom", ThemeType.DEFAULT, new Color(244, 241, 214)));
        load(new Theme("Speed", "Speed theme", "Tobias Sjöblom", ThemeType.BUILTIN, new Color(174, 198, 207)));
        load(new Theme("Ophelia", "Ophelia theme", "Molly Wågström", ThemeType.BUILTIN, new Color(255, 209, 220)));
        load(new Theme("Karl-Johan", "Karl-Johan theme", "Tobias Sjöblom", ThemeType.BUILTIN, new Color(170, 143, 121)));
        setActiveTheme(get("Feather"));
    }

    public List<Theme> getThemes() {
        return themes;
    }

    public void load(Theme theme) {
        getThemes().add(theme);
    }

    public void unload(Theme theme) {
        getThemes().remove(theme);
    }

    public Theme get(String name) {
        return getThemes().stream().filter(theme -> theme.getName().equals(name)).findFirst().orElse(null);
    }

    public Theme getActiveTheme() {
        return activeTheme;
    }

    public void setActiveTheme(Theme activeTheme) {
        this.activeTheme = activeTheme;
    }
}
