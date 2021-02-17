package net.minecraft.entity.player;

import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.IChatComponent;

public enum EnumPlayerModelParts
{
    CAPE(0, "cape"),
    JACKET(1, "jacket"),
    LEFT_SLEEVE(2, "left_sleeve"),
    RIGHT_SLEEVE(3, "right_sleeve"),
    LEFT_PANTS_LEG(4, "left_pants_leg"),
    RIGHT_PANTS_LEG(5, "right_pants_leg"),
    HAT(6, "hat");

    private final int partId;
    private final int partMask;
    private final String partName;
    private final IChatComponent chatComponent;

    EnumPlayerModelParts(int partId, String partName)
    {
        this.partId = partId;
        this.partMask = 1 << partId;
        this.partName = partName;
        this.chatComponent = new ChatComponentTranslation("options.modelPart." + partName);
    }

    public int getPartMask()
    {
        return this.partMask;
    }

    public int getPartId()
    {
        return this.partId;
    }

    public String getPartName()
    {
        return this.partName;
    }

    public IChatComponent getChatComponent() {
        return this.chatComponent;
    }
}
