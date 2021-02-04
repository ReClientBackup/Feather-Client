package com.murengezi.minecraft.client.gui.InGame;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ContainerHopper;
import net.minecraft.inventory.IInventory;
import net.minecraft.util.ResourceLocation;

public class GuiHopper extends GuiContainer {

    private static final ResourceLocation HOPPER_GUI_TEXTURE = new ResourceLocation("textures/gui/container/hopper.png");
    private final IInventory playerInventory, hopperInventory;

    public GuiHopper(InventoryPlayer playerInventory, IInventory hopperInventory) {
        super(new ContainerHopper(playerInventory, hopperInventory, Minecraft.getMinecraft().thePlayer));
        this.playerInventory = playerInventory;
        this.hopperInventory = hopperInventory;
        this.allowUserInput = false;
        this.ySize = 133;
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        getFr().drawString(this.hopperInventory.getDisplayName().getUnformattedText(), 8, 6, 4210752);
        getFr().drawString(this.playerInventory.getDisplayName().getUnformattedText(), 8, this.ySize - 96 + 2, 4210752);
        super.drawGuiContainerForegroundLayer(mouseX, mouseY);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(int mouseX, int mouseY, float partialTicks) {
        GlStateManager.colorAllMax();
        getMc().getTextureManager().bindTexture(HOPPER_GUI_TEXTURE);
        int x = (this.width - this.xSize) / 2;
        int y = (this.height - this.ySize) / 2;
        this.drawTexturedModalRect(x, y, 0, 0, this.xSize, this.ySize);
    }
}
