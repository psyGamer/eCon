package dev.psygamer.econ.network.client;

import dev.psygamer.econ.ECon;
import dev.psygamer.econ.banking.BankAccountHandler;

import net.minecraft.network.PacketBuffer;

import net.minecraftforge.fml.network.NetworkEvent;

import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Supplier;

public class RegisteredBankAccountsMessage {
	
	private final Map<UUID, String> bankAccounts;
	
	public RegisteredBankAccountsMessage() {
		this.bankAccounts = new HashMap<>();
		
		BankAccountHandler.getBankAccounts().forEach(((uuid, account) -> {
			this.bankAccounts.put(uuid, account.getOwnerName());
		}));
	}
	
	public RegisteredBankAccountsMessage(final Map<UUID, String> bankAccounts) {
		this.bankAccounts = bankAccounts;
	}
	
	public void encode(final PacketBuffer buffer) {
		buffer.writeInt(this.bankAccounts.size());
		
		this.bankAccounts.forEach((uuid, name) -> {
			buffer.writeLong(uuid.getMostSignificantBits());
			buffer.writeLong(uuid.getLeastSignificantBits());
			
			buffer.writeInt(name.length());
			buffer.writeCharSequence(name, Charset.defaultCharset());
		});
	}
	
	public static RegisteredBankAccountsMessage decode(final PacketBuffer buffer) {
		final int length = buffer.readInt();
		final HashMap<UUID, String> bankAccounts = new HashMap<>();
		
		for (int i = 0 ; i < length ; i++) {
			final long uuidMost = buffer.readLong();
			final long uuidLeast = buffer.readLong();
			final int nameLength = buffer.readInt();
			
			final String name = (String) buffer.readCharSequence(nameLength, Charset.defaultCharset());
			
			bankAccounts.put(new UUID(uuidMost, uuidLeast), name);
		}
		
		return new RegisteredBankAccountsMessage(bankAccounts);
	}
	
	public void handle(final Supplier<NetworkEvent.Context> context) {
		context.get().enqueueWork(() -> {
			BankAccountHandler.bankAccountPlayerNames = this.bankAccounts;
			
			ECon.getProxy().preparePlayerHeadWidget();
		});
	}
}
