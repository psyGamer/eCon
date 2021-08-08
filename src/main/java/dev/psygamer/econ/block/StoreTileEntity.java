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
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;

import net.minecraft.util.NonNullList;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;


import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.UUID;

public class StoreTileEntity extends TileEntity implements IItemHandlerModifiable, ITickableTileEntity, INamedContainerProvider {
	
	public static final int SLOTS = 37;
	
	private float itemRotation = 0f;
	private float prevItemRotation = 0f;
	
	private String name;
	private UUID owner;
	private int price;
	
	private final NonNullList<ItemStack> items = NonNullList.withSize(SLOTS, ItemStack.EMPTY);
	
	public StoreTileEntity() {
		super(TileEntityTypeRegistry.STORE_BLOCK_TYPE.get());
	}
	
	@Override
	public void tick() {
		this.prevItemRotation = this.itemRotation;
		this.itemRotation = (this.itemRotation + 0.03f) % 360;
	}
	
	@Override
	public void load(final BlockState state, final CompoundNBT compound) {
		super.load(state, compound);
		
		setName(compound.getString("name"));
		setOwner(compound.getUUID("owner"));
		setPrice(compound.getInt("price"));
		
		ItemStackHelper.loadAllItems(compound, this.items);
	}
	
	@Override
	public CompoundNBT save(final CompoundNBT compound) {
		super.save(compound);
		
		compound.putString("name", getName());
		compound.putUUID("owner", getOwner());
		compound.putInt("price", getPrice());
		return ItemStackHelper.saveAllItems(compound, this.items);
	}
	
	@Override
	public ITextComponent getDisplayName() {
		return new TranslationTextComponent("container." + ECon.MODID + ".store");
	}
	
	@Override
	public Container createMenu(final int windowID, final PlayerInventory playerInventory, final PlayerEntity playerEntity) {
		if (playerEntity.getUUID().equals(this.owner))
			return new StoreContainer(windowID, playerInventory, this);
		
		return new StoreCustomerContainer(windowID, playerInventory, this);
	}
	
	@Override
	public CompoundNBT getUpdateTag() {
		return save(new CompoundNBT());
	}
	
	@Override
	public void handleUpdateTag(final BlockState state, final CompoundNBT tag) {
		load(state, tag);
	}
	
	@Nullable
	@Override
	public SUpdateTileEntityPacket getUpdatePacket() {
		return new SUpdateTileEntityPacket(getBlockPos(), -1, save(new CompoundNBT()));
	}
	
	@Override
	public void onDataPacket(final NetworkManager net, final SUpdateTileEntityPacket pkt) {
		load(null, pkt.getTag());
	}
	
	public NonNullList<ItemStack> getItems() {
		return this.items;
	}
	
	@Override
	public int getSlots() {
		return SLOTS;
	}
	
	@Override
	public void setStackInSlot(final int slot, @Nonnull final ItemStack stack) {
		this.items.set(slot, stack);
	}
	
	@Nonnull
	@Override
	public ItemStack getStackInSlot(final int slot) {
		return this.items.get(slot);
	}
	
	@Nonnull
	@Override
	public ItemStack insertItem(final int slot, @Nonnull final ItemStack stack, final boolean simulate) {
		this.items.set(slot, stack);
		
		if (stack.getCount() > stack.getMaxStackSize()) {
			stack.setCount(stack.getMaxStackSize());
		}
		
		this.setChanged();
		
		return ItemStack.EMPTY;
	}
	
	@Nonnull
	@Override
	public ItemStack extractItem(final int slot, final int amount, final boolean simulate) {
		final ItemStack itemCopy = ItemStackHelper.removeItem(this.items, slot, amount);
		
		if (!itemCopy.isEmpty()) {
			this.setChanged();
		}
		
		return itemCopy;
	}
	
	@Override
	public int getSlotLimit(final int slot) {
		return getStackInSlot(slot).getMaxStackSize();
	}
	
	@Override
	public boolean isItemValid(final int slot, @Nonnull final ItemStack stack) {
		return true;
	}
	
	public ItemStack getOfferedItem() {
		return this.items.get(0);
	}
	
	public void setOfferedItem(final ItemStack offeredItem) {
		this.items.set(0, offeredItem);
		setChanged();
	}
	
	public String getName() {
		return this.name == null ? "" : this.name;
	}
	
	public void setName(final String name) {
		this.name = name;
	}
	
	public int getPrice() {
		return this.price;
	}
	
	public void setPrice(final int price) {
		this.price = price;
	}
	
	public UUID getOwner() {
		return this.owner;
	}
	
	public void setOwner(final UUID owner) {
		this.owner = owner;
	}
	
	public float getItemRotation() {
		return this.itemRotation;
	}
	
	public float getPrevItemRotation() {
		return this.prevItemRotation;
	}
}
