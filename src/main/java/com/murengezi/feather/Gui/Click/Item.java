package com.murengezi.feather.Gui.Click;

import com.murengezi.feather.Util.MinecraftUtils;

/**
 * @author Tobias Sjöblom
 * Created on 2021-01-12 at 19:28
 */
public abstract class Item extends MinecraftUtils {

    private int x, y;

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public void setLocation(int x, int y) {
        setX(x);
        setY(y);
    }

    public int getWidth() {
        return 76;
    }

    public int getHeight() {
        return 14;
    }

    public boolean isMouseOn(int mouseX, int mouseY) {
        return mouseX >= getX() && mouseX <= getX() + getWidth() && mouseY >= getY() && mouseY <= getY() + getHeight();
    }

    public abstract void render(int mouseX, int mouseY);
    public abstract void mouseClicked(int mouseX, int mouseY, int mouseButton);
    public abstract void mouseReleased(int mouseX, int mouseY, int mouseButton);
}
