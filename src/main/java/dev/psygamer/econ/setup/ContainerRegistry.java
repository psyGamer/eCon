package dev.psygamer.econ.setup;

import dev.psygamer.econ.ECon;
import dev.psygamer.econ.block.StoreContainer;
import net.minecraft.inventory.container.ContainerType;
import net.minecraftforge.common.extensions.IForgeContainerType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ContainerRegistry {
	
	public static final DeferredRegister<ContainerType<?>> CONTAINERS = DeferredRegister.create(ForgeRegistries.CONTAINERS, ECon.MODID);
	
	public static final RegistryObject<ContainerType<StoreContainer>> STORE_BLOCK_CONTAINER = CONTAINERS.register("store_block",
			() -> IForgeContainerType.create(StoreContainer::new)
	);
	
	public static void register() {
		ContainerRegistry.CONTAINERS.register(FMLJavaModLoadingContext.get().getModEventBus());
	}
}
