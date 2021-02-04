package com.murengezi.minecraft.client.gui.BrowseServers;

import com.google.common.base.Charsets;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.murengezi.minecraft.client.gui.GuiListExtended;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.base64.Base64;
import net.minecraft.client.Minecraft;
import com.murengezi.minecraft.client.gui.GUI;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.lang3.Validate;

import java.awt.image.BufferedImage;
import java.net.UnknownHostException;
import java.util.List;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author Tobias Sjöblom
 * Created on 2021-01-15 at 14:33
 */
public class ServerListEntryNormal implements GuiListExtended.IGuiListEntry {

    private static final ThreadPoolExecutor EXECUTOR = new ScheduledThreadPoolExecutor(5, (new ThreadFactoryBuilder()).setNameFormat("Server Pinger #%d").setDaemon(true).build());
    private static final ResourceLocation UNKNOWN_SERVER = new ResourceLocation("textures/misc/unknown_server.png");
    private static final ResourceLocation SERVER_SELECTION_BUTTONS = new ResourceLocation("textures/gui/server_selection.png");
    private final BrowseScreen parentScreen;
    private final Minecraft mc;
    private final ServerData serverData;
    private final ResourceLocation iconLocation;
    private String encodedIconData;
    private DynamicTexture iconTexture;
    private long field_148298_f;

    protected ServerListEntryNormal(BrowseScreen parentScreen, ServerData serverData) {
        this.parentScreen = parentScreen;
        this.serverData = serverData;
        this.mc = Minecraft.getMinecraft();
        this.iconLocation = new ResourceLocation("servers/" + serverData.serverIP + "/icon");
        this.iconTexture = (DynamicTexture)this.mc.getTextureManager().getTexture(this.iconLocation);
    }

    public void drawEntry(int slotIndex, int x, int y, int listWidth, int slotHeight, int mouseX, int mouseY, boolean isSelected) {
        if (!this.serverData.field_78841_f) {
            this.serverData.field_78841_f = true;
            this.serverData.pingToServer = -2L;
            this.serverData.serverMOTD = "";
            this.serverData.populationInfo = "";
            EXECUTOR.submit(() -> {
                try {
                    ServerListEntryNormal.this.parentScreen.getOldServerPinger().ping(ServerListEntryNormal.this.serverData);
                } catch (UnknownHostException var2) {
                    ServerListEntryNormal.this.serverData.pingToServer = -1L;
                    ServerListEntryNormal.this.serverData.serverMOTD = EnumChatFormatting.DARK_RED + "Can't resolve hostname";
                } catch (Exception var3) {
                    ServerListEntryNormal.this.serverData.pingToServer = -1L;
                    ServerListEntryNormal.this.serverData.serverMOTD = EnumChatFormatting.DARK_RED + "Can't connect to server.";
                }
            });
        }

        boolean flag = this.serverData.version > 47;
        boolean flag1 = this.serverData.version < 47;
        boolean flag2 = flag || flag1;
        this.mc.fontRendererObj.drawStringWithShadow(this.serverData.serverName, x + 32 + 3, y + 1, 16777215);
        List<String> list = this.mc.fontRendererObj.listFormattedStringToWidth(this.serverData.serverMOTD, listWidth - 32 - 2);

        for (int i = 0; i < Math.min(list.size(), 2); ++i) {
            this.mc.fontRendererObj.drawStringWithShadow(list.get(i), x + 32 + 3, y + 12 + this.mc.fontRendererObj.FONT_HEIGHT * i, 8421504);
        }

        String s2 = flag2 ? EnumChatFormatting.DARK_RED + this.serverData.gameVersion : this.serverData.populationInfo;
        int j = this.mc.fontRendererObj.getStringWidth(s2);
        this.mc.fontRendererObj.drawStringWithShadow(s2, x + listWidth - j - 15 - 2, y + 1, 8421504);
        int k = 0;
        String s = null;
        int l;
        String s1;

        if (flag2) {
            l = 5;
            s1 = flag ? "Client out of date!" : "Server out of date!";
            s = this.serverData.playerList;
        } else if (this.serverData.field_78841_f && this.serverData.pingToServer != -2L) {
            if (this.serverData.pingToServer < 0L) {
                l = 5;
            } else if (this.serverData.pingToServer < 150L) {
                l = 0;
            } else if (this.serverData.pingToServer < 300L) {
                l = 1;
            } else if (this.serverData.pingToServer < 600L) {
                l = 2;
            } else if (this.serverData.pingToServer < 1000L) {
                l = 3;
            } else {
                l = 4;
            }

            if (this.serverData.pingToServer < 0L) {
                s1 = "(no connection)";
            } else {
                s1 = this.serverData.pingToServer + "ms";
                s = this.serverData.playerList;
            }
        } else {
            k = 1;
            l = (int)(Minecraft.getSystemTime() / 100L + (slotIndex * 2L) & 7L);

            if (l > 4) {
                l = 8 - l;
            }

            s1 = "Pinging...";
        }

        GlStateManager.colorAllMax();
        this.mc.getTextureManager().bindTexture(GUI.icons);
        GUI.drawModalRectWithCustomSizedTexture(x + listWidth - 15, y, (float)(k * 10), (float)(176 + l * 8), 10, 8, 256.0F, 256.0F);

        if (this.serverData.getBase64EncodedIconData() != null && !this.serverData.getBase64EncodedIconData().equals(this.encodedIconData)) {
            this.encodedIconData = this.serverData.getBase64EncodedIconData();
            this.prepareServerIcon();
            this.parentScreen.getServerList().saveServerList();
        }

        if (this.iconTexture != null) {
            this.func_178012_a(x, y, this.iconLocation);
        } else {
            this.func_178012_a(x, y, UNKNOWN_SERVER);
        }

        int i1 = mouseX - x;
        int j1 = mouseY - y;

        if (i1 >= listWidth - 15 && i1 <= listWidth - 5 && j1 >= 0 && j1 <= 8) {
            this.parentScreen.setHoveringText(s1);
        } else if (i1 >= listWidth - j - 15 - 2 && i1 <= listWidth - 15 - 2 && j1 >= 0 && j1 <= 8) {
            this.parentScreen.setHoveringText(s);
        }

        if (this.mc.gameSettings.touchscreen || isSelected) {
            this.mc.getTextureManager().bindTexture(SERVER_SELECTION_BUTTONS);
            GUI.drawRect(x, y, x + 32, y + 32, -1601138544);
            GlStateManager.colorAllMax();
            int k1 = mouseX - x;
            int l1 = mouseY - y;

            if (this.func_178013_b()) {
                if (k1 < 32 && k1 > 16) {
                    GUI.drawModalRectWithCustomSizedTexture(x, y, 0.0F, 32.0F, 32, 32, 256.0F, 256.0F);
                } else {
                    GUI.drawModalRectWithCustomSizedTexture(x, y, 0.0F, 0.0F, 32, 32, 256.0F, 256.0F);
                }
            }

            if (slotIndex > 0) {
                if (k1 < 16 && l1 < 16) {
                    GUI.drawModalRectWithCustomSizedTexture(x, y, 96.0F, 32.0F, 32, 32, 256.0F, 256.0F);
                } else {
                    GUI.drawModalRectWithCustomSizedTexture(x, y, 96.0F, 0.0F, 32, 32, 256.0F, 256.0F);
                }
            }

            if (slotIndex < this.parentScreen.getServerList().countServers() - 1) {
                if (k1 < 16 && l1 > 16) {
                    GUI.drawModalRectWithCustomSizedTexture(x, y, 64.0F, 32.0F, 32, 32, 256.0F, 256.0F);
                } else {
                    GUI.drawModalRectWithCustomSizedTexture(x, y, 64.0F, 0.0F, 32, 32, 256.0F, 256.0F);
                }
            }
        }
    }

    protected void func_178012_a(int p_178012_1_, int p_178012_2_, ResourceLocation p_178012_3_) {
        this.mc.getTextureManager().bindTexture(p_178012_3_);
        GlStateManager.enableBlend();
        GUI.drawModalRectWithCustomSizedTexture(p_178012_1_, p_178012_2_, 0.0F, 0.0F, 32, 32, 32.0F, 32.0F);
        GlStateManager.disableBlend();
    }

    private boolean func_178013_b()
    {
        return true;
    }

    private void prepareServerIcon() {
        if (this.serverData.getBase64EncodedIconData() == null) {
            this.mc.getTextureManager().deleteTexture(this.iconLocation);
            this.iconTexture = null;
        } else {
            ByteBuf bytebuf = Unpooled.copiedBuffer(this.serverData.getBase64EncodedIconData(), Charsets.UTF_8);
            ByteBuf bytebuf1 = Base64.decode(bytebuf);
            BufferedImage bufferedimage;
            label101:{
                try {
                    bufferedimage = TextureUtil.readBufferedImage(new ByteBufInputStream(bytebuf1));
                    Validate.validState(bufferedimage.getWidth() == 64, "Must be 64 pixels wide");
                    Validate.validState(bufferedimage.getHeight() == 64, "Must be 64 pixels high");
                    break label101;
                } catch (Throwable throwable) {
                    throwable.printStackTrace();
                    this.serverData.setBase64EncodedIconData(null);
                } finally {
                    bytebuf.release();
                    bytebuf1.release();
                }

                return;
            }

            if (this.iconTexture == null) {
                this.iconTexture = new DynamicTexture(bufferedimage.getWidth(), bufferedimage.getHeight());
                this.mc.getTextureManager().loadTexture(this.iconLocation, this.iconTexture);
            }

            bufferedimage.getRGB(0, 0, bufferedimage.getWidth(), bufferedimage.getHeight(), this.iconTexture.getTextureData(), 0, bufferedimage.getWidth());
            this.iconTexture.updateDynamicTexture();
        }
    }

    /**
     * Returns true if the mouse has been pressed on this control.
     */
    public boolean mousePressed(int slotIndex, int p_148278_2_, int p_148278_3_, int p_148278_4_, int p_148278_5_, int p_148278_6_) {
        if (p_148278_5_ <= 32) {
            if (p_148278_5_ < 32 && p_148278_5_ > 16 && this.func_178013_b()) {
                this.parentScreen.selectServer(slotIndex);
                this.parentScreen.connectToSelected();
                return true;
            }
        }

        this.parentScreen.selectServer(slotIndex);

        if (Minecraft.getSystemTime() - this.field_148298_f < 250L) {
            this.parentScreen.connectToSelected();
        }

        this.field_148298_f = Minecraft.getSystemTime();
        return false;
    }

    public void setSelected(int p_178011_1_, int p_178011_2_, int p_178011_3_) {}
    public void mouseReleased(int slotIndex, int x, int y, int mouseEvent, int relativeX, int relativeY) {}

    public ServerData getServerData() {
        return this.serverData;
    }

}
