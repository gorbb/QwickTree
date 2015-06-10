package uk.co.gorbb.qwicktree;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import uk.co.gorbb.qwicktree.config.Config;
import uk.co.gorbb.qwicktree.tree.info.TreeType;
import uk.co.gorbb.qwicktree.util.DisabledList;
import uk.co.gorbb.qwicktree.util.HouseIgnore;
import uk.co.gorbb.qwicktree.util.Message;
import uk.co.gorbb.qwicktree.util.Permission;
import uk.co.gorbb.qwicktree.util.debug.Debugger;

public class QTCommand implements CommandExecutor {
	
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (args.length == 0)
			return helpCommand(sender);

		if (args[0].equalsIgnoreCase("help"))		return helpCommand(sender);
		if (args[0].equalsIgnoreCase("debug"))		return debugCommand(sender);
		if (args[0].equalsIgnoreCase("reload"))		return reloadCommand(sender);
		if (args[0].equalsIgnoreCase("info"))		return infoCommand(sender);
		if (args[0].equalsIgnoreCase("bypass"))		return bypassCommand(sender, args);
		if (args[0].equalsIgnoreCase("disable"))	return disableCommand(sender, args);
		if (args[0].equalsIgnoreCase("enable"))		return enableCommand(sender, args);
		if (args[0].equalsIgnoreCase("list"))		return listCommand(sender);
		if (args[0].equalsIgnoreCase("clear"))		return clearCommand(sender);
		
		return false;
	}
	
	public boolean helpCommand(CommandSender sender) {
		Message.HELP_TITLE.send(sender);
		Message.HELP_ITEM.send(sender, "/qt help", "Shows this help message");
		Message.HELP_ITEM.send(sender, "/qt debug", "Enables or disables the debugger");
		Message.HELP_ITEM.send(sender, "/qt reload", "Reloads the configuration");
		Message.HELP_ITEM.send(sender, "/qt info", "Shows information since the last server reboot");
		Message.HELP_ITEM.send(sender, "/qt bypass <player> [amount]", "Adds bypasses of house blocks for a player.");
		Message.HELP_ITEM.send(sender, "/qt disable [player|all]", "Disables the plugin for the specified player, or all players.");
		Message.HELP_ITEM.send(sender, "/qt enable [player|all]", "Enables the plugin for the specified player, or all players.");
		Message.HELP_ITEM.send(sender, "/qt list", "Lists the players that currently have the plugin disabled.");
		Message.HELP_ITEM.send(sender, "/qt clear", "Clears the list of players for who the plugin is disabled.");
		
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
		
		int newAmount = HouseIgnore.get().addBypass(player, amount);
		
		return Message.BYPASS.send(sender, player.getName(), String.valueOf(newAmount));
	}
	
	public boolean disableCommand(CommandSender sender, String[] args) {
		String name = sender.getName();
		
		if (args.length > 1)
			name = args[1];
		
		if (name.equalsIgnoreCase("all"))
			return globalDisable(sender);
		else
			return playerDisable(sender, name);
	}
	
	private boolean globalDisable(CommandSender sender) {
		if (!Permission.TOGGLE_ALL.has(sender)) return Message.NO_PERMISSION.send(sender);
		if (DisabledList.get().isDisabledForAll()) return Message.ALREADY_DISABLED_ALL.send(sender);
		
		DisabledList.get().disableForAll();
		
		return Message.TOGGLE_DISABLED_ALL.send(sender);
	}
	
	private boolean playerDisable(CommandSender sender, String name) {
		Player player = Bukkit.getPlayer(name);
		if (player == null) player = Bukkit.getPlayerExact(name);
		if (player == null) return Message.PLAYER_NOT_FOUND_OFFLINE.send(sender, name);
		
		//Permission check
		if (player.equals(sender) && !Permission.TOGGLE_SELF.has(sender)) return Message.NO_PERMISSION.send(sender);
		else if (!Permission.TOGGLE_OTHERS.has(sender)) return Message.NO_PERMISSION.send(sender);
		
		if (DisabledList.get().isDisabledForPlayer(player)) return Message.ALREADY_DISABLED.send(sender, player.getName());
		
		DisabledList.get().disableFor(player);
		
		return Message.TOGGLE_DISABLED.send(sender, player.getName());
	}
	
	public boolean enableCommand(CommandSender sender, String[] args) {
		String name = sender.getName();
		
		if (args.length > 1)
			name = args[1];
		
		if (name.equalsIgnoreCase("all"))
			return globalEnable(sender);
		else
			return playerEnable(sender, name);
	}
	
	private boolean globalEnable(CommandSender sender) {
		if (!Permission.TOGGLE_ALL.has(sender)) return Message.NO_PERMISSION.send(sender);
		if (!DisabledList.get().isDisabledForAll()) return Message.ALREADY_ENABLED_ALL.send(sender);
		
		DisabledList.get().enableForAll();
		
		return Message.TOGGLE_ENABLED_ALL.send(sender);
	}
	
	private boolean playerEnable(CommandSender sender, String name) {
		Player player = Bukkit.getPlayer(name);
		if (player == null) player = Bukkit.getPlayerExact(name);
		if (player == null) return Message.PLAYER_NOT_FOUND_OFFLINE.send(sender, name);
		
		//Permission check
		if (player.equals(sender) && !Permission.TOGGLE_SELF.has(sender)) return Message.NO_PERMISSION.send(sender);
		else if (!Permission.TOGGLE_OTHERS.has(sender)) return Message.NO_PERMISSION.send(sender);
		
		if (!DisabledList.get().isDisabledForPlayer(player)) return Message.ALREADY_ENABLED.send(sender, player.getName());
		
		DisabledList.get().enableFor(player);
		
		return Message.TOGGLE_ENABLED.send(sender, player.getName());
	}
	
	public boolean listCommand(CommandSender sender) {
		if (!Permission.TOGGLE_LIST.has(sender)) return Message.NO_PERMISSION.send(sender);
		if (DisabledList.get().isDisabledForAll()) return Message.TOGGLE_DISABLED_ALL.send(sender);
		
		Message.TOGGLE_LIST_TITLE.send(sender);
		
		for (String player: DisabledList.get().getPlayersDisabledFor())
			Message.TOGGLE_LIST_ITEM.send(sender, player);
		
		return true;
	}
	
	public boolean clearCommand(CommandSender sender) {
		if (!Permission.TOGGLE_LIST.has(sender)) return Message.NO_PERMISSION.send(sender);
		
		DisabledList.get().clear();
		
		return Message.TOGGLE_CLEARED.send(sender);
	}
	
}