package com.murengezi.minecraft.client.network;

import com.google.common.base.Charsets;
import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.mojang.authlib.GameProfile;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelException;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.nio.NioSocketChannel;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import com.murengezi.minecraft.client.Minecraft;
import com.murengezi.minecraft.client.multiplayer.ServerAddress;
import com.murengezi.minecraft.client.multiplayer.ServerData;
import net.minecraft.network.EnumConnectionState;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.ServerStatusResponse;
import net.minecraft.network.handshake.client.C00Handshake;
import net.minecraft.network.status.INetHandlerStatusClient;
import net.minecraft.network.status.client.C00PacketServerQuery;
import net.minecraft.network.status.client.C01PacketPing;
import net.minecraft.network.status.server.S00PacketServerInfo;
import net.minecraft.network.status.server.S01PacketPong;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.MathHelper;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class OldServerPinger {

    private static final Splitter PING_RESPONSE_SPLITTER = Splitter.on('\u0000').limit(6);
    private static final Logger logger = LogManager.getLogger();
    private final List<NetworkManager> pingDestinations = Collections.synchronizedList(Lists.newArrayList());

    public void ping(final ServerData server) throws UnknownHostException {
        ServerAddress serveraddress = ServerAddress.getAddressFromString(server.serverIP);
        final NetworkManager networkmanager = NetworkManager.func_181124_a(InetAddress.getByName(serveraddress.getIP()), serveraddress.getPort(), false);
        this.pingDestinations.add(networkmanager);
        server.serverMOTD = "Pinging...";
        server.pingToServer = -1L;
        server.playerList = null;
        networkmanager.setNetHandler(new INetHandlerStatusClient() {
            private boolean field_147403_d = false;
            private boolean field_183009_e = false;
            private long field_175092_e = 0L;
            public void handleServerInfo(S00PacketServerInfo packet) {
                if (this.field_183009_e) {
                    networkmanager.closeChannel(new ChatComponentText("Received unrequested status"));
                } else {
                    this.field_183009_e = true;
                    ServerStatusResponse serverstatusresponse = packet.getResponse();

                    if (serverstatusresponse.getServerDescription() != null) {
                        server.serverMOTD = serverstatusresponse.getServerDescription().getFormattedText();
                    } else {
                        server.serverMOTD = "";
                    }

                    if (serverstatusresponse.getProtocolVersionInfo() != null) {
                        server.gameVersion = serverstatusresponse.getProtocolVersionInfo().getName();
                        server.version = serverstatusresponse.getProtocolVersionInfo().getProtocol();
                    } else {
                        server.gameVersion = "Old";
                        server.version = 0;
                    }

                    if (serverstatusresponse.getPlayerCountData() != null) {
                        server.populationInfo = EnumChatFormatting.GRAY + "" + serverstatusresponse.getPlayerCountData().getOnlinePlayerCount() + "" + EnumChatFormatting.DARK_GRAY + "/" + EnumChatFormatting.GRAY + serverstatusresponse.getPlayerCountData().getMaxPlayers();

                        if (ArrayUtils.isNotEmpty(serverstatusresponse.getPlayerCountData().getPlayers())) {
                            StringBuilder stringbuilder = new StringBuilder();

                            for (GameProfile gameprofile : serverstatusresponse.getPlayerCountData().getPlayers()) {
                                if (stringbuilder.length() > 0) {
                                    stringbuilder.append("\n");
                                }

                                stringbuilder.append(gameprofile.getName());
                            }

                            if (serverstatusresponse.getPlayerCountData().getPlayers().length < serverstatusresponse.getPlayerCountData().getOnlinePlayerCount()) {
                                if (stringbuilder.length() > 0) {
                                    stringbuilder.append("\n");
                                }

                                stringbuilder.append("... and ").append(serverstatusresponse.getPlayerCountData().getOnlinePlayerCount() - serverstatusresponse.getPlayerCountData().getPlayers().length).append(" more ...");
                            }

                            server.playerList = stringbuilder.toString();
                        }
                    } else {
                        server.populationInfo = EnumChatFormatting.DARK_GRAY + "???";
                    }

                    if (serverstatusresponse.getFavicon() != null) {
                        String s = serverstatusresponse.getFavicon();

                        if (s.startsWith("data:image/png;base64,")) {
                            server.setBase64EncodedIconData(s.substring("data:image/png;base64,".length()));
                        } else {
                            OldServerPinger.logger.error("Invalid server icon (unknown format)");
                        }
                    } else {
                        server.setBase64EncodedIconData(null);
                    }

                    this.field_175092_e = Minecraft.getSystemTime();
                    networkmanager.sendPacket(new C01PacketPing(this.field_175092_e));
                    this.field_147403_d = true;
                }
            }

            public void handlePong(S01PacketPong packet) {
                long i = this.field_175092_e;
                long j = Minecraft.getSystemTime();
                server.pingToServer = j - i;
                networkmanager.closeChannel(new ChatComponentText("Finished"));
            }

            public void onDisconnect(IChatComponent reason) {
                if (!this.field_147403_d) {
                    OldServerPinger.logger.error("Can't ping " + server.serverIP + ": " + reason.getUnformattedText());
                    server.serverMOTD = EnumChatFormatting.DARK_RED + "Can't connect to server.";
                    server.populationInfo = "";
                    OldServerPinger.this.tryCompatibilityPing(server);
                }
            }
        });

        try {
            networkmanager.sendPacket(new C00Handshake(47, serveraddress.getIP(), serveraddress.getPort(), EnumConnectionState.STATUS));
            networkmanager.sendPacket(new C00PacketServerQuery());
        } catch (Throwable throwable) {
            logger.error(throwable);
        }
    }

    private void tryCompatibilityPing(final ServerData server) {
        final ServerAddress serveraddress = ServerAddress.getAddressFromString(server.serverIP);
        (new Bootstrap()).group(NetworkManager.CLIENT_NIO_EVENTLOOP.getValue()).handler(new ChannelInitializer<Channel>() {
            protected void initChannel(Channel channel) {
                try {
                    channel.config().setOption(ChannelOption.TCP_NODELAY, true);
                } catch (ChannelException ignored) {}

                channel.pipeline().addLast(new SimpleChannelInboundHandler<ByteBuf>() {
                    public void channelActive(ChannelHandlerContext context) throws Exception {
                        super.channelActive(context);
                        ByteBuf bytebuf = Unpooled.buffer();

                        try {
                            bytebuf.writeByte(254);
                            bytebuf.writeByte(1);
                            bytebuf.writeByte(250);
                            char[] achar = "MC|PingHost".toCharArray();
                            bytebuf.writeShort(achar.length);

                            for (char c0 : achar) {
                                bytebuf.writeChar(c0);
                            }

                            bytebuf.writeShort(7 + 2 * serveraddress.getIP().length());
                            bytebuf.writeByte(127);
                            achar = serveraddress.getIP().toCharArray();
                            bytebuf.writeShort(achar.length);

                            for (char c1 : achar) {
                                bytebuf.writeChar(c1);
                            }

                            bytebuf.writeInt(serveraddress.getPort());
                            context.channel().writeAndFlush(bytebuf).addListener(ChannelFutureListener.CLOSE_ON_FAILURE);
                        } finally {
                            bytebuf.release();
                        }
                    }

                    protected void channelRead0(ChannelHandlerContext context, ByteBuf read) {
                        short short1 = read.readUnsignedByte();

                        if (short1 == 255) {
                            String s = new String(read.readBytes(read.readShort() * 2).array(), Charsets.UTF_16BE);
                            String[] astring = Iterables.toArray(OldServerPinger.PING_RESPONSE_SPLITTER.split(s), String.class);

                            if ("\u00a71".equals(astring[0])) {
                                String s1 = astring[2];
                                String s2 = astring[3];
                                int j = MathHelper.parseIntWithDefault(astring[4], -1);
                                int k = MathHelper.parseIntWithDefault(astring[5], -1);
                                server.version = -1;
                                server.gameVersion = s1;
                                server.serverMOTD = s2;
                                server.populationInfo = EnumChatFormatting.GRAY + "" + j + "" + EnumChatFormatting.DARK_GRAY + "/" + EnumChatFormatting.GRAY + k;
                            }
                        }

                        context.close();
                    }

                    public void exceptionCaught(ChannelHandlerContext context, Throwable cause) {
                        context.close();
                    }
                });
            }
        }).channel(NioSocketChannel.class).connect(serveraddress.getIP(), serveraddress.getPort());
    }

    public void pingPendingNetworks() {
        synchronized (this.pingDestinations) {
            Iterator<NetworkManager> iterator = this.pingDestinations.iterator();

            while (iterator.hasNext()) {
                NetworkManager networkmanager = iterator.next();

                if (networkmanager.isChannelOpen()) {
                    networkmanager.processReceivedPackets();
                } else {
                    iterator.remove();
                    networkmanager.checkDisconnected();
                }
            }
        }
    }

    public void clearPendingNetworks() {
        synchronized (this.pingDestinations) {
            Iterator<NetworkManager> iterator = this.pingDestinations.iterator();

            while (iterator.hasNext()) {
                NetworkManager networkmanager = iterator.next();

                if (networkmanager.isChannelOpen()) {
                    iterator.remove();
                    networkmanager.closeChannel(new ChatComponentText("Cancelled"));
                }
            }
        }
    }
}
