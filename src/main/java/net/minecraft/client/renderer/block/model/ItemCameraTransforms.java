package net.minecraft.client.renderer.block.model;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.lang.reflect.Type;
import net.minecraft.client.renderer.GlStateManager;

public class ItemCameraTransforms {
    
    public static final ItemCameraTransforms DEFAULT = new ItemCameraTransforms();
    public static float field_181690_b = 0.0F, field_181691_c = 0.0F, field_181692_d = 0.0F, field_181693_e = 0.0F, field_181694_f = 0.0F, field_181695_g = 0.0F, field_181696_h = 0.0F, field_181697_i = 0.0F, field_181698_j = 0.0F;
    public final ItemTransformVec3f thirdPerson, firstPerson, head, gui, ground, fixed;

    private ItemCameraTransforms() {
        this(ItemTransformVec3f.DEFAULT, ItemTransformVec3f.DEFAULT, ItemTransformVec3f.DEFAULT, ItemTransformVec3f.DEFAULT, ItemTransformVec3f.DEFAULT, ItemTransformVec3f.DEFAULT);
    }

    public ItemCameraTransforms(ItemCameraTransforms cameraTransforms) {
        this.thirdPerson = cameraTransforms.thirdPerson;
        this.firstPerson = cameraTransforms.firstPerson;
        this.head = cameraTransforms.head;
        this.gui = cameraTransforms.gui;
        this.ground = cameraTransforms.ground;
        this.fixed = cameraTransforms.fixed;
    }

    public ItemCameraTransforms(ItemTransformVec3f thirdPerson, ItemTransformVec3f firstPerson, ItemTransformVec3f head, ItemTransformVec3f gui, ItemTransformVec3f ground, ItemTransformVec3f fixed) {
        this.thirdPerson = thirdPerson;
        this.firstPerson = firstPerson;
        this.head = head;
        this.gui = gui;
        this.ground = ground;
        this.fixed = fixed;
    }

    public void func_181689_a(ItemCameraTransforms.TransformType p_181689_1_) {
        ItemTransformVec3f itemtransformvec3f = this.func_181688_b(p_181689_1_);

        if (itemtransformvec3f != ItemTransformVec3f.DEFAULT) {
            GlStateManager.translate(itemtransformvec3f.translation.x + field_181690_b, itemtransformvec3f.translation.y + field_181691_c, itemtransformvec3f.translation.z + field_181692_d);
            GlStateManager.rotate(itemtransformvec3f.rotation.y + field_181694_f, 0.0F, 1.0F, 0.0F);
            GlStateManager.rotate(itemtransformvec3f.rotation.x + field_181693_e, 1.0F, 0.0F, 0.0F);
            GlStateManager.rotate(itemtransformvec3f.rotation.z + field_181695_g, 0.0F, 0.0F, 1.0F);
            GlStateManager.scale(itemtransformvec3f.scale.x + field_181696_h, itemtransformvec3f.scale.y + field_181697_i, itemtransformvec3f.scale.z + field_181698_j);
        }
    }

    public ItemTransformVec3f func_181688_b(ItemCameraTransforms.TransformType type) {
        switch (type) {
            case THIRD_PERSON:
                return this.thirdPerson;
            case FIRST_PERSON:
                return this.firstPerson;
            case HEAD:
                return this.head;
            case GUI:
                return this.gui;
            case GROUND:
                return this.ground;
            case FIXED:
                return this.fixed;
            default:
                return ItemTransformVec3f.DEFAULT;
        }
    }

    public boolean func_181687_c(ItemCameraTransforms.TransformType type) {
        return !this.func_181688_b(type).equals(ItemTransformVec3f.DEFAULT);
    }

    static class Deserializer implements JsonDeserializer<ItemCameraTransforms> {
        public ItemCameraTransforms deserialize(JsonElement p_deserialize_1_, Type p_deserialize_2_, JsonDeserializationContext p_deserialize_3_) throws JsonParseException {
            JsonObject jsonobject = p_deserialize_1_.getAsJsonObject();
            ItemTransformVec3f itemtransformvec3f = this.func_181683_a(p_deserialize_3_, jsonobject, "thirdperson");
            ItemTransformVec3f itemtransformvec3f1 = this.func_181683_a(p_deserialize_3_, jsonobject, "firstperson");
            ItemTransformVec3f itemtransformvec3f2 = this.func_181683_a(p_deserialize_3_, jsonobject, "head");
            ItemTransformVec3f itemtransformvec3f3 = this.func_181683_a(p_deserialize_3_, jsonobject, "gui");
            ItemTransformVec3f itemtransformvec3f4 = this.func_181683_a(p_deserialize_3_, jsonobject, "ground");
            ItemTransformVec3f itemtransformvec3f5 = this.func_181683_a(p_deserialize_3_, jsonobject, "fixed");
            return new ItemCameraTransforms(itemtransformvec3f, itemtransformvec3f1, itemtransformvec3f2, itemtransformvec3f3, itemtransformvec3f4, itemtransformvec3f5);
        }

        private ItemTransformVec3f func_181683_a(JsonDeserializationContext p_181683_1_, JsonObject p_181683_2_, String p_181683_3_) {
            return p_181683_2_.has(p_181683_3_) ? (ItemTransformVec3f)p_181683_1_.deserialize(p_181683_2_.get(p_181683_3_), ItemTransformVec3f.class) : ItemTransformVec3f.DEFAULT;
        }
    }

    public enum TransformType {
        NONE, THIRD_PERSON, FIRST_PERSON, HEAD, GUI, GROUND, FIXED
    }

}
