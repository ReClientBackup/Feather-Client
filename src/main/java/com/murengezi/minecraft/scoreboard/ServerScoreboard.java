package com.murengezi.minecraft.scoreboard;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S3BPacketScoreboardObjective;
import net.minecraft.network.play.server.S3CPacketUpdateScore;
import net.minecraft.network.play.server.S3DPacketDisplayScoreboard;
import net.minecraft.network.play.server.S3EPacketTeams;
import net.minecraft.server.MinecraftServer;

public class ServerScoreboard extends Scoreboard {

    private final MinecraftServer server;
    private final Set<ScoreObjective> objectives = Sets.newHashSet();
    private ScoreboardSaveData scoreboardSaveData;

    public ServerScoreboard(MinecraftServer mcServer)
    {
        this.server = mcServer;
    }

    public void func_96536_a(Score score) {
        super.func_96536_a(score);

        if (this.objectives.contains(score.getObjective())) {
            this.server.getConfigurationManager().sendPacketToAllPlayers(new S3CPacketUpdateScore(score));
        }

        this.func_96551_b();
    }

    public void func_96516_a(String p_96516_1_) {
        super.func_96516_a(p_96516_1_);
        this.server.getConfigurationManager().sendPacketToAllPlayers(new S3CPacketUpdateScore(p_96516_1_));
        this.func_96551_b();
    }

    public void func_178820_a(String p_178820_1_, ScoreObjective p_178820_2_) {
        super.func_178820_a(p_178820_1_, p_178820_2_);
        this.server.getConfigurationManager().sendPacketToAllPlayers(new S3CPacketUpdateScore(p_178820_1_, p_178820_2_));
        this.func_96551_b();
    }

    public void setObjectiveInDisplaySlot(int slot, ScoreObjective objective) {
        ScoreObjective objective1 = this.getObjectiveInDisplaySlot(slot);
        super.setObjectiveInDisplaySlot(slot, objective);

        if (objective1 != objective && objective1 != null) {
            if (this.func_96552_h(objective1) > 0) {
                this.server.getConfigurationManager().sendPacketToAllPlayers(new S3DPacketDisplayScoreboard(slot, objective));
            } else {
                this.getPlayerIterator(objective1);
            }
        }

        if (objective != null) {
            if (this.objectives.contains(objective)) {
                this.server.getConfigurationManager().sendPacketToAllPlayers(new S3DPacketDisplayScoreboard(slot, objective));
            } else {
                this.func_96549_e(objective);
            }
        }

        this.func_96551_b();
    }

    public boolean addPlayerToTeam(String player, String newTeam) {
        if (super.addPlayerToTeam(player, newTeam)) {
            ScorePlayerTeam scoreplayerteam = this.getTeam(newTeam);
            this.server.getConfigurationManager().sendPacketToAllPlayers(new S3EPacketTeams(scoreplayerteam, Collections.singletonList(player), 3));
            this.func_96551_b();
            return true;
        } else {
            return false;
        }
    }

    public void removePlayerFromTeam(String player, ScorePlayerTeam playerTeam) {
        super.removePlayerFromTeam(player, playerTeam);
        this.server.getConfigurationManager().sendPacketToAllPlayers(new S3EPacketTeams(playerTeam, Collections.singletonList(player), 4));
        this.func_96551_b();
    }

    public void onScoreObjectiveAdded(ScoreObjective objective) {
        super.onScoreObjectiveAdded(objective);
        this.func_96551_b();
    }

    public void func_96532_b(ScoreObjective objective) {
        super.func_96532_b(objective);

        if (this.objectives.contains(objective)) {
            this.server.getConfigurationManager().sendPacketToAllPlayers(new S3BPacketScoreboardObjective(objective, 2));
        }

        this.func_96551_b();
    }

    public void func_96533_c(ScoreObjective objective) {
        super.func_96533_c(objective);

        if (this.objectives.contains(objective)) {
            this.getPlayerIterator(objective);
        }

        this.func_96551_b();
    }

    public void broadcastTeamCreated(ScorePlayerTeam playerTeam) {
        super.broadcastTeamCreated(playerTeam);
        this.server.getConfigurationManager().sendPacketToAllPlayers(new S3EPacketTeams(playerTeam, 0));
        this.func_96551_b();
    }

    public void sendTeamUpdate(ScorePlayerTeam playerTeam) {
        super.sendTeamUpdate(playerTeam);
        this.server.getConfigurationManager().sendPacketToAllPlayers(new S3EPacketTeams(playerTeam, 2));
        this.func_96551_b();
    }

    public void func_96513_c(ScorePlayerTeam playerTeam) {
        super.func_96513_c(playerTeam);
        this.server.getConfigurationManager().sendPacketToAllPlayers(new S3EPacketTeams(playerTeam, 1));
        this.func_96551_b();
    }

    public void func_96547_a(ScoreboardSaveData scoreboardSaveData) {
        this.scoreboardSaveData = scoreboardSaveData;
    }

    protected void func_96551_b() {
        if (this.scoreboardSaveData != null) {
            this.scoreboardSaveData.markDirty();
        }
    }

    public List<Packet<?>> func_96550_d(ScoreObjective objective) {

        List<Packet <?>> list = Lists.newArrayList();
        list.add(new S3BPacketScoreboardObjective(objective, 0));

        for (int i = 0; i < 19; ++i) {
            if (this.getObjectiveInDisplaySlot(i) == objective) {
                list.add(new S3DPacketDisplayScoreboard(i, objective));
            }
        }

        for (Score score : this.getSortedScores(objective)) {
            list.add(new S3CPacketUpdateScore(score));
        }

        return list;
    }

    public void func_96549_e(ScoreObjective objective) {
        List<Packet<?>> list = this.func_96550_d(objective);

        for (EntityPlayerMP entityplayermp : this.server.getConfigurationManager().func_181057_v()) {
            for (Packet<?> packet : list) {
                entityplayermp.playerNetServerHandler.sendPacket(packet);
            }
        }

        this.objectives.add(objective);
    }

    public List<Packet <?>> func_96548_f(ScoreObjective objective) {
        List<Packet<?>> list = Lists.newArrayList();
        list.add(new S3BPacketScoreboardObjective(objective, 1));

        for (int i = 0; i < 19; ++i) {
            if (this.getObjectiveInDisplaySlot(i) == objective) {
                list.add(new S3DPacketDisplayScoreboard(i, objective));
            }
        }

        return list;
    }

    public void getPlayerIterator(ScoreObjective objective) {
        List<Packet<?>> list = this.func_96548_f(objective);

        for (EntityPlayerMP playerMP : this.server.getConfigurationManager().func_181057_v()) {
            for (Packet<?> packet : list) {
                playerMP.playerNetServerHandler.sendPacket(packet);
            }
        }

        this.objectives.remove(objective);
    }

    public int func_96552_h(ScoreObjective objective) {
        int i = 0;

        for (int j = 0; j < 19; ++j) {
            if (this.getObjectiveInDisplaySlot(j) == objective) {
                ++i;
            }
        }

        return i;
    }
}
