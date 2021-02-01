package net.optifine.gui;

import com.murengezi.minecraft.client.Gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiVideoSettings;

import java.io.IOException;
import java.util.List;

public class GuiScreenOF extends GuiScreen
{
    protected void actionPerformedRightClick(GuiButton button) throws IOException
    {
    }

    /**
     * Called when the mouse is clicked. Args : mouseX, mouseY, clickedButton
     */
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        super.mouseClicked(mouseX, mouseY, mouseButton);

        if (mouseButton == 1)
        {
            GuiButton guibutton = getSelectedButton(mouseX, mouseY, this.buttonList);

            if (guibutton != null && guibutton.isEnabled())
            {
                guibutton.playPressSound(this.mc.getSoundHandler());
                try {
                    this.actionPerformedRightClick(guibutton);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static GuiButton getSelectedButton(int x, int y, List<GuiButton> listButtons)
    {
        for (int i = 0; i < listButtons.size(); ++i)
        {
            GuiButton guibutton = listButtons.get(i);

            if (guibutton.isVisible())
            {
                int j = GuiVideoSettings.getButtonWidth(guibutton);
                int k = GuiVideoSettings.getButtonHeight(guibutton);

                if (x >= guibutton.getX() && y >= guibutton.getY() && x < guibutton.getX() + j && y < guibutton.getY() + k)
                {
                    return guibutton;
                }
            }
        }

        return null;
    }
}
