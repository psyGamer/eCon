package dev.psygamer.econ.block.container;

import dev.psygamer.econ.setup.ContainerRegistry;
import dev.psygamer.econ.block.StoreBlock;
import dev.psygamer.econ.block.container.slot.StorePreviewSlot;
import dev.psygamer.econ.block.tileentity.StoreTileEntity;

import net.minecraft.util.IWorldPosCallable;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.network.PacketBuffer;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.tileentity.TileEntity;

import java.util.Objects;

public class StoreCustomerContainer extends Container {
	
	private final PlayerInventory playerInventory;
	private final StoreTileEntity tileEntity;
	private final IWorldPosCallable canInteractWithCallable;
	
	private final StorePreviewSlot previewSlot;
	
	public StoreCustomerContainer(final int windowId, final PlayerInventory playerInventory, final PacketBuffer data) {
		this(windowId, playerInventory, getTileEntity(playerInventory, data));
	}
	
	public StoreCustomerContainer(final int windowId, final PlayerInventory playerInventory, final StoreTileEntity tileEntity) {
		super(ContainerRegistry.STORE_CUSTOMER_CONTAINER.get(), windowId);
		
		this.playerInventory = playerInventory;
		this.tileEntity = tileEntity;
		this.canInteractWithCallable = IWorldPosCallable.create(tileEntity.getLevel(), tileEntity.getBlockPos());
		
		this.previewSlot = new StorePreviewSlot(new Inventory(1), 0, 97, 22);
		this.previewSlot.set(tileEntity.getOfferedItem().copy());
		
		addSlot(this.previewSlot);
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
	
	@Override
	public boolean stillValid(final PlayerEntity playerEntity) {
		return this.canInteractWithCallable.evaluate(
				(world, pos) -> world.getBlockState(pos).getBlock() instanceof StoreBlock &&
						playerEntity.distanceToSqr(
								(double) pos.getX() + 0.5D,
								(double) pos.getY() + 0.5D,
								(double) pos.getZ() + 0.5D
						) <= 64.0D, true
		);
	}
	
	public PlayerInventory getPlayerInventory() {
		return this.playerInventory;
	}
	
	public StoreTileEntity getTileEntity() {
		return this.tileEntity;
	}
	
	public StorePreviewSlot getPreviewSlot() {
		return this.previewSlot;
	}
	
}
