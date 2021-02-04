package com.murengezi.minecraft.scoreboard;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumChatFormatting;

public class Scoreboard {

    private final Map<String, ScoreObjective> scoreObjectives = Maps.newHashMap();
    private final Map<IScoreObjectiveCriteria, List<ScoreObjective>> scoreObjectiveCriterias = Maps.newHashMap();
    private final Map<String, Map<ScoreObjective, Score>> entitiesScoreObjectives = Maps.newHashMap();

    private final ScoreObjective[] objectiveDisplaySlots = new ScoreObjective[19];
    private final Map<String, ScorePlayerTeam> teams = Maps.newHashMap();
    private final Map<String, ScorePlayerTeam> teamMemberships = Maps.newHashMap();
    private static String[] field_178823_g = null;

    public ScoreObjective getObjective(String name) {
        return this.scoreObjectives.get(name);
    }

    public ScoreObjective addScoreObjective(String name, IScoreObjectiveCriteria criteria) {
        if (name.length() > 16) {
            throw new IllegalArgumentException("The objective name '" + name + "' is too long!");
        } else {
            ScoreObjective scoreobjective = this.getObjective(name);

            if (scoreobjective != null) {
                throw new IllegalArgumentException("An objective with the name '" + name + "' already exists!");
            } else {
                scoreobjective = new ScoreObjective(this, name, criteria);
                List<ScoreObjective> list = this.scoreObjectiveCriterias.computeIfAbsent(criteria, k -> Lists.newArrayList());

                list.add(scoreobjective);
                this.scoreObjectives.put(name, scoreobjective);
                this.onScoreObjectiveAdded(scoreobjective);
                return scoreobjective;
            }
        }
    }

    public Collection<ScoreObjective> getObjectivesFromCriteria(IScoreObjectiveCriteria criteria) {
        Collection<ScoreObjective> collection = this.scoreObjectiveCriterias.get(criteria);
        return collection == null ? Lists.newArrayList() : Lists.newArrayList(collection);
    }

    public boolean entityHasObjective(String name, ScoreObjective objective) {
        Map<ScoreObjective, Score> map = this.entitiesScoreObjectives.get(name);

        if (map == null) {
            return false;
        } else {
            Score score = map.get(objective);
            return score != null;
        }
    }

    public Score getValueFromObjective(String name, ScoreObjective objective) {
        if (name.length() > 40) {
            throw new IllegalArgumentException("The player name '" + name + "' is too long!");
        } else {
            Map<ScoreObjective, Score> map = this.entitiesScoreObjectives.computeIfAbsent(name, k -> Maps.newHashMap());

            Score score = map.get(objective);

            if (score == null) {
                score = new Score(this, objective, name);
                map.put(objective, score);
            }

            return score;
        }
    }

    public Collection<Score> getSortedScores(ScoreObjective objective) {
        List<Score> list = Lists.newArrayList();

        for (Map<ScoreObjective, Score> map : this.entitiesScoreObjectives.values()) {
            Score score = map.get(objective);

            if (score != null) {
                list.add(score);
            }
        }

        list.sort(Score.scoreComparator);
        return list;
    }

    public Collection<ScoreObjective> getScoreObjectives() {
        return this.scoreObjectives.values();
    }

    public Collection<String> getObjectiveNames() {
        return this.entitiesScoreObjectives.keySet();
    }

    public void removeObjectiveFromEntity(String name, ScoreObjective objective) {
        if (objective == null) {
            Map<ScoreObjective, Score> map = this.entitiesScoreObjectives.remove(name);

            if (map != null) {
                this.func_96516_a(name);
            }
        } else {
            Map<ScoreObjective, Score> map2 = this.entitiesScoreObjectives.get(name);

            if (map2 != null) {
                Score score = map2.remove(objective);

                if (map2.size() < 1) {
                    Map<ScoreObjective, Score> map1 = this.entitiesScoreObjectives.remove(name);

                    if (map1 != null) {
                        this.func_96516_a(name);
                    }
                } else if (score != null) {
                    this.func_178820_a(name, objective);
                }
            }
        }
    }

    public Collection<Score> getScores() {
        Collection<Map<ScoreObjective, Score>> collection = this.entitiesScoreObjectives.values();
        List<Score> list = Lists.newArrayList();

        for (Map<ScoreObjective, Score> map : collection) {
            list.addAll(map.values());
        }

        return list;
    }

    public Map<ScoreObjective, Score> getObjectivesForEntity(String name) {
        Map<ScoreObjective, Score> map = this.entitiesScoreObjectives.get(name);

        if (map == null) {
            map = Maps.newHashMap();
        }

        return map;
    }

    public void removeObjective(ScoreObjective objective) {
        this.scoreObjectives.remove(objective.getName());

        for (int i = 0; i < 19; ++i) {
            if (this.getObjectiveInDisplaySlot(i) == objective) {
                this.setObjectiveInDisplaySlot(i, null);
            }
        }

        List<ScoreObjective> list = this.scoreObjectiveCriterias.get(objective.getCriteria());

        if (list != null) {
            list.remove(objective);
        }

        for (Map<ScoreObjective, Score> map : this.entitiesScoreObjectives.values()) {
            map.remove(objective);
        }

        this.func_96533_c(objective);
    }

    public void setObjectiveInDisplaySlot(int slot, ScoreObjective objective) {
        this.objectiveDisplaySlots[slot] = objective;
    }

    public ScoreObjective getObjectiveInDisplaySlot(int slot) {
        return this.objectiveDisplaySlots[slot];
    }

    public ScorePlayerTeam getTeam(String teamName) {
        return this.teams.get(teamName);
    }

    public ScorePlayerTeam createTeam(String teamName)
    {
        if (teamName.length() > 16) {
            throw new IllegalArgumentException("The team name '" + teamName + "' is too long!");
        } else {
            ScorePlayerTeam playerTeam = this.getTeam(teamName);

            if (playerTeam != null) {
                throw new IllegalArgumentException("A team with the name '" + teamName + "' already exists!");
            } else {
                playerTeam = new ScorePlayerTeam(this, teamName);
                this.teams.put(teamName, playerTeam);
                this.broadcastTeamCreated(playerTeam);
                return playerTeam;
            }
        }
    }

    public void removeTeam(ScorePlayerTeam team) {
        if (team != null) {
            this.teams.remove(team.getRegisteredName());

            for (String s : team.getMembershipCollection()) {
                this.teamMemberships.remove(s);
            }

            this.func_96513_c(team);
        }
    }

    public boolean addPlayerToTeam(String player, String newTeam) {
        if (player.length() > 40) {
            throw new IllegalArgumentException("The player name '" + player + "' is too long!");
        } else if (!this.teams.containsKey(newTeam)) {
            return false;
        } else {
            ScorePlayerTeam scoreplayerteam = this.getTeam(newTeam);

            if (this.getPlayersTeam(player) != null) {
                this.removePlayerFromTeams(player);
            }

            this.teamMemberships.put(player, scoreplayerteam);
            scoreplayerteam.getMembershipCollection().add(player);
            return true;
        }
    }

    public boolean removePlayerFromTeams(String player) {
        ScorePlayerTeam scoreplayerteam = this.getPlayersTeam(player);

        if (scoreplayerteam != null) {
            this.removePlayerFromTeam(player, scoreplayerteam);
            return true;
        } else {
            return false;
        }
    }

    public void removePlayerFromTeam(String player, ScorePlayerTeam playerTeam) {
        if (this.getPlayersTeam(player) != playerTeam) {
            throw new IllegalStateException("Player is either on another team or not on any team. Cannot remove from team '" + playerTeam.getRegisteredName() + "'.");
        } else {
            this.teamMemberships.remove(player);
            playerTeam.getMembershipCollection().remove(player);
        }
    }

    public Collection<String> getTeamNames() {
        return this.teams.keySet();
    }

    public Collection<ScorePlayerTeam> getTeams() {
        return this.teams.values();
    }

    public ScorePlayerTeam getPlayersTeam(String player) {
        return this.teamMemberships.get(player);
    }

    public void onScoreObjectiveAdded(ScoreObjective objective) {}

    public void func_96532_b(ScoreObjective objective) {}

    public void func_96533_c(ScoreObjective objective) {}

    public void func_96536_a(Score score) {}

    public void func_96516_a(String string) {}

    public void func_178820_a(String string, ScoreObjective objective) {}

    public void broadcastTeamCreated(ScorePlayerTeam playerTeam) {}

    public void sendTeamUpdate(ScorePlayerTeam playerTeam) {}

    public void func_96513_c(ScorePlayerTeam playerTeam) {}

    public static String getObjectiveDisplaySlot(int slot)
    {
        switch (slot) {
            case 0:
                return "list";
            case 1:
                return "sidebar";
            case 2:
                return "belowName";
            default:
                if (slot >= 3 && slot <= 18) {
                    EnumChatFormatting formatting = EnumChatFormatting.func_175744_a(slot - 3);

                    if (formatting != null && formatting != EnumChatFormatting.RESET) {
                        return "sidebar.team." + formatting.getFriendlyName();
                    }
                }

                return null;
        }
    }

    public static int getObjectiveDisplaySlotNumber(String display) {
        if (display.equalsIgnoreCase("list")) {
            return 0;
        } else if (display.equalsIgnoreCase("sidebar")) {
            return 1;
        } else if (display.equalsIgnoreCase("belowName")) {
            return 2;
        } else {
            if (display.startsWith("sidebar.team.")) {
                String s = display.substring("sidebar.team.".length());
                EnumChatFormatting formatting = EnumChatFormatting.getValueByName(s);

                if (formatting != null && formatting.getColorIndex() >= 0) {
                    return formatting.getColorIndex() + 3;
                }
            }
            return -1;
        }
    }

    public static String[] getDisplaySlotStrings() {
        if (field_178823_g == null) {
            field_178823_g = new String[19];

            for (int i = 0; i < 19; ++i) {
                field_178823_g[i] = getObjectiveDisplaySlot(i);
            }
        }

        return field_178823_g;
    }

    public void func_181140_a(Entity p_181140_1_) {
        if (p_181140_1_ != null && !(p_181140_1_ instanceof EntityPlayer) && !p_181140_1_.isEntityAlive()) {
            String s = p_181140_1_.getUniqueID().toString();
            this.removeObjectiveFromEntity(s, null);
            this.removePlayerFromTeams(s);
        }
    }
}
