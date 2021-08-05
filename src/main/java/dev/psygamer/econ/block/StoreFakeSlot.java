package dev.psygamer.econ.block;

import net.minecraft.item.ItemStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.container.Slot;

import net.minecraft.util.math.MathHelper;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;


public class StoreFakeSlot extends Slot {
	
	private static final IInventory EMPTY_INVENTORY = new Inventory(0);
	
	private final int slotIndex;
	private final IItemHandler itemHandler;
	private final StoreContainer container;
	
	public StoreFakeSlot(final StoreContainer container, final IItemHandler itemHandler, final int slotIndex, final int xPosition, final int yPosition) {
		super(EMPTY_INVENTORY, slotIndex, xPosition, yPosition);
		
		this.itemHandler = itemHandler;
		this.slotIndex = slotIndex;
		this.container = container;
	}
	
	@Override
	public void set(ItemStack stack) {
		if (!stack.isEmpty()) {
			stack = stack.copy();
		}
		
		setStackInSlot(stack);
		setChanged();
	}
	
	@Override
	public void setChanged() {
		super.setChanged();
		this.container.setChanged();
	}
	
	@Override
	public ItemStack onTake(final PlayerEntity player, final ItemStack stack) {
		return stack;
	}
	
	public void increase(final int amount) {
		getItem().setCount(
				MathHelper.clamp(getItem().getCount() + amount, 1, getItem().getMaxStackSize())
		);
		
		setChanged();
	}
	
	@Override
	public ItemStack remove(final int amount) {
		return ItemStack.EMPTY;
	}
	
	@Override
	public boolean mayPlace(final ItemStack stack) {
		return true;
	}
	
	@Override
	public boolean mayPickup(final PlayerEntity player) {
		return true;
	}
	
	@Override
	public boolean isSameInventory(final Slot other) {
		return other instanceof StoreFakeSlot && ((StoreFakeSlot) other).itemHandler == this.itemHandler;
	}
	
	@Override
	public ItemStack getItem() {
		if (this.itemHandler.getSlots() <= this.getSlotIndex()) {
			return ItemStack.EMPTY;
		}
		
		return this.itemHandler.getStackInSlot(this.slotIndex);
	}
	
	public IItemHandler getItemHandler() {
		return this.itemHandler;
	}
	
	public void clearStack() {
		setStackInSlot(ItemStack.EMPTY);
		
		setChanged();
	}
	
	private void setStackInSlot(final ItemStack stack) {
		if (this.itemHandler instanceof IItemHandlerModifiable) {
			((IItemHandlerModifiable) this.itemHandler).setStackInSlot(this.slotIndex, stack);
		} else {
			this.itemHandler.extractItem(this.slotIndex, Integer.MAX_VALUE, false);
			this.itemHandler.insertItem(this.slotIndex, stack, false);
		}
	}
}
