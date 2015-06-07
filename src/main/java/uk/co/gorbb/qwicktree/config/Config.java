package uk.co.gorbb.qwicktree.config;

import java.util.LinkedList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.TreeSpecies;
import org.bukkit.block.Block;
import org.bukkit.configuration.Configuration;
import org.bukkit.inventory.ItemStack;

import uk.co.gorbb.qwicktree.QwickTree;
import uk.co.gorbb.qwicktree.tree.NewTree;
import uk.co.gorbb.qwicktree.tree.StandardTree;
import uk.co.gorbb.qwicktree.tree.TreeInfo;
import uk.co.gorbb.qwicktree.tree.info.DamageType;
import uk.co.gorbb.qwicktree.util.Message;
import uk.co.gorbb.qwicktree.util.Permission;

public class Config {
	private static Config instance;
	
	public static Config get() {
		if (instance == null)
			instance = new Config();
		
		return instance;
	}
	
	private Configuration config;
	
	private List<TreeInfo> trees;
	private List<Material> houseBlock,
						   handItems;
	
	private boolean creativeAutoCollect,
					creativeReplant,
					creativeDamage;
	
	private Config() {
		config = QwickTree.get().getConfig();
		QwickTree.get().saveDefaultConfig();
		
		trees = new LinkedList<TreeInfo>();
	}
	
	public void load() {
		houseBlock = toMaterialList(config.getStringList("house"));
		handItems = toMaterialList(config.getStringList("items"));
		
		if (config.getBoolean("usePerms"))
			Permission.NOTIFY.setDefault(false);
		else
			Permission.NOTIFY.setDefault(true);
		
		creativeAutoCollect = config.getBoolean("creative.autoCollect");
		creativeReplant = config.getBoolean("creative.replant");
		creativeDamage = config.getBoolean("creative.damage");
		
		trees.add(processTree(TreeSpecies.GENERIC, "oak"));
		trees.add(processTree(TreeSpecies.REDWOOD, "pine"));
		trees.add(processTree(TreeSpecies.BIRCH, "birch"));
		trees.add(processTree(TreeSpecies.JUNGLE, "jungle"));
		trees.add(processTree(TreeSpecies.ACACIA, "acacia"));
		trees.add(processTree(TreeSpecies.DARK_OAK, "dark_oak"));
	}
	
	private TreeInfo processTree(TreeSpecies species, String name) {
		String tag = "trees." + name + ".";
		
		boolean enabled = config.getBoolean(tag + "enabled");
		boolean replant = config.getBoolean(tag + "replant");
		boolean autoCollect = config.getBoolean(tag + "autoCollect");
		
		int leafReach = config.getInt(tag + "leaf.reach");
		int leafGroundOffset = config.getInt(tag + "leaf.groundOffset");
		int leafMin = config.getInt(tag + "leaf.min");
		
		int logMin = config.getInt(tag + "log.min");
		int logMax = config.getInt(tag + "log.max");
		
		List<String> drops = config.getStringList(tag + "drops");
		
		String type = config.getString(tag + "damage.type");
		DamageType damageType = DamageType.getFromName(type);
		
		if (damageType == null) {
			Message.INVALID_DAMAGE_TYPE.warn(name, type);
			return null;
		}
		
		int damageAmount = config.getInt(tag + "damage.amount");
		
		switch (species) {
			case ACACIA:
			case DARK_OAK:
				return new NewTree(species, enabled, replant, autoCollect, leafReach, leafGroundOffset, leafMin, logMin, logMax, drops, damageType, damageAmount);
			default:
				return new StandardTree(species, enabled, replant, autoCollect, leafReach, leafGroundOffset, leafMin, logMin, logMax, drops, damageType, damageAmount);
		}
	}
	
	private List<Material> toMaterialList(List<String> list) {
		List<Material> materialList = new LinkedList<Material>();
		
		for (String item: list) {
			Material material = Material.getMaterial(item);
			
			if (material != null) materialList.add(material);
			else Message.MATERIAL_CONVERT_ERROR.warn(item);
		}
		
		return materialList;
	}
	
	public TreeInfo getTreeByLog(Block block) {
		for (TreeInfo tree: trees)
			if (tree.isValidLog(block))
				return tree;
		
		return null;
	}
	
	public boolean isHouseBlock(Block block) {
		return houseBlock.contains(block.getType());
	}
	
	public boolean isHandItem(ItemStack item) {
		return handItems.contains(item.getType());
	}
	
	public boolean doCreativeAutoCollect() {
		return creativeAutoCollect;
	}
	
	public boolean doCreativeReplant() {
		return creativeReplant;
	}
	
	public boolean doCreativeDamage() {
		return creativeDamage;
	}
}