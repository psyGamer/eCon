package dev.psygamer.econ.setup;

import dev.psygamer.econ.ECon;

import dev.psygamer.econ.item.MoneyItem;

import net.minecraft.item.Item;
import net.minecraft.item.BlockItem;

import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.DeferredRegister;

public class ItemRegistry {
	
	public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, ECon.MODID);
	
	/* Block Items */
	
	public static final RegistryObject<Item> STORE_BLOCK_ITEM = ITEMS.register("store_block",
			() -> new BlockItem(BlockRegistry.STORE_BLOCK.get(), new Item.Properties().tab(TabRegistry.TAB_ECON)));
	
	/* Euros */
	
	public static final RegistryObject<Item> ONE_EURO = ITEMS.register("one_euro", () -> new MoneyItem(1));
	public static final RegistryObject<Item> FIVE_EUROS = ITEMS.register("five_euros", () -> new MoneyItem(5));
	public static final RegistryObject<Item> TEN_EUROS = ITEMS.register("ten_euros", () -> new MoneyItem(10));
	public static final RegistryObject<Item> TWENTY_EUROS = ITEMS.register("twenty_euros", () -> new MoneyItem(20));
	public static final RegistryObject<Item> FIFTY_EUROS = ITEMS.register("fifty_euros", () -> new MoneyItem(50));
	public static final RegistryObject<Item> ONE_HUNDERT_EUROS = ITEMS.register("one_hundert_euros", () -> new MoneyItem(100));
	public static final RegistryObject<Item> TWO_HUNDERT_EUROS = ITEMS.register("two_hundert_euros", () -> new MoneyItem(200));
	public static final RegistryObject<Item> FIVE_HUNDERT_EUROS = ITEMS.register("five_hundert_euros", () -> new MoneyItem(500));
	
	public static void register() {
		ItemRegistry.ITEMS.register(FMLJavaModLoadingContext.get().getModEventBus());
	}
}
