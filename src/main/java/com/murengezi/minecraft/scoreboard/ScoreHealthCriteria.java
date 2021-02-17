package com.murengezi.minecraft.scoreboard;

import java.util.List;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MathHelper;

public class ScoreHealthCriteria extends ScoreDummyCriteria {

    public ScoreHealthCriteria(String name) {
        super(name);
    }

    public int getScorePoints(List<EntityPlayer> players) {
        float f = 0.0F;

        for (EntityPlayer player : players) {
            f += player.getHealth() + player.getAbsorptionAmount();
        }

        if (players.size() > 0)
        {
            f /= (float) players.size();
        }

        return MathHelper.ceiling_float_int(f);
    }

    public boolean isReadOnly() {
        return true;
    }

    public IScoreObjectiveCriteria.EnumRenderType getRenderType() {
        return IScoreObjectiveCriteria.EnumRenderType.HEARTS;
    }
}
