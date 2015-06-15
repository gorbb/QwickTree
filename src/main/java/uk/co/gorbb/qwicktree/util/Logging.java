package uk.co.gorbb.qwicktree.util;

import net.coreprotect.CoreProtect;
import net.coreprotect.CoreProtectAPI;

import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

@SuppressWarnings("deprecation")
public class Logging {
	
	public static void checkPlugins() {
		checkPluginCoreProtect();
	}
	
	public static void logBreak(Player player, Block block) {
		logBreakCoreProtect(player, block);
	}
	
	public static void logPlace(Player player, Block block) {
		logPlaceCoreProtect(player, block);
	}
	
	/* ### CORE PROTECT ### */
	private static CoreProtectAPI coreProtect;
	
	private static void checkPluginCoreProtect() {
		Plugin plugin = Bukkit.getServer().getPluginManager().getPlugin("CoreProtect");
		
		if (plugin == null || !(plugin instanceof CoreProtect))
			return;
		
		coreProtect = ((CoreProtect) plugin).getAPI();
		
		if (!coreProtect.isEnabled() || coreProtect.APIVersion() < 3)
			coreProtect = null;
	}
	
	private static void logBreakCoreProtect(Player player, Block block) {
		if (coreProtect == null) return;
		
		coreProtect.logRemoval(player.getName(), block.getLocation(), block.getType(), block.getData());
	}
	
	private static void logPlaceCoreProtect(Player player, Block block) {
		if (coreProtect == null) return;
		
		coreProtect.logPlacement(player.getName(), block.getLocation(), block.getType(), block.getData());
	}
}