package com.murengezi.chocolate;

import com.murengezi.chocolate.Config.ConfigManager;
import com.murengezi.chocolate.Discord.DiscordRP;
import com.murengezi.chocolate.Git.GitManager;
import com.murengezi.chocolate.Gui.Adjust.AdjustScreen;
import com.murengezi.chocolate.Gui.Click.ClickGui;
import com.murengezi.chocolate.Image.ImageManager;
import com.murengezi.chocolate.Module.ModuleManager;
import com.murengezi.chocolate.Particle.ParticleManager;
import com.murengezi.chocolate.Theme.ThemeManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * @author Tobias Sjöblom
 * Created on 2021-01-10 at 00:01
 */
public class Chocolate {

    private static Logger logger;
    private static ModuleManager moduleManager;
    private static ImageManager imageManager;
    private static ParticleManager particleManager;
    private static ThemeManager themeManager;
    private static ConfigManager configManager;
    private static GitManager gitManager;
    private static DiscordRP discordRP;
    private static AdjustScreen adjustScreen;
    private static ClickGui clickGui;

    public static void start() {
        logger = LogManager.getLogger();
        moduleManager = new ModuleManager();
        imageManager = new ImageManager();
        particleManager = new ParticleManager();
        themeManager = new ThemeManager();
        configManager = new ConfigManager();
        gitManager = new GitManager();
        discordRP = new DiscordRP();
        adjustScreen = new AdjustScreen();
        clickGui = new ClickGui();

        Runtime.getRuntime().addShutdownHook(new Thread(Chocolate::shutdown));
    }

    public static void shutdown() {
        getDiscordRP().shutdown();
        getConfigManager().saveAllConfigs();
    }

    public static Logger getLogger() {
        return logger;
    }

    public static ModuleManager getModuleManager() {
        return moduleManager;
    }

    public static ImageManager getImageManager() {
        return imageManager;
    }

    public static ParticleManager getParticleManager() {
        return particleManager;
    }

    public static ThemeManager getThemeManager() {
        return themeManager;
    }

    public static ConfigManager getConfigManager() {
        return configManager;
    }

    public static GitManager getGitManager() {
        return gitManager;
    }

    public static DiscordRP getDiscordRP() {
        return discordRP;
    }

    public static AdjustScreen getAdjustScreen() {
        return adjustScreen;
    }

    public static ClickGui getClickGui() {
        return clickGui;
    }
}
