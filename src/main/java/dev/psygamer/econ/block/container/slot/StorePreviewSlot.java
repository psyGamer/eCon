package dev.psygamer.econ.block.container.slot;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;

public class StorePreviewSlot extends Slot {
	
	public StorePreviewSlot(final IInventory p_i1824_1_, final int p_i1824_2_, final int p_i1824_3_, final int p_i1824_4_) {
		super(p_i1824_1_, p_i1824_2_, p_i1824_3_, p_i1824_4_);
	}
	
	@Override
	public boolean mayPlace(final ItemStack p_75214_1_) {
		return false;
	}
	
	@Override
	public boolean mayPickup(final PlayerEntity p_82869_1_) {
		return false;
	}
}
