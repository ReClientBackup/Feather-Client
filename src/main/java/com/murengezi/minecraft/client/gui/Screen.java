package com.murengezi.minecraft.client.gui;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import com.murengezi.feather.Feather;
import com.murengezi.minecraft.client.gui.Singleplayer.YesNoCallback;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiLabel;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.EntityList;
import com.murengezi.minecraft.event.ClickEvent;
import com.murengezi.minecraft.event.HoverEvent;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTException;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.stats.Achievement;
import net.minecraft.stats.StatBase;
import net.minecraft.stats.StatList;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

public abstract class Screen extends GUI implements YesNoCallback {

    private static final Logger LOGGER = LogManager.getLogger();
    private static final Set<String> PROTOCOLS = Sets.newHashSet("http", "https");
    private static final Splitter NEWLINE_SPLITTER = Splitter.on('\n');

    protected RenderItem itemRender;

    public int width;
    public int height;

    protected List<GuiButton> buttonList = Lists.newArrayList();
    protected List<GuiLabel> labelList = Lists.newArrayList();

    public boolean allowUserInput;

    private GuiButton selectedButton;
    private int eventButton;
    private long lastMouseEvent;

    private int touchValue;
    private URI clickedLinkURI;

    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        getButtonList().forEach(guiButton -> guiButton.drawButton(mouseX, mouseY));
        getLabelList().forEach(guiLabel -> guiLabel.drawLabel());
    }

    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        if (keyCode == Keyboard.KEY_ESCAPE) {
            changeScreen(null);

            if (getMc().currentScreen == null) {
                getMc().setIngameFocus();
            }
        }
    }

    public static String getClipboardString() {
        try {
            Transferable transferable = Toolkit.getDefaultToolkit().getSystemClipboard().getContents(null);

            if (transferable != null && transferable.isDataFlavorSupported(DataFlavor.stringFlavor)) {
                return (String)transferable.getTransferData(DataFlavor.stringFlavor);
            }
        }
        catch (Exception ignored) {}

        return "";
    }

    public static void setClipboardString(String copyText) {
        if (!StringUtils.isEmpty(copyText)) {
            try {
                StringSelection stringselection = new StringSelection(copyText);
                Toolkit.getDefaultToolkit().getSystemClipboard().setContents(stringselection, null);
            } catch (Exception ignored) {}
        }
    }

    protected void renderToolTip(ItemStack stack, int x, int y) {
        List<String> list = stack.getTooltip(getMc().thePlayer, getMc().gameSettings.advancedItemTooltips);

        for (int i = 0; i < list.size(); ++i) {
            if (i == 0) {
                list.set(i, stack.getRarity().rarityColor + list.get(i));
            } else {
                list.set(i, EnumChatFormatting.GRAY + list.get(i));
            }
        }

        this.drawHoveringText(list, x, y);
    }

    protected void drawCreativeTabHoveringText(String tabName, int mouseX, int mouseY) {
        this.drawHoveringText(Collections.singletonList(tabName), mouseX, mouseY);
    }

    protected void drawHoveringText(List<String> textLines, int x, int y) {
        if (!textLines.isEmpty()) {
            GlStateManager.disableRescaleNormal();
            RenderHelper.disableStandardItemLighting();
            GlStateManager.disableLighting();
            GlStateManager.disableDepth();
            int i = 0;

            for (String s : textLines) {
                int j = getFr().getStringWidth(s);

                if (j > i) {
                    i = j;
                }
            }

            int l1 = x + 12;
            int i2 = y - 12;
            int k = 8;

            if (textLines.size() > 1) {
                k += 2 + (textLines.size() - 1) * 10;
            }

            if (l1 + i > this.width) {
                l1 -= 28 + i;
            }

            if (i2 + k + 6 > this.height) {
                i2 = this.height - k - 6;
            }

            this.zLevel = 300.0F;
            this.itemRender.zLevel = 300.0F;
            int l = -267386864;
            this.drawGradientRect(l1 - 3, i2 - 4, l1 + i + 3, i2 - 3, l, l);
            this.drawGradientRect(l1 - 3, i2 + k + 3, l1 + i + 3, i2 + k + 4, l, l);
            this.drawGradientRect(l1 - 3, i2 - 3, l1 + i + 3, i2 + k + 3, l, l);
            this.drawGradientRect(l1 - 4, i2 - 3, l1 - 3, i2 + k + 3, l, l);
            this.drawGradientRect(l1 + i + 3, i2 - 3, l1 + i + 4, i2 + k + 3, l, l);
            int i1 = 1347420415;
            int j1 = (i1 & 16711422) >> 1 | i1 & -16777216;
            this.drawGradientRect(l1 - 3, i2 - 3 + 1, l1 - 3 + 1, i2 + k + 3 - 1, i1, j1);
            this.drawGradientRect(l1 + i + 2, i2 - 3 + 1, l1 + i + 3, i2 + k + 3 - 1, i1, j1);
            this.drawGradientRect(l1 - 3, i2 - 3, l1 + i + 3, i2 - 3 + 1, i1, i1);
            this.drawGradientRect(l1 - 3, i2 + k + 2, l1 + i + 3, i2 + k + 3, j1, j1);

            for (int k1 = 0; k1 < textLines.size(); ++k1) {
                String s1 = textLines.get(k1);
                getFr().drawStringWithShadow(s1, (float)l1, (float)i2, -1);

                if (k1 == 0) {
                    i2 += 2;
                }

                i2 += 10;
            }

            this.zLevel = 0.0F;
            this.itemRender.zLevel = 0.0F;
            GlStateManager.enableLighting();
            GlStateManager.enableDepth();
            RenderHelper.enableStandardItemLighting();
            GlStateManager.enableRescaleNormal();
        }
    }

    protected void handleComponentHover(IChatComponent iChatComponent, int x, int y) {
        if (iChatComponent != null && iChatComponent.getChatStyle().getChatHoverEvent() != null) {
            HoverEvent hoverevent = iChatComponent.getChatStyle().getChatHoverEvent();

            if (hoverevent.getAction() == HoverEvent.Action.SHOW_ITEM) {
                ItemStack itemstack = null;

                try {
                    NBTTagCompound tagFromJson = JsonToNBT.getTagFromJson(hoverevent.getValue().getUnformattedText());

                    if (tagFromJson != null) {
                        itemstack = ItemStack.loadItemStackFromNBT(tagFromJson);
                    }
                }
                catch (NBTException ignored)
                {
                }

                if (itemstack != null) {
                    this.renderToolTip(itemstack, x, y);
                } else {
                    this.drawCreativeTabHoveringText(EnumChatFormatting.RED + "Invalid Item!", x, y);
                }
            }
            else if (hoverevent.getAction() == HoverEvent.Action.SHOW_ENTITY) {
                if (getMc().gameSettings.advancedItemTooltips) {
                    try {
                        NBTTagCompound tagFromJson = JsonToNBT.getTagFromJson(hoverevent.getValue().getUnformattedText());

                        if (tagFromJson != null) {
                            List<String> list1 = Lists.newArrayList();
                            list1.add(tagFromJson.getString("name"));

                            if (tagFromJson.hasKey("type", 8)) {
                                String s = tagFromJson.getString("type");
                                list1.add("Type: " + s + " (" + EntityList.getIDFromString(s) + ")");
                            }

                            list1.add(tagFromJson.getString("id"));
                            this.drawHoveringText(list1, x, y);
                        } else {
                            this.drawCreativeTabHoveringText(EnumChatFormatting.RED + "Invalid Entity!", x, y);
                        }
                    } catch (NBTException var10) {
                        this.drawCreativeTabHoveringText(EnumChatFormatting.RED + "Invalid Entity!", x, y);
                    }
                }
            } else if (hoverevent.getAction() == HoverEvent.Action.SHOW_TEXT) {
                this.drawHoveringText(NEWLINE_SPLITTER.splitToList(hoverevent.getValue().getFormattedText()), x, y);
            }
            else if (hoverevent.getAction() == HoverEvent.Action.SHOW_ACHIEVEMENT) {
                StatBase statbase = StatList.getOneShotStat(hoverevent.getValue().getUnformattedText());

                if (statbase != null) {
                    IChatComponent chatComponent = statbase.getStatName();
                    IChatComponent chatComponentTranslation = new ChatComponentTranslation("stats.tooltip.type." + (statbase.isAchievement() ? "achievement" : "statistic"));
                    chatComponentTranslation.getChatStyle().setItalic(Boolean.TRUE);
                    String s1 = statbase instanceof Achievement ? ((Achievement)statbase).getDescription() : null;
                    List<String> list = Lists.newArrayList(chatComponent.getFormattedText(), chatComponentTranslation.getFormattedText());

                    if (s1 != null) {
                        list.addAll(getFr().listFormattedStringToWidth(s1, 150));
                    }

                    this.drawHoveringText(list, x, y);
                } else {
                    this.drawCreativeTabHoveringText(EnumChatFormatting.RED + "Invalid statistic/achievement!", x, y);
                }
            }

            GlStateManager.disableLighting();
        }
    }

    protected void setText(String newChatText, boolean shouldOverwrite) {}

    protected boolean handleComponentClick(IChatComponent chatComponent) {
        if (chatComponent != null) {
            ClickEvent clickevent = chatComponent.getChatStyle().getChatClickEvent();

            if (isShiftKeyDown()) {
                if (chatComponent.getChatStyle().getInsertion() != null) {
                    this.setText(chatComponent.getChatStyle().getInsertion(), false);
                }
            } else if (clickevent != null) {
                if (clickevent.getAction() == ClickEvent.Action.OPEN_URL) {
                    if (!getMc().gameSettings.chatLinks) {
                        return false;
                    }

                    try {
                        URI uri = new URI(clickevent.getValue());
                        String s = uri.getScheme();

                        if (s == null) {
                            throw new URISyntaxException(clickevent.getValue(), "Missing protocol");
                        }

                        if (!PROTOCOLS.contains(s.toLowerCase())) {
                            throw new URISyntaxException(clickevent.getValue(), "Unsupported protocol: " + s.toLowerCase());
                        }

                        if (getMc().gameSettings.chatLinksPrompt) {
                            this.clickedLinkURI = uri;
                            getMc().displayGuiScreen(new ConfirmOpenLinkScreen(this, clickevent.getValue(), 31102009, false));
                        } else {
                            this.openWebLink(uri);
                        }
                    } catch (URISyntaxException urisyntaxexception) {
                        LOGGER.error("Can't open url for " + clickevent, urisyntaxexception);
                    }
                } else if (clickevent.getAction() == ClickEvent.Action.OPEN_FILE) {
                    URI uri1 = (new File(clickevent.getValue())).toURI();
                    this.openWebLink(uri1);
                } else if (clickevent.getAction() == ClickEvent.Action.SUGGEST_COMMAND) {
                    this.setText(clickevent.getValue(), true);
                } else if (clickevent.getAction() == ClickEvent.Action.RUN_COMMAND) {
                    this.sendChatMessage(clickevent.getValue(), false);
                } else {
                    LOGGER.error("Don't know how to handle " + clickevent);
                }

                return true;
            }

        }
        return false;
    }

    public void sendChatMessage(String msg) {
        this.sendChatMessage(msg, true);
    }

    public void sendChatMessage(String msg, boolean addToChat) {
        if (addToChat) {
            getMc().inGameScreen.getChatGUI().addToSentMessages(msg);
        }

        getMc().thePlayer.sendChatMessage(msg);
    }

    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        try {
            if (mouseButton == 0) {
                for (GuiButton button : this.buttonList) {
                    if (button.mousePressed(getMc(), mouseX, mouseY)) {
                        this.selectedButton = button;
                        button.playPressSound(getMc().getSoundHandler());
                        try {
                            this.actionPerformed(button);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void mouseReleased(int mouseX, int mouseY, int mouseButton) {
        if (this.selectedButton != null && mouseButton == 0) {
            this.selectedButton.mouseReleased(mouseX, mouseY);
            this.selectedButton = null;
        }
    }

    protected void mouseClickMove(int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick) {}

    protected void actionPerformed(GuiButton button) throws IOException {}

    public void setWorldAndResolution(int width, int height) {
        this.itemRender = getMc().getRenderItem();
        this.width = width;
        this.height = height;
        this.buttonList.clear();
        this.initGui();
    }

    public void initGui() {}

    public void handleInput() throws IOException
    {
        if (Mouse.isCreated())
        {
            while (Mouse.next())
            {
                this.handleMouseInput();
            }
        }

        if (Keyboard.isCreated())
        {
            while (Keyboard.next())
            {
                this.handleKeyboardInput();
            }
        }
    }

    public void handleMouseInput() throws IOException {
        int i = Mouse.getEventX() * this.width / getMc().displayWidth;
        int j = this.height - Mouse.getEventY() * this.height / getMc().displayHeight - 1;
        int k = Mouse.getEventButton();

        if (Mouse.getEventButtonState()) {
            if (getMc().gameSettings.touchscreen && this.touchValue++ > 0) {
                return;
            }

            this.eventButton = k;
            this.lastMouseEvent = Minecraft.getSystemTime();
            this.mouseClicked(i, j, this.eventButton);
        } else if (k != -1) {
            if (getMc().gameSettings.touchscreen && --this.touchValue > 0) {
                return;
            }

            this.eventButton = -1;
            this.mouseReleased(i, j, k);
        }
        else if (this.eventButton != -1 && this.lastMouseEvent > 0L) {
            long l = Minecraft.getSystemTime() - this.lastMouseEvent;
            this.mouseClickMove(i, j, this.eventButton, l);
        }
    }

    public void handleKeyboardInput() throws IOException {
        if (Keyboard.getEventKeyState()) {
            this.keyTyped(Keyboard.getEventCharacter(), Keyboard.getEventKey());
        }

        getMc().dispatchKeypresses();
    }

    public void updateScreen() {}

    public void onGuiClosed() {}

    public void drawDefaultBackground() {
        this.drawWorldBackground(0);
    }

    public void drawWorldBackground(int tint) {
        if (getMc().theWorld != null) {
            this.drawGradientRect(0, 0, this.width, this.height, -1072689136, -804253680);
        } else {
            this.drawBackground(tint);
        }
    }

    public void drawBackground(int tint) {
        GlStateManager.disableLighting();
        GlStateManager.disableFog();
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldrenderer = tessellator.getWorldRenderer();
        getMc().getTextureManager().bindTexture(optionsBackground);
        GlStateManager.colorAllMax();
        float f = 32.0F;
        worldrenderer.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
        worldrenderer.pos(0.0D, this.height, 0.0D).tex(0.0D, (float)this.height / f + (float)tint).color(64, 64, 64, 255).endVertex();
        worldrenderer.pos(this.width, this.height, 0.0D).tex((float)this.width / f, (float)this.height / f + (float)tint).color(64, 64, 64, 255).endVertex();
        worldrenderer.pos(this.width, 0.0D, 0.0D).tex((float)this.width / f, tint).color(64, 64, 64, 255).endVertex();
        worldrenderer.pos(0.0D, 0.0D, 0.0D).tex(0.0D, tint).color(64, 64, 64, 255).endVertex();
        tessellator.draw();
    }

    public boolean doesGuiPauseGame()
    {
        return true;
    }

    public void confirmClicked(boolean result, int id) {
        if (id == 31102009) {
            if (result) {
                this.openWebLink(this.clickedLinkURI);
            }

            this.clickedLinkURI = null;
            getMc().displayGuiScreen(this);
        }
    }

    private void openWebLink(URI uri) {
        try {
            Class<?> clazz = Class.forName("java.awt.Desktop");
            Object object = clazz.getMethod("getDesktop").invoke(null);
            clazz.getMethod("browse", URI.class).invoke(object, uri);
        } catch (Throwable throwable) {
            LOGGER.error("Couldn't open link", throwable);
        }
    }

    public static boolean isCtrlKeyDown() {
        return Minecraft.isRunningOnMac ? Keyboard.isKeyDown(Keyboard.KEY_LMETA) || Keyboard.isKeyDown(Keyboard.KEY_RMETA) : Keyboard.isKeyDown(Keyboard.KEY_LCONTROL) || Keyboard.isKeyDown(Keyboard.KEY_RCONTROL);
    }

    public static boolean isShiftKeyDown()
    {
        return Keyboard.isKeyDown(42) || Keyboard.isKeyDown(54);
    }

    public static boolean isAltKeyDown() {
        return Keyboard.isKeyDown(Keyboard.KEY_LMENU) || Keyboard.isKeyDown(Keyboard.KEY_RMENU);
    }

    public static boolean isKeyComboCtrlX(int key) {
        return key == Keyboard.KEY_X && isCtrlKeyDown() && !isShiftKeyDown() && !isAltKeyDown();
    }

    public static boolean isKeyComboCtrlV(int key) {
        return key == Keyboard.KEY_V && isCtrlKeyDown() && !isShiftKeyDown() && !isAltKeyDown();
    }

    public static boolean isKeyComboCtrlC(int key) {
        return key == Keyboard.KEY_C && isCtrlKeyDown() && !isShiftKeyDown() && !isAltKeyDown();
    }

    public static boolean isKeyComboCtrlA(int key) {
        return key == Keyboard.KEY_A && isCtrlKeyDown() && !isShiftKeyDown() && !isAltKeyDown();
    }

    public void onResize(int width, int height) {
        this.setWorldAndResolution(width, height);
    }

    public void drawDefaultBackground(int mouseX, int mouseY, int delta) {
        if (getWorld() == null) {
            drawGradientRect(0, 0, this.width, this.height, Feather.getThemeManager().getActiveTheme().getColor().getRGB(), 0xff333333);
            Feather.getParticleManager().render(mouseX, mouseY);
            Feather.getParticleManager().tick(delta);
        }
    }

    public void addButton(GuiButton guiButton) {
        this.buttonList.add(guiButton);
    }

    public GuiButton getButton(int id) {
        return this.buttonList.stream().filter(guiButton -> guiButton.getId() == id).findFirst().orElse(null);
    }

    public List<GuiButton> getButtonList() {
        return buttonList;
    }

    public List<GuiLabel> getLabelList() {
        return labelList;
    }

    public void changeScreen(Screen guiScreen) {
        getMc().displayGuiScreen(guiScreen);
    }

    public void saveSettings() {
        getMc().gameSettings.saveOptions();
    }

    protected void scissorBox(float x, float y, float x2, float y2, ScaledResolution sr) {
        final int factor = sr.getScaleFactor();
        GL11.glScissor((int) (x * factor), (int) ((sr.getScaledHeight() - y2) * factor), (int) ((x2 - x) * factor), (int) ((y2 -y) * factor));
    }
}
