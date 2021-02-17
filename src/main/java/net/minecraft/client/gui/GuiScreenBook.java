package net.minecraft.client.gui;

import com.google.common.collect.Lists;
import com.google.gson.JsonParseException;
import com.murengezi.minecraft.client.gui.GuiButton;
import com.murengezi.minecraft.client.gui.Screen;
import io.netty.buffer.Unpooled;
import java.io.IOException;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import com.murengezi.minecraft.event.ClickEvent;
import net.minecraft.init.Items;
import net.minecraft.item.ItemEditableBook;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.client.C17PacketCustomPayload;
import net.minecraft.util.ChatAllowedCharacters;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Keyboard;

public class GuiScreenBook extends Screen {

    private static final ResourceLocation bookGuiTextures = new ResourceLocation("textures/gui/book.png");
    private final EntityPlayer editingPlayer;
    private final ItemStack bookObj;
    private final boolean bookIsUnsigned;
    private boolean bookIsModified, bookGettingSigned;
    private int updateCount;
    private final int bookImageWidth = 192, bookImageHeight = 192;
    private int bookTotalPages = 1, currPage;
    private NBTTagList bookPages;
    private String bookTitle = "";
    private List<IChatComponent> field_175386_A;
    private int field_175387_B = -1;
    private GuiScreenBook.NextPageButton buttonNextPage, buttonPreviousPage;
    private GuiButton buttonDone, buttonSign, buttonFinalize, buttonCancel;

    public GuiScreenBook(EntityPlayer player, ItemStack book, boolean isUnsigned) {
        this.editingPlayer = player;
        this.bookObj = book;
        this.bookIsUnsigned = isUnsigned;

        if (book.hasTagCompound()) {
            NBTTagCompound nbttagcompound = book.getTagCompound();
            this.bookPages = nbttagcompound.getTagList("pages", 8);

            if (this.bookPages != null) {
                this.bookPages = (NBTTagList)this.bookPages.copy();
                this.bookTotalPages = this.bookPages.tagCount();

                if (this.bookTotalPages < 1) {
                    this.bookTotalPages = 1;
                }
            }
        }

        if (this.bookPages == null && isUnsigned) {
            this.bookPages = new NBTTagList();
            this.bookPages.appendTag(new NBTTagString(""));
            this.bookTotalPages = 1;
        }
    }

    @Override
    public void updateScreen() {
        super.updateScreen();
        ++this.updateCount;
    }

    @Override
    public void initGui() {
        Keyboard.enableRepeatEvents(true);

        if (this.bookIsUnsigned)
        {
            addButton(this.buttonSign = new GuiButton(3, this.width / 2 - 100, 4 + this.bookImageHeight, 98, 20, I18n.format("book.signButton")));
            addButton(this.buttonDone = new GuiButton(0, this.width / 2 + 2, 4 + this.bookImageHeight, 98, 20, I18n.format("gui.done")));
            addButton(this.buttonFinalize = new GuiButton(5, this.width / 2 - 100, 4 + this.bookImageHeight, 98, 20, I18n.format("book.finalizeButton")));
            addButton(this.buttonCancel = new GuiButton(4, this.width / 2 + 2, 4 + this.bookImageHeight, 98, 20, I18n.format("gui.cancel")));
        }
        else
        {
            addButton(this.buttonDone = new GuiButton(0, this.width / 2 - 100, 4 + this.bookImageHeight, 200, 20, I18n.format("gui.done")));
        }

        int i = (this.width - this.bookImageWidth) / 2;
        int j = 2;
        addButton(this.buttonNextPage = new GuiScreenBook.NextPageButton(1, i + 120, j + 154, true));
        addButton(this.buttonPreviousPage = new GuiScreenBook.NextPageButton(2, i + 38, j + 154, false));
        this.updateButtons();
    }

    @Override
    public void onGuiClosed() {
        Keyboard.enableRepeatEvents(false);
        super.onGuiClosed();
    }

    private void updateButtons() {
        this.buttonNextPage.setVisible(!this.bookGettingSigned && (this.currPage < this.bookTotalPages - 1 || this.bookIsUnsigned));
        this.buttonPreviousPage.setVisible(!this.bookGettingSigned && this.currPage > 0);
        this.buttonDone.setVisible(!this.bookIsUnsigned || !this.bookGettingSigned);

        if (this.bookIsUnsigned) {
            this.buttonSign.setVisible(!this.bookGettingSigned);
            this.buttonCancel.setVisible(this.bookGettingSigned);
            this.buttonFinalize.setVisible(this.bookGettingSigned);
            this.buttonFinalize.setEnabled(this.bookTitle.trim().length() > 0);
        }
    }

    private void sendBookToServer(boolean publish) {
        if (this.bookIsUnsigned && this.bookIsModified) {
            if (this.bookPages != null) {
                while (this.bookPages.tagCount() > 1) {
                    String s = this.bookPages.getStringTagAt(this.bookPages.tagCount() - 1);

                    if (s.length() != 0) {
                        break;
                    }

                    this.bookPages.removeTag(this.bookPages.tagCount() - 1);
                }

                if (this.bookObj.hasTagCompound()) {
                    NBTTagCompound nbttagcompound = this.bookObj.getTagCompound();
                    nbttagcompound.setTag("pages", this.bookPages);
                } else {
                    this.bookObj.setTagInfo("pages", this.bookPages);
                }

                String s2 = "MC|BEdit";

                if (publish) {
                    s2 = "MC|BSign";
                    this.bookObj.setTagInfo("author", new NBTTagString(this.editingPlayer.getCommandSenderName()));
                    this.bookObj.setTagInfo("title", new NBTTagString(this.bookTitle.trim()));

                    for (int i = 0; i < this.bookPages.tagCount(); ++i) {
                        String s1 = this.bookPages.getStringTagAt(i);
                        IChatComponent ichatcomponent = new ChatComponentText(s1);
                        s1 = IChatComponent.Serializer.componentToJson(ichatcomponent);
                        this.bookPages.set(i, new NBTTagString(s1));
                    }

                    this.bookObj.setItem(Items.written_book);
                }

                PacketBuffer packetbuffer = new PacketBuffer(Unpooled.buffer());
                packetbuffer.writeItemStackToBuffer(this.bookObj);
                getMc().getNetHandler().addToSendQueue(new C17PacketCustomPayload(s2, packetbuffer));
            }
        }
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        if (button.isEnabled()) {
            switch (button.getId()) {
                case 0:
                    changeScreen(null);
                    this.sendBookToServer(false);
                    break;
                case 3:
                    if (this.bookIsUnsigned) {
                        this.bookGettingSigned = true;
                    }
                    break;
                case 1:
                    if (this.currPage < this.bookTotalPages - 1) {
                        ++this.currPage;
                    } else if (this.bookIsUnsigned) {
                        this.addNewPage();

                        if (this.currPage < this.bookTotalPages - 1) {
                            ++this.currPage;
                        }
                    }
                    break;
                case 2:
                    if (this.currPage > 0) {
                        --this.currPage;
                    }
                    break;
                case 5:
                    if (this.bookGettingSigned) {
                        this.sendBookToServer(true);
                        changeScreen(null);
                    }
                    break;
                case 4:
                    if (this.bookGettingSigned) {
                        this.bookGettingSigned = false;
                    }
                    break;
            }
            this.updateButtons();
        }
        super.actionPerformed(button);
    }

    private void addNewPage() {
        if (this.bookPages != null && this.bookPages.tagCount() < 50) {
            this.bookPages.appendTag(new NBTTagString(""));
            ++this.bookTotalPages;
            this.bookIsModified = true;
        }
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        super.keyTyped(typedChar, keyCode);

        if (this.bookIsUnsigned) {
            if (this.bookGettingSigned) {
                this.keyTypedInTitle(typedChar, keyCode);
            } else {
                this.keyTypedInBook(typedChar, keyCode);
            }
        }
    }

    private void keyTypedInBook(char typedChar, int keyCode) {
        if (Screen.isKeyComboCtrlV(keyCode)) {
            this.pageInsertIntoCurrent(Screen.getClipboardString());
        } else {
            switch (keyCode) {
                case Keyboard.KEY_BACK:
                    String s = this.pageGetCurrent();
                    if (s.length() > 0) {
                        this.pageSetCurrent(s.substring(0, s.length() - 1));
                    }
                    return;
                case Keyboard.KEY_RETURN:
                case Keyboard.KEY_NUMPADENTER:
                    this.pageInsertIntoCurrent("\n");
                    return;
                default:
                    if (ChatAllowedCharacters.isAllowedCharacter(typedChar)) {
                        this.pageInsertIntoCurrent(Character.toString(typedChar));
                    }
            }
        }
    }

    private void keyTypedInTitle(char typedChar, int keyCode) {
        switch (keyCode) {
            case Keyboard.KEY_BACK:
                if (!this.bookTitle.isEmpty()) {
                    this.bookTitle = this.bookTitle.substring(0, this.bookTitle.length() - 1);
                    this.updateButtons();
                }
                return;
            case Keyboard.KEY_RETURN:
            case Keyboard.KEY_NUMPADENTER:
                if (!this.bookTitle.isEmpty()) {
                    this.sendBookToServer(true);
                    changeScreen(null);
                }
                return;
            default:
                if (this.bookTitle.length() < 16 && ChatAllowedCharacters.isAllowedCharacter(typedChar)) {
                    this.bookTitle = this.bookTitle + typedChar;
                    this.updateButtons();
                    this.bookIsModified = true;
                }
        }
    }

    private String pageGetCurrent() {
        return this.bookPages != null && this.currPage >= 0 && this.currPage < this.bookPages.tagCount() ? this.bookPages.getStringTagAt(this.currPage) : "";
    }

    private void pageSetCurrent(String s) {
        if (this.bookPages != null && this.currPage >= 0 && this.currPage < this.bookPages.tagCount()) {
            this.bookPages.set(this.currPage, new NBTTagString(s));
            this.bookIsModified = true;
        }
    }

    private void pageInsertIntoCurrent(String s) {
        String s1 = this.pageGetCurrent();
        String s2 = s1 + s;
        int i = getFr().splitStringWidth(s2 + "" + EnumChatFormatting.BLACK + "_", 118);

        if (i <= 128 && s2.length() < 256) {
            this.pageSetCurrent(s2);
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        GlStateManager.colorAllMax();
        getMc().getTextureManager().bindTexture(bookGuiTextures);
        int i = (this.width - this.bookImageWidth) / 2;
        int j = 2;
        this.drawTexturedModalRect(i, j, 0, 0, this.bookImageWidth, this.bookImageHeight);

        if (this.bookGettingSigned) {
            String s = this.bookTitle;

            if (this.bookIsUnsigned) {
                if (this.updateCount / 6 % 2 == 0) {
                    s = s + "" + EnumChatFormatting.BLACK + "_";
                } else {
                    s = s + "" + EnumChatFormatting.GRAY + "_";
                }
            }

            String s1 = I18n.format("book.editTitle");
            int k = getFr().getStringWidth(s1);
            getFr().drawString(s1, i + 36 + (116 - k) / 2, j + 16 + 16, 0);
            int l = getFr().getStringWidth(s);
            getFr().drawString(s, i + 36 + (116 - l) / 2, j + 48, 0);
            String s2 = I18n.format("book.byAuthor", this.editingPlayer.getCommandSenderName());
            int i1 = getFr().getStringWidth(s2);
            getFr().drawString(EnumChatFormatting.DARK_GRAY + s2, i + 36 + (116 - i1) / 2, j + 48 + 10, 0);
            String s3 = I18n.format("book.finalizeWarning");
            getFr().drawSplitString(s3, i + 36, j + 80, 116, 0);
        } else {
            String s4 = I18n.format("book.pageIndicator", this.currPage + 1, this.bookTotalPages);
            String s5 = "";

            if (this.bookPages != null && this.currPage >= 0 && this.currPage < this.bookPages.tagCount()) {
                s5 = this.bookPages.getStringTagAt(this.currPage);
            }

            if (this.bookIsUnsigned)
            {
                if (getFr().getBidiFlag())
                {
                    s5 = s5 + "_";
                }
                else if (this.updateCount / 6 % 2 == 0)
                {
                    s5 = s5 + "" + EnumChatFormatting.BLACK + "_";
                }
                else
                {
                    s5 = s5 + "" + EnumChatFormatting.GRAY + "_";
                }
            }
            else if (this.field_175387_B != this.currPage)
            {
                if (ItemEditableBook.validBookTagContents(this.bookObj.getTagCompound()))
                {
                    try
                    {
                        IChatComponent ichatcomponent = IChatComponent.Serializer.jsonToComponent(s5);
                        this.field_175386_A = ichatcomponent != null ? GuiUtilRenderComponents.func_178908_a(ichatcomponent, 116, getFr(), true, true) : null;
                    }
                    catch (JsonParseException var13)
                    {
                        this.field_175386_A = null;
                    }
                }
                else
                {
                    ChatComponentText chatcomponenttext = new ChatComponentText(EnumChatFormatting.DARK_RED.toString() + "* Invalid book tag *");
                    this.field_175386_A = Lists.newArrayList(chatcomponenttext);
                }

                this.field_175387_B = this.currPage;
            }

            int j1 = getFr().getStringWidth(s4);
            getFr().drawString(s4, i - j1 + this.bookImageWidth - 44, j + 16, 0);

            if (this.field_175386_A == null)
            {
                getFr().drawSplitString(s5, i + 36, j + 16 + 16, 116, 0);
            }
            else
            {
                int k1 = Math.min(128 / getFr().FONT_HEIGHT, this.field_175386_A.size());

                for (int l1 = 0; l1 < k1; ++l1)
                {
                    IChatComponent ichatcomponent2 = this.field_175386_A.get(l1);
                    getFr().drawString(ichatcomponent2.getUnformattedText(), i + 36, j + 16 + 16 + l1 * getFr().FONT_HEIGHT, 0);
                }

                IChatComponent ichatcomponent1 = this.func_175385_b(mouseX, mouseY);

                if (ichatcomponent1 != null)
                {
                    this.handleComponentHover(ichatcomponent1, mouseX, mouseY);
                }
            }
        }

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (mouseButton == 0) {
            IChatComponent ichatcomponent = this.func_175385_b(mouseX, mouseY);

            if (this.handleComponentClick(ichatcomponent)) {
                return;
            }
        }

        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    protected boolean handleComponentClick(IChatComponent chatComponent) {
        ClickEvent clickevent = chatComponent == null ? null : chatComponent.getChatStyle().getChatClickEvent();

        if (clickevent == null) {
            return false;
        } else if (clickevent.getAction() == ClickEvent.Action.CHANGE_PAGE) {
            String s = clickevent.getValue();

            try {
                int i = Integer.parseInt(s) - 1;

                if (i >= 0 && i < this.bookTotalPages && i != this.currPage) {
                    this.currPage = i;
                    this.updateButtons();
                    return true;
                }
            } catch (Throwable ignored) {}

            return false;
        } else {
            boolean flag = super.handleComponentClick(chatComponent);

            if (flag && clickevent.getAction() == ClickEvent.Action.RUN_COMMAND) {
                changeScreen(null);
            }

            return flag;
        }
    }

    public IChatComponent func_175385_b(int p_175385_1_, int p_175385_2_) {
        if (this.field_175386_A == null) {
            return null;
        } else {
            int i = p_175385_1_ - (this.width - this.bookImageWidth) / 2 - 36;
            int j = p_175385_2_ - 2 - 16 - 16;

            if (i >= 0 && j >= 0) {
                int k = Math.min(128 / getFr().FONT_HEIGHT, this.field_175386_A.size());

                if (i <= 116 && j < getFr().FONT_HEIGHT * k + k) {
                    int l = j / getFr().FONT_HEIGHT;

                    if (l >= 0 && l < this.field_175386_A.size()) {
                        IChatComponent ichatcomponent = this.field_175386_A.get(l);
                        int i1 = 0;

                        for (IChatComponent ichatcomponent1 : ichatcomponent) {
                            if (ichatcomponent1 instanceof ChatComponentText) {
                                i1 += getFr().getStringWidth(((ChatComponentText)ichatcomponent1).getChatComponentText_TextValue());

                                if (i1 > i) {
                                    return ichatcomponent1;
                                }
                            }
                        }
                    }

                    return null;
                } else {
                    return null;
                }
            } else {
                return null;
            }
        }
    }

    static class NextPageButton extends GuiButton {
        private final boolean field_146151_o;

        public NextPageButton(int p_i46316_1_, int p_i46316_2_, int p_i46316_3_, boolean p_i46316_4_) {
            super(p_i46316_1_, p_i46316_2_, p_i46316_3_, 23, 13, "");
            this.field_146151_o = p_i46316_4_;
        }

        public void drawButton(int mouseX, int mouseY) {
            if (this.isVisible()) {
                boolean flag = mouseX >= this.getX() && mouseY >= this.getY() && mouseX < this.getX() + this.getWidth() && mouseY < this.getY() + this.getHeight();
                GlStateManager.colorAllMax();
                getMc().getTextureManager().bindTexture(GuiScreenBook.bookGuiTextures);
                int i = 0;
                int j = 192;

                if (flag) {
                    i += 23;
                }

                if (!this.field_146151_o) {
                    j += 13;
                }

                this.drawTexturedModalRect(this.getX(), this.getY(), i, j, 23, 13);
            }
        }
    }
}
