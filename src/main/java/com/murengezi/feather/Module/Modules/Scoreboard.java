package com.murengezi.feather.Module.Modules;

import com.darkmagician6.eventapi.EventTarget;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.murengezi.feather.Event.RenderScoreboardEvent;
import com.murengezi.feather.Module.Module;
import com.murengezi.feather.Module.ModuleInfo;
import com.murengezi.feather.Module.Setting.Settings.BooleanSetting;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.scoreboard.Score;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.util.EnumChatFormatting;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Tobias Sjöblom
 * Created on 2021-01-27 at 12:51
 */
@ModuleInfo(name = "Scoreboard", description = "Scoreboard", version = "1.0.0", enabled = true)
public class Scoreboard extends Module {

	public Scoreboard() {
		addSetting(new BooleanSetting("Numbers", this, true));
	}

	@EventTarget
	public void onRenderScoreboard(RenderScoreboardEvent event) {
		ScoreObjective objective = event.getObjective();
		ScaledResolution resolution = event.getResolution();
		net.minecraft.scoreboard.Scoreboard scoreboard = objective.getScoreboard();
		Collection<Score> collection = scoreboard.getSortedScores(objective);
		List<Score> list = collection.stream().filter(score -> score.getPlayerName() != null && !score.getPlayerName().startsWith("#")).collect(Collectors.toList());
		boolean numbers = getBooleanSetting("Numbers").getValue();

		if (list.size() > 15) {
			collection = Lists.newArrayList(Iterables.skip(list,collection.size() - 15));
		} else {
			collection = list;
		}

		int stringWidth = getFr().getStringWidth(objective.getDisplayName());

		for (Score score : collection) {
			ScorePlayerTeam scoreplayerteam = scoreboard.getPlayersTeam(score.getPlayerName());
			String s = ScorePlayerTeam.formatPlayerName(scoreplayerteam, score.getPlayerName()) + (numbers ? ": " + EnumChatFormatting.RED + score.getScorePoints() : "");
			stringWidth = Math.max(stringWidth, getFr().getStringWidth(s));
		}

		int offsetRight = 3;
		int height = collection.size() * getFr().FONT_HEIGHT;
		int y = resolution.getScaledHeight() / 2 + height / 3;
		int x = resolution.getScaledWidth() - stringWidth - offsetRight;

		int finalStringWidth = stringWidth;
		list.forEach(score1 -> {
			int index = list.indexOf(score1);
			ScorePlayerTeam scorePlayerTeam = scoreboard.getPlayersTeam(score1.getPlayerName());
			String playerName = ScorePlayerTeam.formatPlayerName(scorePlayerTeam, score1.getPlayerName());
			String score = EnumChatFormatting.RED + "" + score1.getScorePoints();
			int yPos = y - index * getFr().FONT_HEIGHT;
			int xPos = resolution.getScaledWidth() - offsetRight + 1;
			Gui.drawRect(x - 2, yPos, xPos, yPos + getFr().FONT_HEIGHT, 1342177280);
			getFr().drawStringWithShadow(playerName, x, yPos, 553648127);
			if (numbers) {
				getFr().drawStringWithShadow(score, xPos - getFr().getStringWidth(score) - 1, yPos, 553648127);
			}

			if (index == list.size() - 1) {
				String s3 = objective.getDisplayName();
				Gui.drawRect(x - 2, yPos - getFr().FONT_HEIGHT - 1, xPos, yPos - 1, 1610612736);
				Gui.drawRect(x - 2, yPos - 1, xPos, yPos, 1342177280);
				getFr().drawStringWithShadow(s3, x + (float) finalStringWidth / 2 - (float) getFr().getStringWidth(s3) / 2, yPos - getFr().FONT_HEIGHT, 553648127);
			}
		});
	}

}
