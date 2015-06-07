package uk.co.gorbb.qwicktree;

import java.util.HashMap;

import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import uk.co.gorbb.qwicktree.config.Config;
import uk.co.gorbb.qwicktree.tree.info.TreeType;
import uk.co.gorbb.qwicktree.util.Logging;
import uk.co.gorbb.qwicktree.util.SLAPI;

public class QwickTree extends JavaPlugin {
	private static QwickTree instance;
	
	public static QwickTree get() {
		return instance;
	}
	
	private HashMap<String, Integer> houseBlockIgnores;
	
	private HashMap<TreeType, Integer> chopCount;
	
	public QwickTree() {
		instance = this;
		
		houseBlockIgnores = new HashMap<String, Integer>();
		chopCount = new HashMap<TreeType, Integer>();
	}
	
	@Override
	public void onEnable() {
		Config.get().load();
		
		getServer().getPluginManager().registerEvents(new QTListener(), this);
		getCommand("qt").setExecutor(new QTCommand());
		
		//Load ignore HashMap from file...
		houseBlockIgnores = SLAPI.loadIgnoreList(getDataFolder(), "ignore.dat");
		
		//Check for loaded logging plugins
		Logging.checkPlugins();
	}
	
	@Override
	public void onDisable() {
		//Save ignore HashMap to file...
		SLAPI.saveIgnoreList(getDataFolder(), "ignore.dat", houseBlockIgnores);
	}
	
	public void addTreeChop(TreeType type) {
		int count = 1;
		
		if (chopCount.containsKey(type))
			count += chopCount.get(type);
		
		chopCount.put(type, count);
	}
	
	public HashMap<TreeType, Integer> getChopCount() {
		return new HashMap<TreeType, Integer>(chopCount);
	}
	
	public boolean ignoreHouseBlocks(Player player) {
		String uuid = player.getUniqueId().toString();
		
		if (!houseBlockIgnores.containsKey(uuid)) return false;
		
		int ignores = houseBlockIgnores.get(uuid) - 1;
		
		if (ignores < 0)
			return false;
		else if (ignores == 0)
			houseBlockIgnores.remove(uuid);
		else
			houseBlockIgnores.put(uuid, ignores);
		
		return true;
	}
	
	public int addBypass(Player player, int amount) {
		String uuid = player.getUniqueId().toString();
		
		if (houseBlockIgnores.containsKey(uuid))
			amount += houseBlockIgnores.get(uuid);
		
		if (amount < 0) amount = 0;
		
		houseBlockIgnores.put(uuid, amount);
		
		return amount;
	}
	
}