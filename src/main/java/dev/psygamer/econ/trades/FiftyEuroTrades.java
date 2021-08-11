package dev.psygamer.econ.trades;

import dev.psygamer.econ.setup.ItemRegistry;

import net.minecraft.item.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.BasicTrade;

public class FiftyEuroTrades {
	
	public static final class DiamondTrade extends BasicTrade {
		
		public DiamondTrade() {
			super(
					new ItemStack(Items.DIAMOND, 2),
					new ItemStack(ItemRegistry.FIFTY_EUROS.get(), 1),
					2, 5, 1.1f
			);
		}
	}
	
	public static final class EmeraldTrade extends BasicTrade {
		
		public EmeraldTrade() {
			super(
					new ItemStack(Items.EMERALD, 49),
					new ItemStack(ItemRegistry.FIFTY_EUROS.get(), 1),
					2, 4, 1.7f
			);
		}
	}
}
