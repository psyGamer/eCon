package dev.psygamer.econ.trades;

import dev.psygamer.econ.setup.ItemRegistry;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraftforge.common.BasicTrade;

public class TwentyEuroTrades {
	
	public static final class VineTrade extends BasicTrade {
		
		public VineTrade() {
			super(
					new ItemStack(Items.VINE, 64),
					new ItemStack(Items.VINE, 64),
					new ItemStack(ItemRegistry.TWENTY_EUROS.get(), 1),
					2, 4, 1.0f
			);
		}
	}
	
	public static final class IronBlockTrade extends BasicTrade {
		
		public IronBlockTrade() {
			super(
					new ItemStack(Items.IRON_BLOCK, 2),
					new ItemStack(ItemRegistry.TWENTY_EUROS.get(), 1),
					2, 3, 1.1f
			);
		}
	}
}
