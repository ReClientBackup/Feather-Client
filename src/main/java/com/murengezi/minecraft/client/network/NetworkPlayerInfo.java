package com.murengezi.minecraft.client.network;

import com.google.common.base.Objects;
import com.mojang.authlib.GameProfile;
import com.murengezi.minecraft.client.Minecraft;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.network.play.server.S38PacketPlayerListItem;
import com.murengezi.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.WorldSettings;

public class NetworkPlayerInfo {

    private final GameProfile gameProfile;
    private WorldSettings.GameType gameType;
    private int responseTime;
    private boolean playerTexturesLoaded = false;
    private ResourceLocation locationSkin, locationCape;
    private String skinType;

    private IChatComponent displayName;
    private int lastHealth = 0, displayHealth = 0;
    private long lastHealthTime = 0L, healthBlinkTime = 0L, renderVisibilityId = 0L;

    public NetworkPlayerInfo(S38PacketPlayerListItem.AddPlayerData entry) {
        this.gameProfile = entry.getProfile();
        this.gameType = entry.getGameMode();
        this.responseTime = entry.getPing();
        this.displayName = entry.getDisplayName();
    }

    public GameProfile getGameProfile() {
        return this.gameProfile;
    }

    public WorldSettings.GameType getGameType() {
        return this.gameType;
    }

    public int getResponseTime() {
        return this.responseTime;
    }

    protected void setGameType(WorldSettings.GameType gameMode) {
        this.gameType = gameMode;
    }

    protected void setResponseTime(int latency) {
        this.responseTime = latency;
    }

    public boolean hasLocationSkin() {
        return this.locationSkin != null;
    }

    public String getSkinType() {
        return this.skinType == null ? DefaultPlayerSkin.getSkinType(this.gameProfile.getId()) : this.skinType;
    }

    public ResourceLocation getLocationSkin() {
        if (this.locationSkin == null) {
            this.loadPlayerTextures();
        }

        return Objects.firstNonNull(this.locationSkin, DefaultPlayerSkin.getDefaultSkin(this.gameProfile.getId()));
    }

    public ResourceLocation getLocationCape() {
        if (this.locationCape == null) {
            this.loadPlayerTextures();
        }

        return this.locationCape;
    }

    public ScorePlayerTeam getPlayerTeam() {
        return Minecraft.getMinecraft().world.getScoreboard().getPlayersTeam(this.getGameProfile().getName());
    }

    protected void loadPlayerTextures() {
        synchronized (this) {
            if (!this.playerTexturesLoaded) {
                this.playerTexturesLoaded = true;
                Minecraft.getMinecraft().getSkinManager().loadProfileTextures(this.gameProfile, (type, location, profileTexture) -> {
                    switch (type) {
                        case SKIN:
                            NetworkPlayerInfo.this.locationSkin = location;
                            NetworkPlayerInfo.this.skinType = profileTexture.getMetadata("model");

                            if (NetworkPlayerInfo.this.skinType == null) {
                                NetworkPlayerInfo.this.skinType = "default";
                            }
                            break;
                        case CAPE:
                            NetworkPlayerInfo.this.locationCape = location;
                    }
                }, true);
            }
        }
    }

    public void setDisplayName(IChatComponent displayNameIn) {
        this.displayName = displayNameIn;
    }

    public IChatComponent getDisplayName() {
        return this.displayName;
    }

    public int getLastHealth() {
        return this.lastHealth;
    }

    public void setLastHealth(int lastHealth) {
        this.lastHealth = lastHealth;
    }

    public int getDisplayHealth() {
        return this.displayHealth;
    }

    public void setDisplayHealth(int displayHealth) {
        this.displayHealth = displayHealth;
    }

    public long getLastHealthTime() {
        return this.lastHealthTime;
    }

    public void setLastHealthTime(long lastHealthTime) {
        this.lastHealthTime = lastHealthTime;
    }

    public long getHealthBlinkTime() {
        return this.healthBlinkTime;
    }

    public void setHealthBlinkTime(long healthBlinkTime) {
        this.healthBlinkTime = healthBlinkTime;
    }

    public long getRenderVisibilityId() {
        return this.renderVisibilityId;
    }

    public void setRenderVisibilityId(long renderVisibilityId) {
        this.renderVisibilityId = renderVisibilityId;
    }

}
