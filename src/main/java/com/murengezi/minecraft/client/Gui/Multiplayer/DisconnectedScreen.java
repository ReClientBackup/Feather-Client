package com.murengezi.minecraft.client.Gui.Multiplayer;

import com.murengezi.minecraft.client.Gui.GuiButton;
import com.murengezi.minecraft.client.Gui.Screen;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.IChatComponent;

import java.io.IOException;
import java.util.List;

/**
 * @author Tobias Sjöblom
 * Created on 2021-01-20 at 10:59
 */
public class DisconnectedScreen extends Screen {

    private final GuiScreen previousScreen;
    private String reason;
    private IChatComponent message;
    private List<String> multilineMessage;
    private int messageHeight;

    private static final int MENU = 0;

    public DisconnectedScreen(GuiScreen previousScreen, String reasonLocalizationKey, IChatComponent message) {
        this.previousScreen = previousScreen;
        this.reason = I18n.format(reasonLocalizationKey);
        this.message = message;
    }

    @Override
    public void initGui() {
        this.multilineMessage = this.fontRendererObj.listFormattedStringToWidth(this.message.getFormattedText(), this.width - 50);
        this.messageHeight = this.multilineMessage.size() * this.fontRendererObj.FONT_HEIGHT;
        addButton(new GuiButton(MENU, this.width / 2 - 100, this.height / 2 + this.messageHeight / 2 + this.fontRendererObj.FONT_HEIGHT, I18n.format("gui.toMenu")));
        super.initGui();
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        switch (button.getId()) {
            case MENU:
                changeScreen(previousScreen);
                break;
        }
        super.actionPerformed(button);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground(mouseX, mouseY, 60);
        this.drawCenteredString(this.fontRendererObj, this.reason, this.width / 2, this.height / 2 - this.messageHeight / 2 - this.fontRendererObj.FONT_HEIGHT * 2, 11184810);

        if (this.multilineMessage != null) {
            this.multilineMessage.forEach(string -> {
                this.drawCenteredString(this.fontRendererObj, string, this.width / 2, this.height / 2 - this.messageHeight / 2 + (this.multilineMessage.indexOf(string) * this.fontRendererObj.FONT_HEIGHT), 16777215);
            });
        }
        super.drawScreen(mouseX, mouseY, partialTicks);
    }
}
