package dev.psygamer.econ.banking;


import dev.psygamer.econ.ECon;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.WorldSavedData;

import java.util.UUID;

public class BankAccountWorldSavedData extends WorldSavedData {
	
	private static final String DATA_NAME = ECon.MODID + "_BankAccount";
	
	public BankAccountWorldSavedData() {
		super(DATA_NAME);
	}
	
	@Override
	public void load(final CompoundNBT compound) {
		BankAccountHandler.getBankAccounts().clear();
		
		for (final String key : compound.getAllKeys()) {
			BankAccountHandler.registerBankAccount(UUID.fromString(key), compound.getInt(key));
		}
	}
	
	@Override
	public CompoundNBT save(final CompoundNBT compound) {
		for (final UUID uuid : BankAccountHandler.getBankAccounts().keySet()) {
			final long balance = BankAccountHandler.getBalance(uuid);
			
			compound.putLong(uuid.toString(), balance);
		}
		
		BankAccountHandler.resolveDirty();
		
		return compound;
	}
	
	public static BankAccountWorldSavedData get(final ServerWorld world) {
		return world
				.getDataStorage()
				.computeIfAbsent(BankAccountWorldSavedData::new, DATA_NAME);
	}
}
