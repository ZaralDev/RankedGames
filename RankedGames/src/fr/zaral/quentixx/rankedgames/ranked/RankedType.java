package fr.zaral.quentixx.rankedgames.ranked;

import fr.zaral.quentixx.rankedgames.Main;

public class RankedType {
	
	public static RankedType get(int type) {
		for (RankedType t : Main.types)
			if (t.getType() == type)
				return t;
		return null;
	}
	
	private int type;
	private RankedQueue queue;
  
	public RankedType(int type) {
		this.type = type;
		queue = new RankedQueue();
		queue.setType(this);
		queue.setSlots(type * 2);
	}
  
	public RankedQueue getQueue() {
		return this.queue;
	}
  
	public int getType() {
		return this.type;
	}
}
