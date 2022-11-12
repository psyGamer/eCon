package dev.psygamer.econ.setup;

import dev.psygamer.econ.ECon;
import dev.psygamer.econ.block.tileentity.StoreTileEntity;

import net.minecraft.tileentity.TileEntityType;

import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.DeferredRegister;

public class TileEntityTypeRegistry {
	
	public static final DeferredRegister<TileEntityType<?>> TYPES = DeferredRegister.create(ForgeRegistries.TILE_ENTITIES, ECon.MODID);
	
	public static final RegistryObject<TileEntityType<StoreTileEntity>> STORE_BLOCK_TYPE = TYPES.register("store_block",
			() -> TileEntityType.Builder.of(
					StoreTileEntity::new,
					BlockRegistry.STORE_BLOCK.get()
			).build(null)
	);
	
	public static void register() {
		TileEntityTypeRegistry.TYPES.register(FMLJavaModLoadingContext.get().getModEventBus());
	}
}