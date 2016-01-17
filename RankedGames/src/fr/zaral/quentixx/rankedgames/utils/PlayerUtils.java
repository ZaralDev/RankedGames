package fr.zaral.quentixx.rankedgames.utils;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;

public class PlayerUtils
{
  public static void goNaked(Player player)
  {
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
    for (PotionEffect effect : player.getActivePotionEffects()) {
      player.removePotionEffect(effect.getType());
    }
  }
}
