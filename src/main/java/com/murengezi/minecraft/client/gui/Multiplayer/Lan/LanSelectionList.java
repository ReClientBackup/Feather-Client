package com.murengezi.minecraft.client.gui.Multiplayer.Lan;

import com.murengezi.minecraft.client.gui.GuiListExtended;

import java.util.LinkedList;
import java.util.List;

/**
 * @author Tobias Sjöblom
 * Created on 2021-01-15 at 14:26
 */
public class LanSelectionList extends GuiListExtended {

    private final LanScreen parentScreen;
    private final List<GuiListExtended.IGuiListEntry> lanEntries = new LinkedList<>();
    private final GuiListExtended.IGuiListEntry lanScanEntry = new ServerListEntryLanScan();
    private int selectedSlotIndex = -1;

    public LanSelectionList(LanScreen parentScreen, int width, int height, int top, int bottom, int slotHeight) {
        super(width, height, top, bottom, slotHeight);
        this.parentScreen = parentScreen;
    }

    /**
     * Gets the IGuiListEntry object for the given index
     */
    public IGuiListEntry getListEntry(int index) {
        if (this.lanEntries.size() == 0) {
            return lanScanEntry;
        }
        return this.lanEntries.get(index);
    }

    protected int getSize() {
        return Math.max(this.lanEntries.size(), 1);
    }

    public void setSelectedSlotIndex(int selectedSlotIndexIn) {
        this.selectedSlotIndex = selectedSlotIndexIn;
    }

    /**
     * Returns true if the element passed in is currently selected
     */
    protected boolean isSelected(int slotIndex) {
        return slotIndex == this.selectedSlotIndex;
    }

    public int getSelectedSlotIndex() {
        return this.selectedSlotIndex;
    }

    public void loadLanEntries(List<LanServerDetector.LanServer> list) {
        this.lanEntries.clear();

        for (LanServerDetector.LanServer lanServer : list) {
            this.lanEntries.add(new ServerListEntryLanDetected(this.parentScreen, lanServer));
        }
    }

    protected int getScrollBarX() {
        return super.getScrollBarX() + 30;
    }

    public int getListWidth() {
        return super.getListWidth() + 85;
    }
}
