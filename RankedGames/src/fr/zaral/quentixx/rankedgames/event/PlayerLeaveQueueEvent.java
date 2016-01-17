package fr.zaral.quentixx.rankedgames.event;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

import fr.zaral.quentixx.rankedgames.ranked.RankedPlayer;
import fr.zaral.quentixx.rankedgames.ranked.RankedQueue;

public class PlayerLeaveQueueEvent extends PlayerEvent {
	
	private static final HandlerList handlers = new HandlerList();
	private RankedQueue queue;
	private RankedPlayer rankedPlayer;
  
	public PlayerLeaveQueueEvent(Player who, RankedQueue queue) {
		super(who);
		this.rankedPlayer = RankedPlayer.get(who.getName());
		this.queue = queue;
	}
  
	public RankedQueue getQueue() {
		return this.queue;
	}
  
	public RankedPlayer getRankedPlayer() {
		return this.rankedPlayer;
	}
  
	public HandlerList getHandlers() {
		return handlers;
	}
  
	public static HandlerList getHandlerList() {
		return handlers;
	}
}
