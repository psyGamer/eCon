package dev.psygamer.econ.banking;

import java.util.UUID;

public class Transaction {
	
	private final UUID sendingPlayer;
	private final UUID receivingPlayer;
	
	private final long transferAmount;
	private final long unixTime;
	
	public UUID getSendingPlayer() {
		return this.sendingPlayer;
	}
	
	public UUID getReceivingPlayer() {
		return this.receivingPlayer;
	}
	
	public long getTransferAmount() {
		return this.transferAmount;
	}
	
	public long getUnixTimestamp() {
		return this.unixTime;
	}
	
	public Transaction(final UUID sendingPlayer, final UUID receivingPlayer, final long transferAmount, final long unixTime) {
		this.sendingPlayer = sendingPlayer;
		this.receivingPlayer = receivingPlayer;
		this.transferAmount = transferAmount;
		this.unixTime = unixTime;
	}
}
