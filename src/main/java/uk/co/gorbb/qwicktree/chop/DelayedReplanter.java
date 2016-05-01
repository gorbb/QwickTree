package uk.co.gorbb.qwicktree.chop;

import java.util.List;

import org.bukkit.Location;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;

import uk.co.gorbb.qwicktree.tree.TreeInfo;
import uk.co.gorbb.qwicktree.util.Message;

public class DelayedReplanter extends TreeReplanter {
	private Item item;
	
	public DelayedReplanter(Item item, TreeInfo tree, List<Location> baseLocations) {
		super(tree, baseLocations);
		
		this.item = item;
	}
	
	@Override
	public void run() {
		//Check the player has enough saplings to replant
		if (item == null) return;
		
		ItemStack itemStack = item.getItemStack();
		int itemCount = itemStack.getAmount();
		int requiredCount = baseLocations.size();
		
		Message.CUSTOM.info("Item Count: " + itemCount + ", Required: " + requiredCount);
		
		if (itemCount < requiredCount) return;
		
		if (itemCount - requiredCount == 0) item.remove();
		else {
			itemStack.setAmount(itemCount - requiredCount);
			item.setItemStack(itemStack);
		}
		
		super.run();
	}
}