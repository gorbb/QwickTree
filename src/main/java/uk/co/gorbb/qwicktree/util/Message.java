package uk.co.gorbb.qwicktree.util;

import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import uk.co.gorbb.qwicktree.QwickTree;

public enum Message {
	MATERIAL_CONVERT_ERROR("Cannot convert '{0}' to material, ignoring..."),
	CHANCE_CONVERT_ERROR("Error converting {0} to a double. Ignoring entry {1}..."),
	INVALID_DAMAGE_TYPE("Invalid damage type for tree type {0}. Expected one of NONE, NORM, FIXED, MULT, but got {1}. Ignoring tree type..."),
	INVALID_ARGS("&4Invalid arguments. Try &8{0}"),
	
	NO_PERMISSION("&4You do not have access to this command."),
	PLAYER_ONLY("You must be an in-game player to run this command"),
	PLAYER_NOT_FOUND("&4Cannot find a player by the name of &8{0}&4."),
	
	HELP_TITLE("&6 === QwickTree Commands ==="),
	HELP_ITEM("&7{0} &8- &c{1}"),
	
	DEBUG_ENABLED("&8[&bQT-DEBUG&8] &2Debugger enabled"),
	DEBUG_DISABLED("&8[&bQT-DEBUG&8] &2Debugger disabled"),
	DEBUG_TITLE("&8[&bQT-DEBUG&8] &6=== Debugger Output ==="),
	DEBUG_ITEM("&8[&bQT-DEBUG&8] &a> &8{0}&7: &c{1}"),
	
	RELOADED("&2Config reloaded"),
	
	INFO_TITLE("&6 === QwickTree Info ==="),
	INFO_ITEM("&6{0} &8- &f{1}"),
	
	BYPASS("&2Set number of bypasses for &8{0} &2to &8{1}"),
	
	NOTIFY("&8{0} &4has destroyed a block which may be part of a house. The location of this block is &8{1}"),
	;
	
	private static Logger log = QwickTree.get().getLogger();
	
	private String message;
	
	private Message(String message) {
		this.message = message;
	}
	
	private String prepare(String... replace) {
		String send = ChatColor.translateAlternateColorCodes('&', message);
		
		for (int index = 0; index < replace.length; index++)
			send = send.replaceAll("\\{" + index + "}", replace[index]);
		
		return send;
	}
	
	public boolean send(Player player, String... replace) {
		player.sendMessage(prepare(replace));
		
		return true;
	}
	
	public boolean send(CommandSender sender, String... replace) {
		sender.sendMessage(prepare(replace));
		
		return true;
	}
	
	public boolean send(Permission permission, String... replace) {
		Bukkit.broadcast(prepare(replace), permission.getName());
		
		return true;
	}
	
	public boolean info(String... replace) {
		log.info(prepare(replace));
		
		return true;
	}
	
	public boolean warn(String... replace) {
		log.warning(prepare(replace));
		
		return true;
	}
	
	public boolean severe(String... replace) {
		log.severe(prepare(replace));
		
		return true;
	}
}