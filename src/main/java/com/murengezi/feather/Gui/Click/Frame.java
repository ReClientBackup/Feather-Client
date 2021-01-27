package com.murengezi.feather.Gui.Click;

import com.murengezi.feather.Util.MinecraftUtils;
import net.minecraft.client.gui.ScaledResolution;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Tobias Sjöblom
 * Created on 2021-01-12 at 19:25
 */
public abstract class Frame extends MinecraftUtils {

    private final String title;
    private int x, y, dragX, dragY;
    private boolean selected;

    private final List<Item> items;

    public Frame(String title, int x, int y) {
        this.title = title;
        this.x = x;
        this.y = y;

        items = new ArrayList<>();
    }

    public String getTitle() {
        return title;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getDragX() {
        return dragX;
    }

    public void setDragX(int dragX) {
        this.dragX = dragX;
    }

    public int getDragY() {
        return dragY;
    }

    public void setDragY(int dragY) {
        this.dragY = dragY;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public List<Item> getItems() {
        return items;
    }

    public void addItem(Item item) {
        getItems().add(item);
    }

    public int getWidth() {
        return 80;
    }

    public int getHeight() {
        return 14;
    }

    public boolean isMouseOn(int mouseX, int mouseY) {
        return mouseX >= getX() && mouseX <= getX() + getWidth() && mouseY >= getY() && mouseY <= getY() + getHeight();
    }

    public void render(int mouseX, int mouseY, ScaledResolution scaledResolution) {
        if (isSelected()) {
            setX(mouseX - getDragX());
            setY(mouseY - getDragY());
        }

        getItems().forEach(item -> {
            item.render(mouseX, mouseY);
            item.setLocation(getX() + ((getWidth() - item.getWidth()) / 2), getY() + (getItems().indexOf(item) * item.getHeight()) + item.getHeight());
        });
    }

    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (isMouseOn(mouseX, mouseY)) {
            if (mouseButton == 0) {
                setSelected(true);

                setDragX(mouseX - getX());
                setDragY(mouseY - getY());
            }
        } else {
            getItems().forEach(item -> item.mouseClicked(mouseX, mouseY, mouseButton));
        }
    }

    public void mouseReleased(int mouseX, int mouseY, int mouseButton) {
        if (isSelected()) {
            setSelected(false);
        }
        getItems().forEach(item -> item.mouseReleased(mouseX, mouseY, mouseButton));
    }
}
