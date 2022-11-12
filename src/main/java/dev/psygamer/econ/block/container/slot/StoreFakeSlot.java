package dev.psygamer.econ.block.container.slot;

import dev.psygamer.econ.block.container.StoreOwnerContainer;

import net.minecraft.item.ItemStack;
import net.minecraft.entity.player.PlayerEntity;

import net.minecraft.util.math.MathHelper;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

public class StoreFakeSlot extends SlotItemHandler {
	
	private final StoreOwnerContainer parentContainer;
	
	public StoreFakeSlot(final StoreOwnerContainer parentContainer, final IItemHandler itemHandler, final int slotIndex, final int xPosition, final int yPosition) {
		super(itemHandler, slotIndex, xPosition, yPosition);
		
		this.parentContainer = parentContainer;
	}
	
	@Override
	public void set(ItemStack stack) {
		if (!stack.isEmpty()) {
			stack = stack.copy();
		}
		
		stack.setCount(
				MathHelper.clamp(stack.getCount(), 1, stack.getMaxStackSize())
		);
		
		super.set(stack);
	}
	
	public void increase(final int amount) {
		final ItemStack stack = getItem().copy();
		
		stack.setCount(stack.getCount() + amount);
		
		set(stack);
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
