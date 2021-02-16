package net.minecraft.client.player.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.util.IChatComponent;
import net.minecraft.world.IInteractionObject;

public class LocalBlockIntercommunication implements IInteractionObject {

    private final String guiID;
    private final IChatComponent displayName;

    public LocalBlockIntercommunication(String guiID, IChatComponent displayName) {
        this.guiID = guiID;
        this.displayName = displayName;
    }

    public Container createContainer(InventoryPlayer inventory, EntityPlayer player) {
        throw new UnsupportedOperationException();
    }

    /**
     * Gets the name of this command sender (usually username, but possibly "Rcon")
     */
    public String getCommandSenderName() {
        return this.displayName.getUnformattedText();
    }

    /**
     * Returns true if this thing is named
     */
    public boolean hasCustomName() {
        return true;
    }

    public String getGuiID() {
        return this.guiID;
    }

    /**
     * Get the formatted ChatComponent that will be used for the sender's username in chat
     */
    public IChatComponent getDisplayName() {
        return this.displayName;
    }
}
