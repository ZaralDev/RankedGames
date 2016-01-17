package fr.zaral.quentixx.rankedgames.ranked;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import fr.zaral.quentixx.rankedgames.Main;
import fr.zaral.quentixx.rankedgames.event.PlayerLeaveQueueEvent;
import fr.zaral.quentixx.rankedgames.event.PlayerLeaveRankedEvent;

public class RankedPlayer
{
  private static ArrayList<RankedPlayer> rankedPlayers = new ArrayList();
  private Player player;
  private String playerName;
  private boolean voted;
  private boolean hasKit;
  
  public static RankedPlayer get(String name)
  {
    for (RankedPlayer rankedPlayer : rankedPlayers) {
      if (rankedPlayer.getPlayer().getName().equals(name)) {
        return rankedPlayer;
      }
    }
    return null;
  }
  
  public static void remove(RankedPlayer rankedPlayer)
  {
    if (rankedPlayer.getQueue() != null) {
      Bukkit.getServer().getPluginManager().callEvent(new PlayerLeaveQueueEvent(rankedPlayer.getPlayer(), rankedPlayer.getQueue()));
    } else if (Ranked.get(rankedPlayer) != null) {
      Bukkit.getServer().getPluginManager().callEvent(new PlayerLeaveRankedEvent(rankedPlayer.getPlayer(), Ranked.get(rankedPlayer)));
    }
  }
  
  private boolean inGame = false;
  
  public RankedPlayer(Player player)
  {
    this.player = player;
    this.playerName = player.getName();
    this.voted = false;
    rankedPlayers.add(this);
  }
  
  public boolean hasKit()
  {
    return this.hasKit;
  }
  
  public boolean setKit(boolean hasKit)
  {
    this.hasKit = hasKit;
    return hasKit;
  }
  
  public boolean isVip()
  {
    return this.player.hasPermission(Main.plugin.getConfig().getString("permission.vip"));
  }
  
  public boolean isInGame()
  {
    for (Ranked ranked : Main.rankeds) {
      if (ranked.containsPlayer(this)) {
        return true;
      }
    }
    return false;
  }
  
  public Player getPlayer()
  {
    return this.player;
  }
  
  public String getPlayerName()
  {
    return this.playerName;
  }
  
  public RankedQueue getQueue()
  {
    for (int i = 1; i <= 3; i++)
    {
      RankedQueue queue = RankedType.get(i).getQueue();
      if (queue.containsPlayer(this)) {
        return queue;
      }
    }
    return null;
  }
  
  public RankedType getRankedType()
  {
    for (RankedType type : Main.types) {
      if (type.getQueue().containsPlayer(this)) {
        return type;
      }
    }
    return null;
  }
  
  public void joinQueue(RankedQueue queue)
  {
    removeQueue();
    queue.addPlayer(this);
  }
  
  public void removeQueue()
  {
    if (getQueue() != null) {
      getQueue().removePlayer(this);
    }
  }
  
  public void setVoted(boolean voted)
  {
    this.voted = voted;
  }
  
  public boolean hasVoted()
  {
    return this.voted;
  }
  
  public void del()
  {
    rankedPlayers.remove(this);
  }
}
