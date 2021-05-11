package net.minecraft.client.gui;

import com.google.common.collect.ComparisonChain;
import com.google.common.collect.Ordering;
import com.mojang.authlib.GameProfile;
import java.util.Comparator;
import java.util.List;

import com.murengezi.minecraft.client.gui.GUI;
import net.minecraft.client.Minecraft;
import com.murengezi.minecraft.client.network.NetHandlerPlayClient;
import com.murengezi.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EnumPlayerModelParts;
import com.murengezi.minecraft.scoreboard.IScoreObjectiveCriteria;
import com.murengezi.minecraft.scoreboard.ScoreObjective;
import com.murengezi.minecraft.scoreboard.ScorePlayerTeam;
import com.murengezi.minecraft.scoreboard.Scoreboard;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.MathHelper;
import net.minecraft.world.WorldSettings;

public class GuiPlayerTabOverlay extends GUI {
    private static final Ordering<NetworkPlayerInfo> field_175252_a = Ordering.from(new GuiPlayerTabOverlay.PlayerComparator());
    private IChatComponent footer;
    private IChatComponent header;

    /**
     * The last time the playerlist was opened (went from not being renderd, to being rendered)
     */
    private long lastTimeOpened;

    /** Weither or not the playerlist is currently being rendered */
    private boolean isBeingRendered;

    /**
     * Returns the name that should be renderd for the player supplied
     */
    public String getPlayerName(NetworkPlayerInfo networkPlayerInfoIn)
    {
        return networkPlayerInfoIn.getDisplayName() != null ? networkPlayerInfoIn.getDisplayName().getFormattedText() : ScorePlayerTeam.formatPlayerName(networkPlayerInfoIn.getPlayerTeam(), networkPlayerInfoIn.getGameProfile().getName());
    }

    /**
     * Called by GuiIngame to update the information stored in the playerlist, does not actually render the list,
     * however.
     *  
     * @param willBeRendered True if the playerlist is intended to be renderd subsequently.
     */
    public void updatePlayerList(boolean willBeRendered)
    {
        if (willBeRendered && !this.isBeingRendered)
        {
            this.lastTimeOpened = Minecraft.getSystemTime();
        }

        this.isBeingRendered = willBeRendered;
    }

    /**
     * Renders the playerlist, its background, headers and footers.
     */
    public void renderPlayerlist(int width, Scoreboard scoreboardIn, ScoreObjective scoreObjectiveIn)
    {
        NetHandlerPlayClient nethandlerplayclient = getPlayer().sendQueue;
        List<NetworkPlayerInfo> list = field_175252_a.sortedCopy(nethandlerplayclient.getPlayerInfoMap());
        int i = 0;
        int j = 0;

        for (NetworkPlayerInfo networkplayerinfo : list)
        {
            int k = getFr().getStringWidth(this.getPlayerName(networkplayerinfo));
            i = Math.max(i, k);

            if (scoreObjectiveIn != null && scoreObjectiveIn.getRenderType() != IScoreObjectiveCriteria.EnumRenderType.HEARTS)
            {
                k = getFr().getStringWidth(" " + scoreboardIn.getValueFromObjective(networkplayerinfo.getGameProfile().getName(), scoreObjectiveIn).getScorePoints());
                j = Math.max(j, k);
            }
        }

        list = list.subList(0, Math.min(list.size(), 80));
        int l3 = list.size();
        int i4 = l3;
        int j4;

        for (j4 = 1; i4 > 20; i4 = (l3 + j4 - 1) / j4)
        {
            ++j4;
        }

        boolean flag = getMc().isIntegratedServerRunning() || getMc().getNetHandler().getNetworkManager().getIsencrypted();
        int l;

        if (scoreObjectiveIn != null)
        {
            if (scoreObjectiveIn.getRenderType() == IScoreObjectiveCriteria.EnumRenderType.HEARTS)
            {
                l = 90;
            }
            else
            {
                l = j;
            }
        }
        else
        {
            l = 0;
        }

        int i1 = Math.min(j4 * ((flag ? 9 : 0) + i + l + 13), width - 50) / j4;
        int j1 = width / 2 - (i1 * j4 + (j4 - 1) * 5) / 2;
        int k1 = 10;
        int l1 = i1 * j4 + (j4 - 1) * 5;
        List<String> list1 = null;
        List<String> list2 = null;

        if (this.header != null)
        {
            list1 = getFr().listFormattedStringToWidth(this.header.getFormattedText(), width - 50);

            for (String s : list1)
            {
                l1 = Math.max(l1, getFr().getStringWidth(s));
            }
        }

        if (this.footer != null)
        {
            list2 = getFr().listFormattedStringToWidth(this.footer.getFormattedText(), width - 50);

            for (String s2 : list2)
            {
                l1 = Math.max(l1, getFr().getStringWidth(s2));
            }
        }

        if (list1 != null)
        {
            drawRect(width / 2 - l1 / 2 - 1, k1 - 1, width / 2 + l1 / 2 + 1, k1 + list1.size() * getFr().FONT_HEIGHT, Integer.MIN_VALUE);

            for (String s3 : list1)
            {
                int i2 = getFr().getStringWidth(s3);
                getFr().drawStringWithShadow(s3, (float)(width / 2 - i2 / 2), (float)k1, -1);
                k1 += getFr().FONT_HEIGHT;
            }

            ++k1;
        }

        drawRect(width / 2 - l1 / 2 - 1, k1 - 1, width / 2 + l1 / 2 + 1, k1 + i4 * 9, Integer.MIN_VALUE);

        for (int k4 = 0; k4 < l3; ++k4)
        {
            int l4 = k4 / i4;
            int i5 = k4 % i4;
            int j2 = j1 + l4 * i1 + l4 * 5;
            int k2 = k1 + i5 * 9;
            drawRect(j2, k2, j2 + i1, k2 + 8, 553648127);
            GlStateManager.colorAllMax();
            GlStateManager.enableAlpha();
            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);

            if (k4 < list.size()) {
                NetworkPlayerInfo playerInfo = list.get(k4);
                String s1 = this.getPlayerName(playerInfo);
                GameProfile gameprofile = playerInfo.getGameProfile();

                if (flag) {
                    EntityPlayer entityplayer = getWorld().getPlayerEntityByUUID(gameprofile.getId());
                    boolean flag1 = entityplayer != null && entityplayer.isWearing(EnumPlayerModelParts.CAPE) && (gameprofile.getName().equals("Dinnerbone") || gameprofile.getName().equals("Grumm"));
                    getMc().getTextureManager().bindTexture(playerInfo.getLocationSkin());
                    int l2 = 8 + (flag1 ? 8 : 0);
                    int i3 = 8 * (flag1 ? -1 : 1);
                    GUI.drawScaledCustomSizeModalRect(j2, k2, 8.0F, (float)l2, 8, i3, 8, 8, 64.0F, 64.0F);

                    if (entityplayer != null && entityplayer.isWearing(EnumPlayerModelParts.HAT)) {
                        int j3 = 8 + (flag1 ? 8 : 0);
                        int k3 = 8 * (flag1 ? -1 : 1);
                        GUI.drawScaledCustomSizeModalRect(j2, k2, 40.0F, (float)j3, 8, k3, 8, 8, 64.0F, 64.0F);
                    }

                    j2 += 9;
                }

                if (playerInfo.getGameType() == WorldSettings.GameType.SPECTATOR) {
                    s1 = EnumChatFormatting.ITALIC + s1;
                    getFr().drawStringWithShadow(s1, (float)j2, (float)k2, -1862270977);
                } else {
                    getFr().drawStringWithShadow(s1, (float)j2, (float)k2, -1);
                }

                if (scoreObjectiveIn != null && playerInfo.getGameType() != WorldSettings.GameType.SPECTATOR) {
                    int k5 = j2 + i + 1;
                    int l5 = k5 + l;

                    if (l5 - k5 > 5) {
                        this.drawScoreboardValues(scoreObjectiveIn, k2, gameprofile.getName(), k5, l5, playerInfo);
                    }
                }

                this.drawPing(i1, j2 - (flag ? 9 : 0), k2, playerInfo);
            }
        }

        if (list2 != null) {
            k1 = k1 + i4 * 9 + 1;
            drawRect(width / 2 - l1 / 2 - 1, k1 - 1, width / 2 + l1 / 2 + 1, k1 + list2.size() * getFr().FONT_HEIGHT, Integer.MIN_VALUE);

            for (String s4 : list2) {
                int j5 = getFr().getStringWidth(s4);
                getFr().drawStringWithShadow(s4, (float)(width / 2 - j5 / 2), (float)k1, -1);
                k1 += getFr().FONT_HEIGHT;
            }
        }
    }

    protected void drawPing(int p_175245_1_, int p_175245_2_, int p_175245_3_, NetworkPlayerInfo playerInfo) {
        GlStateManager.colorAllMax();
        getMc().getTextureManager().bindTexture(icons);
        int i = 0;
        int j;

        if (playerInfo.getResponseTime() < 0) {
            j = 5;
        } else if (playerInfo.getResponseTime() < 150) {
            j = 0;
        } else if (playerInfo.getResponseTime() < 300) {
            j = 1;
        } else if (playerInfo.getResponseTime() < 600) {
            j = 2;
        } else if (playerInfo.getResponseTime() < 1000) {
            j = 3;
        } else {
            j = 4;
        }

        this.zLevel += 100.0F;
        this.drawTexturedModalRect(p_175245_2_ + p_175245_1_ - 11, p_175245_3_, 0 + i * 10, 176 + j * 8, 10, 8);
        this.zLevel -= 100.0F;
    }

    private void drawScoreboardValues(ScoreObjective objective, int p_175247_2_, String p_175247_3_, int p_175247_4_, int p_175247_5_, NetworkPlayerInfo playerInfo) {
        int i = objective.getScoreboard().getValueFromObjective(p_175247_3_, objective).getScorePoints();

        if (objective.getRenderType() == IScoreObjectiveCriteria.EnumRenderType.HEARTS) {
            getMc().getTextureManager().bindTexture(icons);

            if (this.lastTimeOpened == playerInfo.getRenderVisibilityId()) {
                if (i < playerInfo.getLastHealth()) {
                    playerInfo.setLastHealthTime(Minecraft.getSystemTime());
                    playerInfo.setHealthBlinkTime(getMc().inGameScreen.getUpdateCounter() + 20);
                } else if (i > playerInfo.getLastHealth()) {
                    playerInfo.setLastHealthTime(Minecraft.getSystemTime());
                    playerInfo.setHealthBlinkTime(getMc().inGameScreen.getUpdateCounter() + 10);
                }
            }

            if (Minecraft.getSystemTime() - playerInfo.getLastHealthTime() > 1000L || this.lastTimeOpened != playerInfo.getRenderVisibilityId()) {
                playerInfo.setLastHealth(i);
                playerInfo.setDisplayHealth(i);
                playerInfo.setLastHealthTime(Minecraft.getSystemTime());
            }

            playerInfo.setRenderVisibilityId(this.lastTimeOpened);
            playerInfo.setLastHealth(i);
            int j = MathHelper.ceiling_float_int((float)Math.max(i, playerInfo.getDisplayHealth()) / 2.0F);
            int k = Math.max(MathHelper.ceiling_float_int((float)(i / 2)), Math.max(MathHelper.ceiling_float_int((float)(playerInfo.getDisplayHealth() / 2)), 10));
            boolean flag = playerInfo.getHealthBlinkTime() > (long)getMc().inGameScreen.getUpdateCounter() && (playerInfo.getHealthBlinkTime() - (long)getMc().inGameScreen.getUpdateCounter()) / 3L % 2L == 1L;

            if (j > 0) {
                float f = Math.min((float)(p_175247_5_ - p_175247_4_ - 4) / (float)k, 9.0F);

                if (f > 3.0F) {
                    for (int l = j; l < k; ++l) {
                        this.drawTexturedModalRect((float)p_175247_4_ + (float)l * f, (float)p_175247_2_, flag ? 25 : 16, 0, 9, 9);
                    }

                    for (int j1 = 0; j1 < j; ++j1) {
                        this.drawTexturedModalRect((float)p_175247_4_ + (float)j1 * f, (float)p_175247_2_, flag ? 25 : 16, 0, 9, 9);

                        if (flag) {
                            if (j1 * 2 + 1 < playerInfo.getDisplayHealth()) {
                                this.drawTexturedModalRect((float)p_175247_4_ + (float)j1 * f, (float)p_175247_2_, 70, 0, 9, 9);
                            }

                            if (j1 * 2 + 1 == playerInfo.getDisplayHealth()) {
                                this.drawTexturedModalRect((float)p_175247_4_ + (float)j1 * f, (float)p_175247_2_, 79, 0, 9, 9);
                            }
                        }

                        if (j1 * 2 + 1 < i) {
                            this.drawTexturedModalRect((float)p_175247_4_ + (float)j1 * f, (float)p_175247_2_, j1 >= 10 ? 160 : 52, 0, 9, 9);
                        }

                        if (j1 * 2 + 1 == i) {
                            this.drawTexturedModalRect((float)p_175247_4_ + (float)j1 * f, (float)p_175247_2_, j1 >= 10 ? 169 : 61, 0, 9, 9);
                        }
                    }
                } else {
                    float f1 = MathHelper.clamp_float((float)i / 20.0F, 0.0F, 1.0F);
                    int i1 = (int)((1.0F - f1) * 255.0F) << 16 | (int)(f1 * 255.0F) << 8;
                    String s = "" + (float)i / 2.0F;

                    if (p_175247_5_ - getFr().getStringWidth(s + "hp") >= p_175247_4_) {
                        s = s + "hp";
                    }

                    getFr().drawStringWithShadow(s, (float)((p_175247_5_ + p_175247_4_) / 2 - getFr().getStringWidth(s) / 2), (float)p_175247_2_, i1);
                }
            }
        } else {
            String s1 = EnumChatFormatting.YELLOW + "" + i;
            getFr().drawStringWithShadow(s1, (float)(p_175247_5_ - getFr().getStringWidth(s1)), (float)p_175247_2_, 16777215);
        }
    }

    public void setFooter(IChatComponent footer) {
        this.footer = footer;
    }

    public void setHeader(IChatComponent header) {
        this.header = header;
    }

    public void resetHeaderFooter() {
        this.header = null;
        this.footer = null;
    }

    static class PlayerComparator implements Comparator<NetworkPlayerInfo> {

        private PlayerComparator() {}

        public int compare(NetworkPlayerInfo playerInfo1, NetworkPlayerInfo playerInfo2) {
            ScorePlayerTeam playerTeam1 = playerInfo1.getPlayerTeam();
            ScorePlayerTeam playerTeam2 = playerInfo2.getPlayerTeam();
            return ComparisonChain.start().compareTrueFirst(playerInfo1.getGameType() != WorldSettings.GameType.SPECTATOR, playerInfo2.getGameType() != WorldSettings.GameType.SPECTATOR).compare(playerTeam1 != null ? playerTeam1.getRegisteredName() : "", playerTeam2 != null ? playerTeam2.getRegisteredName() : "").compare(playerInfo1.getGameProfile().getName(), playerInfo2.getGameProfile().getName()).result();
        }
    }
}
