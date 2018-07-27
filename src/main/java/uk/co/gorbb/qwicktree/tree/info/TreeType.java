package uk.co.gorbb.qwicktree.tree.info;

import org.bukkit.Material;
import org.bukkit.TreeSpecies;
import org.bukkit.block.Block;

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
	
	public Material getLogMaterial() {
		switch (this) {
			case OAK:		return Material.OAK_LOG;
			case PINE:		return Material.SPRUCE_LOG;
			case BIRCH:		return Material.BIRCH_LOG;
			case JUNGLE:	return Material.JUNGLE_LOG;
			case ACACIA:	return Material.ACACIA_LOG;
			case DARK_OAK:	return Material.DARK_OAK_LOG;
			case CUSTOM:
			default:
				return null;
		}
	}
	
	public Material getLeafMaterial() {
		switch (this) {
			case OAK:		return Material.OAK_LEAVES;
			case PINE:		return Material.SPRUCE_LEAVES;
			case BIRCH:		return Material.BIRCH_LEAVES;
			case JUNGLE:	return Material.JUNGLE_LEAVES;
			case ACACIA:	return Material.ACACIA_LEAVES;
			case DARK_OAK:	return Material.DARK_OAK_LEAVES;
			case CUSTOM:
			default:
				return null;
		}
	}
	
	public Material getSaplingMaterial() {
		switch (this) {
			case OAK:		return Material.OAK_SAPLING;
			case PINE:		return Material.SPRUCE_SAPLING;
			case BIRCH:		return Material.BIRCH_SAPLING;
			case JUNGLE:	return Material.JUNGLE_SAPLING;
			case ACACIA:	return Material.ACACIA_SAPLING;
			case DARK_OAK:	return Material.DARK_OAK_SAPLING;
			case CUSTOM:
			default:
				return null;
		}
	}
	
	public boolean matchesLog(Material material) {
		return getLogMaterial() == material;
	}
	
	public boolean matchesLeaf(Material material) {
		return getLeafMaterial() == material;
	}
	
	public boolean matchesSapling(Material material) {
		return getSaplingMaterial() == material;
	}
	
	public boolean matchesLog(Block block) {
		return block != null && matchesLog(block.getType());
	}
	
	public boolean matchesLeaf(Block block) {
		return block != null && matchesLeaf(block.getType());
	}
	
	public boolean matchesSapling(Block block) {
		return block != null && matchesSapling(block.getType());
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