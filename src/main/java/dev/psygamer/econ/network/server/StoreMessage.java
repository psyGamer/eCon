package dev.psygamer.econ.network.server;

import dev.psygamer.econ.banking.BankAccountHandler;
import dev.psygamer.econ.banking.Transaction;
import dev.psygamer.econ.banking.TransactionHandler;
import dev.psygamer.econ.block.StoreTileEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkEvent;

import java.time.Instant;
import java.util.UUID;
import java.util.function.Supplier;

public class StoreMessage {
	
	private final BlockPos storePos;
	
	public StoreMessage(final BlockPos storePos) {
		this.storePos = storePos;
	}
	
	public void encode(final PacketBuffer buffer) {
		buffer.writeBlockPos(this.storePos);
	}
	
	public static StoreMessage decode(final PacketBuffer buffer) {
		return new StoreMessage(buffer.readBlockPos());
	}
	
	public void handle(final Supplier<NetworkEvent.Context> context) {
		final ServerPlayerEntity sendingPlayer = context.get().getSender();
		final World storeWorld = context.get().getSender().getLevel();
		
		if (!storeWorld.isLoaded(this.storePos)) return;
		
		final TileEntity tileEntity = storeWorld.getBlockEntity(this.storePos);
		
		if (!(tileEntity instanceof StoreTileEntity)) return;
		
		final UUID sendingPlayerUUID = sendingPlayer.getUUID();
		final UUID receivingPlayerUUID = ((StoreTileEntity) tileEntity).getOwner();
		
		final int transferAmount = ((StoreTileEntity) tileEntity).getPrice();
		
		if (BankAccountHandler.getBankAccount(sendingPlayerUUID) == null ||
				BankAccountHandler.getBankAccount(receivingPlayerUUID) == null ||
				BankAccountHandler.getBankAccount(sendingPlayerUUID).getBalance() < transferAmount) return;
		
		TransactionHandler.processTransaction(
				new Transaction(sendingPlayerUUID, receivingPlayerUUID, transferAmount, Instant.now().getEpochSecond())
		);
	}
}
