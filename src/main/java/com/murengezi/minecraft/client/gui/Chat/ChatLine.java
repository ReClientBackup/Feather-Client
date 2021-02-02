package com.murengezi.minecraft.client.gui.Chat;

import net.minecraft.util.IChatComponent;

public class ChatLine {

    private final IChatComponent line;
    private final int updateCounterCreated;
    private final int id;

    public ChatLine(int updateCounterCreated, IChatComponent line, int id) {
        this.line = line;
        this.updateCounterCreated = updateCounterCreated;
        this.id = id;
    }

    public IChatComponent getChatComponent() {
        return this.line;
    }

    public int getUpdatedCounter() {
        return this.updateCounterCreated;
    }

    public int getId() {
        return this.id;
    }
}
