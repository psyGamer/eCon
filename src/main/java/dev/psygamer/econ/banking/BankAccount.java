package dev.psygamer.econ.banking;

import java.util.UUID;

public class BankAccount {
	
	private final UUID owner;
	private String ownerName;
	
	private long balance;
	
	public BankAccount(final UUID owner, final String ownerName) {
		this.owner = owner;
		this.ownerName = ownerName;
	}
	
	public BankAccount(final UUID owner, final String ownerName, final long balance) {
		this.owner = owner;
		this.ownerName = ownerName;
		this.balance = balance;
	}
	
	public UUID getOwner() {
		return this.owner;
	}
	
	public String getOwnerName() {
		return this.ownerName;
	}
	
	public void setOwnerName(final String ownerName) {
		this.ownerName = ownerName;
	}
	
	public long getBalance() {
		return this.balance;
	}
	
	public void setBalance(final long balance) {
		this.balance = balance;
	}
}
