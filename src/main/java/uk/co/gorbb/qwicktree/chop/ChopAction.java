package uk.co.gorbb.qwicktree.chop;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.Stack;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import uk.co.gorbb.qwicktree.QwickTree;
import uk.co.gorbb.qwicktree.config.Config;
import uk.co.gorbb.qwicktree.tree.TreeInfo;
import uk.co.gorbb.qwicktree.tree.info.TreeType;
import uk.co.gorbb.qwicktree.util.HouseIgnore;
import uk.co.gorbb.qwicktree.util.Logging;
import uk.co.gorbb.qwicktree.util.Message;
import uk.co.gorbb.qwicktree.util.Permission;
import uk.co.gorbb.qwicktree.util.debug.Debugger;

public class ChopAction {
	private Player player;
	private TreeInfo tree;
	private Stack<Block> logsToSearch;
	
	private List<Block> logs;
	private List<Block> leaves;
	private List<Location> baseBlocks; 
	private List<Location> vines;
	
	private Random rnd;
	
	private boolean ignoreHouseBlocks = false;
	private Debugger debugger;
	
	public ChopAction(Player player, TreeInfo tree, Block base) {
		this.player = player;
		this.tree = tree;
		
		logsToSearch = new Stack<Block>();
		logs = new LinkedList<Block>();
		leaves = new LinkedList<Block>();
		baseBlocks = new LinkedList<Location>();
		vines = new LinkedList<Location>();
		
		rnd = new Random();
		debugger = Debugger.get(player);
		
		logsToSearch.push(base);
	}
	
	public void go() {
		if (check())
			if (chop())
				replant();
	}
	
	private boolean check() {
		debugger.addStage("CA.check"); //1
		if (!logSearch()) return false;

		debugger.addStage("CA.check"); //2
		if (!leafSearch()) return false;

		debugger.addStage("CA.check"); //3
		if (logs.size() < tree.getLogMin()) return false;

		debugger.addStage("CA.check"); //4
		return true;
	}
	
	private boolean addLog(Block block) {
		if (logs.contains(block))
			return true;
		
		logs.add(block);
		
		if (logs.size() > tree.getLogMax())
			return false;
		
		return true;
	}
	
	private boolean logSearch() {
		while (!logsToSearch.isEmpty()) {
			Block current = logsToSearch.pop();
			
			if (!addLog(current)) return false;
			
			if (tree.isValidStandingBlock(current.getRelative(BlockFace.DOWN)))
				baseBlocks.add(current.getLocation());
			
			for (int x = -1; x <= 1; x++)
				for (int z = -1; z <= 1; z++)
					for (int y = 0; y <= 1; y++) {
						Block block = current.getRelative(x, y, z);
						
						if (!tree.isValidLog(block)) continue;			//If it's not a valid log, next loop.
						if (logsToSearch.contains(block)) continue;		//If it's already been found, next loop.
						if (logs.contains(block)) continue;				//If it's already found, next loop.
						if (current.equals(block)) continue;			//If it's the block we're searching around, then next loop.
						
						logsToSearch.push(block);
					}
		}
		
		return logsToSearch.isEmpty();
	}
	
	private boolean leafSearch() {
		int leafReach = getLeafReach();
		
		for (Block log: logs) {
			for (int x = -leafReach; x <= leafReach; x++)
				for (int z = -leafReach; z <= leafReach; z++)
					for (int y = 0; y <= leafReach; y++) {
						Block block = log.getRelative(x, y, z);
						
						if (!ignoreHouseBlocks && Config.get().isHouseBlock(block))
							if (HouseIgnore.get().ignoreHouseBlocks(player))
								ignoreHouseBlocks = true;
							else {
								Message.NOTIFY.send(Permission.NOTIFY, player.getName(), formatLocation(block.getLocation()));
								return false;
							}
						
						//If jungle or oak, and vines, then add some.
						if (block.getType() == Material.VINE)
							vines.add(block.getLocation());
						
						if (!tree.isValidLeaf(block)) continue;		//If the block isn't a valid leaf, skip to the next loop.
						if (leaves.contains(block)) continue;		//If the leaf has already been found, skip to the next loop.
						if (groundInReach(block)) continue;			//If the leaf is within 'groundOffset' distance, skip to the next loop.
						
						leaves.add(block);
					}
		}
		
		return true;
	}
	
	private String formatLocation(Location location) {
		return 	location.getWorld().getName() + ":" +
				"X: " + location.getBlockX() + ";" +
				"Y: " + location.getBlockY() + ";" +
				"Z: " + location.getBlockZ() + ";";
	}
	
	private int getLeafReach() {
		int baseLeafReach = tree.getLeafReach();
		
		//If an oak in a swamp, increase by 1
		if (tree.getType() == TreeType.OAK && EnumSet.of(Biome.SWAMPLAND, Biome.SWAMPLAND_MOUNTAINS).contains(logs.get(0).getBiome()))
			baseLeafReach += 1;
		//Large oak, +1 (but only if not swamp)
		else if (tree.getType() == TreeType.OAK && logs.size() > 20)
			baseLeafReach += 1;
		
		//Large pine, +1
		if (tree.getType() == TreeType.PINE && logs.size() > 20)
			baseLeafReach += 1;
		
		//Large jungle, +2
		if (tree.getType() == TreeType.JUNGLE && logs.size() > 20)
			baseLeafReach += 2;
		
		return baseLeafReach;
	}
	
	private boolean groundInReach(Block block) {
		for (int i = 0; i <= tree.getLeafGroundOffset(); i++)
			if (tree.isValidStandingBlock(block.getRelative(BlockFace.DOWN, i)))
				return true;
		
		return false;
	}
	
	private boolean chop() {
		debugger.addStage("CA.chop"); //1
		//First, try to damage the item in the player's hand. If this fails, don't break the tree.
		if (!damageItem()) return false;

		QwickTree.get().addTreeChop(tree.getType());
		
		debugger.addStage("CA.chop"); //2
		//Leaves are broken whether or not autoCollect is enabled, so just break them here
		for (Block leaf: leaves) {
			Logging.logBreak(player, leaf);
			leaf.setType(Material.AIR);
		}

		if (player.getGameMode() == GameMode.CREATIVE && Config.get().doCreativeAutoCollect()
				|| tree.doAutoCollect()) {
			//autoCollect is on, so break the logs.
			for (Block log: logs) {
				Logging.logBreak(player, log);
				log.setType(Material.AIR);
			}
			
			//Add all the stuff to player's inventory.
			dropToInventory();
		}
		else {
			//Break the logs naturally
			for (Block log: logs) {
				Logging.logBreak(player, log);
				log.breakNaturally();
			}
			
			HashMap<Location, ItemStack> drops = processDrops();
			
			int maxVines = vines.size() / 100;
			if (maxVines > 20) maxVines = 20;
			
			for (int i = 0; i < maxVines; i++)
				dropInWorld(vines.get(i), new ItemStack(Material.VINE));
			
			for (Location location: drops.keySet())
				dropInWorld(location, drops.get(location));
		}
		
		return true;
	}
	
	private void dropToInventory() {
		//Start with the logs
		ItemStack logItem = tree.getLogItem(logs.size());
		
		putInInventory(logItem);
		
		int maxVines = vines.size() / 100;
		if (maxVines > 20) maxVines = 20;
		
		putInInventory(new ItemStack(Material.VINE, maxVines));
		
		//Then the drops...
		HashMap<Location, ItemStack> drops = processDrops();
		
		for (ItemStack item: drops.values())
			putInInventory(item);
	}
	
	private void putInInventory(ItemStack item) {
		if (item.getAmount() == 0) return;
		
		HashMap<Integer, ItemStack> returned = player.getInventory().addItem(item);
		
		if (returned == null) return;
		
		for (ItemStack returnee: returned.values())
			dropInWorld(player.getLocation(), returnee);
	}
	
	private void dropInWorld(Location location, ItemStack item) {
		location.getWorld().dropItemNaturally(location, item);
	}
	
	private HashMap<Location, ItemStack> processDrops() {
		HashMap<Location, ItemStack> drops = new HashMap<Location, ItemStack>();
		HashMap<Material, Integer> maxDrops = new HashMap<Material, Integer>();
		HashMap<Material, Double> treeDrops = tree.getDrops();
		double count = (double) leaves.size();
		
		for (Material material: treeDrops.keySet())
			maxDrops.put(material, (int) Math.ceil(count * treeDrops.get(material)));
		
		for (Block leaf: leaves) {
			Material drop = nextItem(treeDrops, maxDrops);
			
			if (drop == null) continue;
			
			drops.put(leaf.getLocation(), tree.processItem(drop, 1));
		}
		
		return drops;
	}
	
	private Material nextItem(HashMap<Material, Double> treeDrops, HashMap<Material, Integer> maxDrops) {
		double number = rnd.nextDouble();
		Material selected = null;
		
		for (Material key: treeDrops.keySet()) {
			selected = key;
			if (number <= treeDrops.get(key)) break;
		}
		
		int dropsLeft = maxDrops.get(selected);
		
		if (dropsLeft <= 0)
			return null;
		
		maxDrops.put(selected, dropsLeft - 1);
		return selected;
	}
	
	private boolean damageItem() {
		if (player.getGameMode() == GameMode.CREATIVE && !Config.get().doCreativeDamage()) return true;
		
		int damageAmt;
		
		switch (tree.getDamageType()) {
			case NONE:
			default:
				damageAmt = 0;
				break;
			case NORM:
				damageAmt = logs.size();
				break;
			case FIXED:
				damageAmt = tree.getDamageAmount();
				break;
			case MULT:
				damageAmt = tree.getDamageAmount() * logs.size();
				break;
		}
		
		if (damageAmt <= 0) return true; //Skip if damage to deal is 0
		
		damageAmt = unbreaking(damageAmt);
		
		ItemStack item = player.getItemInHand();
		short maxDurability = item.getType().getMaxDurability();
		short newDurability = (short) (item.getDurability() + damageAmt);
		
		if (newDurability >= maxDurability) return false; //Don't break the tree if the item would break.
		
		item.setDurability(newDurability);
		
		return true;
	}
	
	private int unbreaking(int damageAmt) {
		int unbreakingLevel = player.getItemInHand().getEnchantmentLevel(Enchantment.DURABILITY);
		
		if (unbreakingLevel == 0 || damageAmt <= 0) return damageAmt;
		
		int newDamageAmt = 0;
		double level = 0.5;
		if (unbreakingLevel == 2) level = 0.6666;
		else if (unbreakingLevel == 3) level = 0.75;
		
		//Since unbreaking is applied for each damage point, I'll do the same
		for (int i = 0; i < damageAmt; i++)
			if (rnd.nextDouble() >= level)
				newDamageAmt ++;
		
		return newDamageAmt;
	}
	
	private void replant() {
		//Check if we should replant or not..
		if (player.getGameMode() == GameMode.CREATIVE && !Config.get().doCreativeReplant()
				|| !tree.doReplant()) return;
		
		TreeReplanter replanter = new TreeReplanter(tree, baseBlocks);
		
		Bukkit.getScheduler().scheduleSyncDelayedTask(QwickTree.get(), replanter);
	}
}