package dev.psygamer.econ.setup;

import dev.psygamer.econ.trades.*;

import net.minecraft.entity.merchant.villager.VillagerTrades;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.event.village.WandererTradesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

@Mod.EventBusSubscriber
public class TradeRegistry {
	
	@SubscribeEvent
	public static void onWanderingTraderSpawn(final WandererTradesEvent event) {
		registerGeneric(event,
				new OneEuroTrades.SnowballTrade(), new OneEuroTrades.WoodenToolTrade(), new OneEuroTrades.CoarseDirtTrade(), new OneEuroTrades.DioriteTrade(),
				new FiveEuroTrades.LapislazuliTrades(), new FiveEuroTrades.IronHoeTrade(),
				new TenEuroTrades.RedstoneTrade(), new TenEuroTrades.GoldBlockTrade(),
				new TwentyEuroTrades.IronBlockTrade(), new TwentyEuroTrades.VineTrade(),
				new FiftyEuroTrades.DiamondTrade(), new FiftyEuroTrades.EmeraldTrade()
		);
		registerRare(event,
				new OneHundertEuroTrades.DiamondTrade(), new OneHundertEuroTrades.EmeraldBlockTrade(),
				new TwoHundertEuroTrades.DiamondBlockTrade(), new TwoHundertEuroTrades.NetheriteHoeTrade(),
				new FiveHundertEuroTrades.NetheriteTrade(), new FiveHundertEuroTrades.NetherstarTrade()
		);
	}
	
	private static void registerGeneric(final WandererTradesEvent event, final VillagerTrades.ITrade... trades) {
		for (final VillagerTrades.ITrade trade : trades) {
			event.getGenericTrades().add(trade);
		}
	}
	
	private static void registerRare(final WandererTradesEvent event, final VillagerTrades.ITrade... trades) {
		for (final VillagerTrades.ITrade trade : trades) {
			event.getRareTrades().add(trade);
		}
	}
}
