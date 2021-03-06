package fr.zaral.quentixx.rankedgames.ranked;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.scheduler.BukkitTask;

import fr.zaral.quentixx.rankedgames.RankedGames;

public class RankedQueue implements Runnable {
	
	private int lastResult = 0;
	private int slots = 0;
	private int lastpos = 0;
	private RankedType type;
	private ArrayList<RankedPlayer> clients = new ArrayList<>();
	BukkitTask task = Bukkit.getServer().getScheduler().runTaskTimer(RankedGames.plugin, this, 140L, 140L);
  
	public void run() {
		if (lastResult != getPlayers().size()) {
			lastResult = getPlayers().size();
			boolean canCreateGame = false;
			ArrayList<RankedPlayer> rankedPlayers = new ArrayList<>();
			ArrayList<RankedPlayer> rankedVipPlayers = new ArrayList<>();
			for (RankedPlayer rankedPlayer : getPlayers()) {
				if (rankedPlayer.isVip())
					rankedVipPlayers.add(rankedPlayer);
				else
					rankedPlayers.add(rankedPlayer);
				if (rankedPlayers.size() + rankedVipPlayers.size() >= getSlots())
					canCreateGame = true;
				if (!canCreateGame) {
					if (getPos(rankedPlayer) != lastpos) {
						lastpos = getPos(rankedPlayer);
						if (!rankedPlayer.isVip())
							rankedPlayer.getPlayer().sendMessage(ChatColor.GREEN + "Vous êtes en position " + ChatColor.YELLOW + lastpos + "/" + getPlayers().size());
					}
				} else {
					ArrayList<RankedPlayer> gamers = new ArrayList<>();
					for (RankedPlayer vip : rankedVipPlayers)
						gamers.add(vip);
					for (RankedPlayer normal : rankedPlayers)
						gamers.add(normal);
					for (RankedPlayer gamer : gamers)
						gamer.removeQueue();
					Ranked ranked = new Ranked(getType(), gamers);
					ranked.startVote();
					return;
				}
			}
		}
	}
  
	public int getPos(RankedPlayer rankedPlayer) {
		for (int i = 0; i < clients.size(); i++)
			if (((RankedPlayer) clients.get(i)).equals(rankedPlayer))
				return i + 1;
		return 0;
	}
  
	public void addPlayer(RankedPlayer rankedPlayer) {
		if (!clients.contains(rankedPlayer))
			clients.add(rankedPlayer);
	}
  
	public void removePlayer(RankedPlayer rankedPlayer) {
		if (clients.contains(rankedPlayer))
			clients.remove(rankedPlayer);
	}
  
	public boolean containsPlayer(RankedPlayer rankedPlayer) {
		return clients.contains(rankedPlayer);
	}
  
	public boolean contains(String name) {
		for (RankedPlayer player : clients)
			if (player.getPlayer().getName().equals(name))
				return true;
		return false;
	}
  
	public ArrayList<RankedPlayer> getPlayers() {
		return this.clients;
	}
  
	public int getSlots() {
		return this.slots;
	}
  
	public void setSlots(int slots) {
		this.slots = slots;
	}
	
	public RankedType setType(RankedType type) {
		this.type = type;
		return type;
	}
  
	public RankedType getType() {
		return this.type;
	}
}
