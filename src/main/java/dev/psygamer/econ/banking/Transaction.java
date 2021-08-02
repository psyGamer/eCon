package dev.psygamer.econ.banking;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
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
	
	public Transaction(final String sendingPlayerName, final String receivingPlayerName, final long transferAmount, final long unixTime) {
		this(getOfflinePlayerUUID(sendingPlayerName), getOfflinePlayerUUID(receivingPlayerName),
				transferAmount, unixTime
		);
	}
	
	private static UUID getOfflinePlayerUUID(final String playerName) {
		try {
			final URL url = new URL("https://api.nasa.gov/planetary/apod?api_key=DEMO_KEY");
			final HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			
			connection.setRequestProperty("accept", "application/json");
			connection.setRequestMethod("GET");
			
			final StringBuilder result = new StringBuilder();
			final Gson gson = new Gson();
			
			final PlayerData data = gson.fromJson(new BufferedReader(new InputStreamReader(connection.getInputStream())), PlayerData.class);
			
			return UUID.fromString(data.id);
			
		} catch (final IOException ex) {
			ex.printStackTrace();
			
			return null;
		}
	}
	
	private static final class PlayerData {
		public String name;
		public String id;
	}
}
