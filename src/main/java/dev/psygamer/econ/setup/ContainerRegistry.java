package dev.psygamer.econ.setup;

import dev.psygamer.econ.ECon;
import dev.psygamer.econ.block.container.StoreOwnerContainer;
import dev.psygamer.econ.block.container.StoreStorageContainer;
import dev.psygamer.econ.block.container.StoreCustomerContainer;

import net.minecraft.inventory.container.ContainerType;

import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.common.extensions.IForgeContainerType;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.DeferredRegister;

public class ContainerRegistry {
	
	public static final DeferredRegister<ContainerType<?>> CONTAINERS = DeferredRegister.create(ForgeRegistries.CONTAINERS, ECon.MODID);
	
	public static final RegistryObject<ContainerType<StoreOwnerContainer>> STORE_BLOCK_CONTAINER = CONTAINERS.register("store_owner",
			() -> IForgeContainerType.create(StoreOwnerContainer::new)
	);
	public static final RegistryObject<ContainerType<StoreStorageContainer>> STORE_STORAGE_CONTAINER = CONTAINERS.register("store_storage",
			() -> IForgeContainerType.create(StoreStorageContainer::new)
	);
	public static final RegistryObject<ContainerType<StoreCustomerContainer>> STORE_CUSTOMER_CONTAINER = CONTAINERS.register("store_customer",
			() -> IForgeContainerType.create(StoreCustomerContainer::new)
	);
	
	public static void register() {
		ContainerRegistry.CONTAINERS.register(FMLJavaModLoadingContext.get().getModEventBus());
	}
}
