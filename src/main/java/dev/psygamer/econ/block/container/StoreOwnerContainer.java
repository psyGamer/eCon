package dev.psygamer.econ.block.container;

import dev.psygamer.econ.block.StoreBlock;
import dev.psygamer.econ.block.container.slot.StoreFakeSlot;
import dev.psygamer.econ.client.screen.store.StoreOwnerScreen;
import dev.psygamer.econ.block.tileentity.StoreTileEntity;
import dev.psygamer.econ.setup.ContainerRegistry;

import net.minecraft.util.IWorldPosCallable;
import net.minecraft.item.ItemStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.network.PacketBuffer;
import net.minecraft.inventory.container.ClickType;
import net.minecraft.inventory.container.Slot;
import net.minecraft.inventory.container.Container;
import net.minecraft.tileentity.TileEntity;

import java.util.Objects;

public class StoreOwnerContainer extends Container {
	
	private StoreOwnerScreen ownerScreen;
	
	private final PlayerInventory playerInventory;
	private final StoreTileEntity tileEntity;
	private final IWorldPosCallable canInteractWithCallable;
	
	private final StoreFakeSlot fakeSlot;
	
	public StoreOwnerContainer(final int windowId, final PlayerInventory playerInventory, final PacketBuffer data) {
		this(windowId, playerInventory, getTileEntity(playerInventory, data));
	}
	
	public StoreOwnerContainer(final int windowId, final PlayerInventory playerInventory, final StoreTileEntity tileEntity) {
		super(ContainerRegistry.STORE_BLOCK_CONTAINER.get(), windowId);
		
		this.playerInventory = playerInventory;
		this.tileEntity = tileEntity;
		this.canInteractWithCallable = IWorldPosCallable.create(tileEntity.getLevel(), tileEntity.getBlockPos());
		this.fakeSlot = new StoreFakeSlot(this, tileEntity, 0, 118, 69);
		
		this.fakeSlot.set(tileEntity.getOfferedItem());
		
		for (int col = 0 ; col < 9 ; col++) {
			for (int row = 0 ; row < 3 ; row++) {
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
		
		this.addSlot(this.fakeSlot);
	}
	
	public void setChanged() {
		this.tileEntity.setChanged();
		this.tileEntity.setOfferedItem(this.fakeSlot.getItem());
	}
	
	@Override
	public ItemStack clicked(final int slotId, final int dragType, final ClickType clickType, final PlayerEntity player) {
		final Slot slot = slotId >= 0 ? this.slots.get(slotId) : null;
		
		if (!(slot instanceof StoreFakeSlot)) {
			return super.clicked(slotId, dragType, clickType, player);
		}
		
		// Disable hot keying to avoid possible problems
		if (dragType == -1 && clickType == ClickType.SWAP) {
			return ItemStack.EMPTY;
		}
		
		if (clickType == ClickType.QUICK_MOVE) {
			slot.set(ItemStack.EMPTY);
		} else if (!player.inventory.getCarried().isEmpty()) {
			if (player.inventory.getCarried().getItem() == slot.getItem().getItem() && (dragType == 1 || (dragType == 5 && clickType == ClickType.QUICK_CRAFT))) { // If same item & right click
				((StoreFakeSlot) slot).increase(1);
			} else {
				slot.set(player.inventory.getCarried().copy());
			}
		} else if (player.inventory.getCarried().isEmpty()) {
			slot.set(ItemStack.EMPTY);
		} else if (slot.mayPlace(player.inventory.getCarried())) {
			slot.set(player.inventory.getCarried().copy());
		}
		
		if (this.ownerScreen instanceof StoreOwnerScreen)
			this.ownerScreen.onItemUpdate(slot.getItem());
		
		return player.inventory.getCarried();
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
	public ItemStack quickMoveStack(final PlayerEntity playerEntity, final int index) {
		final Slot slot = this.slots.get(index);
		
		return ItemStack.EMPTY;
	}
	
	public PlayerInventory getPlayerInventory() {
		return this.playerInventory;
	}
	
	public StoreOwnerScreen getOwnerScreen() {
		return this.ownerScreen;
	}
	
	public void setOwnerScreen(final StoreOwnerScreen ownerScreen) {
		this.ownerScreen = ownerScreen;
	}
	
	public StoreTileEntity getTileEntity() {
		return this.tileEntity;
	}
	
	public StoreFakeSlot getFakeSlot() {
		return this.fakeSlot;
	}
}
