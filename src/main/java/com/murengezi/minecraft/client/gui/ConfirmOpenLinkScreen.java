package com.murengezi.minecraft.client.gui;

import com.murengezi.minecraft.client.gui.WorldSelection.YesNoCallback;
import net.minecraft.client.resources.I18n;

import java.io.IOException;

/**
 * @author Tobias Sjöblom
 * Created on 2021-01-20 at 15:42
 */
public class ConfirmOpenLinkScreen extends YesNoScreen {

    private final String openLinkWarning, copyLinkButtonText, linkText;
    private boolean showSecurityWarning = true;

    private static final int YES = 0;
    private static final int COPY = 1;
    private static final int NO = 2;

    public ConfirmOpenLinkScreen(YesNoCallback parentScreen, String linkTextIn, int parentScreenButtonId, boolean firstGui) {
        super(parentScreen, I18n.format(firstGui ? "chat.link.confirmTrusted" : "chat.link.confirm"), linkTextIn, I18n.format(firstGui ? "chat.link.open" : "gui.yes"), I18n.format(firstGui ? "gui.cancel" : "gui.no"), parentScreenButtonId);
        this.copyLinkButtonText = I18n.format("chat.copy");
        this.openLinkWarning = I18n.format("chat.link.warning");
        this.linkText = linkTextIn;
    }

    @Override
    public void initGui() {
        addButton(new GuiButton(0, this.width / 2 - 50 - 105, this.height / 6 + 96, 100, 20, this.getYesButtonText()));
        addButton(new GuiButton(2, this.width / 2 - 50, this.height / 6 + 96, 100, 20, this.copyLinkButtonText));
        addButton(new GuiButton(1, this.width / 2 - 50 + 105, this.height / 6 + 96, 100, 20, this.getNoButtonText()));

        //super.initGui(); Not create the other buttons
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        switch (button.getId()) {
            case YES:
            case NO:
                getParentScreen().confirmClicked(button.getId() == YES, this.getParentButtonClickedId());
                break;
            case COPY:
                setClipboardString(this.linkText);
                break;
        }
        //super.actionPerformed(button); Custom actions
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);

        if (this.showSecurityWarning) {
            this.drawCenteredString(this.fontRendererObj, this.openLinkWarning, this.width / 2, 110, 16764108);
        }
    }

    public void disableSecurityWarning() {
        this.showSecurityWarning = false;
    }
}
