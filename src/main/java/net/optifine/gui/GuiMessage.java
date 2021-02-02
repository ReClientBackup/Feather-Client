package net.optifine.gui;

import com.google.common.collect.Lists;
import com.murengezi.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiOptionButton;
import com.murengezi.minecraft.client.gui.Screen;
import net.minecraft.client.resources.I18n;
import net.minecraft.src.Config;

import java.io.IOException;
import java.util.List;

public class GuiMessage extends Screen
{
    private Screen parentScreen;
    private String messageLine1;
    private String messageLine2;
    private final List listLines2 = Lists.newArrayList();
    protected String confirmButtonText;
    private int ticksUntilEnable;

    public GuiMessage(Screen parentScreen, String line1, String line2)
    {
        this.parentScreen = parentScreen;
        this.messageLine1 = line1;
        this.messageLine2 = line2;
        this.confirmButtonText = I18n.format("gui.done", new Object[0]);
    }

    /**
     * Adds the buttons (and other controls) to the screen in question. Called when the GUI is displayed and when the
     * window resizes, the buttonList is cleared beforehand.
     */
    public void initGui()
    {
        this.buttonList.add(new GuiOptionButton(0, this.width / 2 - 74, this.height / 6 + 96, this.confirmButtonText));
        this.listLines2.clear();
        this.listLines2.addAll(getFr().listFormattedStringToWidth(this.messageLine2, this.width - 50));
    }

    /**
     * Called by the controls from the buttonList when activated. (Mouse pressed for buttons)
     */
    protected void actionPerformed(GuiButton button) throws IOException
    {
        Config.getMinecraft().displayGuiScreen(this.parentScreen);
    }

    /**
     * Draws the screen and all the components in it. Args : mouseX, mouseY, renderPartialTicks
     */
    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        this.drawDefaultBackground();
        getFr().drawCenteredString(this.messageLine1, this.width / 2, 70, 16777215);
        int i = 90;

        for (Object e : this.listLines2)
        {
            String s = (String) e;
            getFr().drawCenteredString(s, this.width / 2, i, 16777215);
            i += getFr().FONT_HEIGHT;
        }

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    public void setButtonDelay(int ticksUntilEnable)
    {
        this.ticksUntilEnable = ticksUntilEnable;

        for (GuiButton guibutton : this.buttonList)
        {
            guibutton.setEnabled(false);
        }
    }

    /**
     * Called from the main game loop to update the screen.
     */
    public void updateScreen()
    {
        super.updateScreen();

        if (--this.ticksUntilEnable == 0)
        {
            for (GuiButton guibutton : this.buttonList)
            {
                guibutton.setEnabled(true);
            }
        }
    }
}
