package com.murengezi.minecraft.client.gui.Singleplayer;

import com.murengezi.minecraft.client.gui.GuiButton;
import com.murengezi.minecraft.client.gui.Screen;
import com.murengezi.minecraft.client.gui.YesNoScreen;
import com.murengezi.minecraft.client.AnvilConverterException;
import com.murengezi.minecraft.client.gui.GUI;
import com.murengezi.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.gui.GuiSlot;
import com.murengezi.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.storage.ISaveFormat;
import net.minecraft.world.storage.ISaveHandler;
import net.minecraft.world.storage.SaveFormatComparator;
import net.minecraft.world.storage.WorldInfo;
import org.apache.commons.lang3.StringUtils;
import org.lwjgl.opengl.GL11;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.List;

/**
 * @author Tobias Sjöblom
 * Created on 2021-01-13 at 09:56
 */
public class WorldSelectionScreen extends Screen {

    private final Screen previousScreen;

    private static final int SELECT = 0, CREATE = 1, RENAME = 2, DELETE = 3, RECREATE = 4, CANCEL = 5;

    private String worldString, conversationString;
    private List<SaveFormatComparator> worldList;
    private int selectedSlot;
    private boolean startingWorld, deleteWorld;
    private DateFormat dateFormat;
    private WorldSlot worldSlot;

    public WorldSelectionScreen(Screen parentScreen) {
        this.previousScreen = parentScreen;
    }

    @Override
    public void initGui() {
        conversationString = I18n.format("selectWorld.conversation");
        loadWorldList();
        dateFormat = new SimpleDateFormat();
        worldSlot = new WorldSlot();
        worldSlot.registerScrollButtons(4, 5);
        addButton(new GuiButton(SELECT, this.width / 2 - 154, this.height - 52, 150, 20, I18n.format("selectWorld.select")));
        addButton(new GuiButton(CREATE, this.width / 2 + 4, this.height - 52, 150, 20, I18n.format("selectWorld.create")));
        addButton(new GuiButton(RENAME, this.width / 2 - 154, this.height - 28, 72, 20, I18n.format("selectWorld.rename")));
        addButton(new GuiButton(DELETE, this.width / 2 - 76, this.height - 28, 72, 20, I18n.format("selectWorld.delete")));
        addButton(new GuiButton(RECREATE, this.width / 2 + 4, this.height - 28, 72, 20, I18n.format("selectWorld.recreate")));
        addButton(new GuiButton(CANCEL, this.width / 2 + 82, this.height - 28, 72, 20, I18n.format("gui.cancel")));

        getButton(SELECT).setEnabled(false);
        getButton(RENAME).setEnabled(false);
        getButton(DELETE).setEnabled(false);
        getButton(RECREATE).setEnabled(false);
        super.initGui();
    }

    private void loadWorldList() {
        try {
            ISaveFormat iSaveFormat = getMc().getSaveLoader();
            worldList = iSaveFormat.getSaveList();
            Collections.sort(worldList);
            selectedSlot = -1;
        } catch (AnvilConverterException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void handleMouseInput() throws IOException {
        worldSlot.handleMouseInput();
        super.handleMouseInput();
    }

    @Override
    public void updateScreen() {
        super.updateScreen();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawWorldBackground(mouseX, mouseY, 120);

        GlStateManager.pushMatrix();
        scissorBox(worldSlot.getLeft(), worldSlot.getTop(), worldSlot.getRight(), worldSlot.getBottom(), new ScaledResolution());
        GL11.glEnable(GL11.GL_SCISSOR_TEST);
        worldSlot.drawScreen(mouseX, mouseY, partialTicks);
        GL11.glDisable(GL11.GL_SCISSOR_TEST);
        GlStateManager.popMatrix();
        GUI.drawRect(0, 0, worldSlot.getWidth(), worldSlot.getTop(), Integer.MIN_VALUE);
        GUI.drawRect(0, worldSlot.getBottom(), worldSlot.getWidth(), this.height, Integer.MIN_VALUE);

        getFr().drawCenteredString(I18n.format("selectWorld.title"), (float)this.width / 2, 20, 0xffffff);
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        switch (button.getId()) {
            case SELECT:
                selectWorld(selectedSlot);
                break;
            case CREATE:
                changeScreen(new WorldCreateScreen(this));
                break;
            case RENAME:
                changeScreen(new WorldRenameScreen(this, this.getFileName(this.selectedSlot)));
                break;
            case DELETE:
                String displayName = this.getDisplayName(this.selectedSlot);

                if (displayName != null) {
                    this.deleteWorld = true;
                    changeScreen(askDelete(this, displayName, this.selectedSlot));
                }
                break;
            case RECREATE:
                WorldCreateScreen worldCreateScreen = new WorldCreateScreen(this);
                ISaveHandler isavehandler = getMc().getSaveLoader().getSaveLoader(this.getFileName(this.selectedSlot), false);
                WorldInfo worldinfo = isavehandler.loadWorldInfo();
                isavehandler.flush();
                worldCreateScreen.func_146318_a(worldinfo);
                changeScreen(worldCreateScreen);
                break;
            case CANCEL:
                changeScreen(getPreviousScreen());
                break;
        }
        worldSlot.actionPerformed(button);
        super.actionPerformed(button);
    }

    public static YesNoScreen askDelete(YesNoCallback gui, String worldName, int slot) {
        return new YesNoScreen(gui, I18n.format("selectWorld.deleteQuestion"), "'" + worldName + "' " + I18n.format("selectWorld.deleteWarning"), I18n.format("selectWorld.deleteButton"), I18n.format("gui.cancel"), slot);
    }

    @Override
    public void confirmClicked(boolean result, int id) {
        if (this.deleteWorld) {
            this.deleteWorld = false;

            if (result) {
                ISaveFormat isaveformat = getMc().getSaveLoader();
                isaveformat.flushCache();
                isaveformat.deleteWorldDirectory(this.getFileName(id));

                this.loadWorldList();
            }

            changeScreen(this);
        }
    }

    public Screen getPreviousScreen() {
        return previousScreen;
    }

    public void selectWorld(int index) {
        changeScreen(null);

        if (!startingWorld) {
            startingWorld = true;
            String fileName = getFileName(index);

            if (fileName == null) {
                fileName = "World" + index;
            }

            String displayName = getDisplayName(index);

            if (displayName == null) {
                displayName = "World" + index;
            }

            if (getMc().getSaveLoader().canLoadWorld(fileName)) {
                getMc().launchIntegratedServer(fileName, displayName, null);
            }
        }
    }

    private String getFileName(int index) {
        return worldList.get(index).getFileName();
    }

    private String getDisplayName(int index) {
        String name = worldList.get(index).getDisplayName();
        if (StringUtils.isEmpty(name)) {
            name = I18n.format("selectedWorld.world") + " " + (index + 1);
        }
        return name;
    }

    class WorldSlot extends GuiSlot {

        public WorldSlot() {
            super(WorldSelectionScreen.this.width, WorldSelectionScreen.this.height, 32, WorldSelectionScreen.this.height - 64, 36);
        }

        @Override
        public int getSize() {
            return WorldSelectionScreen.this.worldList.size();
        }

        @Override
        protected void elementClicked(int slotIndex, boolean isDoubleClick, int mouseX, int mouseY) {
            WorldSelectionScreen.this.selectedSlot = slotIndex;
            boolean flag = WorldSelectionScreen.this.selectedSlot >= 0 && WorldSelectionScreen.this.selectedSlot < this.getSize();
            WorldSelectionScreen.this.getButton(SELECT).setEnabled(flag);
            WorldSelectionScreen.this.getButton(RENAME).setEnabled(flag);
            WorldSelectionScreen.this.getButton(DELETE).setEnabled(flag);
            WorldSelectionScreen.this.getButton(RECREATE).setEnabled(flag);

            if (isDoubleClick && flag) {
                WorldSelectionScreen.this.selectWorld(slotIndex);
            }
        }

        @Override
        protected boolean isSelected(int slotIndex) {
            return WorldSelectionScreen.this.selectedSlot == slotIndex;
        }

        @Override
        protected void drawSlot(int entryID, int x, int y, int p_180791_4_, int mouseXIn, int mouseYIn) {
            SaveFormatComparator saveFormatComparator = WorldSelectionScreen.this.worldList.get(entryID);
            String displayName = saveFormatComparator.getDisplayName();

            if (StringUtils.isEmpty(displayName)) {
                displayName = WorldSelectionScreen.this.worldString + " " + (entryID + 1);
            }

            String fileName = saveFormatComparator.getFileName();
            String description;

            if (saveFormatComparator.requiresConversion()) {
                description = WorldSelectionScreen.this.conversationString + " ";
            } else {
                description = I18n.format("gameMode." + (saveFormatComparator.isHardcoreModeEnabled() ? "hardcore" : saveFormatComparator.getEnumGameType().getName()));

                if (saveFormatComparator.isHardcoreModeEnabled()) {
                    description = EnumChatFormatting.DARK_RED + description + EnumChatFormatting.RESET;
                }

                if (saveFormatComparator.getCheatsEnabled()) {
                    description += ", " + I18n.format("selectWorld.cheats");
                }
            }

            getFr().drawStringWithShadow(displayName, x + 2, y + 1, 0xffffffff);
            getFr().drawStringWithShadow(fileName, x + 2, y + 12, 0xffaaaaaa);
            getFr().drawStringWithShadow(description, x + 2, y + 12 + 10, 0xffaaaaaa);
        }

        @Override
        public int getContentHeight() {
            return this.getSlotHeight() * WorldSelectionScreen.this.worldList.size();
        }

        @Override
        protected void drawBackground() {

        }
    }
}
