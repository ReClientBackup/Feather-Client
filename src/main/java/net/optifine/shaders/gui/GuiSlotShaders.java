package net.optifine.shaders.gui;

import com.murengezi.minecraft.client.gui.Singleplayer.YesNoCallback;
import com.murengezi.minecraft.client.gui.YesNoScreen;
import net.minecraft.client.gui.GuiSlot;
import net.minecraft.client.resources.I18n;
import net.minecraft.src.Config;
import net.optifine.Lang;
import net.optifine.shaders.IShaderPack;
import net.optifine.shaders.Shaders;
import net.optifine.util.ResUtils;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Properties;

class GuiSlotShaders extends GuiSlot
{
    private ArrayList shaderslist;
    private int selectedIndex;
    private long lastClickedCached = 0L;
    final GuiShaders guiShaders;

    public GuiSlotShaders(GuiShaders guiShaders, int width, int height, int top, int bottom, int slotHeight)
    {
        super(width, height, top, bottom, slotHeight);
        this.guiShaders = guiShaders;
        this.updateList();
        this.amountScrolled = 0.0F;
        int i = this.selectedIndex * slotHeight;
        int j = (bottom - top) / 2;

        if (i > j)
        {
            this.scrollBy(i - j);
        }
    }

    /**
     * Gets the width of the list
     */
    public int getListWidth()
    {
        return this.width - 20;
    }

    public void updateList()
    {
        this.shaderslist = Shaders.listOfShaders();
        this.selectedIndex = 0;
        int i = 0;

        for (int j = this.shaderslist.size(); i < j; ++i)
        {
            if (this.shaderslist.get(i).equals(Shaders.currentShaderName))
            {
                this.selectedIndex = i;
                break;
            }
        }
    }

    protected int getSize()
    {
        return this.shaderslist.size();
    }

    /**
     * The element in the slot that was clicked, boolean for whether it was double clicked or not
     */
    protected void elementClicked(int index, boolean doubleClicked, int mouseX, int mouseY)
    {
        if (index != this.selectedIndex || this.lastClicked != this.lastClickedCached)
        {
            String s = (String)this.shaderslist.get(index);
            IShaderPack ishaderpack = Shaders.getShaderPack(s);

            if (this.checkCompatible(ishaderpack, index))
            {
                this.selectIndex(index);
            }
        }
    }

    private void selectIndex(int index)
    {
        this.selectedIndex = index;
        this.lastClickedCached = this.lastClicked;
        Shaders.setShaderPack((String)this.shaderslist.get(index));
        Shaders.uninit();
        this.guiShaders.updateButtons();
    }

    private boolean checkCompatible(IShaderPack sp, final int index)
    {
        if (sp == null)
        {
            return true;
        }
        else
        {
            InputStream inputstream = sp.getResourceAsStream("/shaders/shaders.properties");
            Properties properties = ResUtils.readProperties(inputstream, "Shaders");

            if (properties == null)
            {
                return true;
            }
            else
            {
                String s = "version.1.8.9";
                String s1 = properties.getProperty(s);

                if (s1 == null)
                {
                    return true;
                }
                else
                {
                    s1 = s1.trim();
                    String s2 = "L5";
                    int i = Config.compareRelease(s2, s1);

                    if (i >= 0)
                    {
                        return true;
                    }
                    else
                    {
                        String s3 = ("HD_U_" + s1).replace('_', ' ');
                        String s4 = I18n.format("of.message.shaders.nv1", s3);
                        String s5 = I18n.format("of.message.shaders.nv2");
                        YesNoCallback yesNoCallback = (result, id) -> {
                            if (result)
                            {
                                GuiSlotShaders.this.selectIndex(index);
                            }

                            getMc().displayGuiScreen(GuiSlotShaders.this.guiShaders);
                        };
                        YesNoScreen yesNoScreen = new YesNoScreen(yesNoCallback, s4, s5, 0);
                        getMc().displayGuiScreen(yesNoScreen);
                        return false;
                    }
                }
            }
        }
    }

    /**
     * Returns true if the element passed in is currently selected
     */
    protected boolean isSelected(int index)
    {
        return index == this.selectedIndex;
    }

    protected int getScrollBarX()
    {
        return this.width - 6;
    }

    /**
     * Return the height of the content being scrolled
     */
    protected int getContentHeight()
    {
        return this.getSize() * 18;
    }

    protected void drawBackground()
    {
    }

    protected void drawSlot(int index, int x, int y, int contentY, int mouseX, int mouseY)
    {
        String s = (String)this.shaderslist.get(index);

        if (s.equals("OFF"))
        {
            s = Lang.get("of.options.shaders.packNone");
        }
        else if (s.equals("(internal)"))
        {
            s = Lang.get("of.options.shaders.packDefault");
        }

        this.guiShaders.drawCenteredString(s, this.width / 2, y + 1, 14737632);
    }

    public int getSelectedIndex()
    {
        return this.selectedIndex;
    }
}
