package com.murengezi.minecraft.client.gui.Options.ResourcePack;

import com.google.gson.JsonParseException;
import com.murengezi.chocolate.Chocolate;
import com.murengezi.minecraft.client.renderer.texture.DynamicTexture;
import com.murengezi.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.resources.IResourcePack;
import net.minecraft.client.resources.data.PackMetadataSection;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.ResourceLocation;

import java.io.IOException;

/**
 * @author Tobias Sjöblom
 * Created on 2021-01-24 at 15:30
 */
public class ResourcePackListEntryDefault extends ResourcePackListEntry {

	private final IResourcePack field_148320_d;
	private final ResourceLocation resourcePackIcon;

	public ResourcePackListEntryDefault(ResourcePacksScreen resourcePacksScreen) {
		super(resourcePacksScreen);

		this.field_148320_d = getMc().getResourcePackRepository().rprDefaultResourcePack;
		DynamicTexture dynamictexture;

		try {
			dynamictexture = new DynamicTexture(this.field_148320_d.getPackImage());
		} catch (IOException e) {
			dynamictexture = TextureUtil.missingTexture;
		}

		this.resourcePackIcon = getMc().getTextureManager().getDynamicTextureLocation("texturepackicon", dynamictexture);
	}


	@Override
	protected int getPackFormat() {
		return 1;
	}

	@Override
	protected String getDescription() {
		try {
			PackMetadataSection packmetadatasection = this.field_148320_d.getPackMetadata(getMc().getResourcePackRepository().rprMetadataSerializer, "pack");

			if (packmetadatasection != null) {
				return packmetadatasection.getPackDescription().getFormattedText();
			}
		} catch (JsonParseException | IOException exception) {
			Chocolate.getLogger().error("Couldn't load metadata info", exception);
		}

		return EnumChatFormatting.RED + "Missing " + "pack.mcmeta" + " :(";
	}






	protected boolean func_148309_e()
	{
		return false;
	}

	protected boolean func_148308_f()
	{
		return false;
	}

	protected boolean func_148314_g()
	{
		return false;
	}

	protected boolean func_148307_h()
	{
		return false;
	}

	@Override
	protected String getName() {
		return "Default";
	}

	@Override
	protected void loadIcon() {
		getMc().getTextureManager().bindTexture(this.resourcePackIcon);
	}

	protected boolean func_148310_d()
	{
		return false;
	}
}
