package dev.psygamer.econ.block;

import dev.psygamer.econ.setup.ContainerRegistry;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.ChestContainer;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;

import java.util.Objects;

public class StoreStorageContainer extends ChestContainer {
	
	public StoreStorageContainer(final int windowId, final PlayerInventory playerInventory, final PacketBuffer data) {
		this(windowId, playerInventory, getTileEntity(playerInventory, data));
	}
	
	public StoreStorageContainer(final int windowId, final PlayerInventory playerInventory, final IInventory inventory) {
		super(ContainerRegistry.STORE_STORAGE_CONTAINER.get(), windowId, playerInventory, inventory, 3);
	}
	
	private static StoreTileEntity getTileEntity(final PlayerInventory playerInventory, final PacketBuffer data) {
		Objects.requireNonNull(playerInventory, "PlayerInventory may not be null");
		Objects.requireNonNull(data, "PacketBuffer may not be null");
		
		final TileEntity tileEntity = playerInventory.player.level.getBlockEntity(data.readBlockPos());
		
		if (tileEntity instanceof StoreTileEntity) {
			return (StoreTileEntity) tileEntity;
		}
		
		throw new IllegalStateException("Could not find TileEntity");
	}
}
