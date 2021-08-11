package dev.psygamer.econ.block.container.slot;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;

public class StorePreviewItemSlot extends Slot {
	
	private boolean hasInitialized = false;
	
	public StorePreviewItemSlot(final IInventory inventory, final int index, final int xPosition, final int yPosition) {
		super(inventory, index, xPosition, yPosition);
	}
	
	@Override
	public void set(@Nonnull final ItemStack stack) {
		if (!this.hasInitialized || stack.getItem() != getItem().getItem()) {
			this.container.setItem(this.getSlotIndex(), stack);
			this.setChanged();
			
			this.hasInitialized = true;
		} else {
			getItem().setCount(
					Math.max(getItem().getMaxStackSize(), getItem().getCount() + stack.getCount())
			);
			
			this.setChanged();
		}
	}
	
	@Override
	public ItemStack onTake(final PlayerEntity playerEntity, final ItemStack itemStack) {
//		return super.onTake(playerEntity, itemStack);
//		set(itemStack);
//
//		return itemStack;
		return ItemStack.EMPTY;
	}
	
	@Override
	public boolean mayPickup(final PlayerEntity playerIn) {
		return true;
	}
	
	@Override
	public boolean mayPlace(@Nonnull final ItemStack stack) {
		return true;
	}
}
