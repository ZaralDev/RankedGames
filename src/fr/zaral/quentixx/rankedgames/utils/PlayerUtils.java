package fr.zaral.quentixx.rankedgames.utils;

import java.sql.SQLException;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;

import fr.zaral.quentixx.rankedgames.Database;
import fr.zaral.quentixx.rankedgames.Rank;

public class PlayerUtils {
	
	public static void goNaked(Player player) {
		PlayerInventory inv = player.getInventory();
		inv.setArmorContents(new ItemStack[4]);
		inv.clear();
		player.setFoodLevel(20);
		player.setBedSpawnLocation(null);
		player.setHealth(20.0D);
		player.setFireTicks(0);
		player.setExp(0.0F);
		player.setLevel(0);
		player.setFallDistance(0.0F);
		for (PotionEffect effect : player.getActivePotionEffects())
			player.removePotionEffect(effect.getType());
	}
	
	public static Rank getRank(Player player) {
		if (Database.isConnected()) {
			try {
				for (Rank rank : Rank.values())
					if (Database.getRatio(player.getName()) >= rank.getRatioRequired())
						return rank;
			} catch (SQLException ex) {
				ex.printStackTrace();
			}
		}
		return null;
	}
	
	public static void tpSpawn(Player player) {
		goNaked(player);
		World world = Bukkit.getServer().getWorlds().get(0);
		player.teleport(world.getSpawnLocation());
		player.sendMessage(ChatColor.GREEN + "Téléportation au spawn!");
	}
}
