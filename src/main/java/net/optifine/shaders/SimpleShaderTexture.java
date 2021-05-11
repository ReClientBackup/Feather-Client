package net.optifine.shaders;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import com.murengezi.minecraft.client.renderer.texture.AbstractTexture;
import com.murengezi.minecraft.client.renderer.texture.TextureUtil;
import com.murengezi.minecraft.client.resources.IResourceManager;
import com.murengezi.minecraft.client.resources.data.AnimationMetadataSection;
import com.murengezi.minecraft.client.resources.data.AnimationMetadataSectionSerializer;
import com.murengezi.minecraft.client.resources.data.FontMetadataSection;
import com.murengezi.minecraft.client.resources.data.FontMetadataSectionSerializer;
import com.murengezi.minecraft.client.resources.data.IMetadataSerializer;
import com.murengezi.minecraft.client.resources.data.LanguageMetadataSection;
import com.murengezi.minecraft.client.resources.data.LanguageMetadataSectionSerializer;
import com.murengezi.minecraft.client.resources.data.PackMetadataSection;
import com.murengezi.minecraft.client.resources.data.PackMetadataSectionSerializer;
import com.murengezi.minecraft.client.resources.data.TextureMetadataSection;
import com.murengezi.minecraft.client.resources.data.TextureMetadataSectionSerializer;
import org.apache.commons.io.IOUtils;

public class SimpleShaderTexture extends AbstractTexture {
   private final String texturePath;
   private static final IMetadataSerializer METADATA_SERIALIZER = makeMetadataSerializer();

   public SimpleShaderTexture(String texturePath) {
      this.texturePath = texturePath;
   }

   public void loadTexture(IResourceManager resourceManager) throws IOException {
      this.deleteGlTexture();
      InputStream inputstream = Shaders.getShaderPackResourceStream(this.texturePath);
      if(inputstream == null) {
         throw new FileNotFoundException("Shader texture not found: " + this.texturePath);
      } else {
         try {
            BufferedImage bufferedimage = TextureUtil.readBufferedImage(inputstream);
            TextureMetadataSection texturemetadatasection = loadTextureMetadataSection(this.texturePath, new TextureMetadataSection(false, false, new ArrayList()));
            TextureUtil.uploadTextureImageAllocate(this.getGlTextureId(), bufferedimage, texturemetadatasection.getTextureBlur(), texturemetadatasection.getTextureClamp());
         } finally {
            IOUtils.closeQuietly(inputstream);
         }
      }
   }

   public static TextureMetadataSection loadTextureMetadataSection(String texturePath, TextureMetadataSection def) {
      String s = texturePath + ".mcmeta";
      String s1 = "texture";
      InputStream inputstream = Shaders.getShaderPackResourceStream(s);
      if(inputstream != null) {
         IMetadataSerializer imetadataserializer = METADATA_SERIALIZER;
         BufferedReader bufferedreader = new BufferedReader(new InputStreamReader(inputstream));

         TextureMetadataSection texturemetadatasection1;
         try {
            JsonObject jsonobject = (new JsonParser()).parse(bufferedreader).getAsJsonObject();
            TextureMetadataSection texturemetadatasection = imetadataserializer.parseMetadataSection(s1, jsonobject);
            if(texturemetadatasection == null) {
               return def;
            }

            texturemetadatasection1 = texturemetadatasection;
         } catch (RuntimeException runtimeexception) {
            SMCLog.warning("Error reading metadata: " + s);
            SMCLog.warning("" + runtimeexception.getClass().getName() + ": " + runtimeexception.getMessage());
            return def;
         } finally {
            IOUtils.closeQuietly(bufferedreader);
            IOUtils.closeQuietly(inputstream);
         }

         return texturemetadatasection1;
      } else {
         return def;
      }
   }

   private static IMetadataSerializer makeMetadataSerializer() {
      IMetadataSerializer imetadataserializer = new IMetadataSerializer();
      imetadataserializer.registerMetadataSectionType(new TextureMetadataSectionSerializer(), TextureMetadataSection.class);
      imetadataserializer.registerMetadataSectionType(new FontMetadataSectionSerializer(), FontMetadataSection.class);
      imetadataserializer.registerMetadataSectionType(new AnimationMetadataSectionSerializer(), AnimationMetadataSection.class);
      imetadataserializer.registerMetadataSectionType(new PackMetadataSectionSerializer(), PackMetadataSection.class);
      imetadataserializer.registerMetadataSectionType(new LanguageMetadataSectionSerializer(), LanguageMetadataSection.class);
      return imetadataserializer;
   }
}
