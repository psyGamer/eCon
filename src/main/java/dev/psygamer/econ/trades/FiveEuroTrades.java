package dev.psygamer.econ.trades;

import dev.psygamer.econ.setup.ItemRegistry;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraftforge.common.BasicTrade;

public class FiveEuroTrades {
	
	public static final class IronHoeTrade extends BasicTrade {
		
		public IronHoeTrade() {
			super(
					new ItemStack(Items.IRON_HOE, 2),
					new ItemStack(Items.LAPIS_LAZULI, 2),
					new ItemStack(ItemRegistry.FIVE_EUROS.get(), 1),
					3, 3, 1.15f
			);
		}
	}
	
	public static final class LapislazuliTrades extends BasicTrade {
		
		public LapislazuliTrades() {
			super(
					new ItemStack(Items.LAPIS_LAZULI, 32),
					new ItemStack(ItemRegistry.FIVE_EUROS.get(), 1),
					8, 2, 1.25f
			);
		}
	}
}
