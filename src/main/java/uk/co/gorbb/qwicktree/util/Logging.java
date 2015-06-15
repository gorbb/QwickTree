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
		//Temporarily removed since it doesn't appear to work.
		//logDetails(checkPluginCoreProtect(), "CoreProtect");
	}
	
	private static void logDetails(boolean result, String pluginName) {
		if (result)
			Message.EXTERNAL_PLUGIN_ENABLED.info("CoreProtect");
		else
			Message.EXTERNAL_PLUGIN_DISABLED.info("CoreProtect");
	}
	
	public static void logBreak(Player player, Block block) {
		//Temporarily removed since it doesn't appear to work.
		//logBreakCoreProtect(player, block);
	}
	
	public static void logPlace(Player player, Block block) {
		//Temporarily removed since it doesn't appear to work.
		//logPlaceCoreProtect(player, block);
	}
	
	/* ### CORE PROTECT ### */
	private static CoreProtectAPI coreProtect;
	
	private static boolean checkPluginCoreProtect() {
		Plugin plugin = Bukkit.getServer().getPluginManager().getPlugin("CoreProtect");
		
		if (plugin == null || !(plugin instanceof CoreProtect))
			return false;
		
		coreProtect = ((CoreProtect) plugin).getAPI();
		
		if (!coreProtect.isEnabled() || coreProtect.APIVersion() < 3)
			coreProtect = null;
		
		return coreProtect != null;
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