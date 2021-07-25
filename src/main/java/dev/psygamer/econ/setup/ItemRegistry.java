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
	
	public static final RegistryObject<Item> ONE_EURO 			= ItemRegistry.ITEMS.register("one_euro",
			() -> new Item(new Item.Properties().tab(TabRegistry.TAB_ECONOMY)
	));
	public static final RegistryObject<Item> FIVE_EUROS 		= ItemRegistry.ITEMS.register("five_euros",
			() -> new Item(new Item.Properties().tab(TabRegistry.TAB_ECONOMY)
	));
	public static final RegistryObject<Item> TEN_EUROS 			= ItemRegistry.ITEMS.register("ten_euros",
			() -> new Item(new Item.Properties().tab(TabRegistry.TAB_ECONOMY)
	));
	public static final RegistryObject<Item> TWENTY_EUROS 		= ItemRegistry.ITEMS.register("twenty_euros",
			() -> new Item(new Item.Properties().tab(TabRegistry.TAB_ECONOMY)
	));
	public static final RegistryObject<Item> FIFTY_EUROS 		= ItemRegistry.ITEMS.register("fifty_euros",
			() -> new Item(new Item.Properties().tab(TabRegistry.TAB_ECONOMY)
	));
	public static final RegistryObject<Item> ONE_HUNDERT_EUROS 	= ItemRegistry.ITEMS.register("one_hundert_euros",
			() -> new Item(new Item.Properties().tab(TabRegistry.TAB_ECONOMY)
	));
	public static final RegistryObject<Item> TWO_HUNDERT_EUROS 	= ItemRegistry.ITEMS.register("two_hundert_euros",
			() -> new Item(new Item.Properties().tab(TabRegistry.TAB_ECONOMY)
	));
	public static final RegistryObject<Item> FIVE_HUNDERT_EUROS = ItemRegistry.ITEMS.register("five_hundert_euros",
			() -> new Item(new Item.Properties().tab(TabRegistry.TAB_ECONOMY)
	));
	
	public static void register() {
		ItemRegistry.ITEMS.register(FMLJavaModLoadingContext.get().getModEventBus());
	}
}
