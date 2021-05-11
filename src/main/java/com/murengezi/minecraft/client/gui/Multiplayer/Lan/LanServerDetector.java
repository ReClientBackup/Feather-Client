package com.murengezi.minecraft.client.gui.Multiplayer.Lan;

import com.google.common.collect.Lists;
import net.minecraft.client.Minecraft;
import com.murengezi.minecraft.client.multiplayer.ThreadLanServerPing;
import net.minecraft.util.HttpUtil;
import org.json.JSONObject;

import java.io.IOException;
import java.net.*;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Tobias Sjöblom
 * Created on 2021-01-19 at 12:54
 */
public class LanServerDetector {

    private static final AtomicInteger field_148551_a = new AtomicInteger(0);

    public static class LanServer {
        private final String lanServerMotd;
        private final String lanServerIpPort;
        private final String lanServerUUID;
        private long timeLastSeen;

        public LanServer(String motd, String address, String uuid) {
            this.lanServerMotd = motd;
            this.lanServerIpPort = address;
            this.lanServerUUID = uuid;
            this.timeLastSeen = Minecraft.getSystemTime();
        }

        public String getServerMotd() {
            return this.lanServerMotd;
        }

        public String getServerIpPort() {
            return this.lanServerIpPort;
        }

        public String getLanServerUUID() {
            return lanServerUUID;
        }

        public void updateLastSeen() {
            this.timeLastSeen = Minecraft.getSystemTime();
        }
    }

    public static class LanServerList {
        private final List<LanServerDetector.LanServer> listOfLanServers = Lists.newArrayList();
        boolean wasUpdated;

        public synchronized boolean getWasUpdated() {
            return this.wasUpdated;
        }

        public synchronized void setWasNotUpdated() {
            this.wasUpdated = false;
        }

        public synchronized List<LanServerDetector.LanServer> getLanServers() {
            return Collections.unmodifiableList(this.listOfLanServers);
        }

        public void func_77551_a(String p_77551_1_, InetAddress p_77551_2_) {
            String s = ThreadLanServerPing.getMotdFromPingResponse(p_77551_1_);
            String s1 = ThreadLanServerPing.getAdFromPingResponse(p_77551_1_);
            String uuid = "606e2ff0ed7748429d6ce1d3321c7838";


            String userName = s.split(" - ")[0];
            try {
                JSONObject json = new JSONObject(HttpUtil.get(new URL("https://api.mojang.com/users/profiles/minecraft/" + userName)));
                uuid = json.getString("id");

            } catch (IOException e) {
                e.printStackTrace();
            }
            if (s1 != null) {
                s1 = p_77551_2_.getHostAddress() + ":" + s1;
                boolean flag = false;

                for (LanServerDetector.LanServer lanServer : this.listOfLanServers) {
                    if (lanServer.getServerIpPort().equals(s1)) {
                        lanServer.updateLastSeen();
                        flag = true;
                        break;
                    }
                }

                if (!flag) {
                    this.listOfLanServers.add(new LanServerDetector.LanServer(s, s1, uuid));
                    this.wasUpdated = true;
                }
            }
        }
    }

    public static class ThreadLanServerFind extends Thread
    {
        private final LanServerDetector.LanServerList localServerList;
        private final InetAddress broadcastAddress;
        private final MulticastSocket socket;

        public ThreadLanServerFind(LanServerDetector.LanServerList lanServerList) throws IOException {
            super("LanServerDetector #" + LanServerDetector.field_148551_a.incrementAndGet());
            this.localServerList = lanServerList;
            this.setDaemon(true);
            this.socket = new MulticastSocket(4445);
            this.broadcastAddress = InetAddress.getByName("224.0.2.60");
            this.socket.setSoTimeout(5000);
            this.socket.joinGroup(this.broadcastAddress);
        }

        public void run() {
            byte[] abyte = new byte[1024];

            while (!this.isInterrupted()) {
                DatagramPacket datagrampacket = new DatagramPacket(abyte, abyte.length);

                try {
                    this.socket.receive(datagrampacket);
                } catch (SocketTimeoutException var5) {
                    continue;
                } catch (IOException ioexception) {
                    break;
                }

                String s = new String(datagrampacket.getData(), datagrampacket.getOffset(), datagrampacket.getLength());
                this.localServerList.func_77551_a(s, datagrampacket.getAddress());
            }

            try {
                this.socket.leaveGroup(this.broadcastAddress);
            } catch (IOException ignored) {}

            this.socket.close();
        }
    }
}
