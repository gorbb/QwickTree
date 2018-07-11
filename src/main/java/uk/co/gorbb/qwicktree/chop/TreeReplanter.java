package uk.co.gorbb.qwicktree.chop;

import java.util.List;

import org.bukkit.Location;

import uk.co.gorbb.qwicktree.tree.TreeInfo;

public class TreeReplanter implements Runnable {
	protected TreeInfo tree;
	protected List<Location> baseLocations;
	protected int toReplant;
	
	public TreeReplanter(TreeInfo tree, List<Location> baseLocations, int toReplant) {
		this.tree = tree;
		this.baseLocations = baseLocations;
		this.toReplant = toReplant;
	}
	
	public void run() {
		//Don't bother if there's nothing left..
		if (toReplant <= 0)
			return;
		
		for (Location location: baseLocations) {
			tree.replantSapling(location);
			
			toReplant--;
			if (toReplant <= 0)
				break;
		}
	}
	
}