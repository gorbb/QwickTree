package uk.co.gorbb.qwicktree.util;

import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import uk.co.gorbb.qwicktree.QwickTree;

public class DisabledList {
	private static DisabledList instance;
	
	public static DisabledList get() {
		if (instance == null)
			instance = new DisabledList();
		
		return instance;
	}
	
	private DisabledList() { }
	
	private boolean disabledForAll;
	private List<String> disabledList;
	
	public void load() {
		disabledList = SLAPI.loadDisabledList(QwickTree.get().getDataFolder(), "disabled.dat");
	}
	
	public void save() {
		SLAPI.saveDisabledList(QwickTree.get().getDataFolder(), "disabled.dat", disabledList);
	}
	
	public boolean isDisabledForAll() {
		return disabledForAll;
	}
	
	public void enableForAll() {
		disabledForAll = false;
	}
	
	public void disableForAll() {
		disabledForAll = true;
	}
	
	public boolean isDisabledFor(Player player) {
		if (disabledForAll) return true;
		
		return isDisabledForPlayer(player);
	}
	
	public boolean isDisabledForPlayer(Player player) {
		String uuid = player.getUniqueId().toString();
		return disabledList.contains(uuid);
	}
	
	public void enableFor(Player player) {
		String uuid = player.getUniqueId().toString();
		disabledList.remove(uuid);
	}
	
	public void disableFor(Player player) {
		String uuid = player.getUniqueId().toString();
		
		if (disabledList.contains(uuid)) return;
		
		disabledList.add(uuid);
	}
	
	public String[] getPlayersDisabledFor() {
		String[] playerNames = new String[disabledList.size()];
		
		for (int index = 0; index < playerNames.length; index++) {
			Player player = Bukkit.getPlayer(UUID.fromString(disabledList.get(index)));
			
			playerNames[index] = player.getName();
		}
		
		return playerNames;
	}
	
	public void clear() {
		disabledList.clear();
		save();
	}
	
}