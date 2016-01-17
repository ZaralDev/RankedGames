package fr.zaral.quentixx.rankedgames.event;

import fr.zaral.quentixx.rankedgames.ranked.Ranked;
import fr.zaral.quentixx.rankedgames.ranked.RankedPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

public class PlayerLeaveRankedEvent extends PlayerEvent {
	
	private static final HandlerList handlers = new HandlerList();
	private Ranked ranked;
	private RankedPlayer rankedPlayer;
  
	public PlayerLeaveRankedEvent(Player who, Ranked ranked) {
		super(who);
		this.rankedPlayer = RankedPlayer.get(who.getName());
		this.ranked = ranked;
	}
  
	public Ranked getRanked() {
		return this.ranked;
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
