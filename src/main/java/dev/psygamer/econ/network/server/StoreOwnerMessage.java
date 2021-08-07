package dev.psygamer.econ.network.server;

import dev.psygamer.econ.block.StoreTileEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fml.network.NetworkEvent;

import java.nio.charset.Charset;
import java.util.function.Supplier;

public class StoreOwnerMessage {
	
	private final int itemPrice;
	private final String itemName;
	private final ItemStack offeredItem;
	
	private final BlockPos tileEntityPos;
	
	public StoreOwnerMessage(final StoreTileEntity tileEntity) {
		this.itemPrice = tileEntity.getPrice();
		this.itemName = tileEntity.getName();
		this.offeredItem = tileEntity.getItem().copy();
		
		this.tileEntityPos = tileEntity.getBlockPos();
	}
	
	public StoreOwnerMessage(final PacketBuffer buffer) {
		this(
				buffer.readInt(),
				(String) buffer.readCharSequence(buffer.readInt(), Charset.defaultCharset()),
				buffer.readItem(),
				buffer.readBlockPos()
		);
	}
	
	public StoreOwnerMessage(final int itemPrice, final String itemName, final ItemStack offeredItem, final BlockPos tileEntityPos) {
		this.itemPrice = itemPrice;
		this.itemName = itemName;
		this.offeredItem = offeredItem;
		
		this.tileEntityPos = tileEntityPos;
	}
	
	public void encode(final PacketBuffer buffer) {
		buffer.writeInt(this.itemPrice);
		buffer.writeInt(this.itemName.length());
		buffer.writeCharSequence(this.itemName, Charset.defaultCharset());
		buffer.writeItem(this.offeredItem);
		buffer.writeBlockPos(this.tileEntityPos);
	}
	
	public void handle(final Supplier<NetworkEvent.Context> context) {
		final ServerPlayerEntity serverPlayer = context.get().getSender();
		final ServerWorld serverWorld = serverPlayer.getLevel();
		
		if (!serverWorld.isLoaded(this.tileEntityPos) ||
				serverPlayer.distanceToSqr(
						this.tileEntityPos.getX(),
						this.tileEntityPos.getY(),
						this.tileEntityPos.getZ()
				) > 9 * 9) return;
		
		final TileEntity tileEntity = serverPlayer.getLevel().getBlockEntity(this.tileEntityPos);
		
		if (tileEntity instanceof StoreTileEntity) {
			final StoreTileEntity storeTileEntity = (StoreTileEntity) tileEntity;
			
			if (!serverPlayer.getUUID().equals(storeTileEntity.getOwner())) return;
			
			storeTileEntity.setName(this.itemName);
			storeTileEntity.setPrice(this.itemPrice);
			storeTileEntity.setItem(0, this.offeredItem);
			
			storeTileEntity.setChanged();
		}
	}
}
