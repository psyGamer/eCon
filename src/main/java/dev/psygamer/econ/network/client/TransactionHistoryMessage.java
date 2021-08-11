package dev.psygamer.econ.network.client;

import dev.psygamer.econ.banking.Transaction;
import dev.psygamer.econ.banking.TransactionHandler;

import net.minecraft.network.PacketBuffer;

import net.minecraftforge.fml.network.NetworkEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Supplier;

public class TransactionHistoryMessage {
	
	private final int start, length;
	private final List<Transaction> transactions;
	
	public TransactionHistoryMessage(final List<Transaction> transactions, final int start, final int length) {
		this.transactions = transactions;
		this.start = start;
		this.length = length;
	}
	
	public void encode(final PacketBuffer buffer) {
		buffer.writeInt(this.start);
		buffer.writeInt(this.length);
		
		for (final Transaction transaction : this.transactions) {
			buffer.writeLong(transaction.getSendingPlayer().getMostSignificantBits());
			buffer.writeLong(transaction.getSendingPlayer().getLeastSignificantBits());
			buffer.writeLong(transaction.getReceivingPlayer().getMostSignificantBits());
			buffer.writeLong(transaction.getReceivingPlayer().getLeastSignificantBits());
			
			buffer.writeLong(transaction.getTransferAmount());
			buffer.writeLong(transaction.getUnixTimestamp());
		}
	}
	
	public static TransactionHistoryMessage decode(final PacketBuffer buffer) {
		final int start = buffer.readInt();
		final int length = buffer.readInt();
		
		final List<Transaction> transactions = new ArrayList<>();
		
		for (int i = 0 ; i < length ; i++) {
			final long sendingMost = buffer.readLong();
			final long sendingLeast = buffer.readLong();
			final long receivingMost = buffer.readLong();
			final long receivingLeast = buffer.readLong();
			
			final UUID sendingPlayer = new UUID(sendingMost, sendingLeast);
			final UUID receivingPlayer = new UUID(receivingMost, receivingLeast);
			
			final long transferAmount = buffer.readLong();
			final long unixTimestamp = buffer.readLong();
			
			transactions.add(new Transaction(sendingPlayer, receivingPlayer, transferAmount, unixTimestamp));
		}
		
		return new TransactionHistoryMessage(transactions, start, length);
	}
	
	public void handle(final Supplier<NetworkEvent.Context> context) {
		context.get().enqueueWork(() -> {
			TransactionHandler.clientTransactionHistory.clear();
			TransactionHandler.clientTransactionHistory.addAll(this.transactions);
		});
	}
}
