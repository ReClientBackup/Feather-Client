package com.murengezi.minecraft.client.gui.Multiplayer.Lan;

import com.murengezi.chocolate.Util.MinecraftUtils;
import com.murengezi.minecraft.client.gui.GuiListExtended;
import com.murengezi.minecraft.client.Minecraft;
import com.murengezi.minecraft.client.resources.I18n;

public class ServerListEntryLanScan extends MinecraftUtils implements GuiListExtended.IGuiListEntry {

    public void drawEntry(int slotIndex, int x, int y, int listWidth, int slotHeight, int mouseX, int mouseY, boolean isSelected) {
        int i = y + slotHeight / 2 - getFr().FONT_HEIGHT / 2;
        String s = "O o o o o";

        switch ((int)(Minecraft.getSystemTime() / 200L % 8L))
        {
            case 0:
                s = "O o o o o";
                break;
            case 1:
            case 7:
                s = "o O o o o";
                break;
            case 2:
            case 6:
                s = "o o O o o";
                break;
            case 3:
            case 5:
                s = "o o o O o";
                break;
            case 4:
                s = "o o o o O";
                break;
        }
        getFr().drawStringWithShadow(s, getMc().currentScreen.width / 2 - getFr().getStringWidth(s) / 2, i, 8421504);
        getFr().drawStringWithShadow(I18n.format("lanServer.scanning"), (float)(getMc().currentScreen.width / 2 - getFr().getStringWidth(I18n.format("lanServer.scanning")) / 2), i + getFr().FONT_HEIGHT, 16777215);

    }

    public boolean mousePressed(int slotIndex, int p_148278_2_, int p_148278_3_, int p_148278_4_, int p_148278_5_, int p_148278_6_) {
        return false;
    }

    public void mouseReleased(int slotIndex, int x, int y, int mouseEvent, int relativeX, int relativeY) {

    }

    public void setSelected(int p_178011_1_, int p_178011_2_, int p_178011_3_) {

    }
}
