package com.murengezi.chocolate.Theme;

import java.awt.*;

/**
 * @author Tobias Sjöblom
 * Created on 2021-01-15 at 11:30
 */
public class Theme {

    private final String name, description, author;
    private final ThemeType type;
    private final Color color;

    public Theme(String name, String description, String author, ThemeType type, Color color) {
        this.name = name;
        this.description = description;
        this.author = author;
        this.type = type;
        this.color = color;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getAuthor() {
        return author;
    }

    public ThemeType getType() {
        return type;
    }

    public Color getColor() {
        return color;
    }
}
