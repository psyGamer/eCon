package dev.psygamer.econ.network.server;

import com.mojang.datafixers.util.Pair;
import dev.psygamer.econ.banking.BankAccountHandler;
import dev.psygamer.econ.banking.Transaction;
import dev.psygamer.econ.banking.TransactionHandler;
import dev.psygamer.econ.block.StoreTileEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fml.network.NetworkEvent;

import java.time.Instant;
import java.util.function.Supplier;

public class StoreTransactionMessage {
	
	private final int quantity;
	private final BlockPos storePos;
	
	public StoreTransactionMessage(final BlockPos storePos, final int quantity) {
		this.quantity = quantity;
		this.storePos = storePos;
	}
	
	public StoreTransactionMessage(final PacketBuffer buffer) {
		this(buffer.readBlockPos(), buffer.readInt());
	}
	
	public void encode(final PacketBuffer buffer) {
		buffer.writeBlockPos(this.storePos);
		buffer.writeInt(this.quantity);
	}
	
	public void handle(final Supplier<NetworkEvent.Context> ctx) {
		ctx.get().enqueueWork(() -> {
			final ServerPlayerEntity serverPlayer = ctx.get().getSender();
			final ServerWorld serverWorld = serverPlayer.getLevel();
			
			if (!serverWorld.isLoaded(this.storePos) ||
					serverPlayer.distanceToSqr(
							this.storePos.getX(),
							this.storePos.getY(),
							this.storePos.getZ()
					) > 9 * 9
			) return;
			
			final TileEntity tileEntity = serverWorld.getBlockEntity(this.storePos);
			
			if (tileEntity instanceof StoreTileEntity) {
				final StoreTileEntity storeTileEntity = (StoreTileEntity) tileEntity;
				final int price = storeTileEntity.getPrice() * this.quantity;
				
				if (BankAccountHandler.getBankAccount(serverPlayer.getUUID()) == null ||
						BankAccountHandler.getBalance(serverPlayer.getUUID()) < price || price < 0
				) return;
				
				final ItemStack offeredItem = storeTileEntity.getOfferedItem();
				
				final int totalItemQuantity = storeTileEntity.getItems().stream()
						.mapToInt(itemStack -> areItemsSame(offeredItem, itemStack) ? itemStack.getCount() : 0)
						.reduce(Integer::sum)
						.orElse(0);
				
				if (this.quantity * offeredItem.getCount() > totalItemQuantity) return;
				
				final Transaction storeTransaction = new Transaction(serverPlayer.getUUID(), storeTileEntity.getOwner(), price, Instant.now().getEpochSecond());
				
				TransactionHandler.processTransaction(storeTransaction);
				
				giveItems(serverPlayer, offeredItem, this.quantity * offeredItem.getCount());
			}
		});
		ctx.get().setPacketHandled(true);
	}
	
	private static void giveItems(final ServerPlayerEntity serverPlayer, final ItemStack offeredItem, final int quantity) {
		int itemsLeft = quantity;
		
		while (itemsLeft > 0) {
			final int stackSize = Math.min(itemsLeft, offeredItem.getMaxStackSize());
			final ItemStack itemStack = offeredItem.copy();
			
			itemsLeft -= stackSize;
			itemStack.setCount(stackSize);
			
			final boolean wasAdditionSuccessful = serverPlayer.inventory.add(itemStack);
			
			if (wasAdditionSuccessful && itemStack.isEmpty()) continue;
			
			final ItemEntity itemEntity = serverPlayer.drop(itemStack, false);
			
			if (itemEntity != null) {
				itemEntity.setNoPickUpDelay();
				itemEntity.setOwner(serverPlayer.getUUID());
			}
		}
	}
	
	private static boolean areItemsSame(final ItemStack stackA, final ItemStack stackB) {
		final CompoundNBT nbtA = stackA.getTag();
		final CompoundNBT nbtB = stackB.getTag();
		
		return stackA.getItem() == stackB.getItem() && ((nbtA == null && nbtB == null) || nbtA.equals(nbtB));
	}
}