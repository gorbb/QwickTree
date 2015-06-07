package uk.co.gorbb.qwicktree.util;

import java.util.HashMap;

import org.bukkit.entity.Player;

import uk.co.gorbb.qwicktree.QwickTree;

public class HouseIgnore {
	private static HouseIgnore instance;
	
	public static HouseIgnore get() {
		if (instance == null)
			instance = new HouseIgnore();
		
		return instance;
	}
	
	private HashMap<String, Integer> houseIgnores;
	
	private HouseIgnore() { }
	
	public void load() {
		houseIgnores = SLAPI.loadIgnoreList(QwickTree.get().getDataFolder(), "ignore.dat");
	}
	
	public void save() {
		SLAPI.saveIgnoreList(QwickTree.get().getDataFolder(), "ignore.dat", houseIgnores);
	}
	

	public boolean ignoreHouseBlocks(Player player) {
		String uuid = player.getUniqueId().toString();
		
		if (!houseIgnores.containsKey(uuid)) return false;
		
		int ignores = houseIgnores.get(uuid) - 1;
		
		if (ignores < 0)
			return false;
		else if (ignores == 0)
			houseIgnores.remove(uuid);
		else
			houseIgnores.put(uuid, ignores);
		
		return true;
	}
	
	public int addBypass(Player player, int amount) {
		String uuid = player.getUniqueId().toString();
		
		if (houseIgnores.containsKey(uuid))
			amount += houseIgnores.get(uuid);
		
		if (amount < 0) amount = 0;
		
		houseIgnores.put(uuid, amount);
		
		return amount;
	}
}