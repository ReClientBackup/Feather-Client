package com.murengezi.feather;

import com.murengezi.feather.Config.ConfigManager;
import com.murengezi.feather.Discord.DiscordRP;
import com.murengezi.feather.Git.GitManager;
import com.murengezi.feather.Gui.Adjust.AdjustScreen;
import com.murengezi.feather.Image.ImageManager;
import com.murengezi.feather.Module.ModuleManager;
import com.murengezi.feather.Particle.ParticleManager;
import com.murengezi.feather.Theme.ThemeManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * @author Tobias Sjöblom
 * Created on 2021-01-10 at 00:01
 */
public class Feather {

    private static Logger logger;
    private static ModuleManager moduleManager;
    private static ImageManager imageManager;
    private static ParticleManager particleManager;
    private static ThemeManager themeManager;
    private static ConfigManager configManager;
    private static GitManager gitManager;
    private static DiscordRP discordRP;
    private static AdjustScreen adjustScreen;

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

        Runtime.getRuntime().addShutdownHook(new Thread(Feather::shutdown));
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

}
