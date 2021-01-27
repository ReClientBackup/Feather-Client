package com.murengezi.minecraft.client.Gui.Multiplayer;

import com.murengezi.feather.Feather;
import com.murengezi.minecraft.client.Gui.GuiButton;
import com.murengezi.minecraft.client.Gui.Screen;
import com.murengezi.feather.Util.MinecraftUtils;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.multiplayer.ServerAddress;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.network.NetHandlerLoginClient;
import net.minecraft.client.resources.I18n;
import net.minecraft.network.EnumConnectionState;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.handshake.client.C00Handshake;
import net.minecraft.network.login.client.C00PacketLoginStart;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;
import org.apache.logging.log4j.Level;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Tobias Sjöblom
 * Created on 2021-01-20 at 10:39
 */
public class ConnectingScreen extends Screen {

    private static final AtomicInteger CONNECTION_ID = new AtomicInteger(0);
    private NetworkManager networkManager;
    private boolean cancelled;
    private final GuiScreen previousScreen;

    private static final int CANCEL = 0;

    public ConnectingScreen(GuiScreen previousScreen, ServerData serverData) {
        this.mc = MinecraftUtils.getMc();
        this.previousScreen = previousScreen;
        ServerAddress serverAddress = ServerAddress.getAddressFromString(serverData.serverIP);
        this.mc.loadWorld(null);
        this.mc.setServerData(serverData);
        this.connect(serverAddress.getIP(), serverAddress.getPort());
    }

    public ConnectingScreen(GuiScreen previousScreen, String hostName, int port) {
        this.previousScreen = previousScreen;
        this.mc.loadWorld(null);
        this.connect(hostName, port);
    }

    @Override
    public void initGui() {
        addButton(new GuiButton(CANCEL, this.width / 2 - 100, this.height / 4 + 132, I18n.format("gui.cancel")));
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        switch (button.getId()) {
            case CANCEL:
                this.cancelled = true;

                if (getNetworkManager() != null) {
                    getNetworkManager().closeChannel(new ChatComponentText("Aborted"));
                }

                changeScreen(previousScreen);
                break;
        }
        super.actionPerformed(button);
    }

    private void connect(String ip, int port) {
        Feather.getLogger().log(Level.INFO, "Connecting to " + ip + ", " + port);
        new Thread("Server Connector #" + CONNECTION_ID.incrementAndGet()) {

            @Override
            public void run() {
                InetAddress inetAddress = null;

                try {
                    if (isCancelled()) {
                        return;
                    }

                    inetAddress = InetAddress.getByName(ip);
                    setNetworkManager(NetworkManager.func_181124_a(inetAddress, port, mc.gameSettings.func_181148_f()));
                    getNetworkManager().setNetHandler(new NetHandlerLoginClient(getNetworkManager(), mc, previousScreen));
                    getNetworkManager().sendPacket(new C00Handshake(47, ip, port, EnumConnectionState.LOGIN));
                    getNetworkManager().sendPacket(new C00PacketLoginStart(mc.getSession().getProfile()));

                } catch (UnknownHostException e) {
                    if (isCancelled()) {
                        return;
                    }

                    Feather.getLogger().error("Could not connect to server.", e);
                    changeScreen(new DisconnectedScreen(previousScreen, "connect.failed", new ChatComponentTranslation("disconnect.genericReason", "Unknown host")));
                } catch (Exception e) {
                    if (isCancelled()) {
                        return;
                    }
                    Feather.getLogger().error("Could not connect to server.", e);
                    String msg = e.toString();

                    if (inetAddress != null) {
                        msg = msg.replaceAll(inetAddress.toString() + ":" + port, "");
                    }

                    changeScreen(new DisconnectedScreen(previousScreen, "connect.failed", new ChatComponentTranslation("disconnect.genericReason", msg)));
                }
                super.run();
            }
        }.start();
    }

    @Override
    public void updateScreen() {
        if (getNetworkManager() != null) {
            if (getNetworkManager().isChannelOpen()) {
                getNetworkManager().processReceivedPackets();
            } else {
                getNetworkManager().checkDisconnected();
            }
        }
        super.updateScreen();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground(mouseX, mouseY, 60);


        this.drawCenteredString(this.fontRendererObj, I18n.format(getNetworkManager() == null ? "connect.connecting" : "connect.authorizing"), this.width / 2, this.height / 2 - 50, 16777215);

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    public NetworkManager getNetworkManager() {
        return networkManager;
    }

    public void setNetworkManager(NetworkManager networkManager) {
        this.networkManager = networkManager;
    }

    public boolean isCancelled() {
        return cancelled;
    }
}
