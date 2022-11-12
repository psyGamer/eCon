package dev.psygamer.econ.block.container;

import dev.psygamer.econ.setup.ContainerRegistry;
import dev.psygamer.econ.block.StoreBlock;
import dev.psygamer.econ.block.container.slot.StoreStorageSlot;
import dev.psygamer.econ.block.tileentity.StoreTileEntity;

import net.minecraft.util.IWorldPosCallable;
import net.minecraft.item.ItemStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.network.PacketBuffer;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.tileentity.TileEntity;

import java.util.Objects;

public class StoreStorageContainer extends Container {
	
	private final StoreTileEntity tileEntity;
	private final IWorldPosCallable canInteractWithCallable;
	
	public StoreStorageContainer(final int windowId, final PlayerInventory playerInventory, final PacketBuffer data) {
		this(windowId, playerInventory, getTileEntity(playerInventory, data));
	}
	
	public StoreStorageContainer(final int windowID, final PlayerInventory playerInventory, final StoreTileEntity tileEntity) {
		super(ContainerRegistry.STORE_STORAGE_CONTAINER.get(), windowID);
		
		this.tileEntity = tileEntity;
		this.canInteractWithCallable = IWorldPosCallable.create(tileEntity.getLevel(), tileEntity.getBlockPos());
		
		// When a slot gets added determines the index, not the 2nd parameter
		for (int row = 0 ; row < 3 ; row++) {
			for (int col = 0 ; col < 9 ; col++) {
				this.addSlot(new StoreStorageSlot(tileEntity,
						row * 9 + col + 1,
						8 + col * 18,
						31 + row * 18
				));
			}
		}
		
		for (int row = 0 ; row < 3 ; row++) {
			for (int col = 0 ; col < 9 ; col++) {
				this.addSlot(new Slot(playerInventory,
						col + row * 9 + 9,
						col * 18 + 8,
						row * 18 + 110
				));
			}
		}
		
		for (int col = 0 ; col < 9 ; col++) {
			this.addSlot(new Slot(playerInventory,
					col,
					col * 18 + 8,
					168
			));
		}
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
	
	@Override
	public ItemStack quickMoveStack(final PlayerEntity playerEntity, final int slotID) {
		final Slot slot = this.slots.get(slotID);
		
		if (slot != null && slot.hasItem()) {
			final ItemStack slotItem = slot.getItem();
			final ItemStack slotItemCopy = slotItem.copy();
			
			if (slotID < 27 && !this.moveItemStackTo(slotItem, 27, this.slots.size(), true)) {
				return ItemStack.EMPTY;
			} else if (!this.moveItemStackTo(slotItem, 0, 27, false)) {
				return ItemStack.EMPTY;
			}
			
			if (slotItem.isEmpty()) {
				slot.set(ItemStack.EMPTY);
			} else {
				slot.setChanged();
			}
			
			return slotItemCopy;
		}
		
		return ItemStack.EMPTY;
	}
	
	public StoreTileEntity getTileEntity() {
		return this.tileEntity;
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
