package com.murengezi.minecraft.client.network;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.exceptions.AuthenticationException;
import com.mojang.authlib.exceptions.AuthenticationUnavailableException;
import com.mojang.authlib.exceptions.InvalidCredentialsException;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.murengezi.minecraft.client.gui.Multiplayer.DisconnectedScreen;
import java.math.BigInteger;
import java.security.PublicKey;
import java.util.Objects;
import javax.crypto.SecretKey;
import net.minecraft.client.Minecraft;
import com.murengezi.minecraft.client.gui.Screen;
import net.minecraft.network.EnumConnectionState;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.login.INetHandlerLoginClient;
import net.minecraft.network.login.client.C01PacketEncryptionResponse;
import net.minecraft.network.login.server.S00PacketDisconnect;
import net.minecraft.network.login.server.S01PacketEncryptionRequest;
import net.minecraft.network.login.server.S02PacketLoginSuccess;
import net.minecraft.network.login.server.S03PacketEnableCompression;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.CryptManager;
import net.minecraft.util.IChatComponent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class NetHandlerLoginClient implements INetHandlerLoginClient {

    private static final Logger logger = LogManager.getLogger();
    private final Minecraft mc;
    private final Screen previousScreen;
    private final NetworkManager networkManager;
    private GameProfile gameProfile;

    public NetHandlerLoginClient(NetworkManager networkManager, Minecraft mc, Screen previousScreen) {
        this.networkManager = networkManager;
        this.mc = mc;
        this.previousScreen = previousScreen;
    }

    public void handleEncryptionRequest(S01PacketEncryptionRequest packet) {
        final SecretKey secretkey = CryptManager.createNewSharedKey();
        String serverId = packet.getServerId();
        PublicKey publickey = packet.getPublicKey();
        String s1 = (new BigInteger(Objects.requireNonNull(CryptManager.getServerIdHash(serverId, publickey, secretkey)))).toString(16);

        if (this.mc.getCurrentServerData() != null && this.mc.getCurrentServerData().func_181041_d()) {
            try {
                this.getSessionService().joinServer(this.mc.getSession().getProfile(), this.mc.getSession().getToken(), s1);
            } catch (AuthenticationException var10) {
                logger.warn("Couldn't connect to auth servers but will continue to join LAN");
            }
        } else {
            try {
                this.getSessionService().joinServer(this.mc.getSession().getProfile(), this.mc.getSession().getToken(), s1);
            } catch (AuthenticationUnavailableException var7) {
                this.networkManager.closeChannel(new ChatComponentTranslation("disconnect.loginFailedInfo", new ChatComponentTranslation("disconnect.loginFailedInfo.serversUnavailable")));
                return;
            } catch (InvalidCredentialsException var8) {
                this.networkManager.closeChannel(new ChatComponentTranslation("disconnect.loginFailedInfo", new ChatComponentTranslation("disconnect.loginFailedInfo.invalidSession")));
                return;
            } catch (AuthenticationException authenticationexception) {
                this.networkManager.closeChannel(new ChatComponentTranslation("disconnect.loginFailedInfo", authenticationexception.getMessage()));
                return;
            }
        }

        this.networkManager.sendPacket(new C01PacketEncryptionResponse(secretkey, publickey, packet.getVerifyToken()), complete -> NetHandlerLoginClient.this.networkManager.enableEncryption(secretkey));
    }

    private MinecraftSessionService getSessionService() {
        return this.mc.getSessionService();
    }

    public void handleLoginSuccess(S02PacketLoginSuccess packet) {
        this.gameProfile = packet.getProfile();
        this.networkManager.setConnectionState(EnumConnectionState.PLAY);
        this.networkManager.setNetHandler(new NetHandlerPlayClient(this.mc, this.previousScreen, this.networkManager, this.gameProfile));
    }

    public void onDisconnect(IChatComponent reason) {
        this.mc.displayGuiScreen(new DisconnectedScreen(this.previousScreen, "connect.failed", reason));
    }

    public void handleDisconnect(S00PacketDisconnect packet) {
        this.networkManager.closeChannel(packet.func_149603_c());
    }

    public void handleEnableCompression(S03PacketEnableCompression packet) {
        if (!this.networkManager.isLocalChannel()) {
            this.networkManager.setCompressionTreshold(packet.getCompressionTreshold());
        }
    }

}
