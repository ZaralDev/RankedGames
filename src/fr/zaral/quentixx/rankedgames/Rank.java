package fr.zaral.quentixx.rankedgames;

import org.bukkit.ChatColor;

public enum Rank {
	
	/**
	 * Created by Quentixx at 17/01/2016
	 */

	BRONZE(ChatColor.RED + "[bronze]", 200),
	
	IRON(ChatColor.GRAY + "[iron]", 400),
	
	GOLD(ChatColor.GOLD + "[gold]", 600),
	
	DIAMOND(ChatColor.AQUA + "[diamond]", 1000);
	
	private String string;
	private int ratioRequired;
	
	private Rank(String string, int ratioRequired) {
		this.string = string;
		this.ratioRequired = ratioRequired;
	}
	
	public String getString() {
		return this.string + ChatColor.RESET;
	}
	
	public int getRatioRequired() {
		return this.ratioRequired;
	}
}
