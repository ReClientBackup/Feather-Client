package com.murengezi.minecraft.client.gui.InGame;

import java.io.IOException;

import com.murengezi.minecraft.client.gui.Chat.ChatScreen;
import com.murengezi.minecraft.client.gui.GuiButton;
import net.minecraft.client.resources.I18n;
import net.minecraft.network.play.client.C0BPacketEntityAction;
import org.lwjgl.input.Keyboard;

public class SleepMPScreen extends ChatScreen {

    private static final int STOP = 1;

    public SleepMPScreen() {
        super("");
    }

    @Override
    public void initGui() {
        super.initGui();
        addButton(new GuiButton(STOP, this.width / 2 - 100, this.height - 40, I18n.format("multiplayer.stopSleeping")));
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        if (keyCode == Keyboard.KEY_ESCAPE) {
            this.wakeFromSleep();
        } else if (keyCode != Keyboard.KEY_RETURN && keyCode != Keyboard.KEY_NUMPADENTER) {
            super.keyTyped(typedChar, keyCode);
        } else {
            String input = this.inputField.getText().trim();

            if (!input.isEmpty()) {
                getPlayer().sendChatMessage(input);
            }

            this.inputField.setText("");
            getMc().inGameScreen.getChatGUI().resetScroll();
        }
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        if (button.getId() == STOP) {
            this.wakeFromSleep();
        } else {
            super.actionPerformed(button);
        }
    }

    private void wakeFromSleep() {
        getPlayer().sendQueue.addToSendQueue(new C0BPacketEntityAction(getPlayer(), C0BPacketEntityAction.Action.STOP_SLEEPING));
    }
}
