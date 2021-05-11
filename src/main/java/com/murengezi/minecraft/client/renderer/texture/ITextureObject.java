package com.murengezi.minecraft.client.renderer.texture;

import java.io.IOException;
import com.murengezi.minecraft.client.resources.IResourceManager;
import net.optifine.shaders.MultiTexID;

public interface ITextureObject {

    void setBlurMipmap(boolean p_174936_1_, boolean p_174936_2_);

    void restoreLastBlurMipmap();

    void loadTexture(IResourceManager resourceManager) throws IOException;

    int getGlTextureId();

    MultiTexID getMultiTexID();

}
