package com.murengezi.feather.API;

import net.minecraft.util.HttpUtil;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.Base64;

/**
 * @author Tobias Sjöblom
 * Created on 2021-01-22 at 10:45
 */
public class MurengeziAPI {

	public static final String API_URL = "http://api.murengezi.com/";

	public static String getPlayerCape(String uuid) {
		try {
			uuid = stripDashes(uuid);
			String json = HttpUtil.get(new URL(API_URL + "cape/" + uuid));
			JSONObject jsonObject = new JSONObject(json);
			if (jsonObject.has("error") || jsonObject.isEmpty()) {
				return null;
			} else if (!jsonObject.isEmpty() && jsonObject.has("url") && jsonObject.getString("url") != null) {
				return new String(Base64.getDecoder().decode(jsonObject.getString("url")));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	private static String stripDashes(String s) {
		return s.replaceAll("-", "");
	}

}
