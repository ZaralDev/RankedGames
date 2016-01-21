package fr.zaral.quentixx.rankedgames.ranked;

import org.bukkit.ChatColor;

public class RankedTeam {

	private ChatColor color;
	
	public RankedTeam(ChatColor color) {
		this.color = color;
	}
	
	public ChatColor getColor() {
		return this.color;
	}
}
