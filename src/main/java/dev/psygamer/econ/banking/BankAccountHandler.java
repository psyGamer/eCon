package dev.psygamer.econ.banking;

import dev.psygamer.econ.network.client.BankAccountMessage;
import dev.psygamer.econ.network.EConPacketHandler;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.PlayerList;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.LogicalSidedProvider;
import net.minecraftforge.fml.network.PacketDistributor;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class BankAccountHandler {
	
	public static BankAccount clientBankAccount;
	public static Map<UUID, String> bankAccountPlayerNames;
	
	private static final Map<UUID, BankAccount> bankAccounts = new HashMap<>();
	private static boolean dirty = false;
	
	public static BankAccount getBankAccount(final UUID playerUUID) {
		if (bankAccounts.containsKey(playerUUID)) {
			return bankAccounts.get(playerUUID);
		}
		
		return null;
	}
	
	public static Map<UUID, BankAccount> getBankAccounts() {
		return bankAccounts;
	}
	
	public static void setBalance(final UUID playerUUID, final long balance) {
		bankAccounts.get(playerUUID).setBalance(balance);
		
		markDirty(playerUUID);
	}
	
	public static long getBalance(final UUID playerUUID) {
		return bankAccounts.get(playerUUID).getBalance();
	}
	
	public static void modifyAccountBalance(final UUID player, final long amount) {
		setBalance(player, getBalance(player) + amount);
		
		markDirty(player);
	}
	
	public static void registerBankAccount(final UUID playerUUID, final String playerName, final long balance) {
		bankAccounts.put(playerUUID, new BankAccount(playerUUID, playerName, balance));
		
		markDirty(playerUUID);
	}
	
	private static void markDirty(final UUID playerUUID) {
		if (EConPacketHandler.INSTANCE != null) {
			final MinecraftServer server = LogicalSidedProvider.INSTANCE.get(LogicalSide.SERVER);
			final PlayerList playerList = server.getPlayerList();
			final ServerPlayerEntity serverPlayer = playerList.getPlayer(playerUUID);
			
			try {
				EConPacketHandler.INSTANCE.send(
						PacketDistributor.PLAYER.with(() -> serverPlayer),
						new BankAccountMessage(BankAccountHandler.getBankAccount(playerUUID))
				);
			} catch (final Exception ignore) {
			}
		}
		
		dirty = true;
	}
	
	public static void resolveDirty() {
		dirty = false;
	}
	
	public static boolean isDirty() {
		return dirty;
	}
}
