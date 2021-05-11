package net.minecraft.client.gui;

import com.murengezi.minecraft.client.gui.GuiButton;
import io.netty.buffer.Unpooled;
import java.io.IOException;
import net.minecraft.client.gui.inventory.GuiContainer;
import com.murengezi.minecraft.client.renderer.GlStateManager;
import com.murengezi.minecraft.client.renderer.RenderHelper;
import com.murengezi.minecraft.client.resources.I18n;
import net.minecraft.entity.IMerchant;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ContainerMerchant;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.client.C17PacketCustomPayload;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.ResourceLocation;
import net.minecraft.village.MerchantRecipe;
import net.minecraft.village.MerchantRecipeList;
import net.minecraft.world.World;

public class GuiMerchant extends GuiContainer {

    private static final ResourceLocation MERCHANT_GUI_TEXTURE = new ResourceLocation("textures/gui/container/villager.png");
    private final IMerchant merchant;
    private GuiMerchant.MerchantButton nextButton, previousButton;
    private int selectedMerchantRecipe;
    private final IChatComponent chatComponent;

    public GuiMerchant(InventoryPlayer inventoryPlayer, IMerchant merchant, World world) {
        super(new ContainerMerchant(inventoryPlayer, merchant, world));
        this.merchant = merchant;
        this.chatComponent = merchant.getDisplayName();
    }

    @Override
    public void initGui() {
        super.initGui();
        int i = (this.width - this.xSize) / 2;
        int j = (this.height - this.ySize) / 2;
        this.buttonList.add(this.nextButton = new GuiMerchant.MerchantButton(1, i + 120 + 27, j + 24 - 1, true));
        this.buttonList.add(this.previousButton = new GuiMerchant.MerchantButton(2, i + 36 - 19, j + 24 - 1, false));
        this.nextButton.setEnabled(false);
        this.previousButton.setEnabled(false);
    }

    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        String s = this.chatComponent.getUnformattedText();
        getFr().drawString(s, this.xSize / 2 - getFr().getStringWidth(s) / 2, 6, 4210752);
        getFr().drawString(I18n.format("container.inventory"), 8, this.ySize - 96 + 2, 4210752);
    }

    @Override
    public void updateScreen() {
        super.updateScreen();
        MerchantRecipeList merchantrecipelist = this.merchant.getRecipes(getPlayer());

        if (merchantrecipelist != null) {
            this.nextButton.setEnabled(this.selectedMerchantRecipe < merchantrecipelist.size() - 1);
            this.previousButton.setEnabled(this.selectedMerchantRecipe > 0);
        }
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        boolean flag = false;

        if (button == this.nextButton) {
            ++this.selectedMerchantRecipe;
            MerchantRecipeList merchantrecipelist = this.merchant.getRecipes(getPlayer());

            if (merchantrecipelist != null && this.selectedMerchantRecipe >= merchantrecipelist.size()) {
                this.selectedMerchantRecipe = merchantrecipelist.size() - 1;
            }

            flag = true;
        } else if (button == this.previousButton) {
            --this.selectedMerchantRecipe;

            if (this.selectedMerchantRecipe < 0) {
                this.selectedMerchantRecipe = 0;
            }

            flag = true;
        }

        if (flag) {
            ((ContainerMerchant)this.inventorySlots).setCurrentRecipeIndex(this.selectedMerchantRecipe);
            PacketBuffer packetbuffer = new PacketBuffer(Unpooled.buffer());
            packetbuffer.writeInt(this.selectedMerchantRecipe);
            getMc().getNetHandler().addToSendQueue(new C17PacketCustomPayload("MC|TrSel", packetbuffer));
        }
        super.actionPerformed(button);
    }

    protected void drawGuiContainerBackgroundLayer(int mouseX, int mouseY, float partialTicks) {
        GlStateManager.colorAllMax();
        getMc().getTextureManager().bindTexture(MERCHANT_GUI_TEXTURE);
        int i = (this.width - this.xSize) / 2;
        int j = (this.height - this.ySize) / 2;
        this.drawTexturedModalRect(i, j, 0, 0, this.xSize, this.ySize);
        MerchantRecipeList merchantrecipelist = this.merchant.getRecipes(getPlayer());

        if (merchantrecipelist != null && !merchantrecipelist.isEmpty())
        {
            int k = this.selectedMerchantRecipe;

            if (k < 0 || k >= merchantrecipelist.size())
            {
                return;
            }

            MerchantRecipe merchantrecipe = merchantrecipelist.get(k);

            if (merchantrecipe.isRecipeDisabled())
            {
                getMc().getTextureManager().bindTexture(MERCHANT_GUI_TEXTURE);
                GlStateManager.colorAllMax();
                GlStateManager.disableLighting();
                this.drawTexturedModalRect(this.guiLeft + 83, this.guiTop + 21, 212, 0, 28, 21);
                this.drawTexturedModalRect(this.guiLeft + 83, this.guiTop + 51, 212, 0, 28, 21);
            }
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);
        MerchantRecipeList merchantrecipelist = this.merchant.getRecipes(getPlayer());

        if (merchantrecipelist != null && !merchantrecipelist.isEmpty()) {
            int i = (this.width - this.xSize) / 2;
            int j = (this.height - this.ySize) / 2;
            int k = this.selectedMerchantRecipe;
            MerchantRecipe merchantrecipe = merchantrecipelist.get(k);
            ItemStack itemstack = merchantrecipe.getItemToBuy();
            ItemStack itemstack1 = merchantrecipe.getSecondItemToBuy();
            ItemStack itemstack2 = merchantrecipe.getItemToSell();
            GlStateManager.pushMatrix();
            RenderHelper.enableGUIStandardItemLighting();
            GlStateManager.disableLighting();
            GlStateManager.enableRescaleNormal();
            GlStateManager.enableColorMaterial();
            GlStateManager.enableLightning();
            this.itemRender.zLevel = 100.0F;
            this.itemRender.renderItemAndEffectIntoGUI(itemstack, i + 36, j + 24);
            this.itemRender.renderItemOverlays(getFr(), itemstack, i + 36, j + 24);

            if (itemstack1 != null) {
                this.itemRender.renderItemAndEffectIntoGUI(itemstack1, i + 62, j + 24);
                this.itemRender.renderItemOverlays(getFr(), itemstack1, i + 62, j + 24);
            }

            this.itemRender.renderItemAndEffectIntoGUI(itemstack2, i + 120, j + 24);
            this.itemRender.renderItemOverlays(getFr(), itemstack2, i + 120, j + 24);
            this.itemRender.zLevel = 0.0F;
            GlStateManager.disableLighting();

            if (this.isPointInRegion(36, 24, 16, 16, mouseX, mouseY) && itemstack != null) {
                this.renderToolTip(itemstack, mouseX, mouseY);
            } else if (itemstack1 != null && this.isPointInRegion(62, 24, 16, 16, mouseX, mouseY)) {
                this.renderToolTip(itemstack1, mouseX, mouseY);
            } else if (itemstack2 != null && this.isPointInRegion(120, 24, 16, 16, mouseX, mouseY)) {
                this.renderToolTip(itemstack2, mouseX, mouseY);
            } else if (merchantrecipe.isRecipeDisabled() && (this.isPointInRegion(83, 21, 28, 21, mouseX, mouseY) || this.isPointInRegion(83, 51, 28, 21, mouseX, mouseY))) {
                this.drawCreativeTabHoveringText(I18n.format("merchant.deprecated"), mouseX, mouseY);
            }

            GlStateManager.popMatrix();
            GlStateManager.enableLightning();
            GlStateManager.enableDepth();
            RenderHelper.enableStandardItemLighting();
        }
    }

    public IMerchant getMerchant() {
        return this.merchant;
    }

    static class MerchantButton extends GuiButton {
        private final boolean field_146157_o;

        public MerchantButton(int buttonID, int x, int y, boolean p_i1095_4_) {
            super(buttonID, x, y, 12, 19, "");
            this.field_146157_o = p_i1095_4_;
        }

        public void drawButton(int mouseX, int mouseY) {
            if (this.isVisible()) {
                getMc().getTextureManager().bindTexture(GuiMerchant.MERCHANT_GUI_TEXTURE);
                GlStateManager.colorAllMax();
                boolean flag = mouseX >= this.getX() && mouseY >= this.getY() && mouseX < this.getX() + this.getWidth() && mouseY < this.getY() + this.getHeight();
                int i = 0;
                int j = 176;

                if (!this.isEnabled()) {
                    j += this.getWidth() * 2;
                } else if (flag) {
                    j += this.getWidth();
                }

                if (!this.field_146157_o) {
                    i += this.getHeight();
                }

                this.drawTexturedModalRect(this.getX(), this.getY(), j, i, this.getWidth(), this.getHeight());
            }
        }
    }
}
