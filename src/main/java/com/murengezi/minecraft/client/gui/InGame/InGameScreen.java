package com.murengezi.minecraft.client.gui.InGame;

import com.darkmagician6.eventapi.EventManager;
import com.murengezi.feather.Event.RenderCrosshairEvent;
import com.murengezi.feather.Event.RenderOverlayEvent;
import com.murengezi.feather.Event.RenderScoreboardEvent;
import com.murengezi.minecraft.client.gui.GUI;
import com.murengezi.minecraft.client.gui.ScaledResolution;
import com.murengezi.minecraft.potion.Potion;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.*;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.boss.BossStatus;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.src.Config;
import net.minecraft.util.*;
import net.minecraft.world.border.WorldBorder;
import net.optifine.CustomColors;

import java.util.Random;

/**
 * @author Tobias Sjöblom
 * Created on 2021-01-20 at 16:58
 */
public class InGameScreen extends GUI {

    private static final ResourceLocation vignetteTexPath = new ResourceLocation("textures/misc/vignette.png");
    private static final ResourceLocation widgetsTexPath = new ResourceLocation("textures/gui/widgets.png");
    private static final ResourceLocation pumpkinBlurTexPath = new ResourceLocation("textures/misc/pumpkinblur.png");
    private final Random rand = new Random();
    private final Minecraft mc;
    private final RenderItem itemRenderer;
    private final GuiNewChat persistantChatGUI;
    private int updateCounter;
    private String recordPlaying = "";
    private int recordPlayingUpFor;
    private boolean recordIsPlaying;
    public float prevVignetteBrightness = 1.0F;
    private int remainingHighlightTicks;
    private ItemStack highlightingItemStack;
    private final OverlayDebugScreen overlayDebug;
    private final GuiSpectator spectatorGui;
    private final GuiPlayerTabOverlay overlayPlayerList;
    private String displayedTitle = "", displayedSubTitle = "";
    private int titlesTimer;
    private int titleFadeIn;
    private int titleDisplayTime;
    private int titleFadeOut;
    private int playerHealth = 0;
    private long lastSystemTime = 0L, healthUpdateCounter = 0L;

    public InGameScreen(Minecraft mc) {
        this.mc = mc;
        this.itemRenderer = mc.getRenderItem();
        this.overlayDebug = new OverlayDebugScreen();
        this.spectatorGui = new GuiSpectator(mc);
        this.persistantChatGUI = new GuiNewChat(mc);
        this.overlayPlayerList = new GuiPlayerTabOverlay(mc, this);
        this.resetTitleTicks();
    }

    public void resetTitleTicks() {
        this.titleFadeIn = 10;
        this.titleDisplayTime = 70;
        this.titleFadeOut = 20;
    }

    public void renderGameOverlay(float partialTicks) {
        ScaledResolution resolution = new ScaledResolution();
        int width = resolution.getScaledWidth();
        int height = resolution.getScaledHeight();
        this.mc.entityRenderer.setupOverlayRendering();
        GlStateManager.enableBlend();

        if (Config.isVignetteEnabled()) {
            this.renderVignette(this.mc.thePlayer.getBrightness(partialTicks), resolution);
        } else {
            GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        }

        ItemStack itemStack = this.mc.thePlayer.inventory.armorItemInSlot(3);

        if (this.mc.gameSettings.thirdPersonView == 0 && itemStack != null && itemStack.getItem() == Item.getItemFromBlock(Blocks.pumpkin)) {
            GlStateManager.disableAlpha();
            getMc().getTextureManager().bindTexture(pumpkinBlurTexPath);
            drawModalRectWithCustomSizedTexture(0, 0, 0, 0, resolution.getScaledWidth(), resolution.getScaledHeight(), resolution.getScaledWidth(), resolution.getScaledHeight());
            GlStateManager.enableAlpha();
        }

        if (!this.mc.thePlayer.isPotionActive(Potion.confusion)) {
            float ticks = this.mc.thePlayer.prevTimeInPortal + (this.mc.thePlayer.timeInPortal - this.mc.thePlayer.prevTimeInPortal) * partialTicks;

            if (ticks > 0.0f) {
                this.renderPortal(ticks, resolution);
            }
        }

        if (this.mc.playerController.isSpectator()) {
            this.spectatorGui.renderTooltip(resolution, partialTicks);
        } else {
            this.renderTooltip(resolution, partialTicks);
        }

        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
        this.mc.getTextureManager().bindTexture(icons);
        GlStateManager.enableBlend();

        if (this.showCrosshair()) {
            GlStateManager.tryBlendFuncSeparate(775, 769, 1, 0);
            GlStateManager.enableAlpha();
            this.drawTexturedModalRect(width / 2 - 7, height / 2 - 7, 0, 0, 16, 16);
        }

        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        this.mc.mcProfiler.startSection("bossHealth");
        this.renderBossHealth(resolution);
        this.mc.mcProfiler.endSection();

        this.renderPlayerStats(resolution);

        GlStateManager.disableBlend();

        if (this.mc.thePlayer.getSleepTimer() > 0) {
            this.mc.mcProfiler.startSection("sleep");
            GlStateManager.disableDepth();
            GlStateManager.disableAlpha();
            int sleepTimer = this.mc.thePlayer.getSleepTimer();
            float alpha = (float)sleepTimer / 100.0f;

            if (alpha > 1.0f) {
                alpha = 1.0f - (float)(sleepTimer - 100) / 10;
            }

            int color = (int)(220.0f * alpha) << 24 | 1052704;
            drawRect(0, 0, width, height, color);
            GlStateManager.enableAlpha();
            GlStateManager.enableDepth();
            this.mc.mcProfiler.endSection();
        }

        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
        int expBarPosX = width / 2 - 91;

        if (this.mc.thePlayer.isRidingHorse()) {
            this.renderHorseJumpBar(resolution, expBarPosX);
        } else {
            this.renderExpBar(resolution, expBarPosX);
        }

        if (this.mc.gameSettings.heldItemTooltips && !this.mc.playerController.isSpectator()) {
            this.drawSelectedItemName(resolution);
        } else if (this.mc.thePlayer.isRidingHorse()) {
            this.spectatorGui.func_175263_a(resolution);
        }

        if (this.mc.gameSettings.showDebugInfo) {
            this.overlayDebug.renderDebugInfo(resolution);
        } else {
            //TODO RenderOverlayEvent
            RenderOverlayEvent renderOverlayEvent = new RenderOverlayEvent(partialTicks, resolution);
            EventManager.call(renderOverlayEvent);
        }

        if (this.recordPlayingUpFor > 0) {
            this.mc.mcProfiler.startSection("overlayMessage");
            float f2 = (float)this.recordPlayingUpFor - partialTicks;
            int l1 = (int)(f2 * 255.0f / 20.0f);

            if (l1 > 255) {
                l1 = 255;
            }

            if (l1 > 8) {
                GlStateManager.pushMatrix();
                GlStateManager.translate((float)(width / 2), (float)(height - 68), 0.0F);
                GlStateManager.enableBlend();
                GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
                int l = 16777215;

                if (this.recordIsPlaying) {
                    l = MathHelper.func_181758_c(f2 / 50.0F, 0.7F, 0.6F) & 16777215;
                }

                this.getFontRenderer().drawString(this.recordPlaying, -this.getFontRenderer().getStringWidth(this.recordPlaying) / 2, -4, l + (l1 << 24 & -16777216));
                GlStateManager.disableBlend();
                GlStateManager.popMatrix();
            }

            this.mc.mcProfiler.endSection();
        }
        
        if (this.titlesTimer > 0) {
            this.mc.mcProfiler.startSection("titleAndSubtitle");
            float f3 = (float)this.titlesTimer - partialTicks;
            int alpha = 255;

            if (this.titlesTimer > this.titleFadeOut + this.titleDisplayTime) {
                float f4 = (float)(this.titleFadeIn + this.titleDisplayTime + this.titleFadeOut) - f3;
                alpha = (int)(f4 * 255.0F / (float)this.titleFadeIn);
            }

            if (this.titlesTimer <= this.titleFadeOut) {
                alpha = (int)(f3 * 255.0F / (float)this.titleFadeOut);
            }

            alpha = MathHelper.clamp_int(alpha, 0, 255);

            if (alpha > 8) {
                GlStateManager.pushMatrix();
                GlStateManager.translate((float)(width / 2), (float)(height / 2), 0.0F);
                GlStateManager.enableBlend();
                GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
                GlStateManager.pushMatrix();
                GlStateManager.scale(4.0F, 4.0F, 4.0F);
                int color = alpha << 24 & -16777216;
                this.getFontRenderer().drawString(this.displayedTitle, (float)(-this.getFontRenderer().getStringWidth(this.displayedTitle) / 2), -10.0F, 16777215 | color, true);
                GlStateManager.popMatrix();
                GlStateManager.pushMatrix();
                GlStateManager.scale(2.0F, 2.0F, 2.0F);
                this.getFontRenderer().drawString(this.displayedSubTitle, (float)(-this.getFontRenderer().getStringWidth(this.displayedSubTitle) / 2), 5.0F, 16777215 | color, true);
                GlStateManager.popMatrix();
                GlStateManager.disableBlend();
                GlStateManager.popMatrix();
            }
            this.mc.mcProfiler.endSection();
        }

        Scoreboard scoreboard = this.mc.theWorld.getScoreboard();
        ScoreObjective scoreObjective = null;
        ScorePlayerTeam scorePlayerTeam = scoreboard.getPlayersTeam(this.mc.thePlayer.getCommandSenderName());

        if (scorePlayerTeam != null) {
            int slot = scorePlayerTeam.getChatFormat().getColorIndex();

            if (slot >= 0) {
                scoreObjective = scoreboard.getObjectiveInDisplaySlot(3 + slot);
            }
        }

        ScoreObjective scoreObjective1 = scoreObjective != null ? scoreObjective : scoreboard.getObjectiveInDisplaySlot(1);

        if (scoreObjective1 != null) {
            EventManager.call(new RenderScoreboardEvent(scoreObjective1, resolution));
        }

        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GlStateManager.disableAlpha();
        GlStateManager.pushMatrix();
        GlStateManager.translate(0.0f, height - 48, 0.0f);
        this.mc.mcProfiler.startSection("chat");
        this.persistantChatGUI.drawChat(this.updateCounter);
        this.mc.mcProfiler.endSection();
        GlStateManager.popMatrix();
        scoreObjective1 = scoreboard.getObjectiveInDisplaySlot(0);

        if (this.mc.gameSettings.keyBindPlayerList.isKeyDown() && (!this.mc.isIntegratedServerRunning() || this.mc.thePlayer.sendQueue.getPlayerInfoMap().size() > 0 || scoreObjective1 != null)) {
            this.overlayPlayerList.updatePlayerList(true);
            this.overlayPlayerList.renderPlayerlist(width, scoreboard, scoreObjective1);
        } else {
            this.overlayPlayerList.updatePlayerList(false);
        }

        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
        GlStateManager.disableLighting();
        GlStateManager.enableAlpha();
    }

    private void renderVignette(float brightness, ScaledResolution resolution) {
        brightness = 1.0f - brightness;
        brightness = MathHelper.clamp_float(brightness, 0.0f, 1.0f);
        WorldBorder worldBorder = this.mc.theWorld.getWorldBorder();
        float f = (float)worldBorder.getClosestDistance(this.mc.thePlayer);
        double d0 = Math.min(worldBorder.getResizeSpeed() * (double)worldBorder.getWarningTime() * 1000.0D, Math.abs(worldBorder.getTargetSize() - worldBorder.getDiameter()));
        double d1 = Math.max(worldBorder.getWarningDistance(), d0);

        if ((double)f < d1) {
            f = 1.0f - (float)((double)f / d1);
        } else {
            f = 0.0f;
        }

        this.prevVignetteBrightness = (float)((double)this.prevVignetteBrightness + (double)(brightness - this.prevVignetteBrightness) * 0.01d);
        GlStateManager.disableDepth();
        GlStateManager.depthMask(false);
        GlStateManager.tryBlendFuncSeparate(0, 769, 1, 0);

        if (f > 0.0f) {
            GlStateManager.color(0.0f, f, f, 1.0f);
        } else {
            GlStateManager.color(this.prevVignetteBrightness, this.prevVignetteBrightness, this.prevVignetteBrightness, 1.0f);
        }

        this.mc.getTextureManager().bindTexture(vignetteTexPath);
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldRenderer = tessellator.getWorldRenderer();
        worldRenderer.begin(7, DefaultVertexFormats.POSITION_TEX);
        worldRenderer.pos(0.0D, resolution.getScaledHeight(), -90.0D).tex(0.0D, 1.0D).func_181675_d();
        worldRenderer.pos(resolution.getScaledWidth(), resolution.getScaledHeight(), -90.0D).tex(1.0D, 1.0D).func_181675_d();
        worldRenderer.pos(resolution.getScaledWidth(), 0.0D, -90.0D).tex(1.0D, 0.0D).func_181675_d();
        worldRenderer.pos(0.0D, 0.0D, -90.0D).tex(0.0D, 0.0D).func_181675_d();
        tessellator.draw();
        GlStateManager.depthMask(true);
        GlStateManager.enableDepth();
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
    }


    private void renderPortal(float ticks, ScaledResolution resolution) {
        if (ticks < 1.0F) {
            ticks = ticks * ticks;
            ticks = ticks * ticks;
            ticks = ticks * 0.8F + 0.2F;
        }

        GlStateManager.disableAlpha();
        GlStateManager.disableDepth();
        GlStateManager.depthMask(false);
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GlStateManager.color(1.0F, 1.0F, 1.0F, ticks);
        this.mc.getTextureManager().bindTexture(TextureMap.locationBlocksTexture);
        TextureAtlasSprite textureatlassprite = this.mc.getBlockRendererDispatcher().getBlockModelShapes().getTexture(Blocks.portal.getDefaultState());
        float f = textureatlassprite.getMinU();
        float f1 = textureatlassprite.getMinV();
        float f2 = textureatlassprite.getMaxU();
        float f3 = textureatlassprite.getMaxV();
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldrenderer = tessellator.getWorldRenderer();
        worldrenderer.begin(7, DefaultVertexFormats.POSITION_TEX);
        worldrenderer.pos(0.0D, resolution.getScaledHeight(), -90.0D).tex(f, f3).func_181675_d();
        worldrenderer.pos(resolution.getScaledWidth(), resolution.getScaledHeight(), -90.0D).tex(f2, f3).func_181675_d();
        worldrenderer.pos(resolution.getScaledWidth(), 0.0D, -90.0D).tex(f2, f1).func_181675_d();
        worldrenderer.pos(0.0D, 0.0D, -90.0D).tex(f, f1).func_181675_d();
        tessellator.draw();
        GlStateManager.depthMask(true);
        GlStateManager.enableDepth();
        GlStateManager.enableAlpha();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
    }

    public void renderTooltip(ScaledResolution resolution, float partialTicks) {
        if (this.mc.getRenderViewEntity() instanceof EntityPlayer) {
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            this.mc.getTextureManager().bindTexture(widgetsTexPath);
            EntityPlayer entityplayer = (EntityPlayer)this.mc.getRenderViewEntity();
            int width = resolution.getScaledWidth() / 2;
            float prevZLevel = this.zLevel;
            this.zLevel = -90.0F;
            this.drawTexturedModalRect(width - 91, resolution.getScaledHeight() - 22, 0, 0, 182, 22);
            this.drawTexturedModalRect(width - 91 - 1 + entityplayer.inventory.currentItem * 20, resolution.getScaledHeight() - 22 - 1, 0, 22, 24, 22);
            this.zLevel = prevZLevel;
            GlStateManager.enableBlend();
            GlStateManager.enableRescaleNormal();
            GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
            RenderHelper.enableGUIStandardItemLighting();

            for (int index = 0; index < 9; ++index) {
                int x = resolution.getScaledWidth() / 2 - 90 + index * 20 + 2;
                int y = resolution.getScaledHeight() - 16 - 3;
                this.renderHotbarItem(index, x, y, partialTicks, entityplayer);
            }

            RenderHelper.disableStandardItemLighting();
            GlStateManager.disableRescaleNormal();
            GlStateManager.disableBlend();
        }
    }

    private void renderHotbarItem(int index, int x, int y, float partialTicks, EntityPlayer entityPlayer) {
        ItemStack itemstack = entityPlayer.inventory.mainInventory[index];

        if (itemstack != null) {
            float animationTick = (float)itemstack.animationsToGo - partialTicks;

            if (animationTick > 0.0F) {
                GlStateManager.pushMatrix();
                float f1 = 1.0F + animationTick / 5.0F;
                GlStateManager.translate((float)(x + 8), (float)(y + 12), 0.0F);
                GlStateManager.scale(1.0F / f1, (f1 + 1.0F) / 2.0F, 1.0F);
                GlStateManager.translate((float)(-(x + 8)), (float)(-(y + 12)), 0.0F);
            }

            this.itemRenderer.renderItemAndEffectIntoGUI(itemstack, x, y);

            if (animationTick > 0.0F) {
                GlStateManager.popMatrix();
            }

            this.itemRenderer.renderItemOverlays(this.mc.fontRendererObj, itemstack, x, y);
        }
    }

    protected boolean showCrosshair() {
        RenderCrosshairEvent renderCrosshairEvent = new RenderCrosshairEvent();
        EventManager.call(renderCrosshairEvent);

        if (this.mc.gameSettings.showDebugInfo && !this.mc.thePlayer.hasReducedDebug() && !this.mc.gameSettings.reducedDebugInfo && !renderCrosshairEvent.isCancelled()) {
            return false;
        } else if (this.mc.playerController.isSpectator()) {
            if (this.mc.pointedEntity != null) {
                return true;
            } else {
                if (this.mc.objectMouseOver != null && this.mc.objectMouseOver.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) {
                    BlockPos blockpos = this.mc.objectMouseOver.getBlockPos();

                    return this.mc.theWorld.getTileEntity(blockpos) instanceof IInventory;
                }
                return false;
            }
        } else {
            return true;
        }
    }

    private void renderBossHealth(ScaledResolution resolution) {
        if (BossStatus.bossName != null && BossStatus.statusBarTime > 0) {
            --BossStatus.statusBarTime;
            int width = resolution.getScaledWidth();
            int textureWidth = 182;
            int x = width / 2 - textureWidth / 2;
            int y = 12;
            int bossHealth = (int)(BossStatus.healthScale * (float)(textureWidth + 1));
            this.drawTexturedModalRect(x, y, 0, 74, textureWidth, 5);
            this.drawTexturedModalRect(x, y, 0, 74, textureWidth, 5);

            if (bossHealth > 0) {
                this.drawTexturedModalRect(x, y, 0, 79, bossHealth, 5);
            }

            String bossName = BossStatus.bossName;
            this.getFontRenderer().drawStringWithShadow(bossName, (float)(width / 2 - this.getFontRenderer().getStringWidth(bossName) / 2), (float)(y - 10), 16777215);
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            this.mc.getTextureManager().bindTexture(icons);
        }
    }

    public FontRenderer getFontRenderer() {
        return this.mc.fontRendererObj;
    }

    private void renderPlayerStats(ScaledResolution resolution) {
        if (this.mc.getRenderViewEntity() instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer)this.mc.getRenderViewEntity();
            int health = MathHelper.ceiling_float_int(player.getHealth());
            boolean highlight = this.healthUpdateCounter > (long)this.updateCounter && (this.healthUpdateCounter - (long)this.updateCounter) / 3L % 2L == 1L;

            if (health < this.playerHealth && player.hurtResistantTime > 0) {
                this.lastSystemTime = Minecraft.getSystemTime();
                this.healthUpdateCounter = this.updateCounter + 20;
            } else if (health > this.playerHealth && player.hurtResistantTime > 0) {
                this.lastSystemTime = Minecraft.getSystemTime();
                this.healthUpdateCounter = this.updateCounter + 10;
            }

            if (Minecraft.getSystemTime() - this.lastSystemTime > 1000L) {
                this.playerHealth = health;
                this.lastSystemTime = Minecraft.getSystemTime();
            }

            this.playerHealth = health;
            this.rand.setSeed(this.updateCounter * 312871L);
            FoodStats foodstats = player.getFoodStats();
            int k = foodstats.getFoodLevel();
            IAttributeInstance attributeMaxHealth = player.getEntityAttribute(SharedMonsterAttributes.maxHealth);
            int left = resolution.getScaledWidth() / 2 - 91;
            int foodAndAirX = resolution.getScaledWidth() / 2 + 91;
            int top = resolution.getScaledHeight() - 39;
            float attributeValue = (float)attributeMaxHealth.getAttributeValue();
            float absorptionAmount = player.getAbsorptionAmount();
            int healthRows = MathHelper.ceiling_float_int((attributeValue + absorptionAmount) / 2.0F / 10.0F);
            int rowHeight = Math.max(10 - (healthRows - 2), 3);
            int j2 = top - (healthRows - 1) * (this.mc.playerController.shouldDrawHUD() ? rowHeight : 1) - (this.mc.playerController.shouldDrawHUD() ? 10 : 0);
            float f2 = absorptionAmount;
            int armorValue = player.getTotalArmorValue();
            int l2 = -1;

            if (player.isPotionActive(Potion.regeneration)) {
                l2 = this.updateCounter % MathHelper.ceiling_float_int(attributeValue + 5.0F);
            }

            this.mc.mcProfiler.startSection("armor");

            for (int index = 0; index < 10; ++index) {
                if (armorValue > 0) {
                    int j3 = left + index * 8;

                    if (index * 2 + 1 < armorValue) {
                        this.drawTexturedModalRect(j3, j2, 34, 9, 9, 9);
                    }

                    if (index * 2 + 1 == armorValue) {
                        this.drawTexturedModalRect(j3, j2, 25, 9, 9, 9);
                    }

                    if (index * 2 + 1 > armorValue) {
                        this.drawTexturedModalRect(j3, j2, 16, 9, 9, 9);
                    }
                }
            }


            if (this.mc.playerController.shouldDrawHUD()) {
                this.mc.mcProfiler.endStartSection("health");

                for (int i6 = MathHelper.ceiling_float_int((attributeValue + absorptionAmount) / 2.0F) - 1; i6 >= 0; --i6) {
                    int j6 = 16;

                    if (player.isPotionActive(Potion.poison)) {
                        j6 += 36;
                    } else if (player.isPotionActive(Potion.wither)) {
                        j6 += 72;
                    }

                    int k3 = 0;

                    if (highlight) {
                        k3 = 1;
                    }

                    int l3 = MathHelper.ceiling_float_int((float)(i6 + 1) / 10.0F) - 1;
                    int i4 = left + i6 % 10 * 8;
                    int j4 = top - l3 * rowHeight;

                    if (health <= 4) {
                        j4 += this.rand.nextInt(2);
                    }

                    if (i6 == l2) {
                        j4 -= 2;
                    }

                    int k4 = 0;

                    if (player.worldObj.getWorldInfo().isHardcoreModeEnabled()) {
                        k4 = 5;
                    }

                    this.drawTexturedModalRect(i4, j4, 16 + k3 * 9, 9 * k4, 9, 9);

                    if (f2 <= 0.0F) {
                        if (i6 * 2 + 1 < health) {
                            this.drawTexturedModalRect(i4, j4, j6 + 36, 9 * k4, 9, 9);
                        }

                        if (i6 * 2 + 1 == health) {
                            this.drawTexturedModalRect(i4, j4, j6 + 45, 9 * k4, 9, 9);
                        }
                    } else {
                        if (f2 == absorptionAmount && absorptionAmount % 2.0F == 1.0F) {
                            this.drawTexturedModalRect(i4, j4, j6 + 153, 9 * k4, 9, 9);
                        } else {
                            this.drawTexturedModalRect(i4, j4, j6 + 144, 9 * k4, 9, 9);
                        }

                        f2 -= 2.0F;
                    }
                }

                Entity entity = player.ridingEntity;

                if (entity == null) {
                    this.mc.mcProfiler.endStartSection("food");

                    for (int k6 = 0; k6 < 10; ++k6) {
                        int j7 = top;
                        int l7 = 16;
                        int k8 = 0;

                        if (player.isPotionActive(Potion.hunger)) {
                            l7 += 36;
                            k8 = 13;
                        }

                        if (player.getFoodStats().getSaturationLevel() <= 0.0F && this.updateCounter % (k * 3 + 1) == 0) {
                            j7 = top + (this.rand.nextInt(3) - 1);
                        }

                        int j9 = foodAndAirX - k6 * 8 - 9;
                        this.drawTexturedModalRect(j9, j7, 16 + k8 * 9, 27, 9, 9);

                        if (k6 * 2 + 1 < k) {
                            this.drawTexturedModalRect(j9, j7, l7 + 36, 27, 9, 9);
                        }

                        if (k6 * 2 + 1 == k) {
                            this.drawTexturedModalRect(j9, j7, l7 + 45, 27, 9, 9);
                        }
                    }
                } else if (entity instanceof EntityLivingBase) {
                    this.mc.mcProfiler.endStartSection("mountHealth");
                    EntityLivingBase entitylivingbase = (EntityLivingBase)entity;
                    int i7 = (int)Math.ceil(entitylivingbase.getHealth());
                    float f3 = entitylivingbase.getMaxHealth();
                    int j8 = (int)(f3 + 0.5F) / 2;

                    if (j8 > 30) {
                        j8 = 30;
                    }

                    int i9 = top;

                    for (int k9 = 0; j8 > 0; k9 += 20) {
                        int l4 = Math.min(j8, 10);
                        j8 -= l4;

                        for (int i5 = 0; i5 < l4; ++i5) {
                            int j5 = 52;

                            int l5 = foodAndAirX - i5 * 8 - 9;
                            this.drawTexturedModalRect(l5, i9, j5, 9, 9, 9);

                            if (i5 * 2 + 1 + k9 < i7) {
                                this.drawTexturedModalRect(l5, i9, j5 + 36, 9, 9, 9);
                            }

                            if (i5 * 2 + 1 + k9 == i7) {
                                this.drawTexturedModalRect(l5, i9, j5 + 45, 9, 9, 9);
                            }
                        }

                        i9 -= 10;
                    }
                }

                this.mc.mcProfiler.endStartSection("air");

                if (player.isInsideOfMaterial(Material.water)) {
                    int l6 = this.mc.thePlayer.getAir();
                    int k7 = MathHelper.ceiling_double_int((double)(l6 - 2) * 10.0D / 300.0D);
                    int i8 = MathHelper.ceiling_double_int((double)l6 * 10.0D / 300.0D) - k7;

                    for (int l8 = 0; l8 < k7 + i8; ++l8) {
                        if (l8 < k7) {
                            this.drawTexturedModalRect(foodAndAirX - l8 * 8 - 9, j2, 16, 18, 9, 9);
                        } else {
                            this.drawTexturedModalRect(foodAndAirX - l8 * 8 - 9, j2, 25, 18, 9, 9);
                        }
                    }
                }

                this.mc.mcProfiler.endSection();
            }
        }
    }

    public void renderHorseJumpBar(ScaledResolution resolution, int x) {
        this.mc.mcProfiler.startSection("jumpBar");
        this.mc.getTextureManager().bindTexture(GUI.icons);
        float horseJumpPower = this.mc.thePlayer.getHorseJumpPower();
        int width = 182;
        int jumpProcess = (int)(horseJumpPower * (float)(width + 1));
        int k = resolution.getScaledHeight() - 32 + 3;
        this.drawTexturedModalRect(x, k, 0, 84, width, 5);

        if (jumpProcess > 0) {
            this.drawTexturedModalRect(x, k, 0, 89, jumpProcess, 5);
        }

        this.mc.mcProfiler.endSection();
    }

    public void renderExpBar(ScaledResolution resolution, int x) {
        this.mc.mcProfiler.startSection("expBar");
        this.mc.getTextureManager().bindTexture(GUI.icons);
        int xpBarCap = this.mc.thePlayer.xpBarCap();

        if (xpBarCap > 0) {
            int width = 182;
            int expPercent = (int)(this.mc.thePlayer.experience * (float)(width + 1));
            int l = resolution.getScaledHeight() - 32 + 3;
            this.drawTexturedModalRect(x, l, 0, 64, width, 5);

            if (expPercent > 0)
            {
                this.drawTexturedModalRect(x, l, 0, 69, expPercent, 5);
            }
        }

        this.mc.mcProfiler.endSection();

        if (this.mc.thePlayer.experienceLevel > 0) {
            this.mc.mcProfiler.startSection("expLevel");
            int color = 8453920;

            if (Config.isCustomColors()) {
                color = CustomColors.getExpBarTextColor(color);
            }

            String expString = String.valueOf(this.mc.thePlayer.experienceLevel);
            int l1 = (resolution.getScaledWidth() - this.getFontRenderer().getStringWidth(expString)) / 2;
            int y = resolution.getScaledHeight() - 31 - 4;
            this.getFontRenderer().drawString(expString, l1 + 1, y, 0);
            this.getFontRenderer().drawString(expString, l1 - 1, y, 0);
            this.getFontRenderer().drawString(expString, l1, y + 1, 0);
            this.getFontRenderer().drawString(expString, l1, y - 1, 0);
            this.getFontRenderer().drawString(expString, l1, y, color);
            this.mc.mcProfiler.endSection();
        }
    }

    public void drawSelectedItemName(ScaledResolution resolution) {
        this.mc.mcProfiler.startSection("selectedItemName");

        if (this.remainingHighlightTicks > 0 && this.highlightingItemStack != null) {
            String displayName = this.highlightingItemStack.getDisplayName();

            if (this.highlightingItemStack.hasDisplayName()) {
                displayName = EnumChatFormatting.ITALIC + displayName;
            }

            int x = (resolution.getScaledWidth() - this.getFontRenderer().getStringWidth(displayName)) / 2;
            int y = resolution.getScaledHeight() - 59;

            if (!this.mc.playerController.shouldDrawHUD()) {
                y += 10;
            } else {
                if (this.mc.thePlayer.getAbsorptionAmount() > 0) {
                    y -= 10;
                }
            }


            int k = (int)((float)this.remainingHighlightTicks * 256.0F / 10.0F);

            if (k > 255) {
                k = 255;
            }

            if (k > 0) {
                GlStateManager.pushMatrix();
                GlStateManager.enableBlend();
                GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
                this.getFontRenderer().drawStringWithShadow(displayName, (float)x, (float)y, 16777215 + (k << 24));
                GlStateManager.disableBlend();
                GlStateManager.popMatrix();
            }
        }

        this.mc.mcProfiler.endSection();
    }

    public void updateTick() {
        if (this.recordPlayingUpFor > 0) {
            --this.recordPlayingUpFor;
        }

        if (this.titlesTimer > 0) {
            --this.titlesTimer;

            if (this.titlesTimer <= 0) {
                this.displayedTitle = "";
                this.displayedSubTitle = "";
            }
        }

        ++this.updateCounter;

        if (this.mc.thePlayer != null) {
            ItemStack itemstack = this.mc.thePlayer.inventory.getCurrentItem();

            if (itemstack == null) {
                this.remainingHighlightTicks = 0;
            } else if (this.highlightingItemStack != null && itemstack.getItem() == this.highlightingItemStack.getItem() && ItemStack.areItemStackTagsEqual(itemstack, this.highlightingItemStack) && (itemstack.isItemStackDamageable() || itemstack.getMetadata() == this.highlightingItemStack.getMetadata())) {
                if (this.remainingHighlightTicks > 0) {
                    --this.remainingHighlightTicks;
                }
            } else {
                this.remainingHighlightTicks = 40;
            }

            this.highlightingItemStack = itemstack;
        }
    }

    public int getUpdateCounter() {
        return updateCounter;
    }

    public GuiNewChat getChatGUI() {
        return persistantChatGUI;
    }

    public GuiSpectator getSpectatorGui() {
        return spectatorGui;
    }

    public void playerListResetHeaderFooter()
    {
        this.overlayPlayerList.resetHeaderFooter();
    }

    public void setRecordPlayingMessage(String message) {
        this.setRecordPlaying(I18n.format("record.nowPlaying", message), true);
    }

    public void setRecordPlaying(String record, boolean playing) {
        this.recordPlaying = record;
        this.recordPlayingUpFor = 60;
        this.recordIsPlaying = playing;
    }

    public void displayTitle(String title, String subTitle, int fadeIn, int displayTime, int fadeOut) {
        if (title == null && subTitle == null && fadeIn < 0 && displayTime < 0 && fadeOut < 0) {
            this.displayedTitle = "";
            this.displayedSubTitle = "";
            this.titlesTimer = 0;
        } else if (title != null) {
            this.displayedTitle = title;
            this.titlesTimer = this.titleFadeIn + this.titleDisplayTime + this.titleFadeOut;
        } else if (subTitle != null) {
            this.displayedSubTitle = subTitle;
        } else {
            if (fadeIn >= 0) {
                this.titleFadeIn = fadeIn;
            }

            if (displayTime >= 0) {
                this.titleDisplayTime = displayTime;
            }

            if (fadeOut >= 0) {
                this.titleFadeOut = fadeOut;
            }

            if (this.titlesTimer > 0) {
                this.titlesTimer = this.titleFadeIn + this.titleDisplayTime + this.titleFadeOut;
            }
        }
    }

    public GuiPlayerTabOverlay getTabList() {
        return overlayPlayerList;
    }
}
