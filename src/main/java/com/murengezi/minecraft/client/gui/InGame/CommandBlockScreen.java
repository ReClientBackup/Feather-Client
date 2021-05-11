package com.murengezi.minecraft.client.gui.InGame;

import com.murengezi.minecraft.client.gui.GuiButton;
import com.murengezi.minecraft.client.gui.Screen;
import io.netty.buffer.Unpooled;
import java.io.IOException;

import net.minecraft.client.gui.GuiTextField;
import com.murengezi.minecraft.client.resources.I18n;
import net.minecraft.command.server.CommandBlockLogic;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.client.C17PacketCustomPayload;
import org.lwjgl.input.Keyboard;

public class CommandBlockScreen extends Screen {

    private GuiTextField commandTextField;
    private GuiTextField previousOutputTextField;
    private final CommandBlockLogic commandBlockLogic;
    private boolean trackOutput;

    private static final int DONE = 0;
    private static final int CANCEL = 1;
    private static final int TRACK = 4;

    public CommandBlockScreen(CommandBlockLogic commandBlockLogic) {
        this.commandBlockLogic = commandBlockLogic;
    }

    @Override
    public void initGui() {
        Keyboard.enableRepeatEvents(true);
        this.buttonList.clear();
        addButton(new GuiButton(DONE, this.width / 2 - 4 - 150, this.height / 4 + 120 + 12, 150, 20, I18n.format("gui.done")));
        addButton(new GuiButton(CANCEL, this.width / 2 + 4, this.height / 4 + 120 + 12, 150, 20, I18n.format("gui.cancel")));
        addButton(new GuiButton(TRACK, this.width / 2 + 150 - 20, 150, 20, 20, "O"));

        this.commandTextField = new GuiTextField(2, this.width / 2 - 150, 50, 300, 20);
        this.commandTextField.setMaxStringLength(32767);
        this.commandTextField.setFocused(true);
        this.commandTextField.setText(this.commandBlockLogic.getCommand());

        this.previousOutputTextField = new GuiTextField(3, this.width / 2 - 150, 150, 276, 20);
        this.previousOutputTextField.setMaxStringLength(32767);
        this.previousOutputTextField.setEnabled(false);
        this.previousOutputTextField.setText("-");

        this.trackOutput = this.commandBlockLogic.shouldTrackOutput();
        this.switchTrackOutput();
        getButton(DONE).setEnabled(this.commandTextField.getText().trim().length() > 0);
        super.initGui();
    }

    @Override
    public void updateScreen() {
        this.commandTextField.updateCursorCounter();
        super.updateScreen();
    }

    @Override
    public void onGuiClosed() {
        Keyboard.enableRepeatEvents(false);
        super.onGuiClosed();
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        if (button.isEnabled()) {
            switch (button.getId()) {
                case DONE:
                    PacketBuffer packetbuffer = new PacketBuffer(Unpooled.buffer());
                    packetbuffer.writeByte(this.commandBlockLogic.func_145751_f());
                    this.commandBlockLogic.func_145757_a(packetbuffer);
                    packetbuffer.writeString(this.commandTextField.getText());
                    packetbuffer.writeBoolean(this.commandBlockLogic.shouldTrackOutput());
                    getMc().getNetHandler().addToSendQueue(new C17PacketCustomPayload("MC|AdvCdm", packetbuffer));

                    if (!this.commandBlockLogic.shouldTrackOutput())
                    {
                        this.commandBlockLogic.setLastOutput(null);
                    }

                    changeScreen(null);
                    break;
                case CANCEL:
                    this.commandBlockLogic.setTrackOutput(this.trackOutput);
                    changeScreen(null);
                    break;
                case TRACK:
                    this.commandBlockLogic.setTrackOutput(!this.commandBlockLogic.shouldTrackOutput());
                    this.switchTrackOutput();
                    break;
            }
        }
        super.actionPerformed(button);
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        this.commandTextField.textBoxKeyTyped(typedChar, keyCode);
        this.previousOutputTextField.textBoxKeyTyped(typedChar, keyCode);
        getButton(DONE).setEnabled(this.commandTextField.getText().trim().length() > 0);

        if (keyCode != 28 && keyCode != 156) {
            if (keyCode == 1) {
                this.actionPerformed(getButton(CANCEL));
            }
        } else {
            this.actionPerformed(getButton(DONE));
        }
        super.keyTyped(typedChar, keyCode);
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        this.commandTextField.mouseClicked(mouseX, mouseY, mouseButton);
        this.previousOutputTextField.mouseClicked(mouseX, mouseY, mouseButton);
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawWorldBackground(mouseX, mouseY, 60);
        getFr().drawCenteredString(I18n.format("advMode.setCommand"), this.width / 2, 20, 16777215);
        getFr().drawString(I18n.format("advMode.command"), this.width / 2 - 150, 37, 10526880);
        this.commandTextField.drawTextBox();
        int y = 75;
        getFr().drawString(I18n.format("advMode.nearestPlayer"), this.width / 2 - 150, y, 10526880);
        getFr().drawString(I18n.format("advMode.randomPlayer"), this.width / 2 - 150, y + getFr().FONT_HEIGHT, 10526880);
        getFr().drawString(I18n.format("advMode.allPlayers"), this.width / 2 - 150, y + 2 * getFr().FONT_HEIGHT, 10526880);
        getFr().drawString(I18n.format("advMode.allEntities"), this.width / 2 - 150, y + 3 * getFr().FONT_HEIGHT, 10526880);
        getFr().drawString("", this.width / 2 - 150, y + 4 * getFr().FONT_HEIGHT, 10526880);

        if (this.previousOutputTextField.getText().length() > 0)
        {
            y = y + 5 * getFr().FONT_HEIGHT + 16;
            getFr().drawString(I18n.format("advMode.previousOutput"), this.width / 2 - 150, y, 10526880);
            this.previousOutputTextField.drawTextBox();
        }

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

    private void switchTrackOutput() {
        if (this.commandBlockLogic.shouldTrackOutput()) {
            getButton(TRACK).displayString = "O";

            if (this.commandBlockLogic.getLastOutput() != null) {
                this.previousOutputTextField.setText(this.commandBlockLogic.getLastOutput().getUnformattedText());
            }
        } else {
            getButton(TRACK).displayString = "X";
            this.previousOutputTextField.setText("-");
        }
    }
}
