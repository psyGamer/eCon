package dev.psygamer.econ.banking;


import dev.psygamer.econ.ECon;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.DimensionSavedDataManager;
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
			final CompoundNBT accountCompound = compound.getCompound(key);
			
			final UUID playerUUID = UUID.fromString(key);
			final String playerName = accountCompound.getString("name");
			
			final long balance = accountCompound.getLong("amount");
			
			BankAccountHandler.registerBankAccount(playerUUID, playerName, balance);
		}
	}
	
	@Override
	public CompoundNBT save(final CompoundNBT compound) {
		for (final UUID uuid : BankAccountHandler.getBankAccounts().keySet()) {
			final String name = BankAccountHandler.getBankAccount(uuid).getOwnerName();
			final long balance = BankAccountHandler.getBalance(uuid);
			
			final CompoundNBT accountCompound = new CompoundNBT();
			
			accountCompound.putString("name", name);
			accountCompound.putLong("amount", balance);
			
			compound.put(uuid.toString(), accountCompound);
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
