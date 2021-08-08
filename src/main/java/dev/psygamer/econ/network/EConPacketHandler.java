package dev.psygamer.econ.network;

import dev.psygamer.econ.ECon;

import dev.psygamer.econ.block.StoreContainer;
import dev.psygamer.econ.network.client.BankAccountMessage;
import dev.psygamer.econ.network.client.NewTransactionMessage;
import dev.psygamer.econ.network.client.RegisteredBankAccountsMessage;
import dev.psygamer.econ.network.client.TransactionHistoryMessage;
import dev.psygamer.econ.network.server.StoreOwnerMessage;
import dev.psygamer.econ.network.server.StoreTransactionMessage;
import dev.psygamer.econ.network.server.TransactionMessage;
import net.minecraft.util.ResourceLocation;

import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;

public class EConPacketHandler {
	
	private static final String PROTOCOL_VERSION = "1";
	private static int channelID = 0;
	
	public static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(
			new ResourceLocation(ECon.MODID, "main"),
			
			() -> PROTOCOL_VERSION,
			
			PROTOCOL_VERSION::equals,
			PROTOCOL_VERSION::equals
	);
	
	public static void register() {
		/* Server -> Client */
		
		INSTANCE.registerMessage(channelID++, BankAccountMessage.class, BankAccountMessage::encode, BankAccountMessage::decode, BankAccountMessage::handle);
		INSTANCE.registerMessage(channelID++, NewTransactionMessage.class, NewTransactionMessage::encode, NewTransactionMessage::decode, NewTransactionMessage::handle);
		INSTANCE.registerMessage(channelID++, TransactionHistoryMessage.class, TransactionHistoryMessage::encode, TransactionHistoryMessage::decode, TransactionHistoryMessage::handle);
		INSTANCE.registerMessage(channelID++, RegisteredBankAccountsMessage.class, RegisteredBankAccountsMessage::encode, RegisteredBankAccountsMessage::decode, RegisteredBankAccountsMessage::handle);
		
		/* Client -> Server */
		
		INSTANCE.registerMessage(channelID++, TransactionMessage.class, TransactionMessage::encode, TransactionMessage::decode, TransactionMessage::handle);
		INSTANCE.registerMessage(channelID++, StoreTransactionMessage.class, StoreTransactionMessage::encode, StoreTransactionMessage::new, StoreTransactionMessage::handle);
		INSTANCE.registerMessage(channelID++, StoreOwnerMessage.class, StoreOwnerMessage::encode, StoreOwnerMessage::new, StoreOwnerMessage::handle);
	}
}
