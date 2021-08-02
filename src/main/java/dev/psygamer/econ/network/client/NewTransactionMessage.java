package dev.psygamer.econ.network.client;

import dev.psygamer.econ.banking.Transaction;
import dev.psygamer.econ.banking.TransactionHandler;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.UUID;
import java.util.function.Supplier;

public class NewTransactionMessage {
	
	private final Transaction transaction;
	
	public NewTransactionMessage(final Transaction transaction) {
		this.transaction = transaction;
	}
	
	public void encode(final PacketBuffer buffer) {
		buffer.writeLong(this.transaction.getSendingPlayer().getMostSignificantBits());
		buffer.writeLong(this.transaction.getSendingPlayer().getLeastSignificantBits());
		buffer.writeLong(this.transaction.getReceivingPlayer().getMostSignificantBits());
		buffer.writeLong(this.transaction.getReceivingPlayer().getLeastSignificantBits());
		
		buffer.writeLong(this.transaction.getTransferAmount());
		buffer.writeLong(this.transaction.getUnixTimestamp());
	}
	
	public static NewTransactionMessage decode(final PacketBuffer buffer) {
		final long sendingMost = buffer.readLong();
		final long sendingLeast = buffer.readLong();
		final long receivingMost = buffer.readLong();
		final long receivingLeast = buffer.readLong();
		
		final UUID sendingPlayer = new UUID(sendingMost, sendingLeast);
		final UUID receivingPlayer = new UUID(receivingMost, receivingLeast);
		
		final long transferAmount = buffer.readLong();
		final long unixTimestamp = buffer.readLong();
		
		return new NewTransactionMessage(new Transaction(sendingPlayer, receivingPlayer, transferAmount, unixTimestamp));
	}
	
	public void handle(final Supplier<NetworkEvent.Context> context) {
		context.get().enqueueWork(() -> TransactionHandler.clientTransactionHistory.addFirst(this.transaction));
	}
}
