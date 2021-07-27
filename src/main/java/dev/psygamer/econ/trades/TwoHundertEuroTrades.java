package dev.psygamer.econ.trades;

import dev.psygamer.econ.setup.ItemRegistry;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraftforge.common.BasicTrade;

public class TwoHundertEuroTrades {
	
	public static final class NetheriteHoeTrade extends BasicTrade {
		
		public NetheriteHoeTrade() {
			super(
					new ItemStack(Items.NETHERITE_HOE, 1),
					new ItemStack(ItemRegistry.FIVE_HUNDERT_EUROS, 1),
					1, 10, 1.0f
			);
		}
	}
	
	public static final class DiamondBlockTrade extends BasicTrade {
		
		public DiamondBlockTrade() {
			super(
					new ItemStack(Items.DIAMOND_BLOCK, 1),
					new ItemStack(ItemRegistry.TWO_HUNDERT_EUROS, 1),
					3, 10, 1.1f
			);
		}
	}
}
