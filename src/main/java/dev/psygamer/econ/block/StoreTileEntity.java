package dev.psygamer.econ.block;

import com.google.common.collect.Lists;
import dev.psygamer.econ.ECon;
import dev.psygamer.econ.setup.TileEntityTypeRegistry;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;

import net.minecraft.util.NonNullList;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;

public class StoreTileEntity extends TileEntity implements IInventory, ITickableTileEntity, INamedContainerProvider {
	
	public static final int SLOTS = 1;
	
	private float itemRotation = 0f;
	private float prevItemRotation = 0f;
	
	private final IItemHandler previewHandler = new ItemStackHandler(SLOTS);
	private final NonNullList<ItemStack> offeredItem = NonNullList.withSize(1, ItemStack.EMPTY);
	
	public NonNullList<ItemStack> getOfferedItem() {
		return this.offeredItem;
	}
	
	public IItemHandler getPreviewHandler() {
		return this.previewHandler;
	}
	
	public StoreTileEntity() {
		super(TileEntityTypeRegistry.STORE_BLOCK_TYPE.get());
	}
	
	public float getItemRotation() {
		return this.itemRotation;
	}
	
	public float getPrevItemRotation() {
		return this.prevItemRotation;
	}
	
	@Override
	public void tick() {
		this.prevItemRotation = this.itemRotation;
		this.itemRotation = (this.itemRotation + 0.03f) % 360;
	}
	
	@Override
	public int getContainerSize() {
		return SLOTS;
	}
	
	@Override
	public boolean isEmpty() {
		return this.offeredItem.isEmpty();
	}
	
	public ItemStack getItem() {
		return getItem(0);
	}
	
	@Override
	public ItemStack getItem(final int slot) {
		return this.offeredItem.get(slot);
	}
	
	@Override
	public ItemStack removeItem(final int slot, final int amount) {
		final ItemStack itemCopy = ItemStackHelper.removeItem(this.offeredItem, slot, amount);
		
		if (!itemCopy.isEmpty()) {
			this.setChanged();
		}
		
		return itemCopy;
	}
	
	@Override
	public ItemStack removeItemNoUpdate(final int slot) {
		return ItemStackHelper.takeItem(Lists.newArrayList(this.offeredItem), slot);
	}
	
	@Override
	public void setItem(final int slot, final ItemStack itemStack) {
		this.offeredItem.set(slot, itemStack);
		if (itemStack.getCount() > this.getMaxStackSize()) {
			itemStack.setCount(this.getMaxStackSize());
		}
		
		this.setChanged();
	}
	
	@Override
	public boolean stillValid(final PlayerEntity player) {
		if (player.level.getBlockEntity(this.worldPosition) != this) {
			return false;
		}
		
		return !(player.distanceToSqr(
				(double) this.worldPosition.getX() + 0.5D,
				(double) this.worldPosition.getY() + 0.5D,
				(double) this.worldPosition.getZ() + 0.5D
		) > 64.0D);

//		return player.distanceToSqr(
//				(double) this.worldPosition.getX() + 0.5D,
//				(double) this.worldPosition.getY() + 0.5D,
//				(double) this.worldPosition.getZ() + 0.5D
//		) < 64.0D;
	}
	
	@Override
	public void clearContent() {
		this.offeredItem.clear();
	}
	
	@Override
	public void load(final BlockState state, final CompoundNBT compound) {
		super.load(state, compound);
		
		ItemStackHelper.loadAllItems(compound, this.offeredItem);
	}
	
	@Override
	public CompoundNBT save(final CompoundNBT compound) {
		super.save(compound);
		
		return ItemStackHelper.saveAllItems(compound, this.offeredItem);
	}
	
	@Override
	public ITextComponent getDisplayName() {
		return new TranslationTextComponent("container." + ECon.MODID + ".store");
	}
	
	@Override
	public Container createMenu(final int windowID, final PlayerInventory playerInventory, final PlayerEntity playerEntity) {
		return new StoreContainer(windowID, playerInventory, this);
	}
	
	@Override
	public CompoundNBT getUpdateTag() {
		return save(new CompoundNBT());
	}
	
	@Override
	public void handleUpdateTag(final BlockState state, final CompoundNBT tag) {
		load(state, tag);
	}
}
