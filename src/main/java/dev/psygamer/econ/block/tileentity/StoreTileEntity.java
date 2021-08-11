package dev.psygamer.econ.block.tileentity;

import dev.psygamer.econ.ECon;
import dev.psygamer.econ.setup.TileEntityTypeRegistry;
import dev.psygamer.econ.block.container.StoreOwnerContainer;
import dev.psygamer.econ.block.container.StoreCustomerContainer;

import net.minecraft.util.NonNullList;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.item.ItemStack;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;

import net.minecraftforge.common.util.Constants;
import net.minecraftforge.items.IItemHandlerModifiable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.UUID;

public class StoreTileEntity extends TileEntity implements IItemHandlerModifiable, ITickableTileEntity, INamedContainerProvider {
	
	public static final int SLOTS = 37;
	
	private float itemRotation = 0f;
	private float prevItemRotation = 0f;
	
	private int stockLeft;
	
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
		
		if (compound.contains("owner"))
			setOwner(compound.getUUID("owner"));
		
		setName(compound.getString("name"));
		setPrice(compound.getInt("price"));
		
		this.stockLeft = this.getItems().stream()
				.mapToInt(itemStack -> itemStack.getItem() == getOfferedItem().getItem() && ItemStack.tagMatches(itemStack, getOfferedItem()) ? itemStack.getCount() : 0)
				.reduce(Integer::sum)
				.orElse(0) - getOfferedItem().getCount();
		
		ItemStackHelper.loadAllItems(compound, this.items);
	}
	
	@Override
	public CompoundNBT save(final CompoundNBT compound) {
		super.save(compound);
		
		if (getOwner() != null)
			compound.putUUID("owner", getOwner());
		
		compound.putString("name", getName());
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
			return new StoreOwnerContainer(windowID, playerInventory, this);
		
		return new StoreCustomerContainer(windowID, playerInventory, this);
	}
	
	@Override
	public CompoundNBT getUpdateTag() {
		final CompoundNBT saveData = save(new CompoundNBT());
		
		saveData.putInt("stockLeft", this.stockLeft);
		
		return saveData;
	}
	
	@Override
	public void handleUpdateTag(final BlockState state, final CompoundNBT tag) {
		load(state, tag);
		
		this.stockLeft = tag.getInt("stockLeft");
		
		final Container openMenu = Minecraft.getInstance().player.containerMenu;
		
		if (openMenu instanceof StoreCustomerContainer)
			((StoreCustomerContainer) openMenu).getPreviewSlot().set(this.getOfferedItem());
	}
	
	@Nullable
	@Override
	public SUpdateTileEntityPacket getUpdatePacket() {
		return new SUpdateTileEntityPacket(getBlockPos(), -1, getUpdateTag());
	}
	
	@Override
	public void onDataPacket(final NetworkManager net, final SUpdateTileEntityPacket pkt) {
		handleUpdateTag(null, pkt.getTag());
	}
	
	@Override
	public void setChanged() {
		recalculateLeftStock();
		
		if (!getLevel().isClientSide())
			this.getLevel().sendBlockUpdated(
					this.getBlockPos(),
					getLevel().getBlockState(getBlockPos()),
					getLevel().getBlockState(getBlockPos()),
					
					Constants.BlockFlags.BLOCK_UPDATE
			);
		
		super.setChanged();
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
		final ItemStack itemStack = stack.copy();
		final ItemStack remainingItemStack = stack.copy();
		
		final int remainingStackSize = itemStack.getCount() - itemStack.getMaxStackSize();
		
		if (itemStack.getCount() > itemStack.getMaxStackSize()) {
			itemStack.setCount(itemStack.getMaxStackSize());
		}
		
		if (!simulate) {
			this.items.set(slot, itemStack);
			this.setChanged();
		}
		
		if (remainingStackSize <= 0)
			return ItemStack.EMPTY;
		
		remainingItemStack.setCount(remainingStackSize);
		
		return remainingItemStack;
	}
	
	@Nonnull
	@Override
	public ItemStack extractItem(final int slot, final int amount, final boolean simulate) {
		final ItemStack itemStack = getStackInSlot(slot).copy();
		final ItemStack extractedItemStack = getStackInSlot(slot).copy();
		
		final int extractedStackSize = Math.min(itemStack.getCount(), amount);
		
		itemStack.setCount(itemStack.getCount() - extractedStackSize);
		
		if (!simulate) {
			this.items.set(slot, itemStack);
			this.setChanged();
		}
		
		if (extractedStackSize <= 0)
			return ItemStack.EMPTY;
		
		extractedItemStack.setCount(extractedStackSize);
		
		return extractedItemStack;
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
		return Math.max(this.price, 0);
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
	
	public void recalculateLeftStock() {
		this.stockLeft = this.getItems().stream()
				.mapToInt(itemStack -> itemStack.getItem() == getOfferedItem().getItem() && ItemStack.tagMatches(itemStack, getOfferedItem()) ? itemStack.getCount() : 0)
				.reduce(Integer::sum)
				.orElse(0) - getOfferedItem().getCount();
	}
	
	public void setLeftStock(final int leftStock) {
		this.stockLeft = leftStock;
	}
	
	public int getLeftStock() {
		return this.stockLeft;
	}
}
