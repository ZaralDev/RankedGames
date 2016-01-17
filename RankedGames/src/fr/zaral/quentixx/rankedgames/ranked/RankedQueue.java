package fr.zaral.quentixx.rankedgames.ranked;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;

import fr.zaral.quentixx.rankedgames.Main;

public class RankedQueue
  implements Runnable
{
  private int lastResult = 0;
  private int slots = 0;
  private int lastpos = 0;
  private RankedType type;
  private ArrayList<RankedPlayer> clients = new ArrayList();
  BukkitTask task = Bukkit.getServer().getScheduler().runTaskTimer(Main.plugin, this, 140L, 140L);
  
  public void run()
  {
    if (this.lastResult != getPlayers().size())
    {
      this.lastResult = getPlayers().size();
      boolean canCreateGame = false;
      ArrayList<RankedPlayer> rankedPlayers = new ArrayList();
      ArrayList<RankedPlayer> rankedVipPlayers = new ArrayList();
      for (RankedPlayer rankedPlayer : getPlayers())
      {
        if (rankedPlayer.isVip()) {
          rankedVipPlayers.add(rankedPlayer);
        } else {
          rankedPlayers.add(rankedPlayer);
        }
        if (rankedPlayers.size() + rankedVipPlayers.size() >= getSlots()) {
          canCreateGame = true;
        }
        if (!canCreateGame)
        {
          if (getPos(rankedPlayer) != this.lastpos)
          {
            this.lastpos = getPos(rankedPlayer);
            if (!rankedPlayer.isVip()) {
              rankedPlayer.getPlayer().sendMessage("�aVous �tes en position �E" + this.lastpos + "/" + getPlayers().size());
            }
          }
        }
        else
        {
          ArrayList<RankedPlayer> gamers = new ArrayList();
          for (RankedPlayer vip : rankedVipPlayers) {
            gamers.add(vip);
          }
          for (RankedPlayer normal : rankedPlayers) {
            gamers.add(normal);
          }
          for (RankedPlayer gamer : gamers)
          {
            gamer.removeQueue();
            Bukkit.broadcastMessage(gamer.getPlayerName() + (gamer.isVip() ? " �AisVip" : " �cisNotVip"));
          }
          Ranked ranked = new Ranked(getType(), gamers);
          ranked.startVote();
          return;
        }
      }
    }
  }
  
  public int getPos(RankedPlayer rankedPlayer)
  {
    for (int i = 0; i < this.clients.size(); i++) {
      if (((RankedPlayer)this.clients.get(i)).equals(rankedPlayer)) {
        return i + 1;
      }
    }
    return 0;
  }
  
  public void addPlayer(RankedPlayer rankedPlayer)
  {
    if (!this.clients.contains(rankedPlayer)) {
      this.clients.add(rankedPlayer);
    }
  }
  
  public void removePlayer(RankedPlayer rankedPlayer)
  {
    if (this.clients.contains(rankedPlayer)) {
      this.clients.remove(rankedPlayer);
    }
  }
  
  public boolean containsPlayer(RankedPlayer rankedPlayer)
  {
    return this.clients.contains(rankedPlayer);
  }
  
  public boolean contains(String name)
  {
    for (RankedPlayer player : this.clients) {
      if (player.getPlayer().getName().equals(name)) {
        return true;
      }
    }
    return false;
  }
  
  public ArrayList<RankedPlayer> getPlayers()
  {
    return this.clients;
  }
  
  public int getSlots()
  {
    return this.slots;
  }
  
  public void setSlots(int slots)
  {
    this.slots = slots;
  }
  
  public RankedType setType(RankedType type)
  {
    this.type = type;
    return type;
  }
  
  public RankedType getType()
  {
    return this.type;
  }
}
