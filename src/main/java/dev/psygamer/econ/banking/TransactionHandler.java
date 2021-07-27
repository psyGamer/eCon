package dev.psygamer.econ.banking;

import java.util.ArrayList;
import java.util.List;

public final class TransactionHandler {
	
	private static final List<Transaction> transactionHistory = new ArrayList<>();
	private static boolean dirty = false;
	
	public static List<Transaction> getTransactionHistory() {
		return transactionHistory;
	}
	
	public static void processTransaction(final Transaction transaction) {
		transactionHistory.add(transaction);
		
		BankAccountHandler.modifyAccountBalance(
				transaction.getSendingPlayer(),
				-transaction.getTransferAmount()
		);
		BankAccountHandler.modifyAccountBalance(
				transaction.getReceivingPlayer(),
				+transaction.getTransferAmount()
		);
		
		markDirty();
	}
	
	public static void registerTransaction(final Transaction transaction) {
	
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
