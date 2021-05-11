package com.murengezi.minecraft.client.network;

import com.google.common.collect.Maps;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.mojang.authlib.GameProfile;
import com.murengezi.minecraft.client.gui.DownloadTerrainScreen;
import com.murengezi.minecraft.client.gui.MainMenuScreen;
import com.murengezi.minecraft.client.gui.Multiplayer.DisconnectedScreen;
import com.murengezi.minecraft.client.gui.Multiplayer.MultiplayerScreen;
import com.murengezi.minecraft.client.gui.YesNoScreen;
import com.murengezi.minecraft.potion.PotionEffect;
import io.netty.buffer.Unpooled;
import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.Map.Entry;
import com.murengezi.minecraft.block.Block;
import com.murengezi.minecraft.client.ClientBrandRetriever;
import com.murengezi.minecraft.client.Minecraft;
import com.murengezi.minecraft.client.audio.GuardianSound;
import com.murengezi.minecraft.client.entity.EntityOtherPlayerMP;
import com.murengezi.minecraft.client.entity.EntityPlayerSP;
import com.murengezi.minecraft.client.gui.Chat.ChatScreen;
import net.minecraft.client.gui.GuiMerchant;
import com.murengezi.minecraft.client.gui.Screen;
import net.minecraft.client.gui.GuiScreenBook;
import net.minecraft.client.gui.GuiWinGame;
import net.minecraft.client.gui.IProgressMeter;
import net.minecraft.client.gui.inventory.GuiContainerCreative;
import com.murengezi.minecraft.client.multiplayer.PlayerControllerMP;
import com.murengezi.minecraft.client.multiplayer.ServerData;
import com.murengezi.minecraft.client.multiplayer.ServerList;
import com.murengezi.minecraft.client.multiplayer.WorldClient;
import com.murengezi.minecraft.client.particle.EntityPickupFX;
import com.murengezi.minecraft.client.player.inventory.ContainerLocalMenu;
import com.murengezi.minecraft.client.player.inventory.LocalBlockIntercommunication;
import com.murengezi.minecraft.client.resources.I18n;
import com.murengezi.minecraft.client.settings.GameSettings;
import com.murengezi.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.DataWatcher;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLeashKnot;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IMerchant;
import net.minecraft.entity.NpcMerchant;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.BaseAttributeMap;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.ai.attributes.RangedAttribute;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.item.EntityEnderEye;
import net.minecraft.entity.item.EntityEnderPearl;
import net.minecraft.entity.item.EntityExpBottle;
import net.minecraft.entity.item.EntityFallingBlock;
import net.minecraft.entity.item.EntityFireworkRocket;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.item.EntityPainting;
import net.minecraft.entity.item.EntityTNTPrimed;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.monster.EntityGuardian;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.entity.projectile.EntityEgg;
import net.minecraft.entity.projectile.EntityFishHook;
import net.minecraft.entity.projectile.EntityLargeFireball;
import net.minecraft.entity.projectile.EntityPotion;
import net.minecraft.entity.projectile.EntitySmallFireball;
import net.minecraft.entity.projectile.EntitySnowball;
import net.minecraft.entity.projectile.EntityWitherSkull;
import net.minecraft.init.Items;
import net.minecraft.inventory.AnimalChest;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.item.Item;
import net.minecraft.item.ItemMap;
import net.minecraft.item.ItemStack;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.PacketThreadUtil;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.network.play.client.C00PacketKeepAlive;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C0FPacketConfirmTransaction;
import net.minecraft.network.play.client.C17PacketCustomPayload;
import net.minecraft.network.play.client.C19PacketResourcePackStatus;
import net.minecraft.network.play.server.S00PacketKeepAlive;
import net.minecraft.network.play.server.S01PacketJoinGame;
import net.minecraft.network.play.server.S02PacketChat;
import net.minecraft.network.play.server.S03PacketTimeUpdate;
import net.minecraft.network.play.server.S04PacketEntityEquipment;
import net.minecraft.network.play.server.S05PacketSpawnPosition;
import net.minecraft.network.play.server.S06PacketUpdateHealth;
import net.minecraft.network.play.server.S07PacketRespawn;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import net.minecraft.network.play.server.S09PacketHeldItemChange;
import net.minecraft.network.play.server.S0APacketUseBed;
import net.minecraft.network.play.server.S0BPacketAnimation;
import net.minecraft.network.play.server.S0CPacketSpawnPlayer;
import net.minecraft.network.play.server.S0DPacketCollectItem;
import net.minecraft.network.play.server.S0EPacketSpawnObject;
import net.minecraft.network.play.server.S0FPacketSpawnMob;
import net.minecraft.network.play.server.S10PacketSpawnPainting;
import net.minecraft.network.play.server.S11PacketSpawnExperienceOrb;
import net.minecraft.network.play.server.S12PacketEntityVelocity;
import net.minecraft.network.play.server.S13PacketDestroyEntities;
import net.minecraft.network.play.server.S14PacketEntity;
import net.minecraft.network.play.server.S18PacketEntityTeleport;
import net.minecraft.network.play.server.S19PacketEntityHeadLook;
import net.minecraft.network.play.server.S19PacketEntityStatus;
import net.minecraft.network.play.server.S1BPacketEntityAttach;
import net.minecraft.network.play.server.S1CPacketEntityMetadata;
import net.minecraft.network.play.server.S1DPacketEntityEffect;
import net.minecraft.network.play.server.S1EPacketRemoveEntityEffect;
import net.minecraft.network.play.server.S1FPacketSetExperience;
import net.minecraft.network.play.server.S20PacketEntityProperties;
import net.minecraft.network.play.server.S21PacketChunkData;
import net.minecraft.network.play.server.S22PacketMultiBlockChange;
import net.minecraft.network.play.server.S23PacketBlockChange;
import net.minecraft.network.play.server.S24PacketBlockAction;
import net.minecraft.network.play.server.S25PacketBlockBreakAnim;
import net.minecraft.network.play.server.S26PacketMapChunkBulk;
import net.minecraft.network.play.server.S27PacketExplosion;
import net.minecraft.network.play.server.S28PacketEffect;
import net.minecraft.network.play.server.S29PacketSoundEffect;
import net.minecraft.network.play.server.S2APacketParticles;
import net.minecraft.network.play.server.S2BPacketChangeGameState;
import net.minecraft.network.play.server.S2CPacketSpawnGlobalEntity;
import net.minecraft.network.play.server.S2DPacketOpenWindow;
import net.minecraft.network.play.server.S2EPacketCloseWindow;
import net.minecraft.network.play.server.S2FPacketSetSlot;
import net.minecraft.network.play.server.S30PacketWindowItems;
import net.minecraft.network.play.server.S31PacketWindowProperty;
import net.minecraft.network.play.server.S32PacketConfirmTransaction;
import net.minecraft.network.play.server.S33PacketUpdateSign;
import net.minecraft.network.play.server.S34PacketMaps;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.network.play.server.S36PacketSignEditorOpen;
import net.minecraft.network.play.server.S37PacketStatistics;
import net.minecraft.network.play.server.S38PacketPlayerListItem;
import net.minecraft.network.play.server.S39PacketPlayerAbilities;
import net.minecraft.network.play.server.S3APacketTabComplete;
import net.minecraft.network.play.server.S3BPacketScoreboardObjective;
import net.minecraft.network.play.server.S3CPacketUpdateScore;
import net.minecraft.network.play.server.S3DPacketDisplayScoreboard;
import net.minecraft.network.play.server.S3EPacketTeams;
import net.minecraft.network.play.server.S3FPacketCustomPayload;
import net.minecraft.network.play.server.S40PacketDisconnect;
import net.minecraft.network.play.server.S41PacketServerDifficulty;
import net.minecraft.network.play.server.S42PacketCombatEvent;
import net.minecraft.network.play.server.S43PacketCamera;
import net.minecraft.network.play.server.S44PacketWorldBorder;
import net.minecraft.network.play.server.S45PacketTitle;
import net.minecraft.network.play.server.S46PacketSetCompressionLevel;
import net.minecraft.network.play.server.S47PacketPlayerListHeaderFooter;
import net.minecraft.network.play.server.S48PacketResourcePackSend;
import net.minecraft.network.play.server.S49PacketUpdateEntityNBT;
import com.murengezi.minecraft.scoreboard.IScoreObjectiveCriteria;
import com.murengezi.minecraft.scoreboard.Score;
import com.murengezi.minecraft.scoreboard.ScoreObjective;
import com.murengezi.minecraft.scoreboard.ScorePlayerTeam;
import com.murengezi.minecraft.scoreboard.Scoreboard;
import com.murengezi.minecraft.scoreboard.Team;
import net.minecraft.stats.Achievement;
import net.minecraft.stats.AchievementList;
import net.minecraft.stats.StatBase;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityBanner;
import net.minecraft.tileentity.TileEntityBeacon;
import net.minecraft.tileentity.TileEntityCommandBlock;
import net.minecraft.tileentity.TileEntityFlowerPot;
import net.minecraft.tileentity.TileEntityMobSpawner;
import net.minecraft.tileentity.TileEntitySign;
import net.minecraft.tileentity.TileEntitySkull;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.MathHelper;
import net.minecraft.util.StringUtils;
import net.minecraft.village.MerchantRecipeList;
import net.minecraft.world.Explosion;
import net.minecraft.world.WorldProviderSurface;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.storage.MapData;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class NetHandlerPlayClient implements INetHandlerPlayClient {

    private static final Logger logger = LogManager.getLogger();

    private final NetworkManager netManager;
    private final GameProfile profile;
    private final Screen guiScreenServer;
    private Minecraft gameController;
    private WorldClient clientWorldController;
    private boolean doneLoadingTerrain;
    private final Map<UUID, NetworkPlayerInfo> playerInfoMap = Maps.newHashMap();
    public int currentServerMaxPlayers = 20;
    private boolean field_147308_k = false;

    private final Random avRandomizer = new Random();

    public NetHandlerPlayClient(Minecraft mc, Screen guiScreen, NetworkManager networkManager, GameProfile gameProfile) {
        this.gameController = mc;
        this.guiScreenServer = guiScreen;
        this.netManager = networkManager;
        this.profile = gameProfile;
    }

    public void cleanup() {
        this.clientWorldController = null;
    }

    public void handleJoinGame(S01PacketJoinGame packet) {
        PacketThreadUtil.checkThreadAndEnqueue(packet, this, this.gameController);
        this.gameController.playerController = new PlayerControllerMP(this.gameController, this);
        this.clientWorldController = new WorldClient(this, new WorldSettings(0L, packet.getGameType(), false, packet.isHardcoreMode(), packet.getWorldType()), packet.getDimension(), packet.getDifficulty(), this.gameController.mcProfiler);
        this.gameController.gameSettings.difficulty = packet.getDifficulty();
        this.gameController.loadWorld(this.clientWorldController);
        this.gameController.player.dimension = packet.getDimension();
        this.gameController.displayGuiScreen(new DownloadTerrainScreen(this));
        this.gameController.player.setEntityId(packet.getEntityId());
        this.currentServerMaxPlayers = packet.getMaxPlayers();
        this.gameController.player.setReducedDebug(packet.isReducedDebugInfo());
        this.gameController.playerController.setGameType(packet.getGameType());
        this.gameController.gameSettings.sendSettingsToServer();
        this.netManager.sendPacket(new C17PacketCustomPayload("MC|Brand", (new PacketBuffer(Unpooled.buffer())).writeString(ClientBrandRetriever.getClientModName())));
    }

    public void handleSpawnObject(S0EPacketSpawnObject packet) {
        PacketThreadUtil.checkThreadAndEnqueue(packet, this, this.gameController);
        double d0 = (double)packet.getX() / 32.0D;
        double d1 = (double)packet.getY() / 32.0D;
        double d2 = (double)packet.getZ() / 32.0D;
        Entity entity = null;

        if (packet.getType() == 10) {
            entity = EntityMinecart.func_180458_a(this.clientWorldController, d0, d1, d2, EntityMinecart.EnumMinecartType.byNetworkID(packet.func_149009_m()));
        } else if (packet.getType() == 90) {
            Entity entity1 = this.clientWorldController.getEntityByID(packet.func_149009_m());

            if (entity1 instanceof EntityPlayer) {
                entity = new EntityFishHook(this.clientWorldController, d0, d1, d2, (EntityPlayer)entity1);
            }

            packet.func_149002_g(0);
        } else if (packet.getType() == 60) {
            entity = new EntityArrow(this.clientWorldController, d0, d1, d2);
        } else if (packet.getType() == 61) {
            entity = new EntitySnowball(this.clientWorldController, d0, d1, d2);
        } else if (packet.getType() == 71) {
            entity = new EntityItemFrame(this.clientWorldController, new BlockPos(MathHelper.floor_double(d0), MathHelper.floor_double(d1), MathHelper.floor_double(d2)), EnumFacing.getHorizontal(packet.func_149009_m()));
            packet.func_149002_g(0);
        } else if (packet.getType() == 77) {
            entity = new EntityLeashKnot(this.clientWorldController, new BlockPos(MathHelper.floor_double(d0), MathHelper.floor_double(d1), MathHelper.floor_double(d2)));
            packet.func_149002_g(0);
        } else if (packet.getType() == 65) {
            entity = new EntityEnderPearl(this.clientWorldController, d0, d1, d2);
        } else if (packet.getType() == 72) {
            entity = new EntityEnderEye(this.clientWorldController, d0, d1, d2);
        } else if (packet.getType() == 76) {
            entity = new EntityFireworkRocket(this.clientWorldController, d0, d1, d2, null);
        } else if (packet.getType() == 63) {
            entity = new EntityLargeFireball(this.clientWorldController, d0, d1, d2, (double)packet.getSpeedX() / 8000.0D, (double)packet.getSpeedY() / 8000.0D, (double)packet.getSpeedZ() / 8000.0D);
            packet.func_149002_g(0);
        } else if (packet.getType() == 64) {
            entity = new EntitySmallFireball(this.clientWorldController, d0, d1, d2, (double)packet.getSpeedX() / 8000.0D, (double)packet.getSpeedY() / 8000.0D, (double)packet.getSpeedZ() / 8000.0D);
            packet.func_149002_g(0);
        } else if (packet.getType() == 66) {
            entity = new EntityWitherSkull(this.clientWorldController, d0, d1, d2, (double)packet.getSpeedX() / 8000.0D, (double)packet.getSpeedY() / 8000.0D, (double)packet.getSpeedZ() / 8000.0D);
            packet.func_149002_g(0);
        } else if (packet.getType() == 62) {
            entity = new EntityEgg(this.clientWorldController, d0, d1, d2);
        } else if (packet.getType() == 73) {
            entity = new EntityPotion(this.clientWorldController, d0, d1, d2, packet.func_149009_m());
            packet.func_149002_g(0);
        } else if (packet.getType() == 75) {
            entity = new EntityExpBottle(this.clientWorldController, d0, d1, d2);
            packet.func_149002_g(0);
        } else if (packet.getType() == 1) {
            entity = new EntityBoat(this.clientWorldController, d0, d1, d2);
        } else if (packet.getType() == 50) {
            entity = new EntityTNTPrimed(this.clientWorldController, d0, d1, d2, null);
        } else if (packet.getType() == 78) {
            entity = new EntityArmorStand(this.clientWorldController, d0, d1, d2);
        } else if (packet.getType() == 51) {
            entity = new EntityEnderCrystal(this.clientWorldController, d0, d1, d2);
        } else if (packet.getType() == 2) {
            entity = new EntityItem(this.clientWorldController, d0, d1, d2);
        } else if (packet.getType() == 70) {
            entity = new EntityFallingBlock(this.clientWorldController, d0, d1, d2, Block.getStateById(packet.func_149009_m() & 65535));
            packet.func_149002_g(0);
        }

        if (entity != null)
        {
            entity.serverPosX = packet.getX();
            entity.serverPosY = packet.getY();
            entity.serverPosZ = packet.getZ();
            entity.rotationPitch = (float)(packet.getPitch() * 360) / 256.0F;
            entity.rotationYaw = (float)(packet.getYaw() * 360) / 256.0F;
            Entity[] aentity = entity.getParts();

            if (aentity != null)
            {
                int i = packet.getEntityID() - entity.getEntityId();

                for (int j = 0; j < aentity.length; ++j)
                {
                    aentity[j].setEntityId(aentity[j].getEntityId() + i);
                }
            }

            entity.setEntityId(packet.getEntityID());
            this.clientWorldController.addEntityToWorld(packet.getEntityID(), entity);

            if (packet.func_149009_m() > 0)
            {
                if (packet.getType() == 60)
                {
                    Entity entity2 = this.clientWorldController.getEntityByID(packet.func_149009_m());

                    if (entity2 instanceof EntityLivingBase && entity instanceof EntityArrow)
                    {
                        ((EntityArrow)entity).shootingEntity = entity2;
                    }
                }

                entity.setVelocity((double)packet.getSpeedX() / 8000.0D, (double)packet.getSpeedY() / 8000.0D, (double)packet.getSpeedZ() / 8000.0D);
            }
        }
    }

    public void handleSpawnExperienceOrb(S11PacketSpawnExperienceOrb packet) {
        PacketThreadUtil.checkThreadAndEnqueue(packet, this, this.gameController);
        Entity entity = new EntityXPOrb(this.clientWorldController, (double)packet.getX() / 32.0D, (double)packet.getY() / 32.0D, (double)packet.getZ() / 32.0D, packet.getXPValue());
        entity.serverPosX = packet.getX();
        entity.serverPosY = packet.getY();
        entity.serverPosZ = packet.getZ();
        entity.rotationYaw = 0.0F;
        entity.rotationPitch = 0.0F;
        entity.setEntityId(packet.getEntityID());
        this.clientWorldController.addEntityToWorld(packet.getEntityID(), entity);
    }

    public void handleSpawnGlobalEntity(S2CPacketSpawnGlobalEntity packet) {
        PacketThreadUtil.checkThreadAndEnqueue(packet, this, this.gameController);
        double d0 = (double)packet.func_149051_d() / 32.0D;
        double d1 = (double)packet.func_149050_e() / 32.0D;
        double d2 = (double)packet.func_149049_f() / 32.0D;
        Entity entity = null;

        if (packet.func_149053_g() == 1) {
            entity = new EntityLightningBolt(this.clientWorldController, d0, d1, d2);
        }

        if (entity != null) {
            entity.serverPosX = packet.func_149051_d();
            entity.serverPosY = packet.func_149050_e();
            entity.serverPosZ = packet.func_149049_f();
            entity.rotationYaw = 0.0F;
            entity.rotationPitch = 0.0F;
            entity.setEntityId(packet.func_149052_c());
            this.clientWorldController.addWeatherEffect(entity);
        }
    }

    public void handleSpawnPainting(S10PacketSpawnPainting packet) {
        PacketThreadUtil.checkThreadAndEnqueue(packet, this, this.gameController);
        EntityPainting entitypainting = new EntityPainting(this.clientWorldController, packet.getPosition(), packet.getFacing(), packet.getTitle());
        this.clientWorldController.addEntityToWorld(packet.getEntityID(), entitypainting);
    }

    public void handleEntityVelocity(S12PacketEntityVelocity packet) {
        PacketThreadUtil.checkThreadAndEnqueue(packet, this, this.gameController);
        Entity entity = this.clientWorldController.getEntityByID(packet.getEntityID());

        if (entity != null) {
            entity.setVelocity((double)packet.getMotionX() / 8000.0D, (double)packet.getMotionY() / 8000.0D, (double)packet.getMotionZ() / 8000.0D);
        }
    }

    public void handleEntityMetadata(S1CPacketEntityMetadata packet) {
        PacketThreadUtil.checkThreadAndEnqueue(packet, this, this.gameController);
        Entity entity = this.clientWorldController.getEntityByID(packet.getEntityId());

        if (entity != null && packet.func_149376_c() != null) {
            entity.getDataWatcher().updateWatchedObjectsFromList(packet.func_149376_c());
        }
    }

    public void handleSpawnPlayer(S0CPacketSpawnPlayer packet) {
        PacketThreadUtil.checkThreadAndEnqueue(packet, this, this.gameController);
        double d0 = (double)packet.getX() / 32.0D;
        double d1 = (double)packet.getY() / 32.0D;
        double d2 = (double)packet.getZ() / 32.0D;
        float f = (float)(packet.getYaw() * 360) / 256.0F;
        float f1 = (float)(packet.getPitch() * 360) / 256.0F;
        EntityOtherPlayerMP entityotherplayermp = new EntityOtherPlayerMP(this.gameController.world, this.getPlayerInfo(packet.getPlayer()).getGameProfile());
        entityotherplayermp.prevPosX = entityotherplayermp.lastTickPosX = entityotherplayermp.serverPosX = packet.getX();
        entityotherplayermp.prevPosY = entityotherplayermp.lastTickPosY = entityotherplayermp.serverPosY = packet.getY();
        entityotherplayermp.prevPosZ = entityotherplayermp.lastTickPosZ = entityotherplayermp.serverPosZ = packet.getZ();
        int i = packet.getCurrentItemID();

        if (i == 0) {
            entityotherplayermp.inventory.mainInventory[entityotherplayermp.inventory.currentItem] = null;
        } else {
            entityotherplayermp.inventory.mainInventory[entityotherplayermp.inventory.currentItem] = new ItemStack(Item.getItemById(i), 1, 0);
        }

        entityotherplayermp.setPositionAndRotation(d0, d1, d2, f, f1);
        this.clientWorldController.addEntityToWorld(packet.getEntityID(), entityotherplayermp);
        List<DataWatcher.WatchableObject> list = packet.func_148944_c();

        if (list != null) {
            entityotherplayermp.getDataWatcher().updateWatchedObjectsFromList(list);
        }
    }

    public void handleEntityTeleport(S18PacketEntityTeleport packet) {
        PacketThreadUtil.checkThreadAndEnqueue(packet, this, this.gameController);
        Entity entity = this.clientWorldController.getEntityByID(packet.getEntityId());

        if (entity != null) {
            entity.serverPosX = packet.getX();
            entity.serverPosY = packet.getY();
            entity.serverPosZ = packet.getZ();
            double d0 = (double)entity.serverPosX / 32.0D;
            double d1 = (double)entity.serverPosY / 32.0D;
            double d2 = (double)entity.serverPosZ / 32.0D;
            float f = (float)(packet.getYaw() * 360) / 256.0F;
            float f1 = (float)(packet.getPitch() * 360) / 256.0F;

            if (Math.abs(entity.posX - d0) < 0.03125D && Math.abs(entity.posY - d1) < 0.015625D && Math.abs(entity.posZ - d2) < 0.03125D) {
                entity.setPositionAndRotation2(entity.posX, entity.posY, entity.posZ, f, f1, 3, true);
            } else {
                entity.setPositionAndRotation2(d0, d1, d2, f, f1, 3, true);
            }

            entity.onGround = packet.getOnGround();
        }
    }

    public void handleHeldItemChange(S09PacketHeldItemChange packet) {
        PacketThreadUtil.checkThreadAndEnqueue(packet, this, this.gameController);

        if (packet.getHeldItemHotbarIndex() >= 0 && packet.getHeldItemHotbarIndex() < InventoryPlayer.getHotbarSize()) {
            this.gameController.player.inventory.currentItem = packet.getHeldItemHotbarIndex();
        }
    }

    public void handleEntityMovement(S14PacketEntity packet) {
        PacketThreadUtil.checkThreadAndEnqueue(packet, this, this.gameController);
        Entity entity = packet.getEntity(this.clientWorldController);

        if (entity != null) {
            entity.serverPosX += packet.func_149062_c();
            entity.serverPosY += packet.func_149061_d();
            entity.serverPosZ += packet.func_149064_e();
            double d0 = (double)entity.serverPosX / 32.0D;
            double d1 = (double)entity.serverPosY / 32.0D;
            double d2 = (double)entity.serverPosZ / 32.0D;
            float f = packet.func_149060_h() ? (float)(packet.func_149066_f() * 360) / 256.0F : entity.rotationYaw;
            float f1 = packet.func_149060_h() ? (float)(packet.func_149063_g() * 360) / 256.0F : entity.rotationPitch;
            entity.setPositionAndRotation2(d0, d1, d2, f, f1, 3, false);
            entity.onGround = packet.getOnGround();
        }
    }

    public void handleEntityHeadLook(S19PacketEntityHeadLook packet) {
        PacketThreadUtil.checkThreadAndEnqueue(packet, this, this.gameController);
        Entity entity = packet.getEntity(this.clientWorldController);

        if (entity != null) {
            float f = (float)(packet.getYaw() * 360) / 256.0F;
            entity.setRotationYawHead(f);
        }
    }

    public void handleDestroyEntities(S13PacketDestroyEntities packet) {
        PacketThreadUtil.checkThreadAndEnqueue(packet, this, this.gameController);

        for (int i = 0; i < packet.getEntityIDs().length; ++i) {
            this.clientWorldController.removeEntityFromWorld(packet.getEntityIDs()[i]);
        }
    }

    public void handlePlayerPosLook(S08PacketPlayerPosLook packet) {
        PacketThreadUtil.checkThreadAndEnqueue(packet, this, this.gameController);
        EntityPlayer entityplayer = this.gameController.player;
        double d0 = packet.getX();
        double d1 = packet.getY();
        double d2 = packet.getZ();
        float f = packet.getYaw();
        float f1 = packet.getPitch();

        if (packet.func_179834_f().contains(S08PacketPlayerPosLook.EnumFlags.X)) {
            d0 += entityplayer.posX;
        } else {
            entityplayer.motionX = 0.0D;
        }

        if (packet.func_179834_f().contains(S08PacketPlayerPosLook.EnumFlags.Y)) {
            d1 += entityplayer.posY;
        } else {
            entityplayer.motionY = 0.0D;
        }

        if (packet.func_179834_f().contains(S08PacketPlayerPosLook.EnumFlags.Z)) {
            d2 += entityplayer.posZ;
        } else {
            entityplayer.motionZ = 0.0D;
        }

        if (packet.func_179834_f().contains(S08PacketPlayerPosLook.EnumFlags.X_ROT)) {
            f1 += entityplayer.rotationPitch;
        }

        if (packet.func_179834_f().contains(S08PacketPlayerPosLook.EnumFlags.Y_ROT)) {
            f += entityplayer.rotationYaw;
        }

        entityplayer.setPositionAndRotation(d0, d1, d2, f, f1);
        this.netManager.sendPacket(new C03PacketPlayer.C06PacketPlayerPosLook(entityplayer.posX, entityplayer.getEntityBoundingBox().minY, entityplayer.posZ, entityplayer.rotationYaw, entityplayer.rotationPitch, false));

        if (!this.doneLoadingTerrain) {
            this.gameController.player.prevPosX = this.gameController.player.posX;
            this.gameController.player.prevPosY = this.gameController.player.posY;
            this.gameController.player.prevPosZ = this.gameController.player.posZ;
            this.doneLoadingTerrain = true;
            this.gameController.displayGuiScreen(null);
        }
    }

    public void handleMultiBlockChange(S22PacketMultiBlockChange packet) {
        PacketThreadUtil.checkThreadAndEnqueue(packet, this, this.gameController);

        for (S22PacketMultiBlockChange.BlockUpdateData s22packetmultiblockchange$blockupdatedata : packet.getChangedBlocks()) {
            this.clientWorldController.invalidateRegionAndSetBlock(s22packetmultiblockchange$blockupdatedata.getPos(), s22packetmultiblockchange$blockupdatedata.getBlockState());
        }
    }

    public void handleChunkData(S21PacketChunkData packet) {
        PacketThreadUtil.checkThreadAndEnqueue(packet, this, this.gameController);

        if (packet.func_149274_i()) {
            if (packet.getExtractedSize() == 0) {
                this.clientWorldController.doPreChunk(packet.getChunkX(), packet.getChunkZ(), false);
                return;
            }

            this.clientWorldController.doPreChunk(packet.getChunkX(), packet.getChunkZ(), true);
        }

        this.clientWorldController.invalidateBlockReceiveRegion(packet.getChunkX() << 4, 0, packet.getChunkZ() << 4, (packet.getChunkX() << 4) + 15, 256, (packet.getChunkZ() << 4) + 15);
        Chunk chunk = this.clientWorldController.getChunkFromChunkCoords(packet.getChunkX(), packet.getChunkZ());
        chunk.fillChunk(packet.func_149272_d(), packet.getExtractedSize(), packet.func_149274_i());
        this.clientWorldController.markBlockRangeForRenderUpdate(packet.getChunkX() << 4, 0, packet.getChunkZ() << 4, (packet.getChunkX() << 4) + 15, 256, (packet.getChunkZ() << 4) + 15);

        if (!packet.func_149274_i() || !(this.clientWorldController.provider instanceof WorldProviderSurface)) {
            chunk.resetRelightChecks();
        }
    }

    public void handleBlockChange(S23PacketBlockChange packet) {
        PacketThreadUtil.checkThreadAndEnqueue(packet, this, this.gameController);
        this.clientWorldController.invalidateRegionAndSetBlock(packet.getBlockPosition(), packet.getBlockState());
    }

    public void handleDisconnect(S40PacketDisconnect packet) {
        this.netManager.closeChannel(packet.getReason());
    }

    public void onDisconnect(IChatComponent reason) {
        this.gameController.loadWorld(null);

        if (this.guiScreenServer != null) {
            this.gameController.displayGuiScreen(new DisconnectedScreen(this.guiScreenServer, "disconnect.lost", reason));
        } else {
            this.gameController.displayGuiScreen(new DisconnectedScreen(new MultiplayerScreen(new MainMenuScreen()), "disconnect.lost", reason));
        }
    }

    public void addToSendQueue(Packet packet) {
        this.netManager.sendPacket(packet);
    }

    public void handleCollectItem(S0DPacketCollectItem packet) {
        PacketThreadUtil.checkThreadAndEnqueue(packet, this, this.gameController);
        Entity entity = this.clientWorldController.getEntityByID(packet.getCollectedItemEntityID());
        EntityLivingBase entitylivingbase = (EntityLivingBase)this.clientWorldController.getEntityByID(packet.getEntityID());

        if (entitylivingbase == null) {
            entitylivingbase = this.gameController.player;
        }

        if (entity != null) {
            if (entity instanceof EntityXPOrb) {
                this.clientWorldController.playSoundAtEntity(entity, "random.orb", 0.2F, ((this.avRandomizer.nextFloat() - this.avRandomizer.nextFloat()) * 0.7F + 1.0F) * 2.0F);
            } else {
                this.clientWorldController.playSoundAtEntity(entity, "random.pop", 0.2F, ((this.avRandomizer.nextFloat() - this.avRandomizer.nextFloat()) * 0.7F + 1.0F) * 2.0F);
            }

            this.gameController.effectRenderer.addEffect(new EntityPickupFX(this.clientWorldController, entity, entitylivingbase, 0.5F));
            this.clientWorldController.removeEntityFromWorld(packet.getCollectedItemEntityID());
        }
    }

    public void handleChat(S02PacketChat packet) {
        PacketThreadUtil.checkThreadAndEnqueue(packet, this, this.gameController);

        if (packet.getType() == 2) {
            this.gameController.inGameScreen.setRecordPlaying(packet.getChatComponent().getUnformattedText(), false);
        } else {
            this.gameController.inGameScreen.getChatGUI().printChatMessage(packet.getChatComponent());
        }
    }

    public void handleAnimation(S0BPacketAnimation packet) {
        PacketThreadUtil.checkThreadAndEnqueue(packet, this, this.gameController);
        Entity entity = this.clientWorldController.getEntityByID(packet.getEntityID());

        if (entity != null) {
            if (packet.getAnimationType() == 0) {
                EntityLivingBase entitylivingbase = (EntityLivingBase)entity;
                entitylivingbase.swingItem();
            } else if (packet.getAnimationType() == 1) {
                entity.performHurtAnimation();
            } else if (packet.getAnimationType() == 2) {
                EntityPlayer entityplayer = (EntityPlayer)entity;
                entityplayer.wakeUpPlayer(false, false, false);
            } else if (packet.getAnimationType() == 4) {
                this.gameController.effectRenderer.emitParticleAtEntity(entity, EnumParticleTypes.CRIT);
            } else if (packet.getAnimationType() == 5) {
                this.gameController.effectRenderer.emitParticleAtEntity(entity, EnumParticleTypes.CRIT_MAGIC);
            }
        }
    }

    public void handleUseBed(S0APacketUseBed packet) {
        PacketThreadUtil.checkThreadAndEnqueue(packet, this, this.gameController);
        packet.getPlayer(this.clientWorldController).trySleep(packet.getBedPosition());
    }

    public void handleSpawnMob(S0FPacketSpawnMob packet) {
        PacketThreadUtil.checkThreadAndEnqueue(packet, this, this.gameController);
        double d0 = (double)packet.getX() / 32.0D;
        double d1 = (double)packet.getY() / 32.0D;
        double d2 = (double)packet.getZ() / 32.0D;
        float f = (float)(packet.getYaw() * 360) / 256.0F;
        float f1 = (float)(packet.getPitch() * 360) / 256.0F;
        EntityLivingBase entitylivingbase = (EntityLivingBase)EntityList.createEntityByID(packet.getEntityType(), this.gameController.world);
        entitylivingbase.serverPosX = packet.getX();
        entitylivingbase.serverPosY = packet.getY();
        entitylivingbase.serverPosZ = packet.getZ();
        entitylivingbase.renderYawOffset = entitylivingbase.rotationYawHead = (float)(packet.getHeadPitch() * 360) / 256.0F;
        Entity[] aentity = entitylivingbase.getParts();

        if (aentity != null) {
            int i = packet.getEntityID() - entitylivingbase.getEntityId();

            for (Entity entity : aentity) {
                entity.setEntityId(entity.getEntityId() + i);
            }
        }

        entitylivingbase.setEntityId(packet.getEntityID());
        entitylivingbase.setPositionAndRotation(d0, d1, d2, f, f1);
        entitylivingbase.motionX = (float)packet.getVelocityX() / 8000.0F;
        entitylivingbase.motionY = (float)packet.getVelocityY() / 8000.0F;
        entitylivingbase.motionZ = (float)packet.getVelocityZ() / 8000.0F;
        this.clientWorldController.addEntityToWorld(packet.getEntityID(), entitylivingbase);
        List<DataWatcher.WatchableObject> list = packet.func_149027_c();

        if (list != null) {
            entitylivingbase.getDataWatcher().updateWatchedObjectsFromList(list);
        }
    }

    public void handleTimeUpdate(S03PacketTimeUpdate packet) {
        PacketThreadUtil.checkThreadAndEnqueue(packet, this, this.gameController);
        this.gameController.world.setTotalWorldTime(packet.getTotalWorldTime());
        this.gameController.world.setWorldTime(packet.getWorldTime());
    }

    public void handleSpawnPosition(S05PacketSpawnPosition packet) {
        PacketThreadUtil.checkThreadAndEnqueue(packet, this, this.gameController);
        this.gameController.player.setSpawnPoint(packet.getSpawnPos(), true);
        this.gameController.world.getWorldInfo().setSpawn(packet.getSpawnPos());
    }

    public void handleEntityAttach(S1BPacketEntityAttach packet) {
        PacketThreadUtil.checkThreadAndEnqueue(packet, this, this.gameController);
        Entity entity = this.clientWorldController.getEntityByID(packet.getEntityId());
        Entity entity1 = this.clientWorldController.getEntityByID(packet.getVehicleEntityId());

        if (packet.getLeash() == 0) {
            boolean flag = false;

            if (packet.getEntityId() == this.gameController.player.getEntityId()) {
                entity = this.gameController.player;

                if (entity1 instanceof EntityBoat) {
                    ((EntityBoat)entity1).setIsBoatEmpty(false);
                }

                flag = entity.ridingEntity == null && entity1 != null;
            } else if (entity1 instanceof EntityBoat) {
                ((EntityBoat)entity1).setIsBoatEmpty(true);
            }

            if (entity == null) {
                return;
            }

            entity.mountEntity(entity1);

            if (flag) {
                GameSettings gamesettings = this.gameController.gameSettings;
                this.gameController.inGameScreen.setRecordPlaying(I18n.format("mount.onboard", GameSettings.getKeyDisplayString(gamesettings.keyBindSneak.getKeyCode())), false);
            }
        } else if (packet.getLeash() == 1 && entity instanceof EntityLiving) {
            if (entity1 != null) {
                ((EntityLiving)entity).setLeashedToEntity(entity1, false);
            } else {
                ((EntityLiving)entity).clearLeashed(false, false);
            }
        }
    }

    public void handleEntityStatus(S19PacketEntityStatus packet) {
        PacketThreadUtil.checkThreadAndEnqueue(packet, this, this.gameController);
        Entity entity = packet.getEntity(this.clientWorldController);

        if (entity != null) {
            if (packet.getOpCode() == 21) {
                this.gameController.getSoundHandler().playSound(new GuardianSound((EntityGuardian)entity));
            } else {
                entity.handleHealthUpdate(packet.getOpCode());
            }
        }
    }

    public void handleUpdateHealth(S06PacketUpdateHealth packet) {
        PacketThreadUtil.checkThreadAndEnqueue(packet, this, this.gameController);
        this.gameController.player.setPlayerSPHealth(packet.getHealth());
        this.gameController.player.getFoodStats().setFoodLevel(packet.getFoodLevel());
        this.gameController.player.getFoodStats().setFoodSaturationLevel(packet.getSaturationLevel());
    }

    public void handleSetExperience(S1FPacketSetExperience packet) {
        PacketThreadUtil.checkThreadAndEnqueue(packet, this, this.gameController);
        this.gameController.player.setXPStats(packet.func_149397_c(), packet.getTotalExperience(), packet.getLevel());
    }

    public void handleRespawn(S07PacketRespawn packet) {
        PacketThreadUtil.checkThreadAndEnqueue(packet, this, this.gameController);

        if (packet.getDimensionID() != this.gameController.player.dimension) {
            this.doneLoadingTerrain = false;
            Scoreboard scoreboard = this.clientWorldController.getScoreboard();
            this.clientWorldController = new WorldClient(this, new WorldSettings(0L, packet.getGameType(), false, this.gameController.world.getWorldInfo().isHardcoreModeEnabled(), packet.getWorldType()), packet.getDimensionID(), packet.getDifficulty(), this.gameController.mcProfiler);
            this.clientWorldController.setWorldScoreboard(scoreboard);
            this.gameController.loadWorld(this.clientWorldController);
            this.gameController.player.dimension = packet.getDimensionID();
            this.gameController.displayGuiScreen(new DownloadTerrainScreen(this));
        }

        this.gameController.setDimensionAndSpawnPlayer(packet.getDimensionID());
        this.gameController.playerController.setGameType(packet.getGameType());
    }

    public void handleExplosion(S27PacketExplosion packet) {
        PacketThreadUtil.checkThreadAndEnqueue(packet, this, this.gameController);
        Explosion explosion = new Explosion(this.gameController.world, null, packet.getX(), packet.getY(), packet.getZ(), packet.getStrength(), packet.getAffectedBlockPositions());
        explosion.doExplosionB(true);
        this.gameController.player.motionX += packet.func_149149_c();
        this.gameController.player.motionY += packet.func_149144_d();
        this.gameController.player.motionZ += packet.func_149147_e();
    }

    public void handleOpenWindow(S2DPacketOpenWindow packet) {
        PacketThreadUtil.checkThreadAndEnqueue(packet, this, this.gameController);
        EntityPlayerSP entityplayersp = this.gameController.player;

        if ("minecraft:container".equals(packet.getGuiId())) {
            entityplayersp.displayGUIChest(new InventoryBasic(packet.getWindowTitle(), packet.getSlotCount()));
            entityplayersp.openContainer.windowId = packet.getWindowId();
        } else if ("minecraft:villager".equals(packet.getGuiId())) {
            entityplayersp.displayVillagerTradeGui(new NpcMerchant(entityplayersp, packet.getWindowTitle()));
            entityplayersp.openContainer.windowId = packet.getWindowId();
        } else if ("EntityHorse".equals(packet.getGuiId())) {
            Entity entity = this.clientWorldController.getEntityByID(packet.getEntityId());

            if (entity instanceof EntityHorse) {
                entityplayersp.displayGUIHorse((EntityHorse)entity, new AnimalChest(packet.getWindowTitle(), packet.getSlotCount()));
                entityplayersp.openContainer.windowId = packet.getWindowId();
            }
        } else if (!packet.hasSlots()) {
            entityplayersp.displayGui(new LocalBlockIntercommunication(packet.getGuiId(), packet.getWindowTitle()));
            entityplayersp.openContainer.windowId = packet.getWindowId();
        } else {
            ContainerLocalMenu containerlocalmenu = new ContainerLocalMenu(packet.getGuiId(), packet.getWindowTitle(), packet.getSlotCount());
            entityplayersp.displayGUIChest(containerlocalmenu);
            entityplayersp.openContainer.windowId = packet.getWindowId();
        }
    }

    public void handleSetSlot(S2FPacketSetSlot packet) {
        PacketThreadUtil.checkThreadAndEnqueue(packet, this, this.gameController);
        EntityPlayer entityplayer = this.gameController.player;

        if (packet.func_149175_c() == -1) {
            entityplayer.inventory.setItemStack(packet.func_149174_e());
        } else {
            boolean flag = false;

            if (this.gameController.currentScreen instanceof GuiContainerCreative) {
                GuiContainerCreative guicontainercreative = (GuiContainerCreative)this.gameController.currentScreen;
                flag = guicontainercreative.getSelectedTabIndex() != CreativeTabs.tabInventory.getTabIndex();
            }

            if (packet.func_149175_c() == 0 && packet.func_149173_d() >= 36 && packet.func_149173_d() < 45) {
                ItemStack itemstack = entityplayer.inventoryContainer.getSlot(packet.func_149173_d()).getStack();

                if (packet.func_149174_e() != null && (itemstack == null || itemstack.stackSize < packet.func_149174_e().stackSize)) {
                    packet.func_149174_e().animationsToGo = 5;
                }

                entityplayer.inventoryContainer.putStackInSlot(packet.func_149173_d(), packet.func_149174_e());
            } else if (packet.func_149175_c() == entityplayer.openContainer.windowId && (packet.func_149175_c() != 0 || !flag)) {
                entityplayer.openContainer.putStackInSlot(packet.func_149173_d(), packet.func_149174_e());
            }
        }
    }

    public void handleConfirmTransaction(S32PacketConfirmTransaction packet) {
        PacketThreadUtil.checkThreadAndEnqueue(packet, this, this.gameController);
        Container container = null;
        EntityPlayer entityplayer = this.gameController.player;

        if (packet.getWindowId() == 0) {
            container = entityplayer.inventoryContainer;
        } else if (packet.getWindowId() == entityplayer.openContainer.windowId) {
            container = entityplayer.openContainer;
        }

        if (container != null && !packet.func_148888_e()) {
            this.addToSendQueue(new C0FPacketConfirmTransaction(packet.getWindowId(), packet.getActionNumber(), true));
        }
    }

    public void handleWindowItems(S30PacketWindowItems packet) {
        PacketThreadUtil.checkThreadAndEnqueue(packet, this, this.gameController);
        EntityPlayer entityplayer = this.gameController.player;

        if (packet.func_148911_c() == 0) {
            entityplayer.inventoryContainer.putStacksInSlots(packet.getItemStacks());
        } else if (packet.func_148911_c() == entityplayer.openContainer.windowId) {
            entityplayer.openContainer.putStacksInSlots(packet.getItemStacks());
        }
    }

    public void handleSignEditorOpen(S36PacketSignEditorOpen packet) {
        PacketThreadUtil.checkThreadAndEnqueue(packet, this, this.gameController);
        TileEntity tileentity = this.clientWorldController.getTileEntity(packet.getSignPosition());

        if (!(tileentity instanceof TileEntitySign)) {
            tileentity = new TileEntitySign();
            tileentity.setWorldObj(this.clientWorldController);
            tileentity.setPos(packet.getSignPosition());
        }

        this.gameController.player.openEditSign((TileEntitySign)tileentity);
    }

    public void handleUpdateSign(S33PacketUpdateSign packet) {
        PacketThreadUtil.checkThreadAndEnqueue(packet, this, this.gameController);
        boolean flag = false;

        if (this.gameController.world.isBlockLoaded(packet.getPos())) {
            TileEntity tileentity = this.gameController.world.getTileEntity(packet.getPos());

            if (tileentity instanceof TileEntitySign) {
                TileEntitySign tileentitysign = (TileEntitySign)tileentity;

                if (tileentitysign.getIsEditable()) {
                    System.arraycopy(packet.getLines(), 0, tileentitysign.signText, 0, 4);
                    tileentitysign.markDirty();
                }

                flag = true;
            }
        }

        if (!flag && this.gameController.player != null) {
            this.gameController.player.addChatMessage(new ChatComponentText("Unable to locate sign at " + packet.getPos().getX() + ", " + packet.getPos().getY() + ", " + packet.getPos().getZ()));
        }
    }

    public void handleUpdateTileEntity(S35PacketUpdateTileEntity packet) {
        PacketThreadUtil.checkThreadAndEnqueue(packet, this, this.gameController);

        if (this.gameController.world.isBlockLoaded(packet.getPos())) {
            TileEntity tileentity = this.gameController.world.getTileEntity(packet.getPos());
            int i = packet.getTileEntityType();

            if (i == 1 && tileentity instanceof TileEntityMobSpawner || i == 2 && tileentity instanceof TileEntityCommandBlock || i == 3 && tileentity instanceof TileEntityBeacon || i == 4 && tileentity instanceof TileEntitySkull || i == 5 && tileentity instanceof TileEntityFlowerPot || i == 6 && tileentity instanceof TileEntityBanner) {
                tileentity.readFromNBT(packet.getNbtCompound());
            }
        }
    }

    public void handleWindowProperty(S31PacketWindowProperty packet) {
        PacketThreadUtil.checkThreadAndEnqueue(packet, this, this.gameController);
        EntityPlayer entityplayer = this.gameController.player;

        if (entityplayer.openContainer != null && entityplayer.openContainer.windowId == packet.getWindowId()) {
            entityplayer.openContainer.updateProgressBar(packet.getVarIndex(), packet.getVarValue());
        }
    }

    public void handleEntityEquipment(S04PacketEntityEquipment packetIn) {
        PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.gameController);
        Entity entity = this.clientWorldController.getEntityByID(packetIn.getEntityID());

        if (entity != null) {
            entity.setCurrentItemOrArmor(packetIn.getEquipmentSlot(), packetIn.getItemStack());
        }
    }

    public void handleCloseWindow(S2EPacketCloseWindow packet) {
        PacketThreadUtil.checkThreadAndEnqueue(packet, this, this.gameController);
        this.gameController.player.closeScreenAndDropStack();
    }

    public void handleBlockAction(S24PacketBlockAction packet) {
        PacketThreadUtil.checkThreadAndEnqueue(packet, this, this.gameController);
        this.gameController.world.addBlockEvent(packet.getBlockPosition(), packet.getBlockType(), packet.getData1(), packet.getData2());
    }

    public void handleBlockBreakAnim(S25PacketBlockBreakAnim packet) {
        PacketThreadUtil.checkThreadAndEnqueue(packet, this, this.gameController);
        this.gameController.world.sendBlockBreakProgress(packet.getBreakerId(), packet.getPosition(), packet.getProgress());
    }

    public void handleMapChunkBulk(S26PacketMapChunkBulk packet) {
        PacketThreadUtil.checkThreadAndEnqueue(packet, this, this.gameController);

        for (int i = 0; i < packet.getChunkCount(); ++i) {
            int j = packet.getChunkX(i);
            int k = packet.getChunkZ(i);
            this.clientWorldController.doPreChunk(j, k, true);
            this.clientWorldController.invalidateBlockReceiveRegion(j << 4, 0, k << 4, (j << 4) + 15, 256, (k << 4) + 15);
            Chunk chunk = this.clientWorldController.getChunkFromChunkCoords(j, k);
            chunk.fillChunk(packet.getChunkBytes(i), packet.getChunkSize(i), true);
            this.clientWorldController.markBlockRangeForRenderUpdate(j << 4, 0, k << 4, (j << 4) + 15, 256, (k << 4) + 15);

            if (!(this.clientWorldController.provider instanceof WorldProviderSurface)) {
                chunk.resetRelightChecks();
            }
        }
    }

    public void handleChangeGameState(S2BPacketChangeGameState packet) {
        PacketThreadUtil.checkThreadAndEnqueue(packet, this, this.gameController);
        EntityPlayer entityplayer = this.gameController.player;
        int i = packet.getGameState();
        float f = packet.func_149137_d();
        int j = MathHelper.floor_float(f + 0.5F);

        if (i >= 0 && i < S2BPacketChangeGameState.MESSAGE_NAMES.length && S2BPacketChangeGameState.MESSAGE_NAMES[i] != null) {
            entityplayer.addChatComponentMessage(new ChatComponentTranslation(S2BPacketChangeGameState.MESSAGE_NAMES[i]));
        }

        if (i == 1) {
            this.clientWorldController.getWorldInfo().setRaining(true);
            this.clientWorldController.setRainStrength(0.0F);
        } else if (i == 2) {
            this.clientWorldController.getWorldInfo().setRaining(false);
            this.clientWorldController.setRainStrength(1.0F);
        } else if (i == 3) {
            this.gameController.playerController.setGameType(WorldSettings.GameType.getByID(j));
        } else if (i == 4) {
            this.gameController.displayGuiScreen(new GuiWinGame());
        } else if (i == 6) {
            this.clientWorldController.playSound(entityplayer.posX, entityplayer.posY + (double)entityplayer.getEyeHeight(), entityplayer.posZ, "random.successful_hit", 0.18F, 0.45F, false);
        } else if (i == 7) {
            this.clientWorldController.setRainStrength(f);
        } else if (i == 8) {
            this.clientWorldController.setThunderStrength(f);
        } else if (i == 10) {
            this.clientWorldController.spawnParticle(EnumParticleTypes.MOB_APPEARANCE, entityplayer.posX, entityplayer.posY, entityplayer.posZ, 0.0D, 0.0D, 0.0D);
            this.clientWorldController.playSound(entityplayer.posX, entityplayer.posY, entityplayer.posZ, "mob.guardian.curse", 1.0F, 1.0F, false);
        }
    }

    public void handleMaps(S34PacketMaps packet) {
        PacketThreadUtil.checkThreadAndEnqueue(packet, this, this.gameController);
        MapData mapdata = ItemMap.loadMapData(packet.getMapId(), this.gameController.world);
        packet.setMapdataTo(mapdata);
        this.gameController.entityRenderer.getMapItemRenderer().updateMapTexture(mapdata);
    }

    public void handleEffect(S28PacketEffect packet) {
        PacketThreadUtil.checkThreadAndEnqueue(packet, this, this.gameController);

        if (packet.isSoundServerwide()) {
            this.gameController.world.playBroadcastSound(packet.getSoundType(), packet.getSoundPos(), packet.getSoundData());
        } else {
            this.gameController.world.playAuxSFX(packet.getSoundType(), packet.getSoundPos(), packet.getSoundData());
        }
    }

    public void handleStatistics(S37PacketStatistics packet) {
        PacketThreadUtil.checkThreadAndEnqueue(packet, this, this.gameController);
        boolean flag = false;

        for (Entry<StatBase, Integer> entry : packet.func_148974_c().entrySet()) {
            StatBase statbase = entry.getKey();
            int i = entry.getValue().intValue();

            if (statbase.isAchievement() && i > 0) {
                if (this.field_147308_k && this.gameController.player.getStatFileWriter().readStat(statbase) == 0) {
                    Achievement achievement = (Achievement)statbase;
                    this.gameController.guiAchievement.displayAchievement(achievement);

                    if (statbase == AchievementList.openInventory) {
                        this.gameController.gameSettings.showInventoryAchievementHint = false;
                        this.gameController.gameSettings.saveOptions();
                    }
                }

                flag = true;
            }

            this.gameController.player.getStatFileWriter().unlockAchievement(this.gameController.player, statbase, i);
        }

        if (!this.field_147308_k && !flag && this.gameController.gameSettings.showInventoryAchievementHint) {
            this.gameController.guiAchievement.displayUnformattedAchievement(AchievementList.openInventory);
        }

        this.field_147308_k = true;

        if (this.gameController.currentScreen instanceof IProgressMeter) {
            ((IProgressMeter)this.gameController.currentScreen).doneLoading();
        }
    }

    public void handleEntityEffect(S1DPacketEntityEffect packet) {
        PacketThreadUtil.checkThreadAndEnqueue(packet, this, this.gameController);
        Entity entity = this.clientWorldController.getEntityByID(packet.getEntityId());

        if (entity instanceof EntityLivingBase) {
            PotionEffect effect = new PotionEffect(packet.getEffectId(), packet.getDuration(), packet.getAmplifier(), false, packet.func_179707_f());
            effect.setPotionDurationMax(packet.func_149429_c());
            ((EntityLivingBase)entity).addPotionEffect(effect);
        }
    }

    /**
     * TODO Chocolate
     */
    public void handleCombatEvent(S42PacketCombatEvent packet) {
        PacketThreadUtil.checkThreadAndEnqueue(packet, this, this.gameController);
        Entity entity = this.clientWorldController.getEntityByID(packet.field_179775_c);
        EntityLivingBase entitylivingbase = entity instanceof EntityLivingBase ? (EntityLivingBase)entity : null;

        if (packet.eventType == S42PacketCombatEvent.Event.END_COMBAT) {
            long i = 1000 * packet.field_179772_d / 20;
        } else if (packet.eventType == S42PacketCombatEvent.Event.ENTITY_DIED) {
            Entity entity1 = this.clientWorldController.getEntityByID(packet.field_179774_b);

            if (entity1 instanceof EntityPlayer) {}
        }
    }

    public void handleServerDifficulty(S41PacketServerDifficulty packet) {
        PacketThreadUtil.checkThreadAndEnqueue(packet, this, this.gameController);
        this.gameController.world.getWorldInfo().setDifficulty(packet.getDifficulty());
        this.gameController.world.getWorldInfo().setDifficultyLocked(packet.isDifficultyLocked());
    }

    public void handleCamera(S43PacketCamera packet) {
        PacketThreadUtil.checkThreadAndEnqueue(packet, this, this.gameController);
        Entity entity = packet.getEntity(this.clientWorldController);

        if (entity != null) {
            this.gameController.setRenderViewEntity(entity);
        }
    }

    public void handleWorldBorder(S44PacketWorldBorder packet) {
        PacketThreadUtil.checkThreadAndEnqueue(packet, this, this.gameController);
        packet.func_179788_a(this.clientWorldController.getWorldBorder());
    }

    public void handleTitle(S45PacketTitle packet) {
        PacketThreadUtil.checkThreadAndEnqueue(packet, this, this.gameController);
        S45PacketTitle.Type type = packet.getType();
        String s = null, s1 = null, s2 = packet.getMessage() != null ? packet.getMessage().getFormattedText() : "";

        switch (type) {
            case TITLE:
                s = s2;
                break;
            case SUBTITLE:
                s1 = s2;
                break;
            case RESET:
                this.gameController.inGameScreen.displayTitle("", "", -1, -1, -1);
                this.gameController.inGameScreen.resetTitleTicks();
                return;
        }

        this.gameController.inGameScreen.displayTitle(s, s1, packet.getFadeInTime(), packet.getDisplayTime(), packet.getFadeOutTime());
    }

    public void handleSetCompressionLevel(S46PacketSetCompressionLevel packet) {
        if (!this.netManager.isLocalChannel()) {
            this.netManager.setCompressionTreshold(packet.func_179760_a());
        }
    }

    public void handlePlayerListHeaderFooter(S47PacketPlayerListHeaderFooter packet) {
        this.gameController.inGameScreen.getTabList().setHeader(packet.getHeader().getFormattedText().length() == 0 ? null : packet.getHeader());
        this.gameController.inGameScreen.getTabList().setFooter(packet.getFooter().getFormattedText().length() == 0 ? null : packet.getFooter());
    }

    public void handleRemoveEntityEffect(S1EPacketRemoveEntityEffect packet) {
        PacketThreadUtil.checkThreadAndEnqueue(packet, this, this.gameController);
        Entity entity = this.clientWorldController.getEntityByID(packet.getEntityId());

        if (entity instanceof EntityLivingBase) {
            ((EntityLivingBase)entity).removePotionEffectClient(packet.getEffectId());
        }
    }

    public void handlePlayerListItem(S38PacketPlayerListItem packet) {
        PacketThreadUtil.checkThreadAndEnqueue(packet, this, this.gameController);

        for (S38PacketPlayerListItem.AddPlayerData data : packet.func_179767_a()) {
            if (packet.func_179768_b() == S38PacketPlayerListItem.Action.REMOVE_PLAYER) {
                this.playerInfoMap.remove(data.getProfile().getId());
            } else {
                NetworkPlayerInfo info = this.playerInfoMap.get(data.getProfile().getId());

                if (packet.func_179768_b() == S38PacketPlayerListItem.Action.ADD_PLAYER) {
                    info = new NetworkPlayerInfo(data);
                    this.playerInfoMap.put(info.getGameProfile().getId(), info);
                }

                if (info != null) {
                    switch (packet.func_179768_b()) {
                        case ADD_PLAYER:
                            info.setGameType(data.getGameMode());
                            info.setResponseTime(data.getPing());
                            break;
                        case UPDATE_GAME_MODE:
                            info.setGameType(data.getGameMode());
                            break;
                        case UPDATE_LATENCY:
                            info.setResponseTime(data.getPing());
                            break;
                        case UPDATE_DISPLAY_NAME:
                            info.setDisplayName(data.getDisplayName());
                    }
                }
            }
        }
    }

    public void handleKeepAlive(S00PacketKeepAlive packet) {
        this.addToSendQueue(new C00PacketKeepAlive(packet.func_149134_c()));
    }

    public void handlePlayerAbilities(S39PacketPlayerAbilities packet) {
        PacketThreadUtil.checkThreadAndEnqueue(packet, this, this.gameController);
        EntityPlayer entityplayer = this.gameController.player;
        entityplayer.capabilities.isFlying = packet.isFlying();
        entityplayer.capabilities.isCreativeMode = packet.isCreativeMode();
        entityplayer.capabilities.disableDamage = packet.isInvulnerable();
        entityplayer.capabilities.allowFlying = packet.isAllowFlying();
        entityplayer.capabilities.setFlySpeed(packet.getFlySpeed());
        entityplayer.capabilities.setPlayerWalkSpeed(packet.getWalkSpeed());
    }

    public void handleTabComplete(S3APacketTabComplete packet) {
        PacketThreadUtil.checkThreadAndEnqueue(packet, this, this.gameController);
        String[] astring = packet.func_149630_c();

        if (this.gameController.currentScreen instanceof ChatScreen) {
            ChatScreen chatScreen = (ChatScreen)this.gameController.currentScreen;
            chatScreen.onAutocompleteResponse(astring);
        }
    }

    public void handleSoundEffect(S29PacketSoundEffect packet) {
        PacketThreadUtil.checkThreadAndEnqueue(packet, this, this.gameController);
        this.gameController.world.playSound(packet.getX(), packet.getY(), packet.getZ(), packet.getSoundName(), packet.getVolume(), packet.getPitch(), false);
    }

    public void handleResourcePack(S48PacketResourcePackSend packet) {
        final String url = packet.getURL();
        final String hash = packet.getHash();

        if (url.startsWith("level://")) {
            String s2 = url.substring("level://".length());
            File file1 = new File(this.gameController.dataDir, "saves");
            File file2 = new File(file1, s2);

            if (file2.isFile()) {
                this.netManager.sendPacket(new C19PacketResourcePackStatus(hash, C19PacketResourcePackStatus.Action.ACCEPTED));
                Futures.addCallback(this.gameController.getResourcePackRepository().setResourcePackInstance(file2), new FutureCallback<Object>() {
                    public void onSuccess(Object p_onSuccess_1_) {
                        NetHandlerPlayClient.this.netManager.sendPacket(new C19PacketResourcePackStatus(hash, C19PacketResourcePackStatus.Action.SUCCESSFULLY_LOADED));
                    }

                    public void onFailure(Throwable p_onFailure_1_) {
                        NetHandlerPlayClient.this.netManager.sendPacket(new C19PacketResourcePackStatus(hash, C19PacketResourcePackStatus.Action.FAILED_DOWNLOAD));
                    }
                });
            } else {
                this.netManager.sendPacket(new C19PacketResourcePackStatus(hash, C19PacketResourcePackStatus.Action.FAILED_DOWNLOAD));
            }
        } else {
            if (this.gameController.getCurrentServerData() != null && this.gameController.getCurrentServerData().getResourceMode() == ServerData.ServerResourceMode.ENABLED) {
                this.netManager.sendPacket(new C19PacketResourcePackStatus(hash, C19PacketResourcePackStatus.Action.ACCEPTED));
                Futures.addCallback(this.gameController.getResourcePackRepository().downloadResourcePack(url, hash), new FutureCallback<Object>() {
                    public void onSuccess(Object result) {
                        NetHandlerPlayClient.this.netManager.sendPacket(new C19PacketResourcePackStatus(hash, C19PacketResourcePackStatus.Action.SUCCESSFULLY_LOADED));
                    }

                    public void onFailure(Throwable throwable) {
                        NetHandlerPlayClient.this.netManager.sendPacket(new C19PacketResourcePackStatus(hash, C19PacketResourcePackStatus.Action.FAILED_DOWNLOAD));
                    }
                });
            } else if (this.gameController.getCurrentServerData() != null && this.gameController.getCurrentServerData().getResourceMode() != ServerData.ServerResourceMode.PROMPT) {
                this.netManager.sendPacket(new C19PacketResourcePackStatus(hash, C19PacketResourcePackStatus.Action.DECLINED));
            } else {
                this.gameController.addScheduledTask(() -> NetHandlerPlayClient.this.gameController.displayGuiScreen(new YesNoScreen((result, id) -> {
                    NetHandlerPlayClient.this.gameController = Minecraft.getMinecraft();

                    if (result) {
                        if (NetHandlerPlayClient.this.gameController.getCurrentServerData() != null) {
                            NetHandlerPlayClient.this.gameController.getCurrentServerData().setResourceMode(ServerData.ServerResourceMode.ENABLED);
                        }

                        NetHandlerPlayClient.this.netManager.sendPacket(new C19PacketResourcePackStatus(hash, C19PacketResourcePackStatus.Action.ACCEPTED));
                        Futures.addCallback(NetHandlerPlayClient.this.gameController.getResourcePackRepository().downloadResourcePack(url, hash), new FutureCallback<Object>() {
                            public void onSuccess(Object result) {
                                NetHandlerPlayClient.this.netManager.sendPacket(new C19PacketResourcePackStatus(hash, C19PacketResourcePackStatus.Action.SUCCESSFULLY_LOADED));
                            }

                            public void onFailure(Throwable throwable) {
                                NetHandlerPlayClient.this.netManager.sendPacket(new C19PacketResourcePackStatus(hash, C19PacketResourcePackStatus.Action.FAILED_DOWNLOAD));
                            }
                        });
                    } else {
                        if (NetHandlerPlayClient.this.gameController.getCurrentServerData() != null) {
                            NetHandlerPlayClient.this.gameController.getCurrentServerData().setResourceMode(ServerData.ServerResourceMode.DISABLED);
                        }

                        NetHandlerPlayClient.this.netManager.sendPacket(new C19PacketResourcePackStatus(hash, C19PacketResourcePackStatus.Action.DECLINED));
                    }

                    ServerList.func_147414_b(NetHandlerPlayClient.this.gameController.getCurrentServerData());
                    NetHandlerPlayClient.this.gameController.displayGuiScreen(null);
                }, I18n.format("multiplayer.texturePrompt.line1"), I18n.format("multiplayer.texturePrompt.line2"), 0)));
            }
        }
    }

    public void handleEntityNBT(S49PacketUpdateEntityNBT packet) {
        PacketThreadUtil.checkThreadAndEnqueue(packet, this, this.gameController);
        Entity entity = packet.getEntity(this.clientWorldController);

        if (entity != null) {
            entity.clientUpdateEntityNBT(packet.getTagCompound());
        }
    }

    public void handleCustomPayload(S3FPacketCustomPayload packet) {
        PacketThreadUtil.checkThreadAndEnqueue(packet, this, this.gameController);

        if ("MC|TrList".equals(packet.getChannelName())) {
            PacketBuffer packetbuffer = packet.getBufferData();

            try {
                int i = packetbuffer.readInt();
                Screen screen = this.gameController.currentScreen;

                if (screen instanceof GuiMerchant && i == this.gameController.player.openContainer.windowId) {
                    IMerchant imerchant = ((GuiMerchant)screen).getMerchant();
                    MerchantRecipeList merchantrecipelist = MerchantRecipeList.readFromBuf(packetbuffer);
                    imerchant.setRecipes(merchantrecipelist);
                }
            } catch (IOException exception) {
                logger.error("Couldn't load trade info", exception);
            } finally {
                packetbuffer.release();
            }
        } else if ("MC|Brand".equals(packet.getChannelName())) {
            this.gameController.player.setClientBrand(packet.getBufferData().readStringFromBuffer(32767));
        } else if ("MC|BOpen".equals(packet.getChannelName())) {
            ItemStack itemstack = this.gameController.player.getCurrentEquippedItem();

            if (itemstack != null && itemstack.getItem() == Items.written_book) {
                this.gameController.displayGuiScreen(new GuiScreenBook(this.gameController.player, itemstack, false));
            }
        }
    }

    public void handleScoreboardObjective(S3BPacketScoreboardObjective packet) {
        PacketThreadUtil.checkThreadAndEnqueue(packet, this, this.gameController);
        Scoreboard scoreboard = this.clientWorldController.getScoreboard();

        if (packet.func_149338_e() == 0) {
            ScoreObjective scoreobjective = scoreboard.addScoreObjective(packet.func_149339_c(), IScoreObjectiveCriteria.DUMMY);
            scoreobjective.setDisplayName(packet.func_149337_d());
            scoreobjective.setRenderType(packet.func_179817_d());
        } else {
            ScoreObjective scoreobjective1 = scoreboard.getObjective(packet.func_149339_c());

            if (packet.func_149338_e() == 1) {
                scoreboard.removeObjective(scoreobjective1);
            } else if (packet.func_149338_e() == 2) {
                scoreobjective1.setDisplayName(packet.func_149337_d());
                scoreobjective1.setRenderType(packet.func_179817_d());
            }
        }
    }

    public void handleUpdateScore(S3CPacketUpdateScore packet) {
        PacketThreadUtil.checkThreadAndEnqueue(packet, this, this.gameController);
        Scoreboard scoreboard = this.clientWorldController.getScoreboard();
        ScoreObjective scoreobjective = scoreboard.getObjective(packet.getObjectiveName());

        if (packet.getScoreAction() == S3CPacketUpdateScore.Action.CHANGE) {
            Score score = scoreboard.getValueFromObjective(packet.getPlayerName(), scoreobjective);
            score.setScorePoints(packet.getScoreValue());
        } else if (packet.getScoreAction() == S3CPacketUpdateScore.Action.REMOVE) {
            if (StringUtils.isNullOrEmpty(packet.getObjectiveName())) {
                scoreboard.removeObjectiveFromEntity(packet.getPlayerName(), null);
            } else if (scoreobjective != null) {
                scoreboard.removeObjectiveFromEntity(packet.getPlayerName(), scoreobjective);
            }
        }
    }

    public void handleDisplayScoreboard(S3DPacketDisplayScoreboard packet) {
        PacketThreadUtil.checkThreadAndEnqueue(packet, this, this.gameController);
        Scoreboard scoreboard = this.clientWorldController.getScoreboard();

        if (packet.func_149370_d().length() == 0) {
            scoreboard.setObjectiveInDisplaySlot(packet.func_149371_c(), null);
        } else {
            ScoreObjective scoreobjective = scoreboard.getObjective(packet.func_149370_d());
            scoreboard.setObjectiveInDisplaySlot(packet.func_149371_c(), scoreobjective);
        }
    }

    public void handleTeams(S3EPacketTeams packet) {
        PacketThreadUtil.checkThreadAndEnqueue(packet, this, this.gameController);
        Scoreboard scoreboard = this.clientWorldController.getScoreboard();
        ScorePlayerTeam scoreplayerteam;

        if (packet.func_149307_h() == 0) {
            scoreplayerteam = scoreboard.createTeam(packet.func_149312_c());
        } else {
            scoreplayerteam = scoreboard.getTeam(packet.func_149312_c());
        }

        if (packet.func_149307_h() == 0 || packet.func_149307_h() == 2) {
            scoreplayerteam.setTeamName(packet.func_149306_d());
            scoreplayerteam.setNamePrefix(packet.func_149311_e());
            scoreplayerteam.setNameSuffix(packet.func_149309_f());
            scoreplayerteam.setChatFormat(EnumChatFormatting.func_175744_a(packet.func_179813_h()));
            scoreplayerteam.func_98298_a(packet.func_149308_i());
            Team.EnumVisible visible = Team.EnumVisible.func_178824_a(packet.func_179814_i());

            if (visible != null) {
                scoreplayerteam.setNameTagVisibility(visible);
            }
        }

        if (packet.func_149307_h() == 0 || packet.func_149307_h() == 3) {
            for (String s : packet.func_149310_g()) {
                scoreboard.addPlayerToTeam(s, packet.func_149312_c());
            }
        }

        if (packet.func_149307_h() == 4) {
            for (String s1 : packet.func_149310_g()) {
                scoreboard.removePlayerFromTeam(s1, scoreplayerteam);
            }
        }

        if (packet.func_149307_h() == 1) {
            scoreboard.removeTeam(scoreplayerteam);
        }
    }

    public void handleParticles(S2APacketParticles packetIn) {
        PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.gameController);

        if (packetIn.getParticleCount() == 0) {
            double d0 = packetIn.getParticleSpeed() * packetIn.getXOffset();
            double d2 = packetIn.getParticleSpeed() * packetIn.getYOffset();
            double d4 = packetIn.getParticleSpeed() * packetIn.getZOffset();

            try {
                this.clientWorldController.spawnParticle(packetIn.getParticleType(), packetIn.isLongDistance(), packetIn.getXCoordinate(), packetIn.getYCoordinate(), packetIn.getZCoordinate(), d0, d2, d4, packetIn.getParticleArgs());
            } catch (Throwable var17) {
                logger.warn("Could not spawn particle effect " + packetIn.getParticleType());
            }
        } else {
            for (int i = 0; i < packetIn.getParticleCount(); ++i) {
                double d1 = this.avRandomizer.nextGaussian() * (double)packetIn.getXOffset();
                double d3 = this.avRandomizer.nextGaussian() * (double)packetIn.getYOffset();
                double d5 = this.avRandomizer.nextGaussian() * (double)packetIn.getZOffset();
                double d6 = this.avRandomizer.nextGaussian() * (double)packetIn.getParticleSpeed();
                double d7 = this.avRandomizer.nextGaussian() * (double)packetIn.getParticleSpeed();
                double d8 = this.avRandomizer.nextGaussian() * (double)packetIn.getParticleSpeed();

                try {
                    this.clientWorldController.spawnParticle(packetIn.getParticleType(), packetIn.isLongDistance(), packetIn.getXCoordinate() + d1, packetIn.getYCoordinate() + d3, packetIn.getZCoordinate() + d5, d6, d7, d8, packetIn.getParticleArgs());
                } catch (Throwable var16) {
                    logger.warn("Could not spawn particle effect " + packetIn.getParticleType());
                    return;
                }
            }
        }
    }

    public void handleEntityProperties(S20PacketEntityProperties packetIn) {
        PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.gameController);
        Entity entity = this.clientWorldController.getEntityByID(packetIn.getEntityId());

        if (entity != null) {
            if (!(entity instanceof EntityLivingBase)) {
                throw new IllegalStateException("Server tried to update attributes of a non-living entity (actually: " + entity + ")");
            } else {
                BaseAttributeMap baseattributemap = ((EntityLivingBase)entity).getAttributeMap();

                for (S20PacketEntityProperties.Snapshot snapshot : packetIn.func_149441_d()) {
                    IAttributeInstance iattributeinstance = baseattributemap.getAttributeInstanceByName(snapshot.func_151409_a());

                    if (iattributeinstance == null) {
                        iattributeinstance = baseattributemap.registerAttribute(new RangedAttribute(null, snapshot.func_151409_a(), 0.0D, 2.2250738585072014E-308D, Double.MAX_VALUE));
                    }

                    iattributeinstance.setBaseValue(snapshot.func_151410_b());
                    iattributeinstance.removeAllModifiers();

                    for (AttributeModifier attributemodifier : snapshot.func_151408_c()) {
                        iattributeinstance.applyModifier(attributemodifier);
                    }
                }
            }
        }
    }

    public NetworkManager getNetworkManager() {
        return this.netManager;
    }

    public Collection<NetworkPlayerInfo> getPlayerInfoMap() {
        return this.playerInfoMap.values();
    }

    public NetworkPlayerInfo getPlayerInfo(UUID p_175102_1_) {
        return this.playerInfoMap.get(p_175102_1_);
    }

    public NetworkPlayerInfo getPlayerInfo(String name) {
        for (NetworkPlayerInfo info : this.playerInfoMap.values()) {
            if (info.getGameProfile().getName().equals(name)) {
                return info;
            }
        }
        return null;
    }

    public GameProfile getGameProfile() {
        return this.profile;
    }

}
