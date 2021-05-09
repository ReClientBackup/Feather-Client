package net.minecraft.client.gui;

import com.google.common.collect.Lists;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Random;

import com.murengezi.minecraft.client.gui.GUI;
import com.murengezi.minecraft.client.gui.Screen;
import com.murengezi.minecraft.client.audio.MusicTicker;
import com.murengezi.minecraft.client.audio.SoundHandler;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.network.play.client.C16PacketClientStatus;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.io.Charsets;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class GuiWinGame extends Screen
{
    private static final Logger logger = LogManager.getLogger();
    private static final ResourceLocation MINECRAFT_LOGO = new ResourceLocation("textures/gui/title/minecraft.png");
    private static final ResourceLocation VIGNETTE_TEXTURE = new ResourceLocation("textures/misc/vignette.png");
    private int tick;
    private List<String> lines;
    private int field_146579_r;
    private final float speed = 0.5F;

    @Override
    public void updateScreen() {
        MusicTicker musicticker = getMc().getMusicTicker();
        SoundHandler soundhandler = getMc().getSoundHandler();

        if (this.tick == 0) {
            musicticker.func_181557_a();
            musicticker.func_181558_a(MusicTicker.MusicType.CREDITS);
            soundhandler.resumeSounds();
        }

        soundhandler.update();
        ++this.tick;
        float f = (float)(this.field_146579_r + this.height + this.height + 24) / this.speed;

        if ((float)this.tick > f) {
            this.sendRespawnPacket();
        }
        super.updateScreen();
    }

    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        if (keyCode == 1)
        {
            this.sendRespawnPacket();
        }
    }

    private void sendRespawnPacket() {
        getPlayer().sendQueue.addToSendQueue(new C16PacketClientStatus(C16PacketClientStatus.EnumState.PERFORM_RESPAWN));
        changeScreen(null);
    }

    public boolean doesGuiPauseGame() {
        return true;
    }

    public void initGui() {
        if (this.lines == null) {
            this.lines = Lists.newArrayList();

            try {
                String line;
                String s1 = "" + EnumChatFormatting.WHITE + EnumChatFormatting.OBFUSCATED + EnumChatFormatting.GREEN + EnumChatFormatting.AQUA;
                int i = 274;
                InputStream inputStream = getMc().getResourceManager().getResource(new ResourceLocation("texts/end.txt")).getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, Charsets.UTF_8));
                Random random = new Random(8124371L);

                while ((line = bufferedReader.readLine()) != null) {
                    String s2;
                    String s3;

                    for (line = line.replaceAll("PLAYERNAME", getMc().getSession().getUsername()); line.contains(s1); line = s2 + EnumChatFormatting.WHITE + EnumChatFormatting.OBFUSCATED + "XXXXXXXX".substring(0, random.nextInt(4) + 3) + s3) {
                        int j = line.indexOf(s1);
                        s2 = line.substring(0, j);
                        s3 = line.substring(j + s1.length());
                    }

                    this.lines.addAll(getMc().fontRenderer.listFormattedStringToWidth(line, i));
                    this.lines.add("");
                }

                inputStream.close();

                for (int k = 0; k < 8; ++k) {
                    this.lines.add("");
                }

                inputStream = getMc().getResourceManager().getResource(new ResourceLocation("texts/credits.txt")).getInputStream();
                bufferedReader = new BufferedReader(new InputStreamReader(inputStream, Charsets.UTF_8));

                while ((line = bufferedReader.readLine()) != null) {
                    line = line.replaceAll("PLAYERNAME", getMc().getSession().getUsername());
                    line = line.replaceAll("\t", "    ");
                    this.lines.addAll(getFr().listFormattedStringToWidth(line, i));
                    this.lines.add("");
                }

                inputStream.close();
                this.field_146579_r = this.lines.size() * 12;
            } catch (Exception exception) {
                logger.error("Couldn't load credits", exception);
            }
        }
    }

    private void drawWinGameScreen(float partialTicks) {
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldrenderer = tessellator.getWorldRenderer();
        getMc().getTextureManager().bindTexture(GUI.optionsBackground);
        worldrenderer.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
        int i = this.width;
        float f = 0.0F - ((float)this.tick + partialTicks) * 0.5F * this.speed;
        float f1 = (float)this.height - ((float)this.tick + partialTicks) * 0.5F * this.speed;
        float f2 = 0.015625F;
        float f3 = ((float)this.tick + partialTicks - 0.0F) * 0.02F;
        float f4 = (float)(this.field_146579_r + this.height + this.height + 24) / this.speed;
        float f5 = (f4 - 20.0F - ((float)this.tick + partialTicks)) * 0.005F;

        if (f5 < f3) {
            f3 = f5;
        }

        if (f3 > 1.0F) {
            f3 = 1.0F;
        }

        f3 = f3 * f3;
        f3 = f3 * 96.0F / 255.0F;
        worldrenderer.pos(0.0D, this.height, this.zLevel).tex(0.0D, f * f2).color(f3, f3, f3, 1.0F).endVertex();
        worldrenderer.pos(i, this.height, this.zLevel).tex((float)i * f2, f * f2).color(f3, f3, f3, 1.0F).endVertex();
        worldrenderer.pos(i, 0.0D, this.zLevel).tex((float)i * f2, f1 * f2).color(f3, f3, f3, 1.0F).endVertex();
        worldrenderer.pos(0.0D, 0.0D, this.zLevel).tex(0.0D, f1 * f2).color(f3, f3, f3, 1.0F).endVertex();
        tessellator.draw();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawWinGameScreen(partialTicks);
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldrenderer = tessellator.getWorldRenderer();
        int i = 274;
        int j = this.width / 2 - i / 2;
        int k = this.height + 50;
        float f = -((float)this.tick + partialTicks) * this.speed;
        GlStateManager.pushMatrix();
        GlStateManager.translate(0.0F, f, 0.0F);
        getMc().getTextureManager().bindTexture(MINECRAFT_LOGO);
        GlStateManager.colorAllMax();
        this.drawTexturedModalRect(j, k, 0, 0, 155, 44);
        this.drawTexturedModalRect(j + 155, k, 0, 45, 155, 44);
        int l = k + 200;

        for (int i1 = 0; i1 < this.lines.size(); ++i1) {
            if (i1 == this.lines.size() - 1) {
                float f1 = (float)l + f - (float)(this.height / 2 - 6);

                if (f1 < 0.0F) {
                    GlStateManager.translate(0.0F, -f1, 0.0F);
                }
            }

            if ((float)l + f + 12.0F + 8.0F > 0.0F && (float)l + f < (float)this.height) {
                String s = this.lines.get(i1);

                if (s.startsWith("[C]")) {
                    getFr().drawStringWithShadow(s.substring(3), (float)(j + (i - getFr().getStringWidth(s.substring(3))) / 2), (float)l, 16777215);
                } else {
                    getFr().fontRandom.setSeed((long)i1 * 4238972211L + (long)(this.tick / 4));
                    getFr().drawStringWithShadow(s, (float)j, (float)l, 16777215);
                }
            }

            l += 12;
        }

        GlStateManager.popMatrix();
        getMc().getTextureManager().bindTexture(VIGNETTE_TEXTURE);
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(0, 769);
        int j1 = this.width;
        int k1 = this.height;
        worldrenderer.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
        worldrenderer.pos(0.0D, k1, this.zLevel).tex(0.0D, 1.0D).color(1.0F, 1.0F, 1.0F, 1.0F).endVertex();
        worldrenderer.pos(j1, k1, this.zLevel).tex(1.0D, 1.0D).color(1.0F, 1.0F, 1.0F, 1.0F).endVertex();
        worldrenderer.pos(j1, 0.0D, this.zLevel).tex(1.0D, 0.0D).color(1.0F, 1.0F, 1.0F, 1.0F).endVertex();
        worldrenderer.pos(0.0D, 0.0D, this.zLevel).tex(0.0D, 0.0D).color(1.0F, 1.0F, 1.0F, 1.0F).endVertex();
        tessellator.draw();
        GlStateManager.disableBlend();
        super.drawScreen(mouseX, mouseY, partialTicks);
    }
}
