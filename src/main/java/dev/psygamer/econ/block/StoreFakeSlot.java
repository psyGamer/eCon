package dev.psygamer.econ.block;

import net.minecraft.item.ItemStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.container.Slot;

import net.minecraft.util.math.MathHelper;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;


public class StoreFakeSlot extends SlotItemHandler {
	
	private final StoreContainer parentContainer;
	
	public StoreFakeSlot(final StoreContainer parentContainer, final IItemHandler itemHandler, final int slotIndex, final int xPosition, final int yPosition) {
		super(itemHandler, slotIndex, xPosition, yPosition);
		
		this.parentContainer = parentContainer;
	}
	
	@Override
	public void set(ItemStack stack) {
		if (!stack.isEmpty()) {
			stack = stack.copy();
		}
		
		super.set(stack);
	}
	
	@Override
	public void setChanged() {
		super.setChanged();
		
		this.parentContainer.setChanged();
	}
	
	@Override
	public boolean mayPlace(final ItemStack stack) {
		return super.mayPlace(stack);
	}
	
	@Override
	public boolean mayPickup(final PlayerEntity playerEntity) {
		return super.mayPickup(playerEntity);
	}
}
