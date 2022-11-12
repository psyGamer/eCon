package dev.psygamer.econ.trades;

import dev.psygamer.econ.setup.ItemRegistry;

import net.minecraft.item.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.BasicTrade;

public class OneEuroTrades {
	
	public static final class DioriteTrade extends BasicTrade {
		
		public DioriteTrade() {
			super(
					new ItemStack(Items.DIORITE, 15),
					new ItemStack(ItemRegistry.ONE_EURO.get(), 1),
					7, 0, 1.5f
			);
		}
	}
	
	public static final class CoarseDirtTrade extends BasicTrade {
		
		public CoarseDirtTrade() {
			super(
					new ItemStack(Items.COARSE_DIRT, 42),
					new ItemStack(ItemRegistry.ONE_EURO.get(), 1),
					4, 0, 1.2f
			);
		}
	}
	
	public static final class SnowballTrade extends BasicTrade {
		
		public SnowballTrade() {
			super(
					new ItemStack(Items.SNOWBALL, 10),
					new ItemStack(ItemRegistry.ONE_EURO.get(), 1),
					4, 0, 1.6f
			);
		}
	}
	
	public static final class WoodenToolTrade extends BasicTrade {
		
		public WoodenToolTrade() {
			super(
					new ItemStack(Items.WOODEN_SHOVEL, 1),
					new ItemStack(Items.WOODEN_HOE, 1),
					new ItemStack(ItemRegistry.ONE_EURO.get(), 1),
					1, 1, 1.0f
			);
		}
	}
}
