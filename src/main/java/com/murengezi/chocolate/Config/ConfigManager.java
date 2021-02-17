package com.murengezi.chocolate.Config;

import com.darkmagician6.eventapi.EventManager;
import com.darkmagician6.eventapi.EventTarget;
import com.murengezi.chocolate.Event.ModuleDisableEvent;
import com.murengezi.chocolate.Event.ModuleEnableEvent;
import com.murengezi.chocolate.Chocolate;
import com.murengezi.chocolate.Module.Adjustable;
import com.murengezi.chocolate.Module.Module;
import com.murengezi.chocolate.Util.MinecraftUtils;
import org.json.JSONObject;

import java.io.*;

/**
 * @author Tobias Sjöblom
 * Created on 2021-01-24 at 21:38
 */
public class ConfigManager extends MinecraftUtils {

	public static final File directory = new File(getMc().dataDir + File.separator + "Chocolate");

	public ConfigManager() {
		if (!getDirectory().exists()) {
			if (getDirectory().mkdir()) {
				Chocolate.getLogger().info("Created config directory.");
			}
		}

		Chocolate.getModuleManager().getModules().forEach(this::createOrLoadConfig);
		EventManager.register(this);
	}

	public void createOrLoadConfig(Module module) {
		File file = new File(getDirectory() + File.separator + module.getClass().getSimpleName() + ".json");
		boolean adjustable = module instanceof Adjustable;
		try {
			if (file.createNewFile()) {
				Chocolate.getLogger().info("Created " + file.getName());
			} else {
				BufferedReader reader = new BufferedReader(new FileReader(file));
				StringBuilder stringBuilder = new StringBuilder();
				String line;
				while ((line = reader.readLine()) != null) {
					stringBuilder.append(line);
				}
				reader.close();

				JSONObject jsonObject = new JSONObject(stringBuilder.toString());

				if (jsonObject.has("enabled")) {
					if (jsonObject.getBoolean("enabled")) {
						if (!module.isEnabled()) {
							module.Toggle(false);
						}
					} else {
						if (module.isEnabled()) {
							module.Toggle(false);
						}
					}
				}

				if (adjustable) {
					Adjustable adjustableModule = (Adjustable) module;
					if (jsonObject.has("region") && jsonObject.has("x") && jsonObject.has("y")) {
						adjustableModule.setRegion(jsonObject.getEnum(Adjustable.Region.class, "region"));
						adjustableModule.setX(jsonObject.getFloat("x"));
						adjustableModule.setY(jsonObject.getFloat("y"));
					}
				}
			}
		} catch (IOException e) {
			Chocolate.getLogger().error("Error while creating or loading config: " + file.getName(), e);
		}
	}

	public void saveAllConfigs() {
		Chocolate.getModuleManager().getModules().forEach(this::saveConfig);
	}

	public void saveConfig(Module module) {
		File file = new File(getDirectory() + File.separator + module.getClass().getSimpleName() + ".json");
		boolean adjustable = module instanceof Adjustable;

		try {
			if (file.createNewFile()) {
				Chocolate.getLogger().info("Created " + file.getName());
			}

			JSONObject jsonObject = new JSONObject();
			jsonObject.put("enabled", module.isEnabled());
			if (adjustable) {
				Adjustable adjustableModule = (Adjustable) module;
				jsonObject.put("region", adjustableModule.getRegion());
				jsonObject.put("x", adjustableModule.getRelativeX());
				jsonObject.put("y", adjustableModule.getRelativeY());
			}

			BufferedWriter writer = new BufferedWriter(new FileWriter(file));
			writer.write(jsonObject.toString(4));
			writer.flush();
			writer.close();
		} catch (IOException e) {
			Chocolate.getLogger().error("Error while creating or writing config: " + file.getName(), e);
		}
	}

	@EventTarget
	public void onModuleEnable(ModuleEnableEvent event) {
		if (event.doSave()) {
			saveConfig(event.getModule());
		}
	}

	@EventTarget
	public void onModuleDisable(ModuleDisableEvent event) {
		if (event.doSave()) {
			saveConfig(event.getModule());
		}
	}

	public static File getDirectory() {
		return directory;
	}
}
