package net.minecraft.client.gui;

import java.io.IOException;

import com.murengezi.minecraft.client.gui.GuiButton;
import com.murengezi.minecraft.client.gui.MainMenuScreen;
import com.murengezi.minecraft.client.gui.Screen;
import com.murengezi.minecraft.client.gui.WorldSelection.YesNoCallback;
import com.murengezi.minecraft.client.gui.YesNoScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.EnumChatFormatting;

public class GuiGameOver extends Screen implements YesNoCallback
{
    /**
     * The integer value containing the number of ticks that have passed since the player's death
     */
    private int enableButtonsTimer;
    private boolean field_146346_f = false;

    /**
     * Adds the buttons (and other controls) to the screen in question. Called when the GUI is displayed and when the
     * window resizes, the buttonList is cleared beforehand.
     */
    public void initGui()
    {
        this.buttonList.clear();

        if (getWorld().getWorldInfo().isHardcoreModeEnabled())
        {
            if (getMc().isIntegratedServerRunning())
            {
                this.buttonList.add(new GuiButton(1, this.width / 2 - 100, this.height / 4 + 96, I18n.format("deathScreen.deleteWorld", new Object[0])));
            }
            else
            {
                this.buttonList.add(new GuiButton(1, this.width / 2 - 100, this.height / 4 + 96, I18n.format("deathScreen.leaveServer", new Object[0])));
            }
        }
        else
        {
            this.buttonList.add(new GuiButton(0, this.width / 2 - 100, this.height / 4 + 72, I18n.format("deathScreen.respawn", new Object[0])));
            this.buttonList.add(new GuiButton(1, this.width / 2 - 100, this.height / 4 + 96, I18n.format("deathScreen.titleScreen", new Object[0])));

            if (getMc().getSession() == null)
            {
                this.buttonList.get(1).setEnabled(false);
            }
        }

        for (GuiButton guibutton : this.buttonList)
        {
            guibutton.setEnabled(false);
        }
    }

    /**
     * Fired when a key is typed (except F11 which toggles full screen). This is the equivalent of
     * KeyListener.keyTyped(KeyEvent e). Args : character (character on the key), keyCode (lwjgl Keyboard key code)
     */
    protected void keyTyped(char typedChar, int keyCode) throws IOException
    {
    }

    /**
     * Called by the controls from the buttonList when activated. (Mouse pressed for buttons)
     */
    protected void actionPerformed(GuiButton button) throws IOException
    {
        switch (button.getId())
        {
            case 0:
                getPlayer().respawnPlayer();
                changeScreen(null);
                break;

            case 1:
                if (getWorld().getWorldInfo().isHardcoreModeEnabled()) {
                    changeScreen(new MainMenuScreen());
                } else {
                    YesNoScreen yesNoScreen = new YesNoScreen(this, I18n.format("deathScreen.quit.confirm"), "", I18n.format("deathScreen.titleScreen", new Object[0]), I18n.format("deathScreen.respawn", new Object[0]), 0);
                    changeScreen(yesNoScreen);
                    //guiyesno.setButtonDelay(20);
                }
        }
    }

    public void confirmClicked(boolean result, int id)
    {
        if (result)
        {
            getWorld().sendQuittingDisconnectingPacket();
            getMc().loadWorld(null);
            changeScreen(new MainMenuScreen());
        }
        else
        {
            getPlayer().respawnPlayer();
            changeScreen(null);
        }
    }

    /**
     * Draws the screen and all the components in it. Args : mouseX, mouseY, renderPartialTicks
     */
    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        this.drawGradientRect(0, 0, this.width, this.height, 1615855616, -1602211792);
        GlStateManager.pushMatrix();
        GlStateManager.scale(2.0F, 2.0F, 2.0F);
        boolean flag = getWorld().getWorldInfo().isHardcoreModeEnabled();
        String s = flag ? I18n.format("deathScreen.title.hardcore") : I18n.format("deathScreen.title");
        getFr().drawCenteredString(s, this.width / 2 / 2, 30, 16777215);
        GlStateManager.popMatrix();

        if (flag)
        {
            getFr().drawCenteredString(I18n.format("deathScreen.hardcoreInfo"), this.width / 2, 144, 16777215);
        }

        getFr().drawCenteredString(I18n.format("deathScreen.score") + ": " + EnumChatFormatting.YELLOW + getPlayer().getScore(), this.width / 2, 100, 16777215);
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    /**
     * Returns true if this GUI should pause the game when it is displayed in single-player
     */
    public boolean doesGuiPauseGame()
    {
        return false;
    }

    /**
     * Called from the main game loop to update the screen.
     */
    public void updateScreen()
    {
        super.updateScreen();
        ++this.enableButtonsTimer;

        if (this.enableButtonsTimer == 20)
        {
            for (GuiButton guibutton : this.buttonList)
            {
                guibutton.setEnabled(true);
            }
        }
    }
}
