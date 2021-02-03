package com.murengezi.minecraft.scoreboard;

import java.util.List;
import net.minecraft.entity.player.EntityPlayer;

public class ScoreDummyCriteria implements IScoreObjectiveCriteria {

    private final String name;

    public ScoreDummyCriteria(String name) {
        this.name = name;
        IScoreObjectiveCriteria.INSTANCES.put(name, this);
    }

    public String getName() {
        return this.name;
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
