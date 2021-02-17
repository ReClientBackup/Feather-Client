package com.murengezi.chocolate.Discord;

import net.arikia.dev.drpc.DiscordEventHandlers;
import net.arikia.dev.drpc.DiscordRPC;
import net.arikia.dev.drpc.DiscordRichPresence;

/**
 * @author Tobias Sjöblom
 * Created on 2021-01-12 at 21:04
 */
public class DiscordRP {

    private boolean running = true;
    private long created = 0;

    public DiscordRP() {
        start();
    }

    public void start() {
        this.created = System.currentTimeMillis();

        DiscordEventHandlers handlers = new DiscordEventHandlers.Builder().build();

        DiscordRPC.discordInitialize("798641745218764820", handlers, true);

        new Thread("Discord RPC Callback") {
            @Override
            public void run() {
                while (running) {
                    DiscordRPC.discordRunCallbacks();
                }
            }
        }.start();
    }

    public void shutdown() {
        running = false;
        DiscordRPC.discordShutdown();
    }

    public void update(String... lines) {
        DiscordRichPresence.Builder b = new DiscordRichPresence.Builder(lines[1]);
        b.setBigImage("large", "");
        b.setDetails(lines[0]);
        b.setStartTimestamps(created);

        DiscordRPC.discordUpdatePresence(b.build());
    }
}
