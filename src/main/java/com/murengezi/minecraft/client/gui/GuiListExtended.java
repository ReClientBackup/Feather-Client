package com.murengezi.minecraft.client.gui;

import net.minecraft.client.gui.GuiSlot;

public abstract class GuiListExtended extends GuiSlot {

    public GuiListExtended(int width, int height, int top, int bottom, int slotHeight) {
        super(width, height, top, bottom, slotHeight);
    }

    protected void elementClicked(int slotIndex, boolean isDoubleClick, int mouseX, int mouseY) {}

    protected boolean isSelected(int slotIndex) {
        return false;
    }

    protected void drawBackground() {}

    protected void drawSlot(int entryID, int x, int y, int slotHeight, int mouseX, int mouseY) {
        this.getListEntry(entryID).drawEntry(entryID, x, y, this.getListWidth(), slotHeight, mouseX, mouseY, this.getSlotIndexFromScreenCoords(mouseX, mouseY) == entryID);
    }

    protected void func_178040_a(int entryId, int p_178040_2_, int p_178040_3_) {
        this.getListEntry(entryId).setSelected(entryId, p_178040_2_, p_178040_3_);
    }

    public boolean mouseClicked(int mouseX, int mouseY, int mouseEvent) {
        if (this.isMouseYWithinSlotBounds(mouseY)) {
            int i = this.getSlotIndexFromScreenCoords(mouseX, mouseY);

            if (i >= 0) {
                int j = this.left + this.width / 2 - this.getListWidth() / 2 + 2;
                int k = this.top + 4 - this.getAmountScrolled() + i * this.slotHeight + this.headerPadding;
                int l = mouseX - j;
                int i1 = mouseY - k;

                if (this.getListEntry(i).mousePressed(i, mouseX, mouseY, mouseEvent, l, i1)) {
                    this.setEnabled(false);
                    return true;
                }
            }
        }
        return false;
    }

    public boolean mouseReleased(int mouseX, int mouseY, int mouseEvent) {
        for (int i = 0; i < this.getSize(); ++i) {
            int j = this.left + this.width / 2 - this.getListWidth() / 2 + 2;
            int k = this.top + 4 - this.getAmountScrolled() + i * this.slotHeight + this.headerPadding;
            int l = mouseX - j;
            int i1 = mouseY - k;
            this.getListEntry(i).mouseReleased(i, mouseX, mouseY, mouseEvent, l, i1);
        }

        this.setEnabled(true);
        return false;
    }



    public abstract IGuiListEntry getListEntry(int index);

    public interface IGuiListEntry {
        void drawEntry(int slotIndex, int x, int y, int listWidth, int slotHeight, int mouseX, int mouseY, boolean isSelected);
        boolean mousePressed(int slotIndex, int p_148278_2_, int p_148278_3_, int p_148278_4_, int p_148278_5_, int p_148278_6_);
        void mouseReleased(int slotIndex, int x, int y, int mouseEvent, int relativeX, int relativeY);
        void setSelected(int p_178011_1_, int p_178011_2_, int p_178011_3_);
    }
}
