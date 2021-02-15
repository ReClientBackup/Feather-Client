package com.murengezi.minecraft.client.gui;

import java.io.IOException;

import net.minecraft.client.gui.GuiOptionButton;
import net.minecraft.client.resources.I18n;

public class MemoryErrorScreen extends Screen {

    @Override
    public void initGui() {
        addButton(new GuiOptionButton(0, this.width / 2 - 155, this.height / 4 + 132, I18n.format("gui.toTitle")));
        addButton(new GuiOptionButton(1, this.width / 2 + 5, this.height / 4 + 132, I18n.format("menu.quit")));
        super.initGui();
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        if (button.getId() == 0) {
            changeScreen(new MainMenuScreen());
        } else if (button.getId() == 1) {
            getMc().shutdown();
        }
        super.actionPerformed(button);
    }

    protected void keyTyped(char typedChar, int keyCode) throws IOException {}

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawWorldBackground();
        getFr().drawCenteredString("Out of memory!", this.width / 2, this.height / 4 - 60 + 20, 16777215);
        getFr().drawString("Minecraft has run out of memory.", this.width / 2 - 140, this.height / 4 - 60 + 60, 10526880);
        getFr().drawString("This could be caused by a bug in the game or by the", this.width / 2 - 140, this.height / 4 - 60 + 60 + 18, 10526880);
        getFr().drawString("Java Virtual Machine not being allocated enough", this.width / 2 - 140, this.height / 4 - 60 + 60 + 27, 10526880);
        getFr().drawString("memory.", this.width / 2 - 140, this.height / 4 - 60 + 60 + 36, 10526880);
        getFr().drawString("To prevent level corruption, the current game has quit.", this.width / 2 - 140, this.height / 4 - 60 + 60 + 54, 10526880);
        getFr().drawString("We've tried to free up enough memory to let you go back to", this.width / 2 - 140, this.height / 4 - 60 + 60 + 63, 10526880);
        getFr().drawString("the main menu and back to playing, but this may not have worked.", this.width / 2 - 140, this.height / 4 - 60 + 60 + 72, 10526880);
        getFr().drawString("Please restart the game if you see this message again.", this.width / 2 - 140, this.height / 4 - 60 + 60 + 81, 10526880);
        super.drawScreen(mouseX, mouseY, partialTicks);
    }
}
