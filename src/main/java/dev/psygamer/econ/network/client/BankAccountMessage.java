package dev.psygamer.econ.network.client;

import dev.psygamer.econ.banking.BankAccount;
import dev.psygamer.econ.banking.BankAccountHandler;

import net.minecraft.network.PacketBuffer;

import net.minecraftforge.fml.network.NetworkEvent;

import java.nio.charset.Charset;
import java.util.UUID;
import java.util.function.Supplier;

public class BankAccountMessage {
	
	private final BankAccount account;
	
	public BankAccountMessage(final BankAccount account) {
		this.account = account;
	}
	
	public void encode(final PacketBuffer buffer) {
		buffer.writeLong(this.account.getOwner().getLeastSignificantBits());
		buffer.writeLong(this.account.getOwner().getMostSignificantBits());
		buffer.writeLong(this.account.getBalance());
		
		buffer.writeInt(this.account.getOwnerName().length());
		buffer.writeCharSequence(this.account.getOwnerName(), Charset.defaultCharset());
	}
	
	public static BankAccountMessage decode(final PacketBuffer buffer) {
		final long uuidLeast = buffer.readLong();
		final long uuidMost = buffer.readLong();
		final long balance = buffer.readLong();
		
		final int ownerNameLength = buffer.readInt();
		final String ownerName = (String) buffer.readCharSequence(ownerNameLength, Charset.defaultCharset());
		
		final UUID ownerUUID = new UUID(uuidMost, uuidLeast);
		final BankAccount account = new BankAccount(ownerUUID, ownerName, balance);
		
		return new BankAccountMessage(account);
	}
	
	public void handle(final Supplier<NetworkEvent.Context> context) {
		context.get().enqueueWork(() -> {
			BankAccountHandler.clientBankAccount = this.account;
		});
	}
}
