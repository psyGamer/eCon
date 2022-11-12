package dev.psygamer.econ.network.server;

import dev.psygamer.econ.banking.BankAccount;
import dev.psygamer.econ.banking.BankAccountHandler;
import dev.psygamer.econ.banking.Transaction;
import dev.psygamer.econ.banking.TransactionHandler;

import net.minecraft.network.PacketBuffer;
import net.minecraft.entity.player.ServerPlayerEntity;

import net.minecraftforge.fml.network.NetworkEvent;

import java.time.Instant;
import java.util.UUID;
import java.util.function.Supplier;

public class TransactionMessage {
	
	private final UUID receiverUUID;
	private final long transferAmount;
	
	public TransactionMessage(final UUID receiverUUID, final long transferAmount) {
		this.receiverUUID = receiverUUID;
		this.transferAmount = transferAmount;
	}
	
	public void encode(final PacketBuffer buffer) {
		buffer.writeLong(this.receiverUUID.getMostSignificantBits());
		buffer.writeLong(this.receiverUUID.getLeastSignificantBits());
		buffer.writeLong(this.transferAmount);
	}
	
	public static TransactionMessage decode(final PacketBuffer buffer) {
		final long receiverMost = buffer.readLong();
		final long receiverLeast = buffer.readLong();
		final long transferAmount = buffer.readLong();
		
		return new TransactionMessage(new UUID(receiverMost, receiverLeast), transferAmount);
	}
	
	public void handle(final Supplier<NetworkEvent.Context> context) {
		context.get().enqueueWork(() -> {
			final ServerPlayerEntity sender = context.get().getSender();
			final BankAccount senderBankAccount = BankAccountHandler.getBankAccount(sender.getUUID());
			final BankAccount receiverBankAccount = BankAccountHandler.getBankAccount(this.receiverUUID);
			
			if (senderBankAccount == null || receiverBankAccount == null || senderBankAccount.getBalance() < this.transferAmount) {
				return; // Cancel transaction
			}
			
			TransactionHandler.processTransaction(
					new Transaction(sender.getUUID(), this.receiverUUID, this.transferAmount, Instant.now().getEpochSecond())
			);
		});
	}
}
