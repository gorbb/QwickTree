package uk.co.gorbb.qwicktree;

import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import uk.co.gorbb.qwicktree.chop.ChopAction;
import uk.co.gorbb.qwicktree.config.Config;
import uk.co.gorbb.qwicktree.tree.TreeInfo;
import uk.co.gorbb.qwicktree.util.Permission;
import uk.co.gorbb.qwicktree.util.debug.Debugger;

public class QTListener implements Listener {
	
	@EventHandler(ignoreCancelled = true)
	public void blockBreakEvent(BlockBreakEvent event) {
		Block block = event.getBlock();
		Player player = event.getPlayer();
		Debugger debugger = Debugger.get(player);
		
		debugger.addStage("QTL.b"); //1
		//Cheaper to check the player first
		if (!canChop(player)) return;

		debugger.addStage("QTL.b"); //2
		//Make sure the tree exists
		TreeInfo tree = Config.get().getTreeByLog(block);
		if (tree == null) return;

		debugger.addStage("QTL.b"); //3
		//Tree should be enabled
		if (!tree.isEnabled()) return;

		debugger.addStage("QTL.b"); //4
		//Tree has to be on the ground
		if (!tree.isValidStandingBlock(block.getRelative(BlockFace.DOWN))) return;

		debugger.addStage("QTL.b"); //5
		//Okay then, chop!
		ChopAction chop = new ChopAction(player, tree, block);
		
		chop.go();

		debugger.addStage("QTL.b"); //6
		Debugger.get(player).outputDebugger();
	}
	
	private boolean canChop(Player player) {
		//Player needs to have one of the 'items' in their hand.
		if (!Config.get().isHandItem(player.getItemInHand())) return false;
		
		//Check player has permission
		if (!Permission.USE.has(player)) return false;
		
		//Check plugin is enabled for player
		
		
		return true;
	}
	
	@EventHandler
	public void playerLogout(PlayerQuitEvent event) {
		Debugger.remove(event.getPlayer());
	}
	
}