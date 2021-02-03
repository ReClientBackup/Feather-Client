package com.murengezi.minecraft.scoreboard;

public class ScoreObjective {

    private final Scoreboard scoreboard;
    private final String name;
    private final IScoreObjectiveCriteria objectiveCriteria;
    private IScoreObjectiveCriteria.EnumRenderType renderType;
    private String displayName;

    public ScoreObjective(Scoreboard scoreboard, String name, IScoreObjectiveCriteria objectiveCriteria)
    {
        this.scoreboard = scoreboard;
        this.name = name;
        this.objectiveCriteria = objectiveCriteria;
        this.displayName = name;
        this.renderType = objectiveCriteria.getRenderType();
    }

    public Scoreboard getScoreboard() {
        return this.scoreboard;
    }

    public String getName() {
        return this.name;
    }

    public IScoreObjectiveCriteria getCriteria() {
        return this.objectiveCriteria;
    }

    public String getDisplayName() {
        return this.displayName;
    }

    public void setDisplayName(String name) {
        this.displayName = name;
        this.scoreboard.func_96532_b(this);
    }

    public IScoreObjectiveCriteria.EnumRenderType getRenderType()
    {
        return this.renderType;
    }

    public void setRenderType(IScoreObjectiveCriteria.EnumRenderType renderType) {
        this.renderType = renderType;
        this.scoreboard.func_96532_b(this);
    }
}
