package dev.psygamer.econ.setup;

import dev.psygamer.econ.ECon;

import net.minecraft.item.Item;

import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ItemRegistry {
	
	public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, ECon.MODID);
	
	/* Euros */
	
	public static final MoneyItem ONE_EURO = new MoneyItem("one_euro", 1);
	public static final MoneyItem FIVE_EUROS = new MoneyItem("five_euros", 5);
	public static final MoneyItem TEN_EUROS = new MoneyItem("ten_euros", 10);
	public static final MoneyItem TWENTY_EUROS = new MoneyItem("twenty_euros", 20);
	public static final MoneyItem FIFTY_EUROS = new MoneyItem("fifty_euros", 50);
	public static final MoneyItem ONE_HUNDERT_EUROS = new MoneyItem("one_hundert_euros", 100);
	public static final MoneyItem TWO_HUNDERT_EUROS = new MoneyItem("two_hundert_euros", 200);
	public static final MoneyItem FIVE_HUNDERT_EUROS = new MoneyItem("five_hundert_euros", 500);
	
	public static void register() {
		ItemRegistry.ITEMS.register(FMLJavaModLoadingContext.get().getModEventBus());
	}
}
