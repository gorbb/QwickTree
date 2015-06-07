package uk.co.gorbb.qwicktree.tree.info;

import org.bukkit.TreeSpecies;

public enum TreeType {
	OAK("Oak"),
	PINE("Pine"),
	BIRCH("Birch"),
	JUNGLE("Jungle"),
	DARK_OAK("Dark Oak"),
	ACACIA("Acacia"),
	CUSTOM("Custom"),
	;
	
	private String name;
	
	private TreeType(String name) {
		this.name = name;
	}
	
	@Override
	public String toString() {
		return name;
	}
	
	public static TreeType getFromSpecies(TreeSpecies species) {
		switch (species) {
			case GENERIC:	return OAK;
			case REDWOOD:	return PINE;
			case BIRCH:		return BIRCH;
			case JUNGLE:	return JUNGLE;
			case DARK_OAK:	return DARK_OAK;
			case ACACIA:	return ACACIA;
			default:		return CUSTOM;
		}
	}
}