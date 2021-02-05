package com.murengezi.minecraft.client.gui;

import com.murengezi.minecraft.client.gui.Singleplayer.YesNoCallback;
import net.minecraft.client.gui.GuiOptionButton;
import net.minecraft.client.resources.I18n;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Tobias Sjöblom
 * Created on 2021-01-20 at 15:23
 */
public class YesNoScreen extends Screen {

    private final YesNoCallback parentScreen;
    private final String messageLine1, messageLine2, yesButtonText, noButtonText;
    private final List<String> lines = new ArrayList<>();
    private final int parentButtonClickedId;

    private static final int YES = 0;
    private static final int NO = 1;

    public YesNoScreen(YesNoCallback parentScreen, String messageLine1, String messageLine2, int parentButtonClickedId) {
        this.parentScreen = parentScreen;
        this.messageLine1 = messageLine1;
        this.messageLine2 = messageLine2;
        this.parentButtonClickedId = parentButtonClickedId;
        this.yesButtonText = I18n.format("gui.yes");
        this.noButtonText = I18n.format("gui.no");
    }

    public YesNoScreen(YesNoCallback parentScreen, String messageLine1, String messageLine2, String yesButtonText, String noButtonText, int parentButtonClickedId) {
        this.parentScreen = parentScreen;
        this.messageLine1 = messageLine1;
        this.messageLine2 = messageLine2;
        this.yesButtonText = yesButtonText;
        this.noButtonText = noButtonText;
        this.parentButtonClickedId = parentButtonClickedId;
    }

    @Override
    public void initGui() {
        addButton(new GuiOptionButton(YES, this.width / 2 - 155, this.height / 6 + 96, this.yesButtonText));
        addButton(new GuiOptionButton(NO, this.width / 2 - 155 + 160, this.height / 6 + 96, this.noButtonText));
        this.lines.clear();
        this.lines.addAll(getFr().listFormattedStringToWidth(this.messageLine2, this.width - 50));
        super.initGui();
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        this.parentScreen.confirmClicked(button.getId() == YES, parentButtonClickedId);
        super.actionPerformed(button);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground(mouseX, mouseY, 60);
        getFr().drawCenteredString(this.messageLine1, this.width / 2, 70, 16777215);
        this.lines.forEach(line -> getFr().drawCenteredString(line, this.width / 2, 90 + (this.lines.indexOf(line) * getFr().FONT_HEIGHT), 16777215));
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    /*public void setButtonDelay(int p_146350_1_)
    {
        this.ticksUntilEnable = p_146350_1_;

        for (GuiButton guibutton : this.buttonList)
        {
            guibutton.setEnabled(false);
        }
    }

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
    }*/

    public YesNoCallback getParentScreen() {
        return parentScreen;
    }

    public String getYesButtonText() {
        return yesButtonText;
    }

    public String getNoButtonText() {
        return noButtonText;
    }

    public int getParentButtonClickedId() {
        return parentButtonClickedId;
    }
}
