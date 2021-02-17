package com.murengezi.minecraft.scoreboard;

import com.murengezi.chocolate.Chocolate;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.WorldSavedData;

public class ScoreboardSaveData extends WorldSavedData {

    private Scoreboard scoreboard;
    private NBTTagCompound delayedInitNbt;

    public ScoreboardSaveData() {
        this("scoreboard");
    }

    public ScoreboardSaveData(String name) {
        super(name);
    }

    public void setScoreboard(Scoreboard scoreboard) {
        this.scoreboard = scoreboard;

        if (this.delayedInitNbt != null) {
            this.readFromNBT(this.delayedInitNbt);
        }
    }

    public void readFromNBT(NBTTagCompound nbt) {
        if (this.scoreboard == null) {
            this.delayedInitNbt = nbt;
        } else {
            this.readObjectives(nbt.getTagList("Objectives", 10));
            this.readScores(nbt.getTagList("PlayerScores", 10));

            if (nbt.hasKey("DisplaySlots", 10)) {
                this.readDisplayConfig(nbt.getCompoundTag("DisplaySlots"));
            }

            if (nbt.hasKey("Teams", 9)) {
                this.readTeams(nbt.getTagList("Teams", 10));
            }
        }
    }

    protected void readTeams(NBTTagList tagList) {
        for (int i = 0; i < tagList.tagCount(); ++i) {
            NBTTagCompound tagCompound = tagList.getCompoundTagAt(i);
            String s = tagCompound.getString("Name");

            if (s.length() > 16) {
                s = s.substring(0, 16);
            }

            ScorePlayerTeam playerTeam = this.scoreboard.createTeam(s);
            String s1 = tagCompound.getString("DisplayName");

            if (s1.length() > 32) {
                s1 = s1.substring(0, 32);
            }

            playerTeam.setTeamName(s1);

            if (tagCompound.hasKey("TeamColor", 8)) {
                playerTeam.setChatFormat(EnumChatFormatting.getValueByName(tagCompound.getString("TeamColor")));
            }

            playerTeam.setNamePrefix(tagCompound.getString("Prefix"));
            playerTeam.setNameSuffix(tagCompound.getString("Suffix"));

            if (tagCompound.hasKey("AllowFriendlyFire", 99)) {
                playerTeam.setAllowFriendlyFire(tagCompound.getBoolean("AllowFriendlyFire"));
            }

            if (tagCompound.hasKey("SeeFriendlyInvisibles", 99)) {
                playerTeam.setSeeFriendlyInvisiblesEnabled(tagCompound.getBoolean("SeeFriendlyInvisibles"));
            }

            if (tagCompound.hasKey("NameTagVisibility", 8)) {
                Team.EnumVisible visibility = Team.EnumVisible.func_178824_a(tagCompound.getString("NameTagVisibility"));

                if (visibility != null) {
                    playerTeam.setNameTagVisibility(visibility);
                }
            }

            if (tagCompound.hasKey("DeathMessageVisibility", 8)) {
                Team.EnumVisible visibility = Team.EnumVisible.func_178824_a(tagCompound.getString("DeathMessageVisibility"));

                if (visibility != null) {
                    playerTeam.setDeathMessageVisibility(visibility);
                }
            }

            this.func_96502_a(playerTeam, tagCompound.getTagList("Players", 8));
        }
    }

    protected void func_96502_a(ScorePlayerTeam playerTeam, NBTTagList tagList) {
        for (int i = 0; i < tagList.tagCount(); ++i) {
            this.scoreboard.addPlayerToTeam(tagList.getStringTagAt(i), playerTeam.getRegisteredName());
        }
    }

    protected void readDisplayConfig(NBTTagCompound tagCompound) {
        for (int i = 0; i < 19; ++i) {
            if (tagCompound.hasKey("slot_" + i, 8)) {
                String s = tagCompound.getString("slot_" + i);
                ScoreObjective scoreobjective = this.scoreboard.getObjective(s);
                this.scoreboard.setObjectiveInDisplaySlot(i, scoreobjective);
            }
        }
    }

    protected void readObjectives(NBTTagList nbt) {
        for (int i = 0; i < nbt.tagCount(); ++i) {
            NBTTagCompound nbttagcompound = nbt.getCompoundTagAt(i);
            IScoreObjectiveCriteria iscoreobjectivecriteria = IScoreObjectiveCriteria.INSTANCES.get(nbttagcompound.getString("CriteriaName"));

            if (iscoreobjectivecriteria != null) {
                String s = nbttagcompound.getString("Name");

                if (s.length() > 16) {
                    s = s.substring(0, 16);
                }

                ScoreObjective scoreobjective = this.scoreboard.addScoreObjective(s, iscoreobjectivecriteria);
                scoreobjective.setDisplayName(nbttagcompound.getString("DisplayName"));
                scoreobjective.setRenderType(IScoreObjectiveCriteria.EnumRenderType.getRenderType(nbttagcompound.getString("RenderType")));
            }
        }
    }

    protected void readScores(NBTTagList nbt) {
        for (int i = 0; i < nbt.tagCount(); ++i) {
            NBTTagCompound tagCompound = nbt.getCompoundTagAt(i);
            ScoreObjective scoreobjective = this.scoreboard.getObjective(tagCompound.getString("Objective"));
            String s = tagCompound.getString("Name");

            if (s.length() > 40) {
                s = s.substring(0, 40);
            }

            Score score = this.scoreboard.getValueFromObjective(s, scoreobjective);
            score.setScorePoints(tagCompound.getInteger("Score"));

            if (tagCompound.hasKey("Locked")) {
                score.setLocked(tagCompound.getBoolean("Locked"));
            }
        }
    }

    public void writeToNBT(NBTTagCompound tagCompound) {
        if (this.scoreboard == null) {
            Chocolate.getLogger().warn("Tried to save scoreboard without having a scoreboard...");
        } else {
            tagCompound.setTag("Objectives", this.objectivesToNbt());
            tagCompound.setTag("PlayerScores", this.scoresToNbt());
            tagCompound.setTag("Teams", this.func_96496_a());
            this.setDisplaySlot(tagCompound);
        }
    }

    protected NBTTagList func_96496_a() {
        NBTTagList tagList = new NBTTagList();

        for (ScorePlayerTeam scoreplayerteam : this.scoreboard.getTeams()) {
            NBTTagCompound tagCompound = new NBTTagCompound();
            tagCompound.setString("Name", scoreplayerteam.getRegisteredName());
            tagCompound.setString("DisplayName", scoreplayerteam.getTeamName());

            if (scoreplayerteam.getChatFormat().getColorIndex() >= 0) {
                tagCompound.setString("TeamColor", scoreplayerteam.getChatFormat().getFriendlyName());
            }

            tagCompound.setString("Prefix", scoreplayerteam.getColorPrefix());
            tagCompound.setString("Suffix", scoreplayerteam.getColorSuffix());
            tagCompound.setBoolean("AllowFriendlyFire", scoreplayerteam.getAllowFriendlyFire());
            tagCompound.setBoolean("SeeFriendlyInvisibles", scoreplayerteam.getSeeFriendlyInvisiblesEnabled());
            tagCompound.setString("NameTagVisibility", scoreplayerteam.getNameTagVisibility().field_178830_e);
            tagCompound.setString("DeathMessageVisibility", scoreplayerteam.getDeathMessageVisibility().field_178830_e);
            NBTTagList tagList1 = new NBTTagList();

            for (String s : scoreplayerteam.getMembershipCollection()) {
                tagList1.appendTag(new NBTTagString(s));
            }

            tagCompound.setTag("Players", tagList1);
            tagList.appendTag(tagCompound);
        }

        return tagList;
    }

    protected void setDisplaySlot(NBTTagCompound tagCompound) {
        NBTTagCompound tagCompound1 = new NBTTagCompound();
        boolean flag = false;

        for (int i = 0; i < 19; ++i) {
            ScoreObjective scoreobjective = this.scoreboard.getObjectiveInDisplaySlot(i);

            if (scoreobjective != null) {
                tagCompound1.setString("slot_" + i, scoreobjective.getName());
                flag = true;
            }
        }

        if (flag) {
            tagCompound.setTag("DisplaySlots", tagCompound1);
        }
    }

    protected NBTTagList objectivesToNbt() {
        NBTTagList nbttaglist = new NBTTagList();

        for (ScoreObjective scoreobjective : this.scoreboard.getScoreObjectives()) {
            if (scoreobjective.getCriteria() != null) {
                NBTTagCompound nbttagcompound = new NBTTagCompound();
                nbttagcompound.setString("Name", scoreobjective.getName());
                nbttagcompound.setString("CriteriaName", scoreobjective.getCriteria().getName());
                nbttagcompound.setString("DisplayName", scoreobjective.getDisplayName());
                nbttagcompound.setString("RenderType", scoreobjective.getRenderType().getName());
                nbttaglist.appendTag(nbttagcompound);
            }
        }

        return nbttaglist;
    }

    protected NBTTagList scoresToNbt() {
        NBTTagList nbttaglist = new NBTTagList();

        for (Score score : this.scoreboard.getScores()) {
            if (score.getObjective() != null) {
                NBTTagCompound nbttagcompound = new NBTTagCompound();
                nbttagcompound.setString("Name", score.getPlayerName());
                nbttagcompound.setString("Objective", score.getObjective().getName());
                nbttagcompound.setInteger("Score", score.getScorePoints());
                nbttagcompound.setBoolean("Locked", score.isLocked());
                nbttaglist.appendTag(nbttagcompound);
            }
        }

        return nbttaglist;
    }
}
