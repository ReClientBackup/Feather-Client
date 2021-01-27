package com.murengezi.feather.Module.Modules;

import com.darkmagician6.eventapi.EventTarget;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.murengezi.feather.Event.RenderScoreboardEvent;
import com.murengezi.feather.Module.Adjustable;
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
public class Scoreboard extends Adjustable {

	public Scoreboard() {
		super(100, 100);
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
		int maxWidth = getFr().getStringWidth(objective.getDisplayName());

		for (Score score : list) {
			ScorePlayerTeam scoreplayerteam = scoreboard.getPlayersTeam(score.getPlayerName());
			String s = ScorePlayerTeam.formatPlayerName(scoreplayerteam, score.getPlayerName()) + (numbers ? ": " + EnumChatFormatting.RED + score.getScorePoints() : "");
			maxWidth = Math.max(maxWidth, getFr().getStringWidth(s));
		}


		maxWidth += numbers ? 10 : 3;

		int x = resolution.getScaledWidth() - maxWidth - 1;
		int y = resolution.getScaledHeight() / 2 - ((list.size() + 1) * (getFr().FONT_HEIGHT + 1) / 2);



		int i = 0;
		Gui.drawRect(x, y + i, x + maxWidth, y + i + getFr().FONT_HEIGHT + 1, Integer.MIN_VALUE);
		Gui.drawRect(x, y + i, x + maxWidth, y + i + getFr().FONT_HEIGHT + 1, Integer.MIN_VALUE);
		getFr().drawCenteredString(objective.getDisplayName(), x + maxWidth / 2, y + i + 1, 0xffffff);
		i += getFr().FONT_HEIGHT + 1;


		for (Score score : Lists.reverse(list)) {
			Gui.drawRect(x, y + i, x + maxWidth, y + i + getFr().FONT_HEIGHT + 1, Integer.MIN_VALUE);

			getFr().drawStringWithShadow(score.getPlayerName(), x + 2, y + i + 1, 0xffffff);

			if (numbers) {
				String points = "" + EnumChatFormatting.RED + score.getScorePoints();
				getFr().drawStringWithShadow(points, x + maxWidth - getFr().getStringWidth(points) - 1, y + i + 1, 0xffffff);
			}

			i += getFr().FONT_HEIGHT + 1;
		}

		setWidth(maxWidth);
		setHeight(i);

		/*if (list.size() > 15) {
			collection = Lists.newArrayList(Iterables.skip(list, collection.size() - 15));
		} else {
			collection = list;
		}

		int stringWidth = getFr().getStringWidth(objective.getDisplayName());

		for (Score score : collection) {
			ScorePlayerTeam scoreplayerteam = scoreboard.getPlayersTeam(score.getPlayerName());
			String s = ScorePlayerTeam.formatPlayerName(scoreplayerteam, score.getPlayerName()) + ": " + EnumChatFormatting.RED + score.getScorePoints();
			stringWidth = Math.max(stringWidth, getFr().getStringWidth(s));
		}

		int height = collection.size() * getFr().FONT_HEIGHT;
		int y = resolution.getScaledHeight() / 2 + height / 3;
		int offsetRight = 3;
		int x = resolution.getScaledWidth() - stringWidth - offsetRight;
		int index = 0;

		for (Score score1 : collection) {
			++index;
			ScorePlayerTeam scoreplayerteam1 = scoreboard.getPlayersTeam(score1.getPlayerName());
			String s1 = ScorePlayerTeam.formatPlayerName(scoreplayerteam1, score1.getPlayerName());
			String s2 = EnumChatFormatting.RED + "" + score1.getScorePoints();
			int k = y - index * getFr().FONT_HEIGHT;
			int l = resolution.getScaledWidth() - offsetRight + 2;
			Gui.drawRect(x - 2, k, l, k + getFr().FONT_HEIGHT, 1342177280);
			getFr().drawString(s1, x, k, 553648127);
			getFr().drawString(s2, l - getFr().getStringWidth(s2), k, 553648127);

			if (index == collection.size()) {
				String s3 = objective.getDisplayName();
				Gui.drawRect(x - 2, k - getFr().FONT_HEIGHT - 1, l, k - 1, 1610612736);
				Gui.drawRect(x - 2, k - 1, l, k, 1342177280);
				getFr().drawString(s3, x + stringWidth / 2 - getFr().getStringWidth(s3) / 2, k - getFr().FONT_HEIGHT, 553648127);
			}
		}*/
	}

}
