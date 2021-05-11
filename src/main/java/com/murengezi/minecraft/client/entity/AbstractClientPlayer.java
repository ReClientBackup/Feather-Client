package com.murengezi.minecraft.client.entity;

import com.mojang.authlib.GameProfile;
import com.murengezi.chocolate.Util.CapeUtils;
import com.murengezi.minecraft.client.Minecraft;
import com.murengezi.minecraft.client.network.NetworkPlayerInfo;
import com.murengezi.minecraft.client.renderer.ImageBufferDownload;
import com.murengezi.minecraft.client.renderer.ThreadDownloadImageData;
import com.murengezi.minecraft.client.renderer.texture.ITextureObject;
import com.murengezi.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.optifine.config.Config;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StringUtils;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraft.world.WorldSettings;
import net.optifine.player.PlayerConfigurations;
import net.optifine.reflect.Reflector;

public abstract class AbstractClientPlayer extends EntityPlayer {

    private NetworkPlayerInfo playerInfo;
    private ResourceLocation capeLocation;
    private long reloadCapeTimeMs;
    private String nameClear;

    public AbstractClientPlayer(World world, GameProfile playerProfile) {
        super(world, playerProfile);
        this.nameClear = playerProfile.getName();


        if (!StringUtils.isNullOrEmpty(this.nameClear)) {
            this.nameClear = StringUtils.stripControlCodes(this.nameClear);
        }

        CapeUtils.downloadCape(this);
        PlayerConfigurations.getPlayerConfiguration(this);
    }

    public boolean isSpectator() {
        NetworkPlayerInfo playerInfo = Minecraft.getMinecraft().getNetHandler().getPlayerInfo(this.getGameProfile().getId());
        return playerInfo != null && playerInfo.getGameType() == WorldSettings.GameType.SPECTATOR;
    }

    public boolean hasPlayerInfo() {
        return this.getPlayerInfo() != null;
    }

    protected NetworkPlayerInfo getPlayerInfo() {
        if (this.playerInfo == null) {
            this.playerInfo = Minecraft.getMinecraft().getNetHandler().getPlayerInfo(this.getUniqueID());
        }

        return this.playerInfo;
    }

    public boolean hasSkin() {
        NetworkPlayerInfo networkplayerinfo = this.getPlayerInfo();
        return networkplayerinfo != null && networkplayerinfo.hasLocationSkin();
    }

    public ResourceLocation getLocationSkin() {
        NetworkPlayerInfo networkplayerinfo = this.getPlayerInfo();
        return networkplayerinfo == null ? DefaultPlayerSkin.getDefaultSkin(this.getUniqueID()) : networkplayerinfo.getLocationSkin();
    }

    public ResourceLocation getLocationCape() {
        if (!Config.isShowCapes()) {
            return null;
        } else {
            if (this.getReloadCapeTimeMs() != 0L && System.currentTimeMillis() > this.getReloadCapeTimeMs()) {
                //CapeUtils.reloadCape(this);
                this.setReloadCapeTimeMs(0L);
            }

            if (this.capeLocation != null) {
                return this.capeLocation;
            } else {
                NetworkPlayerInfo networkplayerinfo = this.getPlayerInfo();
                return networkplayerinfo == null ? null : networkplayerinfo.getLocationCape();
            }
        }
    }

    //ThreadDownloadImageData
    public static void getDownloadImageSkin(ResourceLocation resourceLocationIn, String username) {
        TextureManager texturemanager = Minecraft.getMinecraft().getTextureManager();
        ITextureObject itextureobject = texturemanager.getTexture(resourceLocationIn);

        if (itextureobject == null) {
            itextureobject = new ThreadDownloadImageData(null, String.format("http://skins.minecraft.net/MinecraftSkins/%s.png", StringUtils.stripControlCodes(username)), DefaultPlayerSkin.getDefaultSkin(getOfflineUUID(username)), new ImageBufferDownload());
            texturemanager.loadTexture(resourceLocationIn, itextureobject);
        }

    }

    public static ResourceLocation getLocationSkin(String username) {
        return new ResourceLocation("skins/" + StringUtils.stripControlCodes(username));
    }

    public String getSkinType() {
        NetworkPlayerInfo networkplayerinfo = this.getPlayerInfo();
        return networkplayerinfo == null ? DefaultPlayerSkin.getSkinType(this.getUniqueID()) : networkplayerinfo.getSkinType();
    }

    public float getFovModifier() {
        float f = 1.0F;

        if (this.capabilities.isFlying) {
            f *= 1.1F;
        }

        IAttributeInstance iattributeinstance = this.getEntityAttribute(SharedMonsterAttributes.movementSpeed);
        f = (float)((double)f * ((iattributeinstance.getAttributeValue() / (double)this.capabilities.getWalkSpeed() + 1.0D) / 2.0D));

        if (this.capabilities.getWalkSpeed() == 0.0F || Float.isNaN(f) || Float.isInfinite(f)) {
            f = 1.0F;
        }

        if (this.isUsingItem() && this.getItemInUse().getItem() == Items.bow) {
            int i = this.getItemInUseDuration();
            float f1 = (float)i / 20.0F;

            if (f1 > 1.0F) {
                f1 = 1.0F;
            } else {
                f1 = f1 * f1;
            }

            f *= 1.0F - f1 * 0.15F;
        }

        return Reflector.ForgeHooksClient_getOffsetFOV.exists() ? Reflector.callFloat(Reflector.ForgeHooksClient_getOffsetFOV, this, f): f;
    }

    public String getNameClear() {
        return this.nameClear;
    }

    public void setCapeLocation(ResourceLocation location) {
        this.capeLocation = location;
    }

    public long getReloadCapeTimeMs() {
        return this.reloadCapeTimeMs;
    }

    public void setReloadCapeTimeMs(long ms) {
        this.reloadCapeTimeMs = ms;
    }

    public Vec3 getLook(float partialTicks) {
        return this.getVectorForRotation(this.rotationPitch, this.rotationYaw);
    }
}
