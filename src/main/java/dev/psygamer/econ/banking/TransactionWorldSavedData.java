package dev.psygamer.econ.banking;


import dev.psygamer.econ.ECon;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.WorldSavedData;

import java.util.UUID;

public class TransactionWorldSavedData extends WorldSavedData {
	
	private static final String DATA_NAME = ECon.MODID + "_TransactionHistory";
	
	public TransactionWorldSavedData() {
		super(DATA_NAME);
	}
	
	@Override
	public void load(final CompoundNBT compound) {
		TransactionHandler.getTransactionHistory().clear();
		
		for (final String key : compound.getAllKeys()) {
			final CompoundNBT transactionCompound = compound.getCompound(key);
			
			final UUID sendingPlayerUUID = transactionCompound.getUUID("from");
			final UUID receivingPlayerUUID = transactionCompound.getUUID("to");
			final long transferAmount = transactionCompound.getLong("amount");
			final long timestamp = transactionCompound.getLong("time");
			
			TransactionHandler.registerTransaction(
					new Transaction(sendingPlayerUUID, receivingPlayerUUID, transferAmount, timestamp)
			);
		}
	}
	
	@Override
	public CompoundNBT save(final CompoundNBT compound) {
		for (int i = 0 ; i < TransactionHandler.getTransactionHistory().size() ; i++) {
			final Transaction transaction = TransactionHandler.getTransactionHistory().get(i);
			final CompoundNBT transactionCompound = new CompoundNBT();
			
			transactionCompound.putUUID("from", transaction.getSendingPlayer());
			transactionCompound.putUUID("to", transaction.getReceivingPlayer());
			transactionCompound.putLong("amount", transaction.getTransferAmount());
			transactionCompound.putLong("time", transaction.getUnixTimestamp());
			
			compound.put(String.valueOf(i), transactionCompound);
		}
		
		TransactionHandler.resolveDirty();
		
		return compound;
	}
	
	public static TransactionWorldSavedData get(final ServerWorld world) {
		return world
				.getDataStorage()
				.computeIfAbsent(TransactionWorldSavedData::new, DATA_NAME);
	}
}
