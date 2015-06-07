package uk.co.gorbb.qwicktree.util;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.PermissionDefault;

public enum Permission {
	USE("qwicktree.use"),
	NOTIFY("qwicktree.notify"),
	DEBUG("qwicktree.debug"),
	RELOAD("qwicktree.reload"),
	INFO("qwicktree.info"),
	BYPASS("qwicktree.bypass"),
	TOGGLE_SELF("qwicktree.toggle.self"),
	TOGGLE_OTHERS("qwicktree.toggle.others"),
	TOGGLE_ALL("qwicktree.toggle.all"),
	TOGGLE_LIST("qwicktree.toggle.list"),
	
	;
	
	private String name;
	
	private Permission(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
	
	public boolean has(CommandSender sender) {
		return sender.hasPermission(name);
	}
	
	public void setDefault(boolean value) {
		PermissionDefault defaultValue = value ? PermissionDefault.TRUE : PermissionDefault.FALSE;
		
		setDefault(defaultValue);
	}
	
	
	public void setOp(boolean op) {
		PermissionDefault defaultValue = op ? PermissionDefault.TRUE : PermissionDefault.FALSE;
		
		setDefault(defaultValue);
	}
	
	private void setDefault(PermissionDefault defaultValue) {
		org.bukkit.permissions.Permission permission = Bukkit.getServer().getPluginManager().getPermission(name);
		
		if (permission == null)
			Bukkit.getServer().getPluginManager().addPermission(new org.bukkit.permissions.Permission(name, defaultValue));
		else
			permission.setDefault(defaultValue);		
	}
	
}