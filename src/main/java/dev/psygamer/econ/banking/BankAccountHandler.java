package dev.psygamer.econ.banking;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class BankAccountHandler {
	
	private static final Map<UUID, BankAccount> bankAccounts = new HashMap<>();
	private static boolean dirty = false;
	
	public static BankAccount getBankAccount(final UUID player) {
		if (bankAccounts.containsKey(player)) {
			return bankAccounts.get(player);
		}
		
		return null;
	}
	
	public static Map<UUID, BankAccount> getBankAccounts() {
		return bankAccounts;
	}
	
	public static void setBalance(final UUID player, final long balance) {
		bankAccounts.get(player).setBalance(balance);
		
		markDirty();
	}
	
	public static long getBalance(final UUID player) {
		return bankAccounts.get(player).getBalance();
	}
	
	public static void modifyAccountBalance(final UUID player, final long amount) {
		setBalance(player, getBalance(player) + amount);
	}
	
	public static void registerBankAccount(final UUID player, final long balance) {
		bankAccounts.put(player, new BankAccount(player, balance));
		
		markDirty();
	}
	
	private static void markDirty() {
		dirty = true;
	}
	
	
	public static void resolveDirty() {
		dirty = false;
	}
	
	public static boolean isDirty() {
		return dirty;
	}
}
