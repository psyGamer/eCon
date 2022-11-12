package dev.psygamer.econ.trades;

import dev.psygamer.econ.setup.ItemRegistry;

import net.minecraft.item.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.BasicTrade;

public class TenEuroTrades {
	
	public static final class GoldBlockTrade extends BasicTrade {
		
		public GoldBlockTrade() {
			super(
					new ItemStack(Items.GOLD_BLOCK, 3),
					new ItemStack(ItemRegistry.TEN_EUROS.get(), 1),
					1, 3, 1.0f
			);
		}
	}
	
	public static final class RedstoneTrade extends BasicTrade {
		
		public RedstoneTrade() {
			super(
					new ItemStack(Items.REDSTONE, 32),
					new ItemStack(ItemRegistry.TEN_EUROS.get(), 1),
					5, 3, 1.15f
			);
		}
	}
}
