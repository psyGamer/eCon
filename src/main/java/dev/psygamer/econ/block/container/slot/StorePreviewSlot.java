package dev.psygamer.econ.block.container.slot;

import net.minecraft.item.ItemStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Slot;

public class StorePreviewSlot extends Slot {
	
	public StorePreviewSlot(final IInventory inventory, final int slotId, final int xPosition, final int yPosition) {
		super(inventory, slotId, xPosition, yPosition);
	}
	
	@Override
	public boolean mayPlace(final ItemStack itemStack) {
		return false;
	}
	
	@Override
	public boolean mayPickup(final PlayerEntity player) {
		return false;
	}
}
