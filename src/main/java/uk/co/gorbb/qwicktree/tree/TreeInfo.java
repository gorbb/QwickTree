package uk.co.gorbb.qwicktree.tree;

import java.util.HashMap;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;

import uk.co.gorbb.qwicktree.tree.info.DamageType;
import uk.co.gorbb.qwicktree.tree.info.TreeType;
import uk.co.gorbb.qwicktree.util.Message;

public abstract class TreeInfo {
	private TreeType					treeType;			//Used for internal purposes.
	
	private boolean 					enabled,			//Whether or not this tree is enabled.
										replant,			//Whether or not to replace saplings when the tree is chopped down.
										autoCollect;		//Whether to automatically place the items dropped into the player's inventory.
	
	private int							leafReach,			//How far from the logs to search for leaves.
										leafGroundOffset,	//What level to ignore leaves at depending on how close they are to the ground. 
										leafMin;			//Minimum number of leaves required for the tree to be valid.
	
	private int							logMin,				//Minimum number of logs required for the tree to be valid.
										logMax;				//Maximum number of logs allowed in a valid tree. If any more logs are found, the tree is not valid.
	
	private HashMap<Material, Double>	drops;				//Which items to drop when the tree is chopped.
	
	private DamageType					damageType;			//Which type of damage to deal to a damagable item when the tree is chopped.
	private int							damageAmount;		//The multiplier or amount of damage to deal, depending on the damage type.
	
	public TreeInfo(TreeType treeType, boolean enabled, boolean replant, boolean autoCollect, int leafReach, int leafGroundOffset, int leafMin,
					int logMin, int logMax, List<String> drops, DamageType damageType, int damageAmount) {
		this.treeType = treeType;
		
		this.enabled = enabled;
		this.replant = replant;
		this.autoCollect = autoCollect;
		
		this.leafReach = leafReach;
		this.leafGroundOffset = leafGroundOffset;
		this.leafMin = leafMin;
		
		this.logMin = logMin;
		this.logMax = logMax;
		
		this.drops = processDrops(drops);
		
		this.damageType = damageType;
		this.damageAmount = damageAmount;
	}
	
	private HashMap<Material, Double> processDrops(List<String> drops) {
		HashMap<Material, Double> newDrops = new HashMap<Material, Double>();
		
		for (String row: drops) {
			String[] data = row.split(",");
			
			Material material = Material.getMaterial(data[0]);
			Double chance;
			
			if (material == null) {
				Message.MATERIAL_CONVERT_ERROR.warn(data[0]);
				continue; //Skip if material not found
			}
			
			try {
				chance = Double.parseDouble(data[1]);
			}
			catch (NumberFormatException e) {
				Message.CHANCE_CONVERT_ERROR.warn(data[1], material.toString());
				continue; //Skip if chance not valid
			}
			
			newDrops.put(material, chance);
		}
		
		return newDrops;
	}
	
	public TreeType getType() {
		return treeType;
	}
	
	public boolean isEnabled() {
		return enabled;
	}
	
	public boolean doReplant() {
		return replant;
	}
	
	public boolean doAutoCollect() {
		return autoCollect;
	}
	
	public int getLeafReach() {
		return leafReach;
	}
	
	public int getLeafGroundOffset() {
		return leafGroundOffset;
	}
	
	public int getLeafMin() {
		return leafMin;
	}
	
	public int getLogMin() {
		return logMin;
	}
	
	public int getLogMax() {
		return logMax;
	}
	
	public HashMap<Material, Double> getDrops() {
		return drops;
	}
	
	public DamageType getDamageType() {
		return damageType;
	}
	
	public int getDamageAmount() {
		return damageAmount;
	}
	
	public abstract boolean isValidLog(Block block);
	public abstract boolean isValidLeaf(Block block);
	public abstract boolean isValidSapling(Block block);
	
	public abstract boolean isValidStandingBlock(Block block);
	
	public abstract void replantSapling(Location location);
	
	public abstract ItemStack processItem(Material material, int qty);
	public abstract ItemStack getLogItem(int qty);
}