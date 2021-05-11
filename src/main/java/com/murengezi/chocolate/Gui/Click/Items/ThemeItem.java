package com.murengezi.chocolate.Gui.Click.Items;

import com.murengezi.chocolate.Chocolate;
import com.murengezi.chocolate.Gui.Click.Item;
import com.murengezi.chocolate.Theme.Theme;
import com.murengezi.minecraft.client.gui.GUI;
import com.murengezi.minecraft.client.renderer.GlStateManager;

/**
 * @author Tobias Sjöblom
 * Created on 2021-01-15 at 12:28
 */
public class ThemeItem extends Item {

    private final Theme theme;

    public ThemeItem(Theme theme) {
        this.theme = theme;
    }

    @Override
    public void render(int mouseX, int mouseY) {
        GUI.drawRect(getX(), getY(), getX() + getWidth(), getY() + getHeight(), Integer.MIN_VALUE);
        GUI.drawRect(getX() + 4, getY() + 2, getX() + 14, getY() + 12, Integer.MIN_VALUE);
        GlStateManager.pushMatrix();
        float scale = 0.5f;
        GlStateManager.scale(scale, scale, scale);
        getFr().drawStringWithShadow(getTheme().getName(), (getX() + 18) * (1 / scale), (getY() + 5) * (1/scale), 0xffffff);
        GlStateManager.popMatrix();
        if (getTheme() == Chocolate.getThemeManager().getActiveTheme()) {
            getFr().drawStringWithShadow("✔", getX() + 5.5f, getY() + 2, 0xffffff);
        }
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (isMouseOn(mouseX, mouseY)) {
            if (mouseButton == 0) {
                Chocolate.getThemeManager().setActiveTheme(getTheme());
            }
        }
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int mouseButton) {

    }

    public Theme getTheme() {
        return theme;
    }
}
