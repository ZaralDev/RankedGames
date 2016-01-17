package fr.zaral.quentixx.rankedgames.ranked;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import fr.zaral.quentixx.rankedgames.kit.Kit;
import fr.zaral.quentixx.rankedgames.Main;
import fr.zaral.quentixx.rankedgames.utils.MapUtils;
import fr.zaral.quentixx.rankedgames.utils.PlayerUtils;

public class Ranked {
	
	private RankedType type;
	private ArrayList<RankedPlayer> players;

	public static Ranked get(RankedPlayer rankedPlayer) {
		for (Ranked ranked : Main.rankeds)
			if (ranked.containsPlayer(rankedPlayer))
				return ranked;
		return null;
	}
  
	private HashMap<String, Integer> mapVote = new HashMap<>();
	private boolean voting;
	private boolean canPvp = false;
	private boolean canceled = false;
	private String gameMap = "";
	private boolean started;
	public static int id = 0;
  
	public Ranked(RankedType type, ArrayList<RankedPlayer> players) {
		this.type = type;
		this.players = players;
		voting = false;
		started = false;
		Main.rankeds.add(this);
	}
  
	public RankedType getRankedType() {
		return this.type;
	}
  
	public boolean canPvp() {
		return this.canPvp;
	}
  
	public void setPvp(boolean pvp) {
		this.canPvp = pvp;
	}
  
	public boolean isVoting() {
		return this.voting;
	}
  
	public ArrayList<RankedPlayer> getPlayers() {
		return this.players;
	}
  
	public boolean containsMap(String name) {
		if (mapVote.containsKey(name))
			return true;
		return false;
	}
  
	public boolean isStarted() {
		return this.started;
	}
  
	public boolean setStarted(boolean started) {
		this.started = started;
		return started;
	}
  
	public void addVote(String map) {
		if (!containsMap(map))
			mapVote.put(map, Integer.valueOf(1));
		else
			mapVote.put(map, Integer.valueOf(((Integer) mapVote.get(map)).intValue() + 1));
		if (checkAllVoted())
			endVote();
	}
  
	public void setCanceled(boolean canceled) {
		this.canceled = canceled;
	}
  
	public boolean containsPlayer(RankedPlayer rankedPlayer) {
		return this.players.contains(rankedPlayer);
	}
  
	public boolean checkAllVoted() {
		int vote = 0;
		for (Iterator<Integer> localIterator = this.mapVote.values().iterator(); localIterator.hasNext();) {
			int value = ((Integer)localIterator.next()).intValue();
			vote += value;
		}
		if (vote == this.players.size())
			return true;
		return false;
	}
  
	public void removePlayer(RankedPlayer rankedPlayer) {
		if (containsPlayer(rankedPlayer))
			this.players.remove(rankedPlayer);
	}
  
	private String getCheckVote() {
		int maxValue = 0;
		String idMap1 = null;
		for (Iterator<Integer> localIterator = this.mapVote.values().iterator(); localIterator.hasNext();) {
			int value = ((Integer)localIterator.next()).intValue();
			if (value > maxValue)
				maxValue = value;
		}
		for (Map.Entry<String, Integer> entry : this.mapVote.entrySet()) {
			String map = (String)entry.getKey();
			Integer score = (Integer)entry.getValue();
			if (maxValue == score.intValue())
				idMap1 = map;
		}
		if (idMap1 == null) {
			String[] mapList = MapUtils.getAllExistingMap();
			String[] validMap = new String[mapList.length];
			for (int i = 0; i < mapList.length; i++)
				if (MapUtils.isValidMap(mapList[i], this.type))
					validMap[i] = mapList[i];
			idMap1 = validMap[fr.zaral.quentixx.rankedgames.utils.CodeUtils.randomInt(0, validMap.length - 1)];
		}
		return idMap1;
	}
  
	public void endVote() {
		if (this.voting) {
			this.voting = false;
			String winMap = getCheckVote();
			for (RankedPlayer name : players) {
				Player player = name.getPlayer();
				player.closeInventory();
				player.sendMessage(ChatColor.YELLOW + "La map choisie est: " + ChatColor.GREEN + winMap);
			}
			this.gameMap = winMap;
			startGame(winMap);
		}
	}
  
	public void startVote() {
		messageGame(ChatColor.GOLD + "Le choix de la map va commencer dans 5 secondes");
		voting = true;
		if (!this.canceled) {
			Bukkit.getScheduler().runTaskLater(Main.plugin, new Runnable() {
				public void run() {
					if (!Ranked.this.canceled) {
						for (RankedPlayer rankedPlayer : Ranked.this.players) {
							Player player = rankedPlayer.getPlayer();
							player.openInventory(Main.getInventory(Ranked.this.getRankedType()));
						}
						Bukkit.getScheduler().runTaskLater(Main.plugin, new Runnable() {
							public void run() {
								if (!Ranked.this.canceled) {
									Ranked.this.endVote();
								}
							}
						}, 600L);
					}
				}
			}, 100L);
		}
	}
  
	public void startGame(String mapName) {
		setStarted(true);
		String mapOriginalName = mapName;
		mapName = id + "_" + mapName;
		id += 1;
		Bukkit.getServer().broadcastMessage(mapName);
		MapUtils.copyFile(Main.mapsDir + File.separator + mapOriginalName, Main.backupDir + File.separator + mapName);
		World world = Bukkit.getServer().createWorld(new WorldCreator(Main.backupDir + File.separator + mapName));
		world.setAutoSave(false);
		world.setTime(0L);
		switch (getRankedType().getType()) {
		case 1: 
			break;
		case 2: 
			break;
		case 3: 
			break;
		}
		double x = Main.plugin.getConfig().getDouble("Maps." + this.gameMap + ".x");
		double y = Main.plugin.getConfig().getDouble("Maps." + this.gameMap + ".y");
		double z = Main.plugin.getConfig().getDouble("Maps." + this.gameMap + ".z");
		Location loc = new Location(world, x, y, z);
		ItemStack item = new ItemStack(Material.DIAMOND_SWORD);
		ItemMeta itemMeta = item.getItemMeta();
		itemMeta.setDisplayName(ChatColor.GOLD + "Choisir un kit");
		item.setItemMeta(itemMeta);
		for (RankedPlayer rankedPlayer : getPlayers()) {
			Player player = rankedPlayer.getPlayer();
			player.teleport(loc);
			PlayerUtils.goNaked(player);
			player.getInventory().addItem(new ItemStack[] { item });
		}
		messageGame(ChatColor.YELLOW + "Vous avez 15 secondes pour choisir un kit!");
		Bukkit.getServer().getScheduler().runTaskLater(Main.plugin, new Runnable() {
			public void run() {
				for (final RankedPlayer rankedPlayer : getPlayers()) {
					if (!rankedPlayer.hasKit()) {
						if (Kit.getKits().isEmpty())
							return;
						Kit.getKits().get(0).setStuff(rankedPlayer.getPlayer());
						rankedPlayer.setKit(true);
						rankedPlayer.getPlayer().sendMessage(ChatColor.YELLOW + "La partie commence dans 5 secondes");
					}
					Bukkit.getScheduler().runTaskLater(Main.plugin, new Runnable() {
						public void run() {
							for (RankedPlayer rankedPlayer : Ranked.this.getPlayers()) {
								rankedPlayer.getPlayer().sendMessage("�9Que le meilleur gagne !");
								Ranked.this.canPvp = true;
							}
						}
					}, 100L);
				}
			}
		}, 300L);
	}
  
	public void messageGame(String msg) {
		for (RankedPlayer rankedPlayer : players) {
			Player player = rankedPlayer.getPlayer();
			player.sendMessage(msg);
		}
	}
}
