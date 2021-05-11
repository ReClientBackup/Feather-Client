package com.murengezi.minecraft.client.renderer.block.model;

import com.google.common.collect.Maps;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.MathHelper;
import org.lwjgl.util.vector.Vector3f;

public class BlockPart {

    public final Vector3f positionFrom, positionTo;
    public final Map<EnumFacing, BlockPartFace> mapFaces;
    public final BlockPartRotation partRotation;
    public final boolean shade;

    public BlockPart(Vector3f positionFrom, Vector3f positionTo, Map<EnumFacing, BlockPartFace> mapFaces, BlockPartRotation partRotation, boolean shade) {
        this.positionFrom = positionFrom;
        this.positionTo = positionTo;
        this.mapFaces = mapFaces;
        this.partRotation = partRotation;
        this.shade = shade;
        this.setDefaultUvs();
    }

    private void setDefaultUvs() {
        for (Entry<EnumFacing, BlockPartFace> entry : this.mapFaces.entrySet()) {
            float[] afloat = this.getFaceUvs(entry.getKey());
            entry.getValue().blockFaceUV.setUvs(afloat);
        }
    }

    private float[] getFaceUvs(EnumFacing facing) {
        float[] afloat;

        switch (facing) {
            case DOWN:
            case UP:
                afloat = new float[] {this.positionFrom.x, this.positionFrom.z, this.positionTo.x, this.positionTo.z};
                break;
            case NORTH:
            case SOUTH:
                afloat = new float[] {this.positionFrom.x, 16.0F - this.positionTo.y, this.positionTo.x, 16.0F - this.positionFrom.y};
                break;
            case WEST:
            case EAST:
                afloat = new float[] {this.positionFrom.z, 16.0F - this.positionTo.y, this.positionTo.z, 16.0F - this.positionFrom.y};
                break;
            default:
                throw new NullPointerException();
        }

        return afloat;
    }

    static class Deserializer implements JsonDeserializer<BlockPart> {

        public BlockPart deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext context) throws JsonParseException {
            JsonObject jsonobject = jsonElement.getAsJsonObject();
            Vector3f vector3f = this.parsePositionFrom(jsonobject);
            Vector3f vector3f1 = this.parsePositionTo(jsonobject);
            BlockPartRotation blockpartrotation = this.parseRotation(jsonobject);
            Map<EnumFacing, BlockPartFace> map = this.parseFacesCheck(context, jsonobject);

            if (jsonobject.has("shade") && !JsonUtils.isBoolean(jsonobject, "shade")) {
                throw new JsonParseException("Expected shade to be a Boolean");
            } else {
                boolean flag = JsonUtils.getBoolean(jsonobject, "shade", true);
                return new BlockPart(vector3f, vector3f1, map, blockpartrotation, flag);
            }
        }

        private BlockPartRotation parseRotation(JsonObject jsonObject) {
            BlockPartRotation blockpartrotation = null;

            if (jsonObject.has("rotation")) {
                JsonObject jsonobject = JsonUtils.getJsonObject(jsonObject, "rotation");
                Vector3f vector3f = this.parsePosition(jsonobject, "origin");
                vector3f.scale(0.0625F);
                EnumFacing.Axis enumfacing$axis = this.parseAxis(jsonobject);
                float f = this.parseAngle(jsonobject);
                boolean flag = JsonUtils.getBoolean(jsonobject, "rescale", false);
                blockpartrotation = new BlockPartRotation(vector3f, enumfacing$axis, f, flag);
            }

            return blockpartrotation;
        }

        private float parseAngle(JsonObject jsonObject) {
            float f = JsonUtils.getFloat(jsonObject, "angle");

            if (f != 0.0F && MathHelper.abs(f) != 22.5F && MathHelper.abs(f) != 45.0F) {
                throw new JsonParseException("Invalid rotation " + f + " found, only -45/-22.5/0/22.5/45 allowed");
            } else {
                return f;
            }
        }

        private EnumFacing.Axis parseAxis(JsonObject jsonObject) {
            String s = JsonUtils.getString(jsonObject, "axis");
            EnumFacing.Axis enumfacing$axis = EnumFacing.Axis.byName(s.toLowerCase());

            if (enumfacing$axis == null) {
                throw new JsonParseException("Invalid rotation axis: " + s);
            } else {
                return enumfacing$axis;
            }
        }

        private Map<EnumFacing, BlockPartFace> parseFacesCheck(JsonDeserializationContext context, JsonObject jsonObject) {
            Map<EnumFacing, BlockPartFace> map = this.parseFaces(context, jsonObject);

            if (map.isEmpty()) {
                throw new JsonParseException("Expected between 1 and 6 unique faces, got 0");
            } else {
                return map;
            }
        }

        private Map<EnumFacing, BlockPartFace> parseFaces(JsonDeserializationContext context, JsonObject jsonObject) {
            Map<EnumFacing, BlockPartFace> map = Maps.newEnumMap(EnumFacing.class);
            JsonObject jsonobject = JsonUtils.getJsonObject(jsonObject, "faces");

            for (Entry<String, JsonElement> entry : jsonobject.entrySet()) {
                EnumFacing enumfacing = this.parseEnumFacing(entry.getKey());
                map.put(enumfacing, context.deserialize(entry.getValue(), BlockPartFace.class));
            }

            return map;
        }

        private EnumFacing parseEnumFacing(String name) {
            EnumFacing enumfacing = EnumFacing.byName(name);

            if (enumfacing == null) {
                throw new JsonParseException("Unknown facing: " + name);
            } else {
                return enumfacing;
            }
        }

        private Vector3f parsePositionTo(JsonObject jsonObject) {
            Vector3f vector3f = this.parsePosition(jsonObject, "to");

            if (vector3f.x >= -16.0F && vector3f.y >= -16.0F && vector3f.z >= -16.0F && vector3f.x <= 32.0F && vector3f.y <= 32.0F && vector3f.z <= 32.0F) {
                return vector3f;
            } else {
                throw new JsonParseException("'to' specifier exceeds the allowed boundaries: " + vector3f);
            }
        }

        private Vector3f parsePositionFrom(JsonObject jsonObject) {
            Vector3f vector3f = this.parsePosition(jsonObject, "from");

            if (vector3f.x >= -16.0F && vector3f.y >= -16.0F && vector3f.z >= -16.0F && vector3f.x <= 32.0F && vector3f.y <= 32.0F && vector3f.z <= 32.0F) {
                return vector3f;
            } else {
                throw new JsonParseException("'from' specifier exceeds the allowed boundaries: " + vector3f);
            }
        }

        private Vector3f parsePosition(JsonObject jsonObject, String p_178251_2_) {
            JsonArray jsonarray = JsonUtils.getJsonArray(jsonObject, p_178251_2_);

            if (jsonarray.size() != 3) {
                throw new JsonParseException("Expected 3 " + p_178251_2_ + " values, found: " + jsonarray.size());
            } else {
                float[] afloat = new float[3];

                for (int i = 0; i < afloat.length; ++i) {
                    afloat[i] = JsonUtils.getFloat(jsonarray.get(i), p_178251_2_ + "[" + i + "]");
                }

                return new Vector3f(afloat[0], afloat[1], afloat[2]);
            }
        }
    }

}
