package dev.psygamer.econ.trades;

import dev.psygamer.econ.setup.ItemRegistry;

import net.minecraft.item.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.BasicTrade;

public class FiveHundertEuroTrades {
	
	public static final class NetheriteTrade extends BasicTrade {
		
		public NetheriteTrade() {
			super(
					new ItemStack(Items.NETHERITE_INGOT, 1),
					new ItemStack(ItemRegistry.FIVE_HUNDERT_EUROS.get(), 1),
					1, 25, 1.0f
			);
		}
	}
	
	public static final class NetherstarTrade extends BasicTrade {
		
		public NetherstarTrade() {
			super(
					new ItemStack(Items.NETHER_STAR, 1),
					new ItemStack(ItemRegistry.FIVE_HUNDERT_EUROS.get(), 2),
					1, 30, 1.0f
			);
		}
	}
}
