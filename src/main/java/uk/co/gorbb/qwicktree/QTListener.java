package uk.co.gorbb.qwicktree;

import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import uk.co.gorbb.qwicktree.chop.ChopAction;
import uk.co.gorbb.qwicktree.config.Config;
import uk.co.gorbb.qwicktree.tree.TreeInfo;
import uk.co.gorbb.qwicktree.util.DisabledList;
import uk.co.gorbb.qwicktree.util.Permission;
import uk.co.gorbb.qwicktree.util.debug.Debugger;

public class QTListener implements Listener {
	
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void blockBreakEvent(BlockBreakEvent event) {
		Block block = event.getBlock();
		Player player = event.getPlayer();
		Debugger debugger = Debugger.get(player);
		
		blockEvent(debugger, player, block);
		
		debugger.outputDebugger();
	}
	
	private void blockEvent(Debugger debugger, Player player, Block block) {
		debugger.addStage("QTL.blockEvent"); //1
		//Cheaper to check the player first
		if (!canChop(debugger, player)) return;

		debugger.addStage("QTL.blockEvent"); //2
		//Make sure the tree exists
		TreeInfo tree = Config.get().getTreeByLog(block);
		if (tree == null) return;

		debugger.addStage("QTL.blockEvent"); //3
		//Tree should be enabled
		if (!tree.isEnabled()) return;

		debugger.addStage("QTL.blockEvent"); //4
		//Tree has to be on the ground or stump should be enabled
		if (!tree.isValidStandingBlock(block.getRelative(BlockFace.DOWN)) && !tree.getAllowStump()) return;

		debugger.addStage("QTL.blockEvent"); //5
		//Okay then, chop!
		ChopAction chop = new ChopAction(player, tree, block);
		
		chop.go();

		debugger.addStage("QTL.blockEvent"); //6
	}
	
	private boolean canChop(Debugger debugger, Player player) {
		debugger.addStage("QTL.canChop"); //1
		//Player needs to have one of the 'items' in their hand.
		if (!Config.get().isHandItem(player.getInventory().getItemInMainHand())) return false;
		
		debugger.addStage("QTL.canChop"); //2
		//Check player has permission or usePerms is false in config
		if (!Permission.USE.has(player) && Config.get().usePerms()) return false;
		
		debugger.addStage("QTL.canChop"); //3
		//Check plugin is enabled for player
		if (DisabledList.get().isDisabledFor(player)) return false;
		
		debugger.addStage("QTL.canChop"); //4
		return true;
	}
	
	@EventHandler
	public void playerLogout(PlayerQuitEvent event) {
		Debugger.remove(event.getPlayer());
	}
	
}