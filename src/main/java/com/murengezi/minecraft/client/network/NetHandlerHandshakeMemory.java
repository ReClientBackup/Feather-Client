package com.murengezi.minecraft.client.network;

import net.minecraft.network.NetworkManager;
import net.minecraft.network.handshake.INetHandlerHandshakeServer;
import net.minecraft.network.handshake.client.C00Handshake;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.NetHandlerLoginServer;
import net.minecraft.util.IChatComponent;

public class NetHandlerHandshakeMemory implements INetHandlerHandshakeServer {

    private final MinecraftServer server;
    private final NetworkManager networkManager;

    public NetHandlerHandshakeMemory(MinecraftServer server, NetworkManager networkManager) {
        this.server = server;
        this.networkManager = networkManager;
    }

    public void processHandshake(C00Handshake packet) {
        this.networkManager.setConnectionState(packet.getRequestedState());
        this.networkManager.setNetHandler(new NetHandlerLoginServer(this.server, this.networkManager));
    }

    public void onDisconnect(IChatComponent reason) {}

}
