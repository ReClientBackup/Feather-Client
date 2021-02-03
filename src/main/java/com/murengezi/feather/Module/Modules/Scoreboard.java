package com.murengezi.feather.Module.Modules;

import com.darkmagician6.eventapi.EventTarget;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.murengezi.feather.Event.RenderScoreboardEvent;
import com.murengezi.feather.Module.Adjustable;
import com.murengezi.feather.Module.ModuleInfo;
import com.murengezi.feather.Module.Setting.Settings.BooleanSetting;
import com.murengezi.minecraft.client.gui.GUI;
import com.murengezi.minecraft.scoreboard.Score;
import com.murengezi.minecraft.scoreboard.ScoreObjective;
import com.murengezi.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.util.EnumChatFormatting;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Tobias Sjöblom
 * Created on 2021-01-27 at 12:51
 */
@ModuleInfo(name = "Scoreboard", description = "Scoreboard", version = "1.0.0", enabled = true)
public class Scoreboard extends Adjustable {

	public Scoreboard() {
		super(0.0f, 0.0f, Region.CENTER_RIGHT);
		addSetting(new BooleanSetting("Numbers", this, true));
	}

	@EventTarget
	public void onRenderScoreboard(RenderScoreboardEvent event) {
		ScoreObjective objective = event.getObjective();
		com.murengezi.minecraft.scoreboard.Scoreboard scoreboard = objective.getScoreboard();
		Collection<Score> collection = scoreboard.getSortedScores(objective);
		List<Score> list = collection.stream().filter(score -> score.getPlayerName() != null && !score.getPlayerName().startsWith("#")).collect(Collectors.toList());
		Collections.reverse(list);

		boolean showNumbers = getBooleanSetting("Numbers").getValue();

		if (list.size() > 15) {
			collection = Lists.newArrayList(Iterables.skip(list,collection.size() - 15));
		} else {
			collection = list;
		}

		int stringWidth = getFr().getStringWidth(objective.getDisplayName());

		for (Score score : collection) {
			ScorePlayerTeam scoreplayerteam = scoreboard.getPlayersTeam(score.getPlayerName());
			String s = ScorePlayerTeam.formatPlayerName(scoreplayerteam, score.getPlayerName()) + (showNumbers ? ": " + EnumChatFormatting.RED + score.getScorePoints() : "");
			stringWidth = Math.max(stringWidth, getFr().getStringWidth(s));
		}

		int height = (list.size() + 1) * getFr().FONT_HEIGHT;
		float y = getY();
		float x = getX();
		setHeight(height + 3);
		setWidth(stringWidth + 3);

		GUI.drawRect(x, y, x + getWidth(), y + getFr().FONT_HEIGHT + 1, 1342177280 + 1610612736);
		GUI.drawRect(x, y + getFr().FONT_HEIGHT + 1, x + getWidth(), y + getHeight(), 1342177280);
		String s3 = objective.getDisplayName();
		getFr().drawCenteredString(s3, x + getWidth() / 2, y + 1, 553648127);

		list.forEach(score -> {
			int index = list.indexOf(score) + 1;

			float yPos = y + 2 + index * getFr().FONT_HEIGHT;

			ScorePlayerTeam scorePlayerTeam = scoreboard.getPlayersTeam(score.getPlayerName());

			String playerName = ScorePlayerTeam.formatPlayerName(scorePlayerTeam, score.getPlayerName());

			String point = EnumChatFormatting.RED + "" + score.getScorePoints();

			getFr().drawStringWithShadow(playerName, x + 2, yPos, 553648127);

			if (showNumbers) {
				getFr().drawStringWithShadow(point, x + getWidth() - getFr().getStringWidth(point) - 1, yPos, 553648127);
			}
		});
	}

}
