package uk.co.gorbb.qwicktree.tree;

import java.util.List;

import org.bukkit.Material;
import org.bukkit.TreeSpecies;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;

import uk.co.gorbb.qwicktree.tree.info.BlockInfo;
import uk.co.gorbb.qwicktree.tree.info.DamageType;

public class NewTree extends StandardTree {
	private BlockInfo		logBlock,
							leafBlock,
							saplingBlock;
	
	public NewTree(TreeSpecies species, boolean enabled, boolean replant, boolean autoCollect, int leafReach, int leafGroundOffset, int leafMin,
			int logMin, int logMax, List<String> drops, DamageType damageType, int damageAmount) {
		super(species, enabled, replant, autoCollect, leafReach, leafGroundOffset, leafMin, logMin, logMax, drops, damageType, damageAmount);
		
		byte logLeaf = (byte) (species == TreeSpecies.DARK_OAK ? 1 : 0);
		byte sapling = (byte) (species == TreeSpecies.DARK_OAK ? 5 : 4);
		
		logBlock = new BlockInfo(Material.LOG_2, logLeaf);
		leafBlock = new BlockInfo(Material.LEAVES_2, logLeaf);
		saplingBlock = new BlockInfo(Material.SAPLING, sapling);
		
	}

	@Override
	public boolean isValidLog(Block block) {
		return logBlock.matches(block);
	}

	@Override
	public boolean isValidLeaf(Block block) {
		return leafBlock.matches(block);
	}

	@Override
	public boolean isValidSapling(Block block) {
		return saplingBlock.matches(block);
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
	public ItemStack processItem(Material material, int qty) {
		switch (material) {
			case LOG:
			case LOG_2:
				return logBlock.toItemStack(qty);
			case LEAVES:
			case LEAVES_2:
				return leafBlock.toItemStack(qty);
			case SAPLING:
				return saplingBlock.toItemStack(qty);
			default:
				return super.processItem(material, qty);
		}
	}
	
	@Override
	public ItemStack getLogItem(int qty) {
		return logBlock.toItemStack(qty);
	}

}
