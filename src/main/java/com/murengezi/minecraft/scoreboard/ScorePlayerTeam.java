package com.murengezi.minecraft.scoreboard;

import com.google.common.collect.Sets;
import java.util.Collection;
import java.util.Set;
import net.minecraft.util.EnumChatFormatting;

public class ScorePlayerTeam extends Team {

    private final Scoreboard scoreboard;
    private final String registeredName;
    private final Set<String> membershipSet = Sets.<String>newHashSet();
    private String teamName, namePrefix = "", colorSuffix = "";
    private boolean allowFriendlyFire = true, canSeeFriendlyInvisibles = true;
    private Team.EnumVisible nameTagVisibility = Team.EnumVisible.ALWAYS, deathMessageVisibility = Team.EnumVisible.ALWAYS;
    private EnumChatFormatting chatFormat = EnumChatFormatting.RESET;

    public ScorePlayerTeam(Scoreboard scoreboard, String name) {
        this.scoreboard = scoreboard;
        this.registeredName = name;
        this.teamName = name;
    }

    public String getRegisteredName() {
        return this.registeredName;
    }

    public String getTeamName() {
        return this.teamName;
    }

    public void setTeamName(String name) {
        if (name == null) {
            throw new IllegalArgumentException("Name cannot be null");
        } else {
            this.teamName = name;
            this.scoreboard.sendTeamUpdate(this);
        }
    }

    public Collection<String> getMembershipCollection() {
        return this.membershipSet;
    }

    public String getColorPrefix() {
        return this.namePrefix;
    }

    public void setNamePrefix(String prefix) {
        if (prefix == null) {
            throw new IllegalArgumentException("Prefix cannot be null");
        } else {
            this.namePrefix = prefix;
            this.scoreboard.sendTeamUpdate(this);
        }
    }

    public String getColorSuffix() {
        return this.colorSuffix;
    }

    public void setNameSuffix(String suffix) {
        this.colorSuffix = suffix;
        this.scoreboard.sendTeamUpdate(this);
    }

    public String formatString(String input) {
        return this.getColorPrefix() + input + this.getColorSuffix();
    }

    public static String formatPlayerName(Team team, String p_96667_1_) {
        return team == null ? p_96667_1_ : team.formatString(p_96667_1_);
    }

    public boolean getAllowFriendlyFire() {
        return this.allowFriendlyFire;
    }

    public void setAllowFriendlyFire(boolean friendlyFire) {
        this.allowFriendlyFire = friendlyFire;
        this.scoreboard.sendTeamUpdate(this);
    }

    public boolean getSeeFriendlyInvisiblesEnabled() {
        return this.canSeeFriendlyInvisibles;
    }

    public void setSeeFriendlyInvisiblesEnabled(boolean friendlyInvisibles) {
        this.canSeeFriendlyInvisibles = friendlyInvisibles;
        this.scoreboard.sendTeamUpdate(this);
    }

    public Team.EnumVisible getNameTagVisibility() {
        return this.nameTagVisibility;
    }

    public Team.EnumVisible getDeathMessageVisibility() {
        return this.deathMessageVisibility;
    }

    public void setNameTagVisibility(Team.EnumVisible visible) {
        this.nameTagVisibility = visible;
        this.scoreboard.sendTeamUpdate(this);
    }

    public void setDeathMessageVisibility(Team.EnumVisible visible) {
        this.deathMessageVisibility = visible;
        this.scoreboard.sendTeamUpdate(this);
    }

    public int func_98299_i() {
        int i = 0;

        if (this.getAllowFriendlyFire()) {
            i |= 1;
        }

        if (this.getSeeFriendlyInvisiblesEnabled()) {
            i |= 2;
        }

        return i;
    }

    public void func_98298_a(int p_98298_1_) {
        this.setAllowFriendlyFire((p_98298_1_ & 1) > 0);
        this.setSeeFriendlyInvisiblesEnabled((p_98298_1_ & 2) > 0);
    }

    public void setChatFormat(EnumChatFormatting chatFormat) {
        this.chatFormat = chatFormat;
    }

    public EnumChatFormatting getChatFormat() {
        return this.chatFormat;
    }
}
