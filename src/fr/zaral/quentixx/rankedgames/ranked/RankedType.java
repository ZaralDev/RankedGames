package fr.zaral.quentixx.rankedgames.ranked;

import org.bukkit.ChatColor;

import fr.zaral.quentixx.rankedgames.Main;

public class RankedType {
	
	public static RankedType get(int type) {
		for (RankedType t : Main.types)
			if (t.getType() == type)
				return t;
		return null;
	}
	
	private int type;
	private RankedQueue queue;
	private RankedTeam[] teams;
  
	public RankedType(int type, RankedTeam[] teams) {
		this.type = type;
		this.teams = teams;
		queue = new RankedQueue();
		queue.setType(this);
		queue.setSlots(type * 2);
	}
  
	public RankedQueue getQueue() {
		return this.queue;
	}
  
	public int getType() {
		return this.type;
	}
	
	public RankedTeam[] getTeams() {
		return this.teams;
	}
	
	public RankedTeam getTeam(ChatColor color) {
		for (RankedTeam team : teams)
			if (team.getColor().equals(color))
				return team;
		return null;
	}
}
