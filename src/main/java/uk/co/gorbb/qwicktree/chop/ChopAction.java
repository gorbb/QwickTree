package uk.co.gorbb.qwicktree.chop;

import java.util.ArrayList;
import java.util.Collection;
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
	private List<Location> baseBlocks;
	private Location dropLocation;
	
	private List<Block> logs,
						leaves,
						vines;
	
	private Random rnd;
	
	private boolean ignoreHouseBlocks;
	private Debugger debugger;
	
	public ChopAction(Player player, TreeInfo tree, Block baseBlock) {
		this.player = player;
		this.tree = tree;
		
		logsToSearch = new Stack<Block>();
		logs = new LinkedList<Block>();
		leaves = new LinkedList<Block>();
		baseBlocks = new LinkedList<Location>();
		vines = new LinkedList<Block>();
		
		rnd = new Random();
		
		ignoreHouseBlocks = false;
		
		debugger = Debugger.get(player);
		
		logsToSearch.add(baseBlock);
		dropLocation = baseBlock.getLocation();
	}
	
	public void go() {
		debugger.addStage("CA.go"); //1
		//Check
		if (!check()) return;

		debugger.addStage("CA.go"); //2
		//Damage
		if (!damage()) return;

		debugger.addStage("CA.go"); //3
		//Chop
		chop();

		debugger.addStage("CA.go"); //4
		//Replant
		replant();

		debugger.addStage("CA.go"); //5
	}
	
	private boolean check() {
		debugger.addStage("CA.check"); //1
		//Log search
		if (!logSearch()) return false;

		debugger.addStage("CA.check"); //2
		//Leaf/other search
		if (!leafSearch()) return false;

		debugger.addStage("CA.check"); //3
		//Size check
		if (!checkSize()) return false;

		debugger.addStage("CA.check"); //4
		return true;
	}
	
	private boolean damage() {
		return doDamage();
	}
	
	private void chop() {
		QwickTree.get().addTreeChop(tree.getType());
		
		//Break all of the leaves and vines
		debugger.addStage("CA.chop"); //1
		for (Block leaf: leaves)
			breakBlock(leaf);

		debugger.addStage("CA.chop"); //2
		for (Block vine: vines)
			breakBlock(vine);

		debugger.addStage("CA.chop"); //3
		//Check if autoCollect
		if (player.getGameMode() == GameMode.CREATIVE && Config.get().doCreativeAutoCollect()
				|| tree.doAutoCollect()) {
			
			for (Block log: logs)
				breakBlock(log);
			
			dropToInventory();
		}
		//Check if groupDrops
		else if (Config.get().doGroupDrops()) {
			for (Block log: logs)
				breakBlock(log);
			
			dropToGroup();
		}
		//Check normal
		else {
			for (Block log: logs)
				breakBlockNaturally(log);
			
			dropToWorld();
		}
		debugger.addStage("CA.chop"); //4
	}
	
	private void replant() {
		//Check if the tree should be replanted, or not..
		if (player.getGameMode() == GameMode.CREATIVE && !Config.get().doCreativeReplant()
				|| !tree.doReplant()) return;
		
		TreeReplanter replanter = new TreeReplanter(tree, baseBlocks);
		
		Bukkit.getScheduler().scheduleSyncDelayedTask(QwickTree.get(), replanter);
	}
	
	/* ### CHECK ### */
	private boolean logSearch() {
		while (!logsToSearch.isEmpty()) {
			//Get the next block to search around
			Block current = logsToSearch.pop();
			
			//Process it...
			if (!processCurrentLog(current)) return false;
			
			//Then search around it.
			if (!searchCurrentLog(current)) return false;
		}
		
		return true;
	}
	
	private boolean processCurrentLog(Block block) {
		//Don't add it if it's already in the list
		if (logs.contains(block)) return true;
		
		//Add it to the list
		logs.add(block);
		
		//Check against max tree size
		if (logs.size() > tree.getLogMax()) return false;
		
		//If it's a standing block, then add it to base blocks too, but only if there's space
		if (tree.isValidStandingBlock(block.getRelative(BlockFace.DOWN)) && baseBlocks.size() < 4)
			baseBlocks.add(block.getLocation());
		
		return true;
	}
	
	private boolean searchCurrentLog(Block current) {
		int yStart = tree.getAnyBlock() ? -1 : 0;
		
		for (int x = -1; x <= 1; x++)
			for (int z = -1; z <= 1; z++)
				for (int y = yStart; y <= 1; y++) {
					Block block = current.getRelative(x, y, z);
					
					if (!houseBlockSearch(block)) return false;
					
					if (!tree.isValidLog(block) ||				//If it's not a valid log...
							logsToSearch.contains(block) ||		//...or is already set to search around...
							logs.contains(block) ||				//...or has already been searched around...
							current.equals(block))				//...or is the current one...
						continue;								//...then skip to the next loop.
					
					logsToSearch.push(block);
				}
		
		return true;
	}
	
	private boolean houseBlockSearch(Block log) {
		for (int x = -2; x <= 2; x++)
			for (int z = -2; z <= 2; z++)
				for (int y = -1; y <= 1; y++) {
					Block current = log.getRelative(x,  y,  z);
					
					// Check for house block
					if (!ignoreHouseBlocks && Config.get().isHouseBlock(current))
						if (HouseIgnore.get().ignoreHouseBlocks(player))
							ignoreHouseBlocks = true;
						else {
							Message.NOTIFY.send(Permission.NOTIFY, player.getName(), formatLocation(current));
							return false;
						}
				}

		return true;
	}
	
	private boolean leafSearch() {
		int leafReach = getLeafReach();
		
		for (Block log: logs) //For each log
			for (int x = -leafReach; x <= leafReach; x++)
				for (int z = -leafReach; z <= leafReach; z++)
					for (int y = 0; y <= leafReach; y++) {
						Block current = log.getRelative(x, y, z);
						
						if (!processCurrentLeaf(current)) return false;
					}
		
		return true;
	}
	
	private boolean processCurrentLeaf(Block current) {
		//Check for vines
		if (current.getType() == Material.VINE && vines.size() < 20)
			vines.add(current);
		
		//Check for leaves
		if (!tree.isValidLeaf(current) ||		//If it's not a valid leaf...
				leaves.contains(current) ||		//...or the leaf has already been found...
				groundInReach(current))			//...or it's within ground reach...
			return true;						//...then get outta here!
		
		leaves.add(current);
		return true;
	}
	
	
	private boolean checkSize() {
		debugger.setStage("Log Size", logs.size());
		debugger.setStage("Leaf Size", leaves.size());
		
		if (logs.size() < tree.getLogMin()) return false;
		if (logs.size() > tree.getLogMax()) return false;
		
		if (leaves.size() < tree.getLeafMin()) return false;
		
		return true;
	}
	
	/* ### DAMAGE ### */
	private boolean doDamage() {
		//If player is creative and shouldn't do damage, then return
		if (player.getGameMode() == GameMode.CREATIVE && !Config.get().doCreativeDamage()) return true;
		
		//Work out base damage
		int damageAmt;
		
		switch (tree.getDamageType()) {
			case NONE:
			default:
				damageAmt = 0;
				break;
			case NORM:
				damageAmt = (short) logs.size();
				break;
			case FIXED:
				damageAmt = (short) tree.getDamageAmount();
				break;
			case MULT:
				damageAmt = (short) (tree.getDamageAmount() * logs.size());
		}
		
		//Work out unbreaking
		damageAmt = calculateUnbreaking(damageAmt);
		
		//Check we can do this damage
		ItemStack item = player.getItemInHand();
		short newDurability = (short) (item.getDurability() + damageAmt); //Figure out the new durability of the item
		
		if (newDurability > item.getType().getMaxDurability()) return false; //If the item cannot take this much damage, then return
		
		//Apply damage
		item.setDurability(newDurability);
		
		return true;
	}
	
	private int calculateUnbreaking(int damageAmt) {
		int unbreakingLevel = player.getItemInHand().getEnchantmentLevel(Enchantment.DURABILITY);
		
		//If the item doesn't have unbreaking, or the damage amount is already nothing (or less?!), then don't do anything
		if (unbreakingLevel == 0 || damageAmt <= 0) return damageAmt;
		
		int newDamageAmt = 0;
		double chance = 0.5;
		if (unbreakingLevel == 2) chance = 0.6666;
		else if (unbreakingLevel == 3) chance = 0.75;
		
		//Since unbreaking is applied for EACH point of damage, I'll do the same.
		for (int point = 0; point < damageAmt; point++)
			if (rnd.nextDouble() >= chance)
				newDamageAmt ++;
		
		return newDamageAmt;
	}
	
	/* ### CHOP ### */
	private void dropToInventory() {
		ItemStack[] items = combineItems();
		HashMap<Integer, ItemStack> returned = player.getInventory().addItem(items);
		
		if (returned == null) return;
		
		ItemStack[] returnedItems = returned.values().toArray(new ItemStack[returned.size()]);
		
		dropAt(dropLocation, returnedItems);
	}
	
	private void dropToGroup() {
		dropAt(dropLocation, combineItems());
	}
	
	private void dropToWorld() {
		HashMap<Location, ItemStack> drops = processDrops();
		
		for (Location location: drops.keySet())
			dropAt(location, drops.get(location));
	}
	
	
	private ItemStack[] combineItems() {
		Collection<ItemStack> drops = processDrops().values();
		HashMap<Material, Integer> combinedDrops = new HashMap<Material, Integer>();
		
		//First get how many of each item we should have
		for (ItemStack drop: drops) {
			int qty = drop.getAmount();
			
			if (combinedDrops.containsKey(drop.getType()))
				qty += combinedDrops.get(drop.getType());
			
			combinedDrops.put(drop.getType(), qty);
		}
		
		//Add the logs, since everything here is being combined...
		combinedDrops.put(Material.LOG, logs.size());
		
		//And then the vines if there are any.
		if (vines.size() > 0)
			combinedDrops.put(Material.VINE, vines.size());
		
		//Then create ItemStacks of this
		List<ItemStack> combinedList = new ArrayList<ItemStack>(combinedDrops.size());
		
		for (Material material: combinedDrops.keySet())
			combinedList.add(tree.processItem(material, combinedDrops.get(material)));
		
		return combinedList.toArray(new ItemStack[combinedList.size()]);
	}
	
	private HashMap<Location, ItemStack> processDrops() {
		HashMap<Location, ItemStack> drops = new HashMap<Location, ItemStack>();
		
		//For each leaf...
		for (Block leaf: leaves) {
			//Get a random material to use.
			Material drop = getRandomDrop();
			if (drop == null) continue;
			
			drops.put(leaf.getLocation(), tree.processItem(drop, 1));
		}
		
		return drops;
	}
	
	private Material getRandomDrop() {
		HashMap<Double, Material> dropChances = tree.getDrops();
		double number = rnd.nextDouble();
		Material selected = null;
		
		for (double dropChance: dropChances.keySet()) {
			selected = dropChances.get(dropChance);
			
			if (number <= dropChance) break;
		}
		
		return selected;
	}
	
	private void dropAt(Location location, ItemStack... items) {
		for (ItemStack item: items)
			location.getWorld().dropItemNaturally(location, item);
	}

	/* ### OTHER ### */
	
	private int getLeafReach() {
		int baseLeafReach = tree.getLeafReach();
		
		TreeType type = tree.getType();
		int size = logs.size();
		Biome biome = logs.get(0).getBiome();
		
		
		if (type == TreeType.OAK)
			if (biome == Biome.SWAMPLAND || biome == Biome.SWAMPLAND_MOUNTAINS)
				baseLeafReach += 1;		//Oak in swamp, increase by 1
			else if (size >= 15)
				baseLeafReach += 1;		//Large oak elsewhere, increase by 1
		
		if (type == TreeType.PINE && size >= 20)
			baseLeafReach += 1;			//Large pine, increase by 1
		
		if (type == TreeType.JUNGLE && size >= 20)
			baseLeafReach += 2;			//Large jungle, increase by 1
		
		return baseLeafReach;
	}
	
	private String formatLocation(Block block) {
		Location location = block.getLocation();
		
		return  location.getWorld().getName() + ", " +
				location.getBlockX() + ", " + 
				location.getBlockY() + ", " +
				location.getBlockZ();
	}
	
	private boolean groundInReach(Block block) {
		int groundReach = tree.getLeafGroundOffset();
		
		for (int distance = 1; distance <= groundReach; distance++) {
			Block newBlock = block.getRelative(BlockFace.DOWN, distance);
			
			if (tree.isValidStandingBlock(newBlock)) return true;
		}
		
		return false;
	}
	
	private void breakBlock(Block block) {
		Logging.logBreak(player, block);
		block.setType(Material.AIR);
	}
	
	private void breakBlockNaturally(Block block) {
		Logging.logBreak(player, block);
		block.breakNaturally();
	}
	
}