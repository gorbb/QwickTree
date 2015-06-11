package uk.co.gorbb.qwicktree.tree.info;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;

public class BlockInfo {
	private Material material;
	private byte data;
	
	public BlockInfo(Material material, byte data) {
		this.material = material;
		this.data = data;
	}
	
	public Material getMaterial() {
		return material;
	}
	
	public byte getData() {
		return data;
	}
	
	@SuppressWarnings("deprecation")
	public boolean matches(Block block) {
		return block.getType() == material && block.getData() == data;
	}
	
	@SuppressWarnings("deprecation")
	public boolean matches(Block block, int mod) {
		return block.getType() == material && matchesData(block.getData(), mod);
	}
	
	public boolean matchesData(byte data, int mod) {
		return data % mod == this.data;
	}
	
	@SuppressWarnings("deprecation")
	public ItemStack toItemStack(int qty) {
		return new ItemStack(material, qty, (short) 0, data);
	}
	
}