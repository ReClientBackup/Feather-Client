package com.murengezi.minecraft.client.gui.Singleplayer;

import java.io.IOException;
import java.util.Random;

import com.murengezi.minecraft.client.gui.GuiButton;
import com.murengezi.minecraft.client.gui.Screen;
import net.minecraft.client.gui.GuiCreateFlatWorld;
import net.minecraft.client.gui.GuiCustomizeWorldScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ChatAllowedCharacters;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.WorldType;
import net.minecraft.world.storage.ISaveFormat;
import net.minecraft.world.storage.WorldInfo;
import org.apache.commons.lang3.StringUtils;
import org.lwjgl.input.Keyboard;

public class WorldCreateScreen extends Screen {

    private final Screen previousScreen;
    private GuiTextField worldName;
    private GuiTextField worldSeed;
    private String worldFileName;
    private String gameMode = "survival";
    private String field_175300_s;
    private boolean generateStructures = true;

    private boolean allowCheats;
    private boolean field_146339_u;
    private boolean bonusItems;
    private boolean isHardcore;
    private boolean creatingWorld;
    private boolean field_146344_y;
    private String seed;
    private String name;
    private int selectedIndex;
    public String chunkProviderSettingsJson = "";

    private static final String[] disallowedFilenames = new String[] {"CON", "COM", "PRN", "AUX", "CLOCK$", "NUL", "COM1", "COM2", "COM3", "COM4", "COM5", "COM6", "COM7", "COM8", "COM9", "LPT1", "LPT2", "LPT3", "LPT4", "LPT5", "LPT6", "LPT7", "LPT8", "LPT9"};


    private static final int CREATE = 0;
    private static final int CANCEL = 1;
    private static final int GAMEMODE = 2;
    private static final int MOREOPTIONS = 3;
    private static final int MAPFEATURES = 4;
    private static final int MAPTYPE = 5;
    private static final int ALLOWCOMMANDS = 6;
    private static final int BONUSITEMS = 7;
    private static final int CUSTOMIZETYPE = 8;

    public WorldCreateScreen(Screen previousScreen) {
        this.previousScreen = previousScreen;
        this.seed = "";
        this.name = I18n.format("selectWorld.newWorld");
    }

    @Override
    public void updateScreen() {
        this.worldName.updateCursorCounter();
        this.worldSeed.updateCursorCounter();
        super.updateScreen();
    }

    @Override
    public void initGui() {
        Keyboard.enableRepeatEvents(true);
        this.buttonList.clear();

        addButton(new GuiButton(CREATE, this.width / 2 - 155, this.height - 28, 150, 20, I18n.format("selectWorld.create")));
        addButton(new GuiButton(CANCEL, this.width / 2 + 5, this.height - 28, 150, 20, I18n.format("gui.cancel")));
        addButton(new GuiButton(GAMEMODE, this.width / 2 - 75, 115, 150, 20, I18n.format("selectWorld.gameMode")));
        addButton(new GuiButton(MOREOPTIONS, this.width / 2 - 75, 187, 150, 20, I18n.format("selectWorld.moreWorldOptions")));
        addButton(new GuiButton(MAPFEATURES, this.width / 2 - 155, 100, 150, 20, I18n.format("selectWorld.mapFeatures")));
        getButton(MAPFEATURES).setVisible(false);
        
        addButton(new GuiButton(MAPTYPE, this.width / 2 + 5, 100, 150, 20, I18n.format("selectWorld.mapType")));
        getButton(MAPTYPE).setVisible(false);

        addButton(new GuiButton(BONUSITEMS, this.width / 2 + 5, 151, 150, 20, I18n.format("selectWorld.bonusItems")));
        getButton(BONUSITEMS).setVisible(false);
        
        addButton(new GuiButton(ALLOWCOMMANDS, this.width / 2 - 155, 151, 150, 20, I18n.format("selectWorld.allowCommands")));
        getButton(ALLOWCOMMANDS).setVisible(false);
        
        addButton(new GuiButton(CUSTOMIZETYPE, this.width / 2 + 5, 120, 150, 20, I18n.format("selectWorld.customizeType")));
        getButton(CUSTOMIZETYPE).setVisible(false);
        
        this.worldName = new GuiTextField(9, this.width / 2 - 100, 60, 200, 20);
        this.worldName.setFocused(true);
        this.worldName.setText(this.name);
        
        this.worldSeed = new GuiTextField(10, this.width / 2 - 100, 60, 200, 20);
        this.worldSeed.setText(this.seed);
        
        this.func_146316_a(this.field_146344_y);
        this.func_146314_g();
        this.updateDisplayStrings();
        super.initGui();
    }

    private void func_146314_g() {
        this.worldFileName = this.worldName.getText().trim();

        for (char c0 : ChatAllowedCharacters.allowedCharactersArray) {
            this.worldFileName = this.worldFileName.replace(c0, '_');
        }

        if (StringUtils.isEmpty(this.worldFileName)) {
            this.worldFileName = "World";
        }

        this.worldFileName = formatWorldName(getMc().getSaveLoader(), this.worldFileName);
    }

    private void updateDisplayStrings() {
        getButton(GAMEMODE).displayString = I18n.format("selectWorld.gameMode") + ": " + I18n.format("selectWorld.gameMode." + this.gameMode);
        getButton(MAPFEATURES).displayString = I18n.format("selectWorld.mapFeatures") + " ";

        if (this.generateStructures) {
            getButton(MAPFEATURES).displayString = getButton(MAPFEATURES).displayString + I18n.format("options.on");
        } else {
            getButton(MAPFEATURES).displayString = getButton(MAPFEATURES).displayString + I18n.format("options.off");
        }

        getButton(BONUSITEMS).displayString = I18n.format("selectWorld.bonusItems") + " ";

        if (this.bonusItems && !this.isHardcore) {
            getButton(BONUSITEMS).displayString = getButton(BONUSITEMS).displayString + I18n.format("options.on");
        } else {
            getButton(BONUSITEMS).displayString = getButton(BONUSITEMS).displayString + I18n.format("options.off");
        }

        getButton(MAPTYPE).displayString = I18n.format("selectWorld.mapType") + " " + I18n.format(WorldType.worldTypes[this.selectedIndex].getTranslateName());
        getButton(ALLOWCOMMANDS).displayString = I18n.format("selectWorld.allowCommands") + " ";

        if (this.allowCheats && !this.isHardcore) {
            getButton(ALLOWCOMMANDS).displayString = getButton(ALLOWCOMMANDS).displayString + I18n.format("options.on");
        } else {
            getButton(ALLOWCOMMANDS).displayString = getButton(ALLOWCOMMANDS).displayString + I18n.format("options.off");
        }
    }

    public static String formatWorldName(ISaveFormat saveFormat, String worldName) {
        StringBuilder worldNameBuilder = new StringBuilder(worldName.replaceAll("[\\./\"]", "_"));

        for (String disallowed : disallowedFilenames) {
            if (worldNameBuilder.toString().equalsIgnoreCase(disallowed)) {
                worldNameBuilder = new StringBuilder("_" + worldNameBuilder + "_");
            }
        }
        worldName = worldNameBuilder.toString();

        while (saveFormat.getWorldInfo(worldName) != null) {
            worldName = worldName + "-";
        }

        return worldName;
    }

    @Override
    public void onGuiClosed() {
        Keyboard.enableRepeatEvents(false);
        super.onGuiClosed();
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        if (button.isEnabled()) {
            switch (button.getId()) {
                case CREATE:
                    changeScreen(null);

                    if (this.creatingWorld) {
                        return;
                    }

                    this.creatingWorld = true;
                    long i = (new Random()).nextLong();
                    String s = this.worldSeed.getText();

                    if (!StringUtils.isEmpty(s)) {
                        try {
                            long j = Long.parseLong(s);

                            if (j != 0L) {
                                i = j;
                            }
                        }
                        catch (NumberFormatException exception) {
                            i = s.hashCode();
                        }
                    }

                    WorldSettings.GameType gameType = WorldSettings.GameType.getByName(this.gameMode);
                    WorldSettings worldsettings = new WorldSettings(i, gameType, this.generateStructures, this.isHardcore, WorldType.worldTypes[this.selectedIndex]);
                    worldsettings.setWorldName(this.chunkProviderSettingsJson);

                    if (this.bonusItems && !this.isHardcore) {
                        worldsettings.enableBonusChest();
                    }

                    if (this.allowCheats && !this.isHardcore) {
                        worldsettings.enableCommands();
                    }

                    getMc().launchIntegratedServer(this.worldFileName, this.worldName.getText().trim(), worldsettings);
                    break;
                case CANCEL:
                    changeScreen(previousScreen);
                    break;
                case GAMEMODE:
                    if (this.gameMode.equals("survival")) {
                        if (!this.field_146339_u) {
                            this.allowCheats = false;
                        }

                        this.gameMode = "hardcore";
                        this.isHardcore = true;
                        getButton(ALLOWCOMMANDS).setEnabled(false);
                        getButton(BONUSITEMS).setEnabled(false);
                        this.updateDisplayStrings();
                    } else if (this.gameMode.equals("hardcore")) {
                        if (!this.field_146339_u) {
                            this.allowCheats = true;
                        }

                        this.gameMode = "creative";
                        this.isHardcore = false;
                        getButton(ALLOWCOMMANDS).setEnabled(true);
                        getButton(BONUSITEMS).setEnabled(true);
                    } else {
                        if (!this.field_146339_u) {
                            this.allowCheats = false;
                        }

                        this.gameMode = "survival";
                        getButton(ALLOWCOMMANDS).setEnabled(true);
                        getButton(BONUSITEMS).setEnabled(true);
                        this.isHardcore = false;
                    }

                    this.updateDisplayStrings();
                    break;
                case MOREOPTIONS:
                    this.func_146316_a(!this.field_146344_y);
                    break;
                case MAPFEATURES:
                    this.generateStructures = !this.generateStructures;
                    this.updateDisplayStrings();
                    break;
                case MAPTYPE:
                    ++this.selectedIndex;

                    if (this.selectedIndex >= WorldType.worldTypes.length) {
                        this.selectedIndex = 0;
                    }

                    while (!this.func_175299_g()) {
                        ++this.selectedIndex;

                        if (this.selectedIndex >= WorldType.worldTypes.length) {
                            this.selectedIndex = 0;
                        }
                    }

                    this.chunkProviderSettingsJson = "";
                    this.updateDisplayStrings();
                    this.func_146316_a(this.field_146344_y);
                    break;
                case ALLOWCOMMANDS:
                    this.field_146339_u = true;
                    this.allowCheats = !this.allowCheats;
                    this.updateDisplayStrings();
                    break;
                case BONUSITEMS:
                    this.bonusItems = !this.bonusItems;
                    this.updateDisplayStrings();
                    break;
                case CUSTOMIZETYPE:
                    if (WorldType.worldTypes[this.selectedIndex] == WorldType.FLAT)
                    {
                        changeScreen(new GuiCreateFlatWorld(this, this.chunkProviderSettingsJson));
                    } else {
                        changeScreen(new GuiCustomizeWorldScreen(this, this.chunkProviderSettingsJson));
                    }
                    break;
            }
        }
    }

    private boolean func_175299_g() {
        WorldType worldtype = WorldType.worldTypes[this.selectedIndex];
        return worldtype != null && worldtype.getCanBeCreated() && (worldtype != WorldType.DEBUG_WORLD || isShiftKeyDown());
    }

    private void func_146316_a(boolean p_146316_1_) {
        this.field_146344_y = p_146316_1_;

        if (WorldType.worldTypes[this.selectedIndex] == WorldType.DEBUG_WORLD) {
            getButton(GAMEMODE).setVisible(!this.field_146344_y);
            getButton(GAMEMODE).setEnabled(false);

            if (this.field_175300_s == null) {
                this.field_175300_s = this.gameMode;
            }

            this.gameMode = "spectator";
            getButton(MAPFEATURES).setVisible(false);
            getButton(BONUSITEMS).setVisible(false);
            getButton(MAPTYPE).setVisible(this.field_146344_y);
            getButton(ALLOWCOMMANDS).setVisible(false);
            getButton(CUSTOMIZETYPE).setVisible(false);
        } else {
            getButton(GAMEMODE).setVisible(!this.field_146344_y);
            getButton(GAMEMODE).setEnabled(true);

            if (this.field_175300_s != null) {
                this.gameMode = this.field_175300_s;
                this.field_175300_s = null;
            }

            getButton(MAPFEATURES).setVisible(this.field_146344_y && WorldType.worldTypes[this.selectedIndex] != WorldType.CUSTOMIZED);
            getButton(BONUSITEMS).setVisible(this.field_146344_y);
            getButton(MAPTYPE).setVisible(this.field_146344_y);
            getButton(ALLOWCOMMANDS).setVisible(this.field_146344_y);
            getButton(CUSTOMIZETYPE).setVisible(this.field_146344_y && (WorldType.worldTypes[this.selectedIndex] == WorldType.FLAT || WorldType.worldTypes[this.selectedIndex] == WorldType.CUSTOMIZED));
        }

        this.updateDisplayStrings();

        if (this.field_146344_y) {
            getButton(MOREOPTIONS).displayString = I18n.format("gui.done");
        } else {
            getButton(MOREOPTIONS).displayString = I18n.format("selectWorld.moreWorldOptions");
        }
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        if (this.worldName.isFocused() && !this.field_146344_y) {
            this.worldName.textBoxKeyTyped(typedChar, keyCode);
            this.name = this.worldName.getText();
        } else if (this.worldSeed.isFocused() && this.field_146344_y) {
            this.worldSeed.textBoxKeyTyped(typedChar, keyCode);
            this.seed = this.worldSeed.getText();
        }


        if (keyCode == Keyboard.KEY_RETURN || keyCode == Keyboard.KEY_NUMPADENTER) {
            this.actionPerformed(getButton(CREATE));
        }

        getButton(CREATE).setEnabled(this.worldName.getText().length() > 0);
        this.func_146314_g();
        super.keyTyped(typedChar, keyCode);
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (this.field_146344_y) {
            this.worldSeed.mouseClicked(mouseX, mouseY, mouseButton);
        } else {
            this.worldName.mouseClicked(mouseX, mouseY, mouseButton);
        }

        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground(mouseX, mouseY, 60);
        getFr().drawCenteredString(I18n.format("selectWorld.create"), this.width / 2, 20, -1);

        if (this.field_146344_y) {
            getFr().drawString(I18n.format("selectWorld.enterSeed"), this.width / 2 - 100, 47, -6250336);
            this.worldSeed.drawTextBox();
        } else {
            getFr().drawString(I18n.format("selectWorld.enterName"), this.width / 2 - 100, 47, -6250336);
            getFr().drawString(I18n.format("selectWorld.resultFolder") + " " + this.worldFileName, this.width / 2 - 100, 85, -6250336);
            this.worldName.drawTextBox();
        }

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    public void func_146318_a(WorldInfo info) {
        this.name = I18n.format("selectWorld.newWorld.copyOf", info.getWorldName());
        this.seed = info.getSeed() + "";
        this.selectedIndex = info.getTerrainType().getWorldTypeID();
        this.chunkProviderSettingsJson = info.getGeneratorOptions();
        this.generateStructures = info.isMapFeaturesEnabled();
        this.allowCheats = info.areCommandsAllowed();

        if (info.isHardcoreModeEnabled()) {
            this.gameMode = "hardcore";
        } else if (info.getGameType().isSurvivalOrAdventure()) {
            this.gameMode = "survival";
        } else if (info.getGameType().isCreative()) {
            this.gameMode = "creative";
        }
    }
}
