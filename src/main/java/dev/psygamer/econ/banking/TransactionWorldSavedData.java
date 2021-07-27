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
		
		for (int i = 0 ; i < compound.getAllKeys().size() / 3 ; i++) {
			final UUID sendingPlayerUUID = compound.getUUID(i + "_from");
			final UUID receivingPlayerUUID = compound.getUUID(i + "_to");
			final long transferAmount = compound.getLong(i + "_amount");
			
			final Transaction transaction = new Transaction(sendingPlayerUUID, receivingPlayerUUID, transferAmount);
			
			TransactionHandler.registerTransaction(transaction);
		}
	}
	
	@Override
	public CompoundNBT save(final CompoundNBT compound) {
		for (int i = 0 ; i < TransactionHandler.getTransactionHistory().size() ; i++) {
			final Transaction transaction = TransactionHandler.getTransactionHistory().get(i);
			
			compound.putUUID(i + "_from", transaction.getSendingPlayer());
			compound.putUUID(i + "_to", transaction.getReceivingPlayer());
			compound.putLong(i + "_amount", transaction.getTransferAmount());
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
