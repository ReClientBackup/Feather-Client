package com.murengezi.minecraft.client.resources.data;

import com.google.common.collect.Lists;
import com.google.gson.*;
import net.minecraft.util.JsonUtils;
import org.apache.commons.lang3.Validate;

import java.lang.reflect.Type;
import java.util.List;

public class AnimationMetadataSectionSerializer extends BaseMetadataSectionSerializer<AnimationMetadataSection> implements JsonSerializer<AnimationMetadataSection> {

	public AnimationMetadataSection deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext context) throws JsonParseException {
		List<AnimationFrame> list = Lists.newArrayList();
		JsonObject jsonobject = JsonUtils.getJsonObject(jsonElement, "metadata section");
		int i = JsonUtils.getInt(jsonobject, "frametime", 1);

		if (i != 1) {
			Validate.inclusiveBetween(1L, 2147483647L, i, "Invalid default frame time");
		}

		if (jsonobject.has("frames")) {
			try {
				JsonArray jsonarray = JsonUtils.getJsonArray(jsonobject, "frames");

				for (int j = 0; j < jsonarray.size(); ++j) {
					JsonElement jsonelement = jsonarray.get(j);
					AnimationFrame animationframe = this.parseAnimationFrame(j, jsonelement);

					if (animationframe != null) {
						list.add(animationframe);
					}
				}
			} catch (ClassCastException classcastexception) {
				throw new JsonParseException("Invalid animation->frames: expected array, was " + jsonobject.get("frames"), classcastexception);
			}
		}

		int k = JsonUtils.getInt(jsonobject, "width", -1);
		int l = JsonUtils.getInt(jsonobject, "height", -1);

		if (k != -1) {
			Validate.inclusiveBetween(1L, 2147483647L, k, "Invalid width");
		}

		if (l != -1) {
			Validate.inclusiveBetween(1L, 2147483647L, l, "Invalid height");
		}

		boolean flag = JsonUtils.getBoolean(jsonobject, "interpolate", false);
		return new AnimationMetadataSection(list, k, l, i, flag);
	}

	private AnimationFrame parseAnimationFrame(int p_110492_1_, JsonElement p_110492_2_) {
		if (p_110492_2_.isJsonPrimitive()) {
			return new AnimationFrame(JsonUtils.getInt(p_110492_2_, "frames[" + p_110492_1_ + "]"));
		} else if (p_110492_2_.isJsonObject()) {
			JsonObject jsonobject = JsonUtils.getJsonObject(p_110492_2_, "frames[" + p_110492_1_ + "]");
			int i = JsonUtils.getInt(jsonobject, "time", -1);

			if (jsonobject.has("time")) {
				Validate.inclusiveBetween(1L, 2147483647L, i, "Invalid frame time");
			}

			int j = JsonUtils.getInt(jsonobject, "index");
			Validate.inclusiveBetween(0L, 2147483647L, j, "Invalid frame index");
			return new AnimationFrame(j, i);
		} else {
			return null;
		}
	}

	public JsonElement serialize(AnimationMetadataSection section, Type type, JsonSerializationContext context) {
		JsonObject jsonobject = new JsonObject();
		jsonobject.addProperty("frametime", section.getFrameTime());

		if (section.getFrameWidth() != -1) {
			jsonobject.addProperty("width", section.getFrameWidth());
		}

		if (section.getFrameHeight() != -1) {
			jsonobject.addProperty("height", section.getFrameHeight());
		}

		if (section.getFrameCount() > 0) {
			JsonArray jsonarray = new JsonArray();

			for (int i = 0; i < section.getFrameCount(); ++i) {
				if (section.frameHasTime(i)) {
					JsonObject jsonobject1 = new JsonObject();
					jsonobject1.addProperty("index", section.getFrameIndex(i));
					jsonobject1.addProperty("time", section.getFrameTimeSingle(i));
					jsonarray.add(jsonobject1);
				} else {
					jsonarray.add(new JsonPrimitive(section.getFrameIndex(i)));
				}
			}

			jsonobject.add("frames", jsonarray);
		}

		return jsonobject;
	}

	public String getSectionName() {
		return "animation";
	}

}
