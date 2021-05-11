package net.minecraft.client.resources.model;

import java.util.List;
import com.murengezi.minecraft.client.renderer.block.model.BakedQuad;
import com.murengezi.minecraft.client.renderer.block.model.ItemCameraTransforms;
import com.murengezi.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.EnumFacing;

public class BuiltInModel implements IBakedModel
{
    private final ItemCameraTransforms cameraTransforms;

    public BuiltInModel(ItemCameraTransforms p_i46086_1_)
    {
        this.cameraTransforms = p_i46086_1_;
    }

    public List<BakedQuad> getFaceQuads(EnumFacing p_177551_1_)
    {
        return null;
    }

    public List<BakedQuad> getGeneralQuads()
    {
        return null;
    }

    public boolean isAmbientOcclusion()
    {
        return false;
    }

    public boolean isGui3d()
    {
        return true;
    }

    public boolean isBuiltInRenderer()
    {
        return true;
    }

    public TextureAtlasSprite getTexture()
    {
        return null;
    }

    public ItemCameraTransforms getItemCameraTransforms()
    {
        return this.cameraTransforms;
    }
}
