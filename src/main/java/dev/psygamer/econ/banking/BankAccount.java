package dev.psygamer.econ.banking;

import java.util.UUID;

public class BankAccount {
	
	private final UUID owner;
	private long balance;
	
	public BankAccount(final UUID owner) {
		this.owner = owner;
	}
	
	public BankAccount(final UUID owner, final long balance) {
		this.owner = owner;
		this.balance = balance;
	}
	
	public UUID getOwner() {
		return this.owner;
	}
	
	public long getBalance() {
		return this.balance;
	}
	
	public void setBalance(final long balance) {
		this.balance = balance;
	}
}
