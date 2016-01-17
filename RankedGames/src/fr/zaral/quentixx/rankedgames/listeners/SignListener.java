package fr.zaral.quentixx.rankedgames.listeners;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import fr.zaral.quentixx.rankedgames.SpamLevel;
import fr.zaral.quentixx.rankedgames.ranked.RankedPlayer;
import fr.zaral.quentixx.rankedgames.ranked.RankedType;

public class SignListener implements Listener {

	@EventHandler
	public void onSignChange(SignChangeEvent event) {
		Player player = event.getPlayer();
		if (player.hasPermission("rankedgames.create.sign")) {
			Sign sign = (Sign)event.getBlock().getState();
			if ((event.getLine(0).equalsIgnoreCase("[RG]")) && (!event.getLine(1).isEmpty())) {
				int type = 0;
				try {
					type = Integer.parseInt(event.getLine(1));
					if ((type < 1) || (type > 3)) {
						player.sendMessage(ChatColor.RED + "Impossible de cr�er le panneau (Le type de pvp doit être égal à 1, 2 ou 3)");
						event.getBlock().breakNaturally();
						return;
					}
					event.setLine(0, ChatColor.GREEN + "[RankedGames]");
					event.setLine(1, ChatColor.DARK_RED + "" +  type + "vs" + type);
					sign.update(true);
				} catch (NumberFormatException ex) {
					event.getBlock().breakNaturally();
					player.sendMessage(ChatColor.RED + "Impossible de créer le panneau (Valeur num�rique requise dans le type de pvp)");
				}
			}
		}
	}
  
	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		if ((event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) && ((event.getClickedBlock().getType() == Material.WALL_SIGN) || (event.getClickedBlock().getType() == Material.SIGN))) {
			Sign sign = (Sign)event.getClickedBlock().getState();
			if (sign == null)
				return;
			if (sign.getLine(0).equalsIgnoreCase(ChatColor.GREEN + "[RankedGames]")) {
				SpamLevel.put(player);
				if (SpamLevel.isSpam(player)) {
					player.sendMessage(SpamLevel.getMessage());
					return;
				}
				if (!sign.getLine(1).isEmpty())
				{
					String line1 = sign.getLine(1);
					RankedType type = null;
					if (line1.equals(ChatColor.DARK_RED + "1vs1"))
						type = RankedType.get(1);
					else if (line1.equals(ChatColor.DARK_RED + "2vs2"))
						type = RankedType.get(2);
					else if (line1.equals(ChatColor.DARK_RED + "3vs3"))
						type = RankedType.get(3);
					else {
						player.sendMessage(ChatColor.RED + "Erreur, merci de contacter un administrateur");
						return;
					}
					if (type == null) {
						player.sendMessage(ChatColor.RED + "Impossible de rejoindre la file d'attente!");
						return;
					}
					RankedPlayer rankedPlayer = RankedPlayer.get(player.getName());
					if (rankedPlayer == null)
						rankedPlayer = new RankedPlayer(player);
					if (rankedPlayer.isInGame()) {
						player.sendMessage(ChatColor.RED + "Vous êtes déjà dans une partie");
						return;
					}
					if (rankedPlayer.getQueue() == null) {
						rankedPlayer.joinQueue(type.getQueue());
						player.sendMessage(ChatColor.GREEN + "Vous avez rejoint la queue: " + ChatColor.GRAY + type.getType() + "vs" + type.getType());
						if (rankedPlayer.isVip())
							player.sendMessage(ChatColor.GREEN + "Vous êtes en position " + ChatColor.YELLOW + "prioritaire");
					} else if (rankedPlayer.getQueue() == type.getQueue()) {
						player.sendMessage(ChatColor.RED + "Vous avez quitté la queue: " + ChatColor.GRAY + rankedPlayer.getRankedType().getType() + "vs" + rankedPlayer.getRankedType().getType());
						rankedPlayer.removeQueue();
					} else {
						player.sendMessage(ChatColor.GREEN + "Vous avez quitté votre ancienne queue pour rejoindre " + ChatColor.GRAY + type.getType() + "vs" + type.getType());
						if (rankedPlayer.isVip())
							player.sendMessage(ChatColor.GREEN + "Vous êtes en position " + ChatColor.YELLOW + "prioritaire");
						rankedPlayer.removeQueue();
						rankedPlayer.joinQueue(type.getQueue());
					}
				}
			}
		}
	}
}
