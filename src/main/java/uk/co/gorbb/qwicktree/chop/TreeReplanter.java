package uk.co.gorbb.qwicktree.chop;

import java.util.List;

import org.bukkit.Location;

import uk.co.gorbb.qwicktree.tree.TreeInfo;

public class TreeReplanter implements Runnable {
	protected TreeInfo tree;
	protected List<Location> baseLocations;
	
	public TreeReplanter(TreeInfo tree, List<Location> baseLocations) {
		this.tree = tree;
		this.baseLocations = baseLocations;
	}
	
	public void run() {
		for (Location location: baseLocations)
			tree.replantSapling(location);
	}
	
}