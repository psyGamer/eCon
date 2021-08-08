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
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nullable;
import java.util.UUID;

public class StoreTileEntity extends TileEntity implements IInventory, ITickableTileEntity, INamedContainerProvider {
	
	public static final int SLOTS = 37;
	
	private float itemRotation = 0f;
	private float prevItemRotation = 0f;
	
	private String name;
	private UUID owner;
	private int price;
	
	private final IItemHandler offeredItemHandler = new ItemStackHandler(1);
	private final NonNullList<ItemStack> items = NonNullList.withSize(SLOTS, ItemStack.EMPTY);
	
	public IItemHandler getOfferedItemHandler() {
		return this.offeredItemHandler;
	}
	
	public StoreTileEntity() {
		super(TileEntityTypeRegistry.STORE_BLOCK_TYPE.get());
	}
	
	@Override
	public void tick() {
		this.prevItemRotation = this.itemRotation;
		this.itemRotation = (this.itemRotation + 0.03f) % 360;
	}
	
	@Override
	public ItemStack removeItem(final int slot, final int amount) {
		final ItemStack itemCopy = ItemStackHelper.removeItem(this.items, slot, amount);
		
		if (!itemCopy.isEmpty()) {
			this.setChanged();
		}
		
		return itemCopy;
	}
	
	@Override
	public ItemStack removeItemNoUpdate(final int slot) {
		return ItemStackHelper.takeItem(Lists.newArrayList(this.items), slot);
	}
	
	@Override
	public void setItem(final int slot, final ItemStack itemStack) {
		this.items.set(slot, itemStack);
		
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
		this.items.clear();
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
	
	@Override
	public int getContainerSize() {
		return SLOTS;
	}
	
	@Override
	public boolean isEmpty() {
		return this.items.isEmpty();
	}
	
	@Override
	public ItemStack getItem(final int slot) {
		return this.items.get(slot);
	}
	
	public NonNullList<ItemStack> getItems() {
		return this.items;
	}
	
	public ItemStack getOfferedItem() {
		return getItem(0);
	}
	
	public void setOfferedItem(final ItemStack offeredItem) {
		setItem(0, offeredItem);
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
