package dev.psygamer.econ.block.container.slot;

import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

public class StoreStorageSlot extends SlotItemHandler {
	
	public StoreStorageSlot(final IItemHandler itemHandler, final int slotId, final int xPosition, final int yPosition) {
		super(itemHandler, slotId, xPosition, yPosition);
	}
}
