package com.murengezi.minecraft.client.main;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mojang.authlib.properties.PropertyMap;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;
import com.murengezi.minecraft.client.Minecraft;
import net.minecraft.util.Session;
import net.minecraft.util.StringUtils;

import java.io.File;
import java.net.Authenticator;
import java.net.InetSocketAddress;
import java.net.PasswordAuthentication;
import java.net.Proxy;
import java.util.List;

/**
 * @author Tobias Sjöblom
 * Created on 2021-02-02 at 10:19
 */
public class Main {

	public static void main(String[] args) {
		System.setProperty("java.net.preferIPv4Stack", "true");
		OptionParser optionparser = new OptionParser();
		optionparser.allowsUnrecognizedOptions();
		optionparser.accepts("fullscreen");
		optionparser.accepts("checkGlErrors");
		OptionSpec<String> server = optionparser.accepts("server").withRequiredArg();
		OptionSpec<Integer> port = optionparser.accepts("port").withRequiredArg().ofType(Integer.class).defaultsTo(25565);
		OptionSpec<File> gameDir = optionparser.accepts("gameDir").withRequiredArg().ofType(File.class).defaultsTo(new File("."));
		OptionSpec<File> assetsDir = optionparser.accepts("assetsDir").withRequiredArg().ofType(File.class);
		OptionSpec<File> resourcePackDir = optionparser.accepts("resourcePackDir").withRequiredArg().ofType(File.class);
		OptionSpec<String> proxyHost = optionparser.accepts("proxyHost").withRequiredArg();
		OptionSpec<Integer> proxyPort = optionparser.accepts("proxyPort").withRequiredArg().defaultsTo("8080", new String[0]).ofType(Integer.class);
		OptionSpec<String> proxyUser = optionparser.accepts("proxyUser").withRequiredArg();
		OptionSpec<String> proxyPass = optionparser.accepts("proxyPass").withRequiredArg();
		OptionSpec<String> username = optionparser.accepts("username").withRequiredArg().defaultsTo("Player" + Minecraft.getSystemTime() % 1000L);
		OptionSpec<String> uuid = optionparser.accepts("uuid").withRequiredArg();
		OptionSpec<String> accessToken = optionparser.accepts("accessToken").withRequiredArg().required();
		OptionSpec<String> version = optionparser.accepts("version").withRequiredArg().required();
		OptionSpec<Integer> width = optionparser.accepts("width").withRequiredArg().ofType(Integer.class).defaultsTo(854);
		OptionSpec<Integer> height = optionparser.accepts("height").withRequiredArg().ofType(Integer.class).defaultsTo(480);
		OptionSpec<String> profileProperties = optionparser.accepts("profileProperties").withRequiredArg().defaultsTo("{}");
		OptionSpec<String> assetIndex = optionparser.accepts("assetIndex").withRequiredArg();
		OptionSpec<String> userType = optionparser.accepts("userType").withRequiredArg().defaultsTo("legacy");
		OptionSpec<String> ignored = optionparser.nonOptions();
		OptionSet optionset = optionparser.parse(args);
		List<String> list = optionset.valuesOf(ignored);

		if (!list.isEmpty()) {
			System.out.println("Completely ignored arguments: " + list);
		}

		String host = optionset.valueOf(proxyHost);
		Proxy proxy = Proxy.NO_PROXY;

		if (host != null) {
			try {
				proxy = new Proxy(Proxy.Type.SOCKS, new InetSocketAddress(host, optionset.valueOf(proxyPort)));
			} catch (Exception ignored1) {}
		}

		final String s1 = optionset.valueOf(proxyUser);
		final String s2 = optionset.valueOf(proxyPass);

		if (!proxy.equals(Proxy.NO_PROXY) && StringUtils.isNullOrEmpty(s1) && StringUtils.isNullOrEmpty(s2)) {
			Authenticator.setDefault(new Authenticator() {
				protected PasswordAuthentication getPasswordAuthentication()
				{
					return new PasswordAuthentication(s1, s2.toCharArray());
				}
			});
		}

		int inputWidth = optionset.valueOf(width);
		int inputHeight = optionset.valueOf(height);
		boolean fullscreen = optionset.has("fullscreen");
		boolean checkGlErrors = optionset.has("checkGlErrors");
		String s3 = optionset.valueOf(version);
		Gson gson = (new GsonBuilder()).registerTypeAdapter(PropertyMap.class, new PropertyMap.Serializer()).create();
		PropertyMap profilePropertiesMap = gson.fromJson(optionset.valueOf(profileProperties), PropertyMap.class);
		File gameDirFile = optionset.valueOf(gameDir);
		File assetsDirFile = optionset.has(assetsDir) ? optionset.valueOf(assetsDir) : new File(gameDirFile, "assets/");
		File resourcePackDirFile = optionset.has(resourcePackDir) ? optionset.valueOf(resourcePackDir) : new File(gameDirFile, "resourcepacks/");
		String uuidString = optionset.has(uuid) ? uuid.value(optionset) : username.value(optionset);
		String assetIndexString = optionset.has(assetIndex) ? assetIndex.value(optionset) : null;
		String serverAddress = optionset.valueOf(server);
		Integer serverPort = optionset.valueOf(port);
		Session session = new Session(username.value(optionset), uuidString, accessToken.value(optionset), userType.value(optionset));
		GameConfiguration gameConfiguration = new GameConfiguration(new GameConfiguration.UserInformation(session, profilePropertiesMap, proxy), new GameConfiguration.DisplayInformation(inputWidth, inputHeight, fullscreen, checkGlErrors), new GameConfiguration.FolderInformation(gameDirFile, resourcePackDirFile, assetsDirFile, assetIndexString), new GameConfiguration.GameInformation(s3), new GameConfiguration.ServerInformation(serverAddress, serverPort));
		Runtime.getRuntime().addShutdownHook(new Thread("Client Shutdown Thread") {
			public void run() {
				Minecraft.stopIntegratedServer();
			}
		});
		Thread.currentThread().setName("Client thread");
		(new Minecraft(gameConfiguration)).run();
	}
}
