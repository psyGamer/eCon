package dev.psygamer.econ.trades;

import dev.psygamer.econ.setup.ItemRegistry;

import net.minecraft.item.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.BasicTrade;

public class OneHundertEuroTrades {
	
	public static final class DiamondTrade extends BasicTrade {
		
		public DiamondTrade() {
			super(
					new ItemStack(Items.DIAMOND, 4),
					new ItemStack(ItemRegistry.ONE_HUNDERT_EUROS.get(), 1),
					4, 10, 1.2f
			);
		}
	}
	
	public static final class EmeraldBlockTrade extends BasicTrade {
		
		public EmeraldBlockTrade() {
			super(
					new ItemStack(Items.EMERALD_BLOCK, 10),
					new ItemStack(ItemRegistry.ONE_HUNDERT_EUROS.get(), 1),
					2, 10, 1.1f
			);
		}
	}
}
