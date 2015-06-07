package uk.co.gorbb.qwicktree.util.debug;

import java.util.HashMap;

import org.bukkit.entity.Player;

import uk.co.gorbb.qwicktree.util.Message;

public class Debugger {
	private static HashMap<Player, Debugger> instances = new HashMap<Player, Debugger>();
	
	public static Debugger get(Player player) {
		if (instances.get(player) == null)
			instances.put(player, new Debugger(player));
		
		return instances.get(player);
	}
	
	public static void remove(Player player) {
		instances.remove(player);
	}
	
	
	
	private Player player;
	private HashMap<String, Integer> stages;
	private boolean enabled;
	
	private Debugger(Player player) {
		this.player = player;
		
		stages = new HashMap<String, Integer>();
		enabled = false;
	}
	
	public boolean toggleEnabled() {
		enabled = !enabled;
		
		return enabled;
	}
	
	public boolean isEnabled() {
		return enabled;
	}
	
	public void addStage(String key) {
		int stage = 1;
		
		if (stages.containsKey(key))
			stage += stages.get(key);
		
		setStage(key, stage);
	}
	
	public void setStage(String key, int stage) {
		stages.put(key, stage);
	}
	
	public void outputDebugger() {
		if (enabled) {
			Message.DEBUG_TITLE.send(player);
			
			for (String key: stages.keySet())
				Message.DEBUG_ITEM.send(player, key, stages.get(key).toString());
		}
		
		stages.clear();
	}
}