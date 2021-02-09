package com.murengezi.minecraft.client.gui.Multiplayer;

import com.murengezi.minecraft.client.gui.GuiButton;
import com.murengezi.minecraft.client.gui.Screen;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.IChatComponent;

import java.io.IOException;
import java.util.List;

/**
 * @author Tobias Sjöblom
 * Created on 2021-01-20 at 10:59
 */
public class DisconnectedScreen extends Screen {

    private final Screen previousScreen;
    private final String reason;
    private final IChatComponent message;
    private List<String> multilineMessage;
    private int messageHeight;

    private static final int MENU = 0;

    public DisconnectedScreen(Screen previousScreen, String reasonLocalizationKey, IChatComponent message) {
        this.previousScreen = previousScreen;
        this.reason = I18n.format(reasonLocalizationKey);
        this.message = message;
    }

    @Override
    public void initGui() {
        this.multilineMessage = getFr().listFormattedStringToWidth(this.message.getFormattedText(), this.width - 50);
        this.messageHeight = this.multilineMessage.size() * getFr().FONT_HEIGHT;
        addButton(new GuiButton(MENU, this.width / 2 - 100, this.height / 2 + this.messageHeight / 2 + getFr().FONT_HEIGHT, I18n.format("gui.toMenu")));
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
        this.drawWorldBackground(mouseX, mouseY, 60);
        getFr().drawCenteredString(this.reason, this.width / 2, this.height / 2 - this.messageHeight / 2 - getFr().FONT_HEIGHT * 2, 11184810);

        if (this.multilineMessage != null) {
            this.multilineMessage.forEach(string -> {
                getFr().drawCenteredString(string, this.width / 2, this.height / 2 - this.messageHeight / 2 + (this.multilineMessage.indexOf(string) * getFr().FONT_HEIGHT), 16777215);
            });
        }
        super.drawScreen(mouseX, mouseY, partialTicks);
    }
}
