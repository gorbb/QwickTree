package uk.co.gorbb.qwicktree.tree;

import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.TreeSpecies;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.Tree;

import uk.co.gorbb.qwicktree.tree.info.DamageType;
import uk.co.gorbb.qwicktree.tree.info.TreeType;

public class StandardTree extends TreeInfo {
	private TreeSpecies species;
	
	public StandardTree(TreeSpecies species, boolean enabled, boolean replant, boolean autoCollect, boolean stump, boolean anyBlock, int leafReach, int leafGroundOffset, int leafMin,
						int logMin, int logMax, List<String> drops, DamageType damageType, int damageAmount) {
		super(TreeType.getFromSpecies(species), enabled, replant, autoCollect, stump, anyBlock, leafReach, leafGroundOffset, leafMin, logMin, logMax, drops, damageType, damageAmount);
		
		this.species = species;
	}
	
	public TreeSpecies getSpecies() {
		return species;
	}
	
	@Override
	public boolean isValidLog(Block block) {
		return checkSpecies(Material.LOG, block.getState());
	}

	@Override
	public boolean isValidLeaf(Block block) {
		return checkSpecies(Material.LEAVES, block.getState());
	}

	@Override
	public boolean isValidSapling(Block block) {
		return checkSpecies(Material.SAPLING, block.getState());
	}
	
	private boolean checkSpecies(Material material, BlockState state) {
		if (state.getType() != material) return false;
		
		Tree tree = (Tree) state.getData();
		
		return tree.getSpecies() == species;
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
		
		Tree data = new Tree(species);
		
		BlockState state = block.getState();
		state.setData(data);
		
		state.update();
	}

	@Override
	public ItemStack processItem(Material material, int qty) {
		if (!Tree.class.isAssignableFrom(material.getData())) //Quick way to see if we can set the item to the same type as the tree (i.e. leaves, sapling, plank, etc.)
			return new ItemStack(material, qty);
		
		Tree tree = new Tree(material);
		
		tree.setSpecies(species);
		return tree.toItemStack(qty);
	}
	
	@Override
	public ItemStack getLogItem(int qty) {
		return processItem(Material.LOG, qty);
	}
	
}