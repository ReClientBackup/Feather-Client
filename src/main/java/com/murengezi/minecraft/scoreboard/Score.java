package com.murengezi.minecraft.scoreboard;

import java.util.Comparator;
import java.util.List;
import net.minecraft.entity.player.EntityPlayer;

public class Score
{
    public static final Comparator<Score> scoreComparator = (score1, score2) -> score1.getScorePoints() > score2.getScorePoints() ? 1 : (score1.getScorePoints() < score2.getScorePoints() ? -1 : score2.getPlayerName().compareToIgnoreCase(score1.getPlayerName()));
    private final Scoreboard scoreboard;
    private final ScoreObjective objective;
    private final String playerName;
    private int scorePoints;
    private boolean locked;
    //TODO figure out this one
    private boolean field_178818_g;

    public Score(Scoreboard scoreboard, ScoreObjective objective, String playerName)
    {
        this.scoreboard = scoreboard;
        this.objective = objective;
        this.playerName = playerName;
        this.field_178818_g = true;
    }

    public void increaseScore(int amount) {
        if (this.objective.getCriteria().isReadOnly()) {
            throw new IllegalStateException("Cannot modify read-only score");
        } else {
            this.setScorePoints(this.getScorePoints() + amount);
        }
    }

    public void decreaseScore(int amount) {
        if (this.objective.getCriteria().isReadOnly()) {
            throw new IllegalStateException("Cannot modify read-only score");
        } else {
            this.setScorePoints(this.getScorePoints() - amount);
        }
    }

    public void increaseByOne() {
        if (this.objective.getCriteria().isReadOnly()) {
            throw new IllegalStateException("Cannot modify read-only score");
        } else {
            this.increaseScore(1);
        }
    }

    public int getScorePoints() {
        return this.scorePoints;
    }

    public void setScorePoints(int points) {
        int i = this.scorePoints;
        this.scorePoints = points;

        if (i != points || this.field_178818_g) {
            this.field_178818_g = false;
            this.getScoreboard().func_96536_a(this);
        }
    }

    public ScoreObjective getObjective() {
        return this.objective;
    }

    public String getPlayerName() {
        return this.playerName;
    }

    public Scoreboard getScoreboard() {
        return this.scoreboard;
    }

    public boolean isLocked() {
        return this.locked;
    }

    public void setLocked(boolean locked) {
        this.locked = locked;
    }

    public void func_96651_a(List<EntityPlayer> p_96651_1_) {
        this.setScorePoints(this.objective.getCriteria().getScorePoints(p_96651_1_));
    }
}
