package com.murengezi.minecraft.client.multiplayer;

import com.google.common.collect.Lists;
import java.io.File;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ServerList {

    private static final Logger logger = LogManager.getLogger();
    private final Minecraft mc;
    private final List<ServerData> servers = Lists.newArrayList();

    public ServerList(Minecraft mc) {
        this.mc = mc;
        this.loadServerList();
    }

    public void loadServerList() {
        try {
            this.servers.clear();
            NBTTagCompound nbttagcompound = CompressedStreamTools.read(new File(this.mc.dataDir, "servers.dat"));

            if (nbttagcompound == null) {
                return;
            }

            NBTTagList nbttaglist = nbttagcompound.getTagList("servers", 10);

            for (int i = 0; i < nbttaglist.tagCount(); ++i) {
                this.servers.add(ServerData.getServerDataFromNBTCompound(nbttaglist.getCompoundTagAt(i)));
            }
        }
        catch (Exception exception)
        {
            logger.error("Couldn't load server list", exception);
        }
    }

    public void saveServerList() {
        try {
            NBTTagList nbttaglist = new NBTTagList();

            for (ServerData serverdata : this.servers) {
                nbttaglist.appendTag(serverdata.getNBTCompound());
            }

            NBTTagCompound nbttagcompound = new NBTTagCompound();
            nbttagcompound.setTag("servers", nbttaglist);
            CompressedStreamTools.safeWrite(nbttagcompound, new File(this.mc.dataDir, "servers.dat"));
        } catch (Exception exception) {
            logger.error("Couldn't save server list", exception);
        }
    }


    public ServerData getServerData(int index) {
        return this.servers.get(index);
    }

    public void removeServerData(int index) {
        this.servers.remove(index);
    }

    public void addServerData(ServerData data) {
        this.servers.add(data);
    }

    public int countServers() {
        return this.servers.size();
    }

    public void swapServers(int server1, int server2)
    {
        ServerData serverdata = this.getServerData(server1);
        this.servers.set(server1, this.getServerData(server2));
        this.servers.set(server2, serverdata);
        this.saveServerList();
    }

    public void setServerData(int index, ServerData data) {
        this.servers.set(index, data);
    }

    public static void func_147414_b(ServerData data) {
        ServerList serverlist = new ServerList(Minecraft.getMinecraft());
        serverlist.loadServerList();

        for (int i = 0; i < serverlist.countServers(); ++i) {
            ServerData serverdata = serverlist.getServerData(i);

            if (serverdata.serverName.equals(data.serverName) && serverdata.serverIP.equals(data.serverIP)) {
                serverlist.setServerData(i, data);
                break;
            }
        }

        serverlist.saveServerList();
    }
}
