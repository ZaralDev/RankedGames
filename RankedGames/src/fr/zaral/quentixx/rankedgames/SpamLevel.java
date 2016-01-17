package fr.zaral.quentixx.rankedgames;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

public class SpamLevel implements Runnable {
	
	public BukkitTask task;
	private static HashMap<String, Integer> level;
  
	public SpamLevel() {
		this.task = Bukkit.getServer().getScheduler().runTaskTimer(Main.plugin, this, 0L, 40L);
		level = new HashMap<>();
	}
  
	public void run() {
		for (Player player : Bukkit.getServer().getOnlinePlayers())
			if (level.containsKey(player.getName()))
				level.remove(player.getName());
	}
  
	public static void put(Player player) {
		level.put(player.getName(), Integer.valueOf(level.containsKey(player.getName()) ? ((Integer)level.get(player.getName())).intValue() + 1 : 1));
	}
  
	public static boolean contains(Player player) {
		return level.containsKey(player.getName());
	}
  
	public static boolean isSpam(Player player) {
		if ((contains(player)) && (((Integer)level.get(player.getName())).intValue() > 1))
			return true;
		return false;
	}
  
	public static String getMessage() {
		return ChatColor.RED + "Merci de ne pas spam!";
	}
}
