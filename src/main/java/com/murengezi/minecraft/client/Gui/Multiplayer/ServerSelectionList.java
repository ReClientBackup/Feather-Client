package com.murengezi.minecraft.client.Gui.Multiplayer;

import com.murengezi.minecraft.client.Gui.GuiListExtended;
import net.minecraft.client.multiplayer.ServerList;

import java.util.LinkedList;
import java.util.List;

/**
 * @author Tobias Sjöblom
 * Created on 2021-01-15 at 14:26
 */
public class ServerSelectionList extends GuiListExtended {

    private final MultiplayerScreen parentScreen;
    private final List<ServerListEntryNormal> normalEntries = new LinkedList<>();
    private int selectedSlotIndex = -1;

    public ServerSelectionList(MultiplayerScreen parentScreen, int width, int height, int top, int bottom, int slotHeight) {
        super(width, height, top, bottom, slotHeight);
        this.parentScreen = parentScreen;
    }

    public GuiListExtended.IGuiListEntry getListEntry(int index) {
        return this.normalEntries.get(index);
    }

    protected int getSize() {
        return this.normalEntries.size();
    }

    public void setSelectedSlotIndex(int selectedSlotIndexIn) {
        this.selectedSlotIndex = selectedSlotIndexIn;
    }

    protected boolean isSelected(int slotIndex) {
        return slotIndex == this.selectedSlotIndex;
    }

    public int getSelectedSlotIndex() {
        return this.selectedSlotIndex;
    }

    public void loadNormalEntries(ServerList serverList) {
        this.normalEntries.clear();

        for (int i = 0; i < serverList.countServers(); ++i) {
            this.normalEntries.add(new ServerListEntryNormal(this.parentScreen, serverList.getServerData(i)));
        }
    }

    protected int getScrollBarX() {
        return super.getScrollBarX() + 30;
    }

    public int getListWidth() {
        return super.getListWidth() + 85;
    }
}
