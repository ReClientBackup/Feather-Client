package com.murengezi.minecraft.scoreboard;

import java.util.List;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumChatFormatting;

public class GoalColor implements IScoreObjectiveCriteria {

    private final String goalName;

    public GoalColor(String goalName, EnumChatFormatting formatting) {
        this.goalName = goalName + formatting.getFriendlyName();
        IScoreObjectiveCriteria.INSTANCES.put(this.goalName, this);
    }

    public String getName() {
        return this.goalName;
    }

    public int getScorePoints(List<EntityPlayer> players) {
        return 0;
    }

    public boolean isReadOnly() {
        return false;
    }

    public IScoreObjectiveCriteria.EnumRenderType getRenderType() {
        return IScoreObjectiveCriteria.EnumRenderType.INTEGER;
    }
}
