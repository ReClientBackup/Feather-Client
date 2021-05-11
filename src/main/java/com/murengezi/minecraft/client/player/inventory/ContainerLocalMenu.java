package com.murengezi.minecraft.client.player.inventory;

import com.google.common.collect.Maps;
import java.util.Map;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.util.IChatComponent;
import net.minecraft.world.ILockableContainer;
import net.minecraft.world.LockCode;

public class ContainerLocalMenu extends InventoryBasic implements ILockableContainer {

    private final String guiID;
    private final Map<Integer, Integer> fields = Maps.newHashMap();

    public ContainerLocalMenu(String id, IChatComponent title, int slotCount) {
        super(title, slotCount);
        this.guiID = id;
    }

    public int getField(int id) {
        return this.fields.getOrDefault(id, 0);
    }

    public void setField(int id, int value) {
        this.fields.put(id, value);
    }

    public int getFieldCount() {
        return this.fields.size();
    }

    public boolean isLocked() {
        return false;
    }

    public void setLockCode(LockCode code) {}

    public LockCode getLockCode() {
        return LockCode.EMPTY_CODE;
    }

    public String getGuiID() {
        return this.guiID;
    }

    public Container createContainer(InventoryPlayer inventory, EntityPlayer player) {
        throw new UnsupportedOperationException();
    }

}
