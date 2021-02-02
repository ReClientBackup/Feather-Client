package com.murengezi.minecraft.client.main;

import com.mojang.authlib.properties.PropertyMap;
import net.minecraft.util.Session;

import java.io.File;
import java.net.Proxy;

/**
 * @author Tobias Sjöblom
 * Created on 2021-02-02 at 10:16
 */
public class GameConfiguration {


	public final GameConfiguration.UserInformation userInfo;
	public final GameConfiguration.DisplayInformation displayInfo;
	public final GameConfiguration.FolderInformation folderInfo;
	public final GameConfiguration.GameInformation gameInfo;
	public final GameConfiguration.ServerInformation serverInfo;

	public GameConfiguration(UserInformation userInfo, GameConfiguration.DisplayInformation displayInfo, GameConfiguration.FolderInformation folderInfo, GameConfiguration.GameInformation gameInfo, GameConfiguration.ServerInformation serverInfo) {
		this.userInfo = userInfo;
		this.displayInfo = displayInfo;
		this.folderInfo = folderInfo;
		this.gameInfo = gameInfo;
		this.serverInfo = serverInfo;
	}

	public static class DisplayInformation {

		public final int width, height;
		public final boolean fullscreen, checkGlErrors;

		public DisplayInformation(int width, int height, boolean fullscreen, boolean checkGlErrors) {
			this.width = width;
			this.height = height;
			this.fullscreen = fullscreen;
			this.checkGlErrors = checkGlErrors;
		}
	}

	public static class FolderInformation {

		public final File mcDataDir;
		public final File resourcePacksDir;
		public final File assetsDir;
		public final String assetIndex;

		public FolderInformation(File mcDataDir, File resourcePacksDir, File assetsDir, String assetIndex) {
			this.mcDataDir = mcDataDir;
			this.resourcePacksDir = resourcePacksDir;
			this.assetsDir = assetsDir;
			this.assetIndex = assetIndex;
		}
	}

	public static class GameInformation {

		public final String version;

		public GameInformation(String versionIn) {
			this.version = versionIn;
		}
	}

	public static class ServerInformation {

		public final String serverName;
		public final int serverPort;

		public ServerInformation(String serverName, int serverPort) {
			this.serverName = serverName;
			this.serverPort = serverPort;
		}
	}

	public static class UserInformation {

		public final Session session;
		public final PropertyMap profileProperties;
		public final Proxy proxy;

		public UserInformation(Session session, PropertyMap profileProperties, Proxy proxy) {
			this.session = session;
			this.profileProperties = profileProperties;
			this.proxy = proxy;
		}
	}

}
