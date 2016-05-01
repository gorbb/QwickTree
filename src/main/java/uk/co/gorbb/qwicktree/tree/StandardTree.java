package uk.co.gorbb.qwicktree.tree;

import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.TreeSpecies;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;
import org.bukkit.material.Sapling;
import org.bukkit.material.Wood;

import uk.co.gorbb.qwicktree.tree.info.DamageType;
import uk.co.gorbb.qwicktree.tree.info.TreeType;

public class StandardTree extends TreeInfo {
	private TreeSpecies species;
	
	public StandardTree(TreeSpecies species, boolean enabled, boolean replant, boolean autoCollect, boolean stump, boolean anyBlock, int leafReach, int leafGroundOffset, int leafMin,
						int logMin, int logMax, List<String> drops, DamageType damageType, int damageAmount, int replantTimer) {
		super(TreeType.getFromSpecies(species), enabled, replant, autoCollect, stump, anyBlock, leafReach, leafGroundOffset, leafMin, logMin, logMax, drops, damageType, damageAmount, replantTimer);
		
		this.species = species;
	}
	
	public TreeSpecies getSpecies() {
		return species;
	}
	
	@Override
	public boolean isValidLog(Block block) {
		return checkSpecies(logMaterial, block.getState());
	}

	@Override
	public boolean isValidLeaf(Block block) {
		return checkSpecies(leafMaterial, block.getState());
	}

	@Override
	public boolean isValidSapling(Block block) {
		return checkSpecies(Material.SAPLING, block.getState());
	}
	
	@Override
	public boolean isValidSapling(ItemStack item) {
		return checkSpecies(Material.SAPLING, item.getType(), item.getData());
	}
	
	private boolean checkSpecies(Material material, BlockState state) {
		return checkSpecies(material, state.getType(), state.getData());
	}
	
	private boolean checkSpecies(Material material, Material type, MaterialData data) {
		if (type != material) return false;
		
		Wood wood = (Wood) data;
		
		return wood.getSpecies() == species;
	}

	@Override
	public boolean isValidStandingBlock(Block block) {
		switch (block.getType()) {
			case DIRT:
			case GRASS:
				return true;
			default:
				return false;
		}
	}
	
	@Override
	public void replantSapling(Location location) {
		Block block = location.getBlock();
		block.setType(Material.SAPLING);
		
		Sapling sapling = new Sapling(species);
		
		BlockState state = block.getState();
		state.setData(sapling);
		
		state.update();
	}

	@Override
	public ItemStack processItem(Material material, int qty) {
		if (!Wood.class.isAssignableFrom(material.getData())) //Quick way to see if we can set the item to the same type as the tree (i.e. leaves, sapling, plank, etc.)
			return new ItemStack(material, qty);
		
		Wood tree = new Wood(material, species);
		
		tree.setSpecies(species);
		return tree.toItemStack(qty);
	}
	
	@Override
	public ItemStack getLogItem(int qty) {
		return processItem(Material.LOG, qty);
	}
	
}