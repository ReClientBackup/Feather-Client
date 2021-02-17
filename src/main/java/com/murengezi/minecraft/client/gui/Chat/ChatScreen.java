package com.murengezi.minecraft.client.gui.Chat;

import com.google.common.collect.Lists;
import java.io.IOException;
import java.util.List;

import com.murengezi.minecraft.client.gui.Screen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.network.play.client.C14PacketTabComplete;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import org.apache.commons.lang3.StringUtils;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

public class ChatScreen extends Screen {

    private String historyBuffer = "";
    private int sentHistoryCursor = -1;
    private boolean playerNamesFound;
    private boolean waitingOnAutocomplete;
    private int autocompleteIndex;
    private final List<String> foundPlayerNames = Lists.newArrayList();
    protected GuiTextField inputField;
    private final String defaultInputFieldText;

    public ChatScreen(String defaultText) {
        this.defaultInputFieldText = defaultText;
    }

    @Override
    public void initGui() {
        Keyboard.enableRepeatEvents(true);
        this.sentHistoryCursor = getMc().inGameScreen.getChatGUI().getSentMessages().size();
        this.inputField = new GuiTextField(0, 4, this.height - 12, this.width - 4, 12);
        this.inputField.setMaxStringLength(100);
        this.inputField.setDrawBackground(false);
        this.inputField.setFocused(true);
        this.inputField.setText(this.defaultInputFieldText);
        this.inputField.setCanLoseFocus(false);
        super.initGui();
    }

    @Override
    public void onGuiClosed() {
        Keyboard.enableRepeatEvents(false);
        getMc().inGameScreen.getChatGUI().resetScroll();
        super.onGuiClosed();
    }

    @Override
    public void updateScreen() {
        this.inputField.updateCursorCounter();
        super.updateScreen();
    }

    //TODO Fix fullscreen bug
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        this.waitingOnAutocomplete = false;

        if (keyCode == 15) {
            this.autocompletePlayerNames();
        } else {
            this.playerNamesFound = false;
        }

        if (keyCode == 1) {
            changeScreen(null);
        } else if (keyCode != 28 && keyCode != 156) {
            if (keyCode == 200) {
                this.getSentHistory(-1);
            } else if (keyCode == 208) {
                this.getSentHistory(1);
            } else if (keyCode == 201) {
                getMc().inGameScreen.getChatGUI().scroll(getMc().inGameScreen.getChatGUI().getLineCount() - 1);
            } else if (keyCode == 209) {
                getMc().inGameScreen.getChatGUI().scroll(-getMc().inGameScreen.getChatGUI().getLineCount() + 1);
            } else {
                this.inputField.textBoxKeyTyped(typedChar, keyCode);
            }
        } else {
            String s = this.inputField.getText().trim();

            if (s.length() > 0) {
                this.sendChatMessage(s);
            }

            changeScreen(null);
        }
    }

    @Override
    public void handleMouseInput() throws IOException {
        int deltaWheel = Mouse.getEventDWheel();

        if (deltaWheel != 0) {
            if (deltaWheel > 1) {
                deltaWheel = 1;
            }

            if (deltaWheel < -1) {
                deltaWheel = -1;
            }

            if (!isShiftKeyDown()) {
                deltaWheel *= 7;
            }

            getMc().inGameScreen.getChatGUI().scroll(deltaWheel);
        }
        super.handleMouseInput();
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (mouseButton == 0) {
            IChatComponent ichatcomponent = getMc().inGameScreen.getChatGUI().getChatComponent(Mouse.getX(), Mouse.getY());

            if (this.handleComponentClick(ichatcomponent)) {
                return;
            }
        }

        this.inputField.mouseClicked(mouseX, mouseY, mouseButton);
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    protected void setText(String newChatText, boolean shouldOverwrite) {
        if (shouldOverwrite) {
            this.inputField.setText(newChatText);
        } else {
            this.inputField.writeText(newChatText);
        }
    }

    public void autocompletePlayerNames() {
        if (this.playerNamesFound) {
            this.inputField.deleteFromCursor(this.inputField.func_146197_a(-1, this.inputField.getCursorPosition(), false) - this.inputField.getCursorPosition());

            if (this.autocompleteIndex >= this.foundPlayerNames.size()) {
                this.autocompleteIndex = 0;
            }
        } else {
            int i = this.inputField.func_146197_a(-1, this.inputField.getCursorPosition(), false);
            this.foundPlayerNames.clear();
            this.autocompleteIndex = 0;
            String string = this.inputField.getText().substring(0, this.inputField.getCursorPosition());
            this.sendAutocompleteRequest(string);

            if (this.foundPlayerNames.isEmpty()) {
                return;
            }

            this.playerNamesFound = true;
            this.inputField.deleteFromCursor(i - this.inputField.getCursorPosition());
        }

        if (this.foundPlayerNames.size() > 1) {
            StringBuilder stringbuilder = new StringBuilder();

            for (String s2 : this.foundPlayerNames) {
                if (stringbuilder.length() > 0) {
                    stringbuilder.append(", ");
                }

                stringbuilder.append(s2);
            }

            getMc().inGameScreen.getChatGUI().printChatMessageWithOptionalDeletion(new ChatComponentText(stringbuilder.toString()), 1);
        }

        this.inputField.writeText(this.foundPlayerNames.get(this.autocompleteIndex++));
    }

    private void sendAutocompleteRequest(String string) {
        if (string.length() >= 1) {
            BlockPos blockPos = null;

            if (getMc().objectMouseOver != null && getMc().objectMouseOver.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) {
                blockPos = getMc().objectMouseOver.getBlockPos();
            }

            getPlayer().sendQueue.addToSendQueue(new C14PacketTabComplete(string, blockPos));
            this.waitingOnAutocomplete = true;
        }
    }

    public void getSentHistory(int msgPos)
    {
        int i = this.sentHistoryCursor + msgPos;
        int j = getMc().inGameScreen.getChatGUI().getSentMessages().size();
        i = MathHelper.clamp_int(i, 0, j);

        if (i != this.sentHistoryCursor) {
            if (i == j) {
                this.sentHistoryCursor = j;
                this.inputField.setText(this.historyBuffer);
            } else {
                if (this.sentHistoryCursor == j) {
                    this.historyBuffer = this.inputField.getText();
                }

                this.inputField.setText(getMc().inGameScreen.getChatGUI().getSentMessages().get(i));
                this.sentHistoryCursor = i;
            }
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawRect(2, this.height - 14, this.width - 2, this.height - 2, Integer.MIN_VALUE);
        this.inputField.drawTextBox();
        IChatComponent ichatcomponent = getMc().inGameScreen.getChatGUI().getChatComponent(Mouse.getX(), Mouse.getY());

        if (ichatcomponent != null && ichatcomponent.getChatStyle().getChatHoverEvent() != null) {
            this.handleComponentHover(ichatcomponent, mouseX, mouseY);
        }

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    public void onAutocompleteResponse(String[] p_146406_1_) {
        if (this.waitingOnAutocomplete) {
            this.playerNamesFound = false;
            this.foundPlayerNames.clear();

            for (String s : p_146406_1_) {
                if (s.length() > 0) {
                    this.foundPlayerNames.add(s);
                }
            }

            String s1 = this.inputField.getText().substring(this.inputField.func_146197_a(-1, this.inputField.getCursorPosition(), false));
            String s2 = StringUtils.getCommonPrefix(p_146406_1_);

            if (s2.length() > 0 && !s1.equalsIgnoreCase(s2)) {
                this.inputField.deleteFromCursor(this.inputField.func_146197_a(-1, this.inputField.getCursorPosition(), false) - this.inputField.getCursorPosition());
                this.inputField.writeText(s2);
            } else if (this.foundPlayerNames.size() > 0) {
                this.playerNamesFound = true;
                this.autocompletePlayerNames();
            }
        }
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

    public GuiTextField getInputField() {
        return inputField;
    }
}
