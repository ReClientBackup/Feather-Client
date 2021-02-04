package net.minecraft.client.gui;

public abstract class GuiListExtended extends GuiSlot {

    public GuiListExtended(int width, int height, int top, int bottom, int slotHeight) {
        super(width, height, top, bottom, slotHeight);
    }

    protected void elementClicked(int slotIndex, boolean isDoubleClick, int mouseX, int mouseY) {}

    protected boolean isSelected(int slotIndex) {
        return false;
    }

    protected void drawBackground() {}

    protected void drawSlot(int entryID, int x, int y, int p_180791_4_, int mouseXIn, int mouseYIn) {
        this.getListEntry(entryID).drawEntry(entryID, x, y, this.getListWidth(), p_180791_4_, mouseXIn, mouseYIn, this.getSlotIndexFromScreenCoords(mouseXIn, mouseYIn) == entryID);
    }

    protected void func_178040_a(int slotIndex, int x, int y) {
        this.getListEntry(slotIndex).setSelected(slotIndex, x, y);
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

    public boolean mouseReleased(int p_148181_1_, int p_148181_2_, int p_148181_3_) {
        for (int i = 0; i < this.getSize(); ++i) {
            int j = this.left + this.width / 2 - this.getListWidth() / 2 + 2;
            int k = this.top + 4 - this.getAmountScrolled() + i * this.slotHeight + this.headerPadding;
            int l = p_148181_1_ - j;
            int i1 = p_148181_2_ - k;
            this.getListEntry(i).mouseReleased(i, p_148181_1_, p_148181_2_, p_148181_3_, l, i1);
        }

        this.setEnabled(true);
        return false;
    }

    public abstract GuiListExtended.IGuiListEntry getListEntry(int index);

    public interface IGuiListEntry {
        void setSelected(int slotIndex, int x, int y);

        void drawEntry(int slotIndex, int x, int y, int listWidth, int slotHeight, int mouseX, int mouseY, boolean isSelected);

        boolean mousePressed(int slotIndex, int x, int y, int mouseEvent, int relativeX, int relativeY);

        void mouseReleased(int slotIndex, int x, int y, int mouseEvent, int relativeX, int relativeY);
    }
}
