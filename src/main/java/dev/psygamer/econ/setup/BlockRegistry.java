package dev.psygamer.econ.setup;

import dev.psygamer.econ.ECon;

import dev.psygamer.econ.block.StoreBlock;
import dev.psygamer.econ.item.TabRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.HorizontalBlock;
import net.minecraft.block.RotatedPillarBlock;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.function.Supplier;

public class BlockRegistry {
	
	public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, ECon.MODID);
	
	public static final RegistryObject<HorizontalBlock> STORE_BLOCK = register("store_block", StoreBlock::new);
	
	public static void register() {
		BlockRegistry.BLOCKS.register(FMLJavaModLoadingContext.get().getModEventBus());
	}
	
	private static <T extends Block> RegistryObject<T> register(final String registryName, final Supplier<T> blockSupplier) {
		return BLOCKS.register(registryName, blockSupplier);
	}
}
