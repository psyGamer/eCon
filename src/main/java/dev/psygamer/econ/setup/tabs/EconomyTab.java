package dev.psygamer.econ.setup.tabs;

import dev.psygamer.econ.setup.ItemRegistry;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;

public class EconomyTab extends ItemGroup {
	
	public EconomyTab() {
		super("economy");
	}
	
	@Override
	public ItemStack makeIcon() {
		return new ItemStack(ItemRegistry.FIFTY_EUROS.get());
	}
}
