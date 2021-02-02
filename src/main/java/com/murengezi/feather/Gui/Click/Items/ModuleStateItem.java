package com.murengezi.feather.Gui.Click.Items;

import com.murengezi.feather.Gui.Click.Item;
import com.murengezi.feather.Module.Module;
import com.murengezi.minecraft.client.gui.GUI;
import net.minecraft.client.renderer.GlStateManager;

/**
 * @author Tobias Sjöblom
 * Created on 2021-01-12 at 19:30
 */
public class ModuleStateItem extends Item {

    private final Module module;

    public ModuleStateItem(Module module) {
        this.module = module;
    }

    @Override
    public void render(int mouseX, int mouseY) {
        GUI.drawRect(getX(), getY(), getX() + getWidth(), getY() + getHeight(), Integer.MIN_VALUE);
        GUI.drawRect(getX() + 4, getY() + 2, getX() + 14, getY() + 12, Integer.MIN_VALUE);
        GlStateManager.pushMatrix();
        float scale = 0.5f;
        GlStateManager.scale(scale, scale, scale);
        getFr().drawStringWithShadow("Enabled", (getX() + 18) * (1 / scale), (getY() + 5) * (1/scale), 0xffffff);
        GlStateManager.popMatrix();
        if (getModule().isEnabled()) {
            getFr().drawStringWithShadow("✔", getX() + 5.5f, getY() + 2, 0xffffff);
        }
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (isMouseOn(mouseX, mouseY)) {
            if (mouseButton == 0) {
                getModule().Toggle(true);
            }
        }
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int mouseButton) {

    }

    public Module getModule() {
        return module;
    }
}
