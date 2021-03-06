package fr.zaral.quentixx.rankedgames.listeners;

import java.sql.SQLException;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

import fr.zaral.quentixx.rankedgames.Database;
import fr.zaral.quentixx.rankedgames.RankedGames;
import fr.zaral.quentixx.rankedgames.event.PlayerLeaveQueueEvent;
import fr.zaral.quentixx.rankedgames.event.PlayerLeaveRankedEvent;
import fr.zaral.quentixx.rankedgames.kit.Kit;
import fr.zaral.quentixx.rankedgames.ranked.Ranked;
import fr.zaral.quentixx.rankedgames.ranked.RankedPlayer;
import fr.zaral.quentixx.rankedgames.ranked.RankedQueue;
import fr.zaral.quentixx.rankedgames.utils.PlayerUtils;

public class PlayerListener implements Listener {
	
	@EventHandler
	public void onPlayerLogin(PlayerLoginEvent event) {
		Player player = event.getPlayer();
		String name = player.getName();
		String uuid = player.getUniqueId().toString();
		if (Database.isConnected()) {
			try {
				if (!Database.isRegistredUser(uuid))
					Database.registerUser(name, uuid);
				Database.updateName(name, uuid);
			} catch (SQLException ex) {
				ex.printStackTrace();
			}
		}
	}
	
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		if (PlayerUtils.getRank(player) != null)
			player.setDisplayName(PlayerUtils.getRank(player).getString() + " " + player.getName());
	}
	
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event) {
		Player player = event.getPlayer();
		RankedPlayer rankedPlayer = RankedPlayer.get(player.getName());
		if (rankedPlayer != null)
			RankedPlayer.remove(rankedPlayer);
	}
  
	@EventHandler
	public void onPlayerLeaveQueue(PlayerLeaveQueueEvent event) {
		Player player = event.getPlayer();
		RankedPlayer rankedPlayer = event.getRankedPlayer();
		String queueName = rankedPlayer.getRankedType().getType() + "vs" + rankedPlayer.getRankedType().getType();
		RankedQueue queue = event.getQueue();
		rankedPlayer.removeQueue();
		for (RankedPlayer rankedPlayer1 : queue.getPlayers())
			rankedPlayer1.getPlayer().sendMessage(ChatColor.RED + "" + player.getName() + " a quitté la queue (" + ChatColor.GRAY + queueName + ChatColor.RED + ")");
		rankedPlayer.del();
	}
  
	@EventHandler
	public void onPlayerLeaveRanked(PlayerLeaveRankedEvent event) {
		Player player = event.getPlayer();
		RankedPlayer rankedPlayer = event.getRankedPlayer();
		Ranked ranked = event.getRanked();
		ranked.removePlayer(rankedPlayer);
		ranked.messageGame(ChatColor.RED + "" + player.getName() + " a quitté la partie");
		if (!ranked.isStarted()) {
			ranked.setCanceled(true);
			for (int i = 0; i < ranked.getPlayers().size(); i++) {
				RankedPlayer rankedPlayer2 = (RankedPlayer)ranked.getPlayers().get(i);
				rankedPlayer2.getPlayer().sendMessage(ChatColor.RED + "Il n'y a plus assez de joueurs pour créer la partie, " + ChatColor.GREEN + "retour dans la file d'attente...");
				ranked.removePlayer(rankedPlayer2);
				rankedPlayer2.joinQueue(ranked.getRankedType().getQueue());
			}
		}
	}
  
	@EventHandler
	public void onPlayerDrop(PlayerDropItemEvent event) {
		Player player = event.getPlayer();
		RankedPlayer rankedPlayer = RankedPlayer.get(player.getName());
		if ((rankedPlayer != null) && (rankedPlayer.isInGame()))
			event.setCancelled(true);
	}
  
	@EventHandler
	public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
		if ((event.getDamager().getType() == EntityType.PLAYER) && (event.getEntityType() == EntityType.PLAYER)) {
			Player damager = (Player)event.getDamager();
			Player target = (Player)event.getEntity();
			RankedPlayer rankedDamager = RankedPlayer.get(damager.getName());
			RankedPlayer rankedTarget = RankedPlayer.get(target.getName());
			if ((rankedDamager != null) && (rankedTarget != null)) {
				Ranked ranked = Ranked.get(rankedTarget);
				if ((ranked != null) && (ranked.containsPlayer(rankedTarget)) &&  (!ranked.canPvp()))
					event.setCancelled(true);
			}
		}
	}
  
	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		if (!event.hasItem())
			return;
		ItemStack clicked = event.getItem();
		RankedPlayer rankedTarget = RankedPlayer.get(player.getName());
		if ((rankedTarget != null) && (rankedTarget.isInGame()) && (clicked.hasItemMeta()) && (clicked.getType() == Material.DIAMOND_SWORD) && (!rankedTarget.hasKit()) && (clicked.getItemMeta().hasDisplayName())) {
			event.setCancelled(true);
			player.openInventory(RankedGames.kitsInventory);
		}
	}
	
	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent event) {
		if (event.getEntity() instanceof Player) {
			Player player = (Player) event.getEntity();
			String name = player.getName();
			RankedPlayer rankedPlayer = RankedPlayer.get(name);
			if (rankedPlayer != null) {
				Ranked ranked = Ranked.get(rankedPlayer);
				if (ranked != null)
					ranked.lose(rankedPlayer);
			}
			// Mieux non?
		}
		
		/* Ce code de merde est fait par Zaral :P
		Player player = event.getEntity().getPlayer();
		RankedPlayer rankedPlayer = RankedPlayer.get(player.getName());
		if ((rankedPlayer != null) && (rankedPlayer.isInGame())) {
			//Player killer = event.getEntity().getKiller();
			Ranked ranked = Ranked.get(rankedPlayer);
			ranked.messageGame(ChatColor.RED + player.getName() + " a perdu !");
			ranked.removePlayer(rankedPlayer);
			if (ranked.getPlayers().size() == 1) {
				RankedPlayer winer = ranked.getPlayers().get(0);
				for (Player p : winer.getPlayer().getWorld().getPlayers()) {
					p.sendMessage(ChatColor.GOLD + winer.getPlayerName() + " a gagné la partie !");
				}
			}
		}*/
	}
	
	@EventHandler
	public void onInventoryClick(InventoryClickEvent event) {
		if ((event.getWhoClicked() instanceof Player)) {
			Player player = (Player)event.getWhoClicked();
			RankedPlayer rankedPlayer = RankedPlayer.get(player.getName());
			if (rankedPlayer != null) {
				Ranked ranked = Ranked.get(rankedPlayer);
				if (ranked == null)
					return;
				if (event.getInventory().getName().equals(RankedGames.getInventory(ranked.getRankedType()).getName())) {
					event.setCancelled(true);
					if ((ranked.isVoting()) && (!rankedPlayer.hasVoted()) && (event.getCurrentItem() != null) && (event.getCurrentItem().hasItemMeta()) && (event.getCurrentItem().getItemMeta().hasDisplayName())) {
						String getMap = event.getCurrentItem().getItemMeta().getDisplayName();
						player.sendMessage(ChatColor.YELLOW + "Vous avez voté pour la map " + getMap);
						rankedPlayer.setVoted(true);
						ranked.addVote(getMap);
						player.closeInventory();
					}
				} else if (event.getInventory().getName().equals(RankedGames.kitsInventory.getName())) {
					event.setCancelled(true);
					Kit kit = null;
					for (Kit k : Kit.getKits()) {
						if ((event.getCurrentItem().hasItemMeta()) && (event.getCurrentItem().getItemMeta().hasDisplayName()) && (event.getCurrentItem().getItemMeta().getDisplayName().equals(k.getIcon().getItemMeta().getDisplayName()))) {
							kit = k;
							break;
						}
					}
					if (kit == null)
						return;
					kit.setStuff(player);
					rankedPlayer.setKit(true);
					player.sendMessage(ChatColor.YELLOW + "Vous avez choisi le kit " + ChatColor.GREEN + kit.getName());
				}
			}
		}
	}
}
