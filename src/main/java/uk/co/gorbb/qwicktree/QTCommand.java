package uk.co.gorbb.qwicktree;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import uk.co.gorbb.qwicktree.config.Config;
import uk.co.gorbb.qwicktree.tree.info.TreeType;
import uk.co.gorbb.qwicktree.util.Message;
import uk.co.gorbb.qwicktree.util.Permission;
import uk.co.gorbb.qwicktree.util.debug.Debugger;

public class QTCommand implements CommandExecutor {
	
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (args.length == 0)
			helpCommand(sender);

		if (args[0].equalsIgnoreCase("help"))	return helpCommand(sender);
		if (args[0].equalsIgnoreCase("debug"))	return debugCommand(sender);
		if (args[0].equalsIgnoreCase("reload"))	return reloadCommand(sender);
		if (args[0].equalsIgnoreCase("info"))	return infoCommand(sender);
		if (args[0].equalsIgnoreCase("bypass"))	return bypassCommand(sender, args);
		
		return false;
	}
	
	public boolean helpCommand(CommandSender sender) {
		Message.HELP_TITLE.send(sender);
		Message.HELP_ITEM.send(sender, "/qt help", "Shows this help message");
		Message.HELP_ITEM.send(sender, "/qt debug", "Enables or disables the debugger");
		Message.HELP_ITEM.send(sender, "/qt reload", "Reloads the configuration");
		Message.HELP_ITEM.send(sender, "/qt info", "Shows information since the last server reboot");
		Message.HELP_ITEM.send(sender, "/qt bypass <player> [amount]", "Adds bypasses of house blocks for a player.");
		
		return true;
	}
	
	public boolean debugCommand(CommandSender sender) {
		if (!(sender instanceof Player)) return Message.PLAYER_ONLY.send(sender);
		if (!Permission.DEBUG.has(sender)) return Message.NO_PERMISSION.send(sender);
		Player player = (Player) sender;
		
		if (Debugger.get(player).toggleEnabled())
			return Message.DEBUG_ENABLED.send(sender);
		else
			return Message.DEBUG_DISABLED.send(sender);
	}
	
	public boolean reloadCommand(CommandSender sender) {
		if (!Permission.RELOAD.has(sender)) return Message.NO_PERMISSION.send(sender);
		
		Config.get().load();
		
		return Message.RELOADED.send(sender);
	}
	
	public boolean infoCommand(CommandSender sender) {
		if (!Permission.INFO.has(sender)) return Message.NO_PERMISSION.send(sender);
		
		HashMap<TreeType, Integer> chops = QwickTree.get().getChopCount();
		
		Message.INFO_TITLE.send(sender);
		
		for (TreeType type: chops.keySet())
			Message.INFO_ITEM.send(sender, type.toString(), chops.get(type).toString());
		
		return true;
	}
	
	public boolean bypassCommand(CommandSender sender, String[] args) {
		if (!Permission.BYPASS.has(sender)) return Message.NO_PERMISSION.send(sender);
		if (args.length <= 1) return Message.INVALID_ARGS.send(sender, "/qt bypass <player> [amount]");
		
		Player player = Bukkit.getPlayer(args[1]);
		if (player == null || player.isOnline() == false) return Message.PLAYER_NOT_FOUND.send(sender, args[1]);
		
		int amount = 1;
		
		if (args.length > 2)
			try {
				amount = Integer.parseInt(args[2]);
			}
			catch (NumberFormatException e) {
				return Message.INVALID_ARGS.send(sender, "/qt bypass <player> [amount]");
			}
		
		int newAmount = QwickTree.get().addBypass(player, amount);
		
		return Message.BYPASS.send(sender, player.getName(), String.valueOf(newAmount));
	}
	
}