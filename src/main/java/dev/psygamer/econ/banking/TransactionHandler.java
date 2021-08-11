package dev.psygamer.econ.banking;

import dev.psygamer.econ.network.EConPacketHandler;
import dev.psygamer.econ.network.client.NewTransactionMessage;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.PlayerList;

import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.LogicalSidedProvider;
import net.minecraftforge.fml.network.PacketDistributor;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

public final class TransactionHandler {
	
	public static final LinkedList<Transaction> clientTransactionHistory = new LinkedList<>();
	
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
		
		final MinecraftServer server = LogicalSidedProvider.INSTANCE.get(LogicalSide.SERVER);
		final PlayerList playerList = server.getPlayerList();
		
		final ServerPlayerEntity sendingServerPlayer = playerList.getPlayer(transaction.getSendingPlayer());
		final ServerPlayerEntity receivingServerPlayer = playerList.getPlayer(transaction.getReceivingPlayer());
		
		try {
			EConPacketHandler.INSTANCE.send(
					PacketDistributor.PLAYER.with(() -> sendingServerPlayer),
					new NewTransactionMessage(transaction)
			);
		} catch (final Exception ignore) {
		}
		try {
			EConPacketHandler.INSTANCE.send(
					PacketDistributor.PLAYER.with(() -> receivingServerPlayer),
					new NewTransactionMessage(transaction)
			);
		} catch (final Exception ignore) {
		}
		
		markDirty();
	}
	
	public static void registerTransaction(final Transaction transaction) {
		transactionHistory.add(transaction);
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
	
	public static List<Transaction> getTransactions(final UUID player, int start, int length) {
		
		final List<Transaction> transactionsHistory = TransactionHandler.getTransactionHistory();
		final List<Transaction> transactions = new ArrayList<>();
		
		if (start < 0) {
			start = 0;
		}
		
		if (start + length >= transactionsHistory.size()) {
			length = transactionsHistory.size() - start - 1;
		}
		
		int i = start;
		int j = start;
		
		while (i <= start + length && j < transactionsHistory.size()) {
			final Transaction transaction = TransactionHandler.getTransactionHistory().get(transactionsHistory.size() - i - 1);
			
			if (player.equals(transaction.getSendingPlayer()) ||
					player.equals(transaction.getReceivingPlayer())
			) {
				transactions.add(transaction);
				
				i++;
			}
			
			j++;
		}
		
		return transactions;
	}
}
