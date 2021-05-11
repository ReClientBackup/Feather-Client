package net.minecraft.client.gui;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.murengezi.minecraft.client.gui.GUI;
import com.murengezi.minecraft.client.gui.Screen;
import com.murengezi.minecraft.client.renderer.GlStateManager;
import com.murengezi.minecraft.client.renderer.Tessellator;
import com.murengezi.minecraft.client.renderer.WorldRenderer;
import com.murengezi.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ChatAllowedCharacters;
import net.minecraft.util.MathHelper;

public class GuiTextField extends GUI {

    private final int id;
    public int x, y;
    private final int width, height;
    private String text = "";
    private int maxStringLength = 32;
    private int cursorCounter;
    private boolean drawBackground = true, canLoseFocus = true, focused, enabled = true;
    private int lineScrollOffset, cursorPosition, selectionEnd, enabledColor = 14737632, disabledColor = 7368816;

    private boolean visible = true;
    private GuiPageButtonList.GuiResponder field_175210_x;
    private Predicate<String> field_175209_y = Predicates.alwaysTrue();

    public GuiTextField(int componentId, int x, int y, int width, int height) {
        this.id = componentId;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public void func_175207_a(GuiPageButtonList.GuiResponder p_175207_1_) {
        this.field_175210_x = p_175207_1_;
    }

    public void updateCursorCounter() {
        ++this.cursorCounter;
    }

    public void setText(String text) {
        if (this.field_175209_y.apply(text)) {
            if (text.length() > this.maxStringLength) {
                this.text = text.substring(0, this.maxStringLength);
            } else {
                this.text = text;
            }

            this.setCursorPositionEnd();
        }
    }

    public String getText() {
        return this.text;
    }

    public String getSelectedText() {
        int i = this.cursorPosition < this.selectionEnd ? this.cursorPosition : this.selectionEnd;
        int j = this.cursorPosition < this.selectionEnd ? this.selectionEnd : this.cursorPosition;
        return this.text.substring(i, j);
    }

    public void func_175205_a(Predicate<String> p_175205_1_)
    {
        this.field_175209_y = p_175205_1_;
    }

    public void writeText(String text) {
        String s = "";
        String s1 = ChatAllowedCharacters.filterAllowedCharacters(text);
        int i = Math.min(this.cursorPosition, this.selectionEnd);
        int j = Math.max(this.cursorPosition, this.selectionEnd);
        int k = this.maxStringLength - this.text.length() - (i - j);
        int l;

        if (this.text.length() > 0) {
            s = s + this.text.substring(0, i);
        }

        if (k < s1.length()) {
            s = s + s1.substring(0, k);
            l = k;
        } else {
            s = s + s1;
            l = s1.length();
        }

        if (this.text.length() > 0 && j < this.text.length()) {
            s = s + this.text.substring(j);
        }

        if (this.field_175209_y.apply(s)) {
            this.text = s;
            this.moveCursorBy(i - this.selectionEnd + l);

            if (this.field_175210_x != null) {
                this.field_175210_x.func_175319_a(this.id, this.text);
            }
        }
    }

    public void deleteWords(int position) {
        if (this.text.length() != 0) {
            if (this.selectionEnd != this.cursorPosition) {
                this.writeText("");
            } else {
                this.deleteFromCursor(this.getNthWordFromCursor(position) - this.cursorPosition);
            }
        }
    }

    public void deleteFromCursor(int position) {
        if (this.text.length() != 0) {
            if (this.selectionEnd != this.cursorPosition) {
                this.writeText("");
            } else {
                boolean flag = position < 0;
                int i = flag ? this.cursorPosition + position : this.cursorPosition;
                int j = flag ? this.cursorPosition : this.cursorPosition + position;
                String s = "";

                if (i >= 0) {
                    s = this.text.substring(0, i);
                }

                if (j < this.text.length()) {
                    s = s + this.text.substring(j);
                }

                if (this.field_175209_y.apply(s)) {
                    this.text = s;

                    if (flag) {
                        this.moveCursorBy(position);
                    }

                    if (this.field_175210_x != null) {
                        this.field_175210_x.func_175319_a(this.id, this.text);
                    }
                }
            }
        }
    }

    public int getId()
    {
        return this.id;
    }

    public int getNthWordFromCursor(int position) {
        return this.getNthWordFromPos(position, this.getCursorPosition());
    }

    public int getNthWordFromPos(int position, int cursorPosition) {
        return this.func_146197_a(position, cursorPosition, true);
    }

    public int func_146197_a(int position, int cursorPosition, boolean p_146197_3_) {
        int i = cursorPosition;
        boolean flag = position < 0;
        int j = Math.abs(position);

        for (int k = 0; k < j; ++k) {
            if (!flag) {
                int l = this.text.length();
                i = this.text.indexOf(32, i);

                if (i == -1) {
                    i = l;
                } else {
                    while (p_146197_3_ && i < l && this.text.charAt(i) == 32) {
                        ++i;
                    }
                }
            } else {
                while (p_146197_3_ && i > 0 && this.text.charAt(i - 1) == 32) {
                    --i;
                }

                while (i > 0 && this.text.charAt(i - 1) != 32) {
                    --i;
                }
            }
        }

        return i;
    }

    public void moveCursorBy(int p_146182_1_) {
        this.setCursorPosition(this.selectionEnd + p_146182_1_);
    }

    public void setCursorPosition(int cursorPosition) {
        this.cursorPosition = cursorPosition;
        int i = this.text.length();
        this.cursorPosition = MathHelper.clamp_int(this.cursorPosition, 0, i);
        this.setSelectionPos(this.cursorPosition);
    }

    public void setCursorPositionZero() {
        this.setCursorPosition(0);
    }

    public void setCursorPositionEnd() {
        this.setCursorPosition(this.text.length());
    }

    public boolean textBoxKeyTyped(char typedChar, int keyCode) {
        if (!this.focused) {
            return false;
        } else if (Screen.isKeyComboCtrlA(keyCode)) {
            this.setCursorPositionEnd();
            this.setSelectionPos(0);
            return true;
        } else if (Screen.isKeyComboCtrlC(keyCode)) {
            Screen.setClipboardString(this.getSelectedText());
            return true;
        } else if (Screen.isKeyComboCtrlV(keyCode)) {
            if (this.enabled) {
                this.writeText(Screen.getClipboardString());
            }

            return true;
        } else if (Screen.isKeyComboCtrlX(keyCode)) {
            Screen.setClipboardString(this.getSelectedText());

            if (this.enabled) {
                this.writeText("");
            }

            return true;
        } else {
            switch (keyCode) {
                case 14:
                    if (Screen.isCtrlKeyDown()) {
                        if (this.enabled) {
                            this.deleteWords(-1);
                        }
                    } else if (this.enabled) {
                        this.deleteFromCursor(-1);
                    }
                    return true;
                case 199:
                    if (Screen.isShiftKeyDown()) {
                        this.setSelectionPos(0);
                    } else {
                        this.setCursorPositionZero();
                    }
                    return true;
                case 203:
                    if (Screen.isShiftKeyDown()) {
                        if (Screen.isCtrlKeyDown()) {
                            this.setSelectionPos(this.getNthWordFromPos(-1, this.getSelectionEnd()));
                        } else {
                            this.setSelectionPos(this.getSelectionEnd() - 1);
                        }
                    } else if (Screen.isCtrlKeyDown()) {
                        this.setCursorPosition(this.getNthWordFromCursor(-1));
                    } else {
                        this.moveCursorBy(-1);
                    }
                    return true;
                case 205:
                    if (Screen.isShiftKeyDown()) {
                        if (Screen.isCtrlKeyDown()) {
                            this.setSelectionPos(this.getNthWordFromPos(1, this.getSelectionEnd()));
                        } else {
                            this.setSelectionPos(this.getSelectionEnd() + 1);
                        }
                    } else if (Screen.isCtrlKeyDown()) {
                        this.setCursorPosition(this.getNthWordFromCursor(1));
                    } else {
                        this.moveCursorBy(1);
                    }
                    return true;
                case 207:
                    if (Screen.isShiftKeyDown()) {
                        this.setSelectionPos(this.text.length());
                    } else {
                        this.setCursorPositionEnd();
                    }
                    return true;
                case 211:
                    if (Screen.isCtrlKeyDown()) {
                        if (this.enabled) {
                            this.deleteWords(1);
                        }
                    } else if (this.enabled) {
                        this.deleteFromCursor(1);
                    }
                    return true;
                default:
                    if (ChatAllowedCharacters.isAllowedCharacter(typedChar)) {
                        if (this.enabled) {
                            this.writeText(Character.toString(typedChar));
                        }
                        return true;
                    } else {
                        return false;
                    }
            }
        }
    }

    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        boolean isMouseOn = mouseX >= this.x && mouseX < this.x + this.width && mouseY >= this.y && mouseY < this.y + this.height;

        if (this.canLoseFocus) {
            this.setFocused(isMouseOn);
        }

        if (this.focused && isMouseOn && mouseButton == 0) {
            int i = mouseX - this.x;

            if (this.drawBackground) {
                i -= 4;
            }

            String s = getFr().trimStringToWidth(this.text.substring(this.lineScrollOffset), this.getWidth());
            this.setCursorPosition(getFr().trimStringToWidth(s, i).length() + this.lineScrollOffset);
        }
    }

    public void drawTextBox() {
        if (this.getVisible()) {
            if (this.getDrawBackground()) {
                drawRect(this.x - 1, this.y - 1, this.x + this.width + 1, this.y + this.height + 1, -6250336);
                drawRect(this.x, this.y, this.x + this.width, this.y + this.height, -16777216);
            }

            int color = this.enabled ? this.enabledColor : this.disabledColor;
            int j = this.cursorPosition - this.lineScrollOffset;
            int k = this.selectionEnd - this.lineScrollOffset;
            String s = getFr().trimStringToWidth(this.text.substring(this.lineScrollOffset), this.getWidth());
            boolean flag = j >= 0 && j <= s.length();
            boolean flag1 = this.focused && this.cursorCounter / 6 % 2 == 0 && flag;
            int l = this.drawBackground ? this.x + 4 : this.x;
            int i1 = this.drawBackground ? this.y + (this.height - 8) / 2 : this.y;
            int j1 = l;

            if (k > s.length()) {
                k = s.length();
            }

            if (s.length() > 0) {
                String s1 = flag ? s.substring(0, j) : s;
                j1 = getFr().drawStringWithShadow(s1, (float)l, (float)i1, color);
            }

            boolean flag2 = this.cursorPosition < this.text.length() || this.text.length() >= this.getMaxStringLength();
            int k1 = j1;

            if (!flag) {
                k1 = j > 0 ? l + this.width : l;
            } else if (flag2) {
                k1 = j1 - 1;
                --j1;
            }

            if (s.length() > 0 && flag && j < s.length()) {
                j1 = getFr().drawStringWithShadow(s.substring(j), (float)j1, (float)i1, color);
            }

            if (flag1) {
                if (flag2) {
                    GUI.drawRect(k1, i1 - 1, k1 + 1, i1 + 1 + getFr().FONT_HEIGHT, -3092272);
                } else {
                    getFr().drawStringWithShadow("_", (float)k1, (float)i1, color);
                }
            }

            if (k != j) {
                int l1 = l + getFr().getStringWidth(s.substring(0, k));
                this.drawCursorVertical(k1, i1 - 1, l1 - 1, i1 + 1 + getFr().FONT_HEIGHT);
            }
        }
    }

    /**
     * draws the vertical line cursor in the textbox
     */
    private void drawCursorVertical(int p_146188_1_, int p_146188_2_, int p_146188_3_, int p_146188_4_) {
        if (p_146188_1_ < p_146188_3_)
        {
            int i = p_146188_1_;
            p_146188_1_ = p_146188_3_;
            p_146188_3_ = i;
        }

        if (p_146188_2_ < p_146188_4_)
        {
            int j = p_146188_2_;
            p_146188_2_ = p_146188_4_;
            p_146188_4_ = j;
        }

        if (p_146188_3_ > this.x + this.width)
        {
            p_146188_3_ = this.x + this.width;
        }

        if (p_146188_1_ > this.x + this.width)
        {
            p_146188_1_ = this.x + this.width;
        }

        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldrenderer = tessellator.getWorldRenderer();
        GlStateManager.color(0.0F, 0.0F, 255.0F, 255.0F);
        GlStateManager.disableTexture2D();
        GlStateManager.enableColorLogic();
        GlStateManager.colorLogicOp(5387);
        worldrenderer.begin(7, DefaultVertexFormats.POSITION);
        worldrenderer.pos(p_146188_1_, p_146188_4_, 0.0D).endVertex();
        worldrenderer.pos(p_146188_3_, p_146188_4_, 0.0D).endVertex();
        worldrenderer.pos(p_146188_3_, p_146188_2_, 0.0D).endVertex();
        worldrenderer.pos(p_146188_1_, p_146188_2_, 0.0D).endVertex();
        tessellator.draw();
        GlStateManager.disableColorLogic();
        GlStateManager.enableTexture2D();
    }

    public void setMaxStringLength(int length) {
        this.maxStringLength = length;

        if (this.text.length() > length) {
            this.text = this.text.substring(0, length);
        }
    }

    public int getMaxStringLength() {
        return this.maxStringLength;
    }

    public int getCursorPosition() {
        return this.cursorPosition;
    }

    public boolean getDrawBackground() {
        return this.drawBackground;
    }

    public void setDrawBackground(boolean drawBackground) {
        this.drawBackground = drawBackground;
    }

    public void setTextColor(int color) {
        this.enabledColor = color;
    }

    public void setDisabledTextColour(int color) {
        this.disabledColor = color;
    }

    public void setFocused(boolean focused) {
        if (focused && !this.focused) {
            this.cursorCounter = 0;
        }

        this.focused = focused;
    }

    public boolean isFocused() {
        return this.focused;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public int getSelectionEnd() {
        return this.selectionEnd;
    }

    public int getWidth() {
        return this.getDrawBackground() ? this.width - 8 : this.width;
    }

    public void setSelectionPos(int pos) {
        int i = this.text.length();

        if (pos > i) {
            pos = i;
        }

        if (pos < 0) {
            pos = 0;
        }

        this.selectionEnd = pos;

        if (getFr() != null) {
            if (this.lineScrollOffset > i) {
                this.lineScrollOffset = i;
            }

            int j = this.getWidth();
            String s = getFr().trimStringToWidth(this.text.substring(this.lineScrollOffset), j);
            int k = s.length() + this.lineScrollOffset;

            if (pos == this.lineScrollOffset) {
                this.lineScrollOffset -= getFr().trimStringToWidth(this.text, j, true).length();
            }

            if (pos > k) {
                this.lineScrollOffset += pos - k;
            } else if (pos <= this.lineScrollOffset) {
                this.lineScrollOffset -= this.lineScrollOffset - pos;
            }

            this.lineScrollOffset = MathHelper.clamp_int(this.lineScrollOffset, 0, i);
        }
    }

    public void setCanLoseFocus(boolean canLoseFocus) {
        this.canLoseFocus = canLoseFocus;
    }

    public boolean getVisible() {
        return this.visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }
}
