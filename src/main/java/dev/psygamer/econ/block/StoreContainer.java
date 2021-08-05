package dev.psygamer.econ.block;

import dev.psygamer.econ.setup.BlockRegistry;
import dev.psygamer.econ.setup.ContainerRegistry;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.ClickType;
import net.minecraft.inventory.container.IContainerListener;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.PacketBuffer;
import net.minecraft.inventory.container.Container;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IWorldPosCallable;

import java.util.Objects;

public class StoreContainer extends Container {
	
	private final StoreTileEntity tileEntity;
	private final IWorldPosCallable canInteractWithCallable;
	
	private final StoreFakeSlot fakeSlot;
	
	public StoreContainer(final int windowId, final PlayerInventory playerInventory, final StoreTileEntity tileEntity) {
		super(ContainerRegistry.STORE_BLOCK_CONTAINER.get(), windowId);
		
		this.tileEntity = tileEntity;
		this.canInteractWithCallable = IWorldPosCallable.create(tileEntity.getLevel(), tileEntity.getBlockPos());
		this.fakeSlot = new StoreFakeSlot(this, tileEntity.getPreviewHandler(), 0, 80, 35);
		
		this.fakeSlot.set(tileEntity.getOfferedItem().get(0));
		
		for (int row = 0 ; row < 3 ; row++) {
			for (int col = 0 ; col < 9 ; col++) {
				this.addSlot(new Slot(playerInventory,
						col + row * 9 + 9,
						col * 18 + 8,
						166 - (4 - row) * 18 - 10
				));
			}
		}
		
		for (int col = 0 ; col < 9 ; col++) {
			this.addSlot(new Slot(playerInventory,
					col,
					col * 18 + 8,
					142
			));
		}
		
		this.addSlot(this.fakeSlot);
	}
	
	public void setChanged() {
		this.tileEntity.setChanged();
		this.tileEntity.getOfferedItem().set(0, this.fakeSlot.getItem());
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
		
		return player.inventory.getCarried();
	}
	
	//	@Override
//	public ItemStack clicked(final int slotClicked, final int mouseButton, final ClickType type, final PlayerEntity playerEntity) {
//		if (slotClicked >= 0 && slotClicked < this.slots.size() &&
//				this.slots.get(slotClicked) instanceof StoreFakeSlot
//		) {
//			final StoreFakeSlot slot = (StoreFakeSlot) this.slots.get(slotClicked);
//			final ItemStack carried = playerEntity.inventory.getCarried();
//
//			/*
//				0 - Left click
//				1 - Right click
//				2 - Mouse wheel click
//			 */
//
//			if (mouseButton == 2) {
//				final ItemStack slotCopy = slot.getItem().copy();
//
//				slotCopy.setCount(slotCopy.getMaxStackSize());
//
//				return slotCopy;
//			}
//
//			if (carried.getItem() == slot.getItem().getItem()) {
//				slot.increase(
//						mouseButton == 1 ? 1 : carried.getCount()
//				);
//			} else {
//				slot.set(carried);
//
//				if (mouseButton == 1) {
//					slot.getItem().setCount(1);
//				}
//			}
//
//			return ItemStack.EMPTY;
//		}
//
//		return super.clicked(slotClicked, mouseButton, type, playerEntity);
//	}
	
	public StoreContainer(final int windowId, final PlayerInventory playerInventory, final PacketBuffer data) {
		this(windowId, playerInventory, getTileEntity(playerInventory, data));
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
		
		if (slot != null && slot.hasItem()) {
			final ItemStack itemStack = slot.getItem();
			
			if ((index < StoreTileEntity.SLOTS && !this.moveItemStackTo(itemStack, StoreTileEntity.SLOTS, this.slots.size(), true)) ||
					!this.moveItemStackTo(itemStack, 0, StoreTileEntity.SLOTS, false)
			) {
				return ItemStack.EMPTY;
			}
			
			if (itemStack.isEmpty()) {
				slot.set(ItemStack.EMPTY);
			} else {
				slot.setChanged();
			}
		}
		
		return ItemStack.EMPTY;
	}
}
