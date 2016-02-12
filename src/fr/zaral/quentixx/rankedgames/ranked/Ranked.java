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
import fr.zaral.quentixx.rankedgames.RankedGames;
import fr.zaral.quentixx.rankedgames.utils.MapUtils;
import fr.zaral.quentixx.rankedgames.utils.PlayerUtils;

public class Ranked {
	
	private RankedType type;
	private ArrayList<RankedPlayer> players;

	public static Ranked get(RankedPlayer rankedPlayer) {
		for (Ranked ranked : RankedGames.rankeds)
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
	private World world;
  
	public Ranked(RankedType type, ArrayList<RankedPlayer> players) {
		this.type = type;
		this.players = players;
		voting = false;
		started = false;
		RankedGames.rankeds.add(this);
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
	
	public World getWorld() {
		return this.world;
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
			Bukkit.getScheduler().runTaskLater(RankedGames.plugin, new Runnable() {
				public void run() {
					if (!Ranked.this.canceled) {
						for (RankedPlayer rankedPlayer : Ranked.this.players) {
							Player player = rankedPlayer.getPlayer();
							player.openInventory(RankedGames.getInventory(Ranked.this.getRankedType()));
						}
						Bukkit.getScheduler().runTaskLater(RankedGames.plugin, new Runnable() {
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
		MapUtils.copyFile(RankedGames.mapsDir + File.separator + mapOriginalName, RankedGames.backupDir + File.separator + mapName);
		world = Bukkit.getServer().createWorld(new WorldCreator(RankedGames.backupDir + File.separator + mapName));
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
		double x = RankedGames.plugin.getConfig().getDouble("Maps." + this.gameMap + ".x");
		double y = RankedGames.plugin.getConfig().getDouble("Maps." + this.gameMap + ".y");
		double z = RankedGames.plugin.getConfig().getDouble("Maps." + this.gameMap + ".z");
		Location loc = new Location(world, x, y, z);
		ItemStack item = new ItemStack(Material.DIAMOND_SWORD);
		ItemMeta itemMeta = item.getItemMeta();
		itemMeta.setDisplayName(ChatColor.GOLD + "Choisir un kit");
		item.setItemMeta(itemMeta);
		for (RankedPlayer rankedPlayer : getPlayers()) {
			// TODO A FAIRE!
			rankedPlayer.setTeam(getRankedType().getTeam(ChatColor.RED));
			/*Player player = rankedPlayer.getPlayer();
			player.teleport(loc);
			PlayerUtils.goNaked(player);
			player.getInventory().addItem(new ItemStack[] { item });*/
		}
		messageGame(ChatColor.YELLOW + "Vous avez 15 secondes pour choisir un kit!");
		Bukkit.getServer().getScheduler().runTaskLater(RankedGames.plugin, new Runnable() {
			public void run() {
				for (final RankedPlayer rankedPlayer : getPlayers()) {
					if (!rankedPlayer.hasKit()) {
						if (Kit.getKits().isEmpty())
							return;
						Kit.getKits().get(0).setStuff(rankedPlayer.getPlayer());
						rankedPlayer.setKit(true);
						rankedPlayer.getPlayer().sendMessage(ChatColor.YELLOW + "La partie commence dans 5 secondes");
					}
					Bukkit.getScheduler().runTaskLater(RankedGames.plugin, new Runnable() {
						public void run() {
							for (RankedPlayer rankedPlayer : Ranked.this.getPlayers()) {
								rankedPlayer.getPlayer().sendMessage("ï¿½9Que le meilleur gagne !");
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
	
	public void lose(RankedPlayer rankedPlayer) {
		String name = rankedPlayer.getPlayerName();
		rankedPlayer.getPlayer().sendMessage(ChatColor.RED + "Vous avez perdu!");
		removePlayer(rankedPlayer);
		messageGame(ChatColor.RED + "" + name + " a perdu!");
		RankedTeam winTeam = null;
		if (getPlayers().size() <= getRankedType().getType()) {
			int i = 0;
			for (RankedPlayer rankedP : getPlayers()) {
				for (RankedTeam team : getRankedType().getTeams()) {
					if (rankedP.getTeam().equals(team))
						i++;
					if (i == getPlayers().size()) {
						winTeam = team;
						break;
					}
				}
			}
		}
		if (winTeam != null)
			win(winTeam);
	}
	
	public void win(RankedTeam winTeam) {
		// TODO Code sûrement pas stable
		ArrayList<RankedPlayer> winners = new ArrayList<>();
		for (RankedPlayer rankedPlayer : getPlayers())
			if (rankedPlayer.getTeam().equals(winTeam))
				winners.add(rankedPlayer);
		String winnersString = "";
		int i = 0;
		for (RankedPlayer winner : winners) {
			winnersString += winner.getPlayerName() + (i == winners.size() ? "" : ", ");
			i++;
		}
		messageGame(ChatColor.AQUA + "Duel remporté! (" + winnersString + ")");
		reset();
	}
	
	public void reset() {
		for (RankedPlayer rankedPlayer : getPlayers()) {
			PlayerUtils.tpSpawn(rankedPlayer.getPlayer());
			rankedPlayer.del();
		}
		Bukkit.getServer().unloadWorld(getWorld(), false);
		// TODO A finir!
	}
}
