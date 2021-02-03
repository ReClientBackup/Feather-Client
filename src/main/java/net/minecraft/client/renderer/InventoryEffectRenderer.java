package net.minecraft.client.renderer;

import java.util.Collection;

import com.murengezi.minecraft.potion.Potion;
import com.murengezi.minecraft.potion.PotionEffect;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.resources.I18n;
import net.minecraft.inventory.Container;

public abstract class InventoryEffectRenderer extends GuiContainer
{
    /** True if there is some potion effect to display */
    private boolean hasActivePotionEffects;

    public InventoryEffectRenderer(Container inventorySlotsIn)
    {
        super(inventorySlotsIn);
    }

    /**
     * Adds the buttons (and other controls) to the screen in question. Called when the GUI is displayed and when the
     * window resizes, the buttonList is cleared beforehand.
     */
    public void initGui()
    {
        super.initGui();
        this.updateActivePotionEffects();
    }

    protected void updateActivePotionEffects()
    {
        this.guiLeft = (this.width - this.xSize) / 2;
        this.hasActivePotionEffects = !getPlayer().getActivePotionEffects().isEmpty();
    }

    /**
     * Draws the screen and all the components in it. Args : mouseX, mouseY, renderPartialTicks
     */
    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        super.drawScreen(mouseX, mouseY, partialTicks);

        if (this.hasActivePotionEffects)
        {
            this.drawActivePotionEffects();
        }
    }

    /**
     * Display the potion effects list
     */
    private void drawActivePotionEffects()
    {
        int x = this.guiLeft - 124;
        int y = this.guiTop;
        Collection<PotionEffect> collection = getPlayer().getActivePotionEffects();

        if (!collection.isEmpty())
        {
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            GlStateManager.disableLighting();
            int l = 33;

            if (collection.size() > 5)
            {
                l = 132 / (collection.size() - 1);
            }

            for (PotionEffect effect : getPlayer().getActivePotionEffects())
            {
                Potion potion = Potion.potionTypes[effect.getPotionID()];
                GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
                getMc().getTextureManager().bindTexture(inventoryBackground);
                this.drawTexturedModalRect(x, y, 0, 166, 140, 32);

                if (potion.hasStatusIcon())
                {
                    int i1 = potion.getStatusIconIndex();
                    this.drawTexturedModalRect(x + 6, y + 7, 0 + i1 % 8 * 18, 198 + i1 / 8 * 18, 18, 18);
                }

                String s1 = I18n.format(potion.getName());

                if (effect.getAmplifier() == 1)
                {
                    s1 = s1 + " " + I18n.format("enchantment.level.2");
                }
                else if (effect.getAmplifier() == 2)
                {
                    s1 = s1 + " " + I18n.format("enchantment.level.3");
                }
                else if (effect.getAmplifier() == 3)
                {
                    s1 = s1 + " " + I18n.format("enchantment.level.4");
                }

                getFr().drawStringWithShadow(s1, (float)(x + 10 + 18), (float)(y + 6), 16777215);
                String s = Potion.getDurationString(effect);
                getFr().drawStringWithShadow(s, (float)(x + 10 + 18), (float)(y + 6 + 10), 8355711);
                y += l;
            }
        }
    }
}
