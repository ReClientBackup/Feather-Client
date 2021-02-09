package com.murengezi.minecraft.client.gui.Options;

import com.murengezi.minecraft.client.gui.GuiButton;
import com.murengezi.minecraft.client.gui.Screen;
import net.minecraft.client.gui.GuiOptionButton;
import net.minecraft.client.gui.GuiOptionSlider;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.util.EnumChatFormatting;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Tobias Sjöblom
 * Created on 2021-01-14 at 19:31
 */
public class ChatOptionsScreen extends Screen {

    private final Screen previousScreen;
    private static final List<GameSettings.Options> chatOptions = new ArrayList<>(Arrays.asList(
            GameSettings.Options.CHAT_VISIBILITY,
            GameSettings.Options.CHAT_COLOR,
            GameSettings.Options.CHAT_LINKS,
            GameSettings.Options.CHAT_OPACITY,
            GameSettings.Options.CHAT_LINKS_PROMPT,
            GameSettings.Options.CHAT_SCALE,
            GameSettings.Options.CHAT_HEIGHT_FOCUSED,
            GameSettings.Options.CHAT_HEIGHT_UNFOCUSED,
            GameSettings.Options.CHAT_WIDTH,
            GameSettings.Options.REDUCED_DEBUG_INFO));

    private static final int DONE = 0;

    public ChatOptionsScreen(Screen previousScreen) {
        this.previousScreen = previousScreen;
    }

    @Override
    public void initGui() {
        chatOptions.forEach(option -> {
            int index = chatOptions.indexOf(option);
            if (option.getEnumFloat()) {
                this.buttonList.add(new GuiOptionSlider(option.returnEnumOrdinal(), this.width / 2 - 155 + index % 2 * 160, this.height / 6 + 42 + 24 * (index >> 1), option));
            } else {
                this.buttonList.add(new GuiOptionButton(option.returnEnumOrdinal(), this.width / 2 - 155 + index % 2 * 160, this.height / 6 + 42 + 24 * (index >> 1), option, getGs().getKeyBinding(option)));
            }
        });

        addButton(new GuiButton(DONE, this.width / 2 - 100, this.height / 6 + 168, I18n.format("gui.done")));
        super.initGui();
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        if (button.isEnabled()) {
            switch (button.getId()) {
                case DONE:
                    saveSettings();
                    changeScreen(previousScreen);
                    break;
                default:
                    if (button instanceof GuiOptionButton) {
                        getGs().setOptionValue(((GuiOptionButton)button).getOptions(), 1);
                        button.displayString = getGs().getKeyBinding(GameSettings.Options.getEnumOptions(button.getId()));
                    }
                    break;
            }
        }
        super.actionPerformed(button);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawWorldBackground(mouseX, mouseY, 60);
        drawRect(this.width / 2 - 160, this.height / 6 + 15, this.width / 2 + 160, this.height / 6 + 193, Integer.MIN_VALUE);
        drawCenteredString(getFr(), EnumChatFormatting.UNDERLINE + I18n.format("options.chat.title"), this.width / 2, this.height / 6 + 20, 0xffffffff);
        super.drawScreen(mouseX, mouseY, partialTicks);
    }
}
