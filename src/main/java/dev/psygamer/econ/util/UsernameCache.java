package dev.psygamer.econ.util;

import com.google.common.base.CharMatcher;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.io.Resources;
import com.google.common.util.concurrent.UncheckedExecutionException;
import com.google.gson.stream.JsonReader;
import com.mojang.authlib.GameProfile;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;

/**
 * @author diesieben07
 * <p>
 * Original Source Code: https://github.com/diesieben07/SevenCommons/blob/1.7/src/main/java/de/take_weiland/mods/commons/internal/UsernameCache.java
 */
public final class UsernameCache {
	
	private static LoadingCache<UUID, String> cache;
	private static final String USERNAME_API_URL = "https://api.mojang.com/user/profiles/%s/names";
	
	public static String get(final UUID uuid) {
		try {
			return cache.get(uuid);
		} catch (UncheckedExecutionException | ExecutionException e) {
			return null;
		}
	}
	
	public static void invalidate(final UUID uuid) {
		cache.invalidate(uuid);
	}
	
	private UsernameCache() {
	}
	
	static void initCache(final int cacheSize) {
		cache = CacheBuilder.newBuilder()
				.maximumSize(cacheSize)
				.build(new CacheLoader<UUID, String>() {
					
					@Override
					public String load(final UUID uuid) {
						try {
							return fetchNameFromMojangAPI(uuid);
						} catch (final IOException ex) {
							return null;
						}
					}
				});
	}
	
	@SubscribeEvent
	public static void onPlayerLogin(final PlayerEvent.PlayerLoggedInEvent event) {
		final GameProfile profile = event.getPlayer().getGameProfile();
		
		cache.put(profile.getId(), profile.getName());
	}
	
	private static String fetchNameFromMojangAPI(final UUID uuid) throws IOException {
		final String uuidString = uuid.toString().replace("-", "");
		
		try (BufferedReader reader = Resources.asCharSource(
				new URL(String.format(USERNAME_API_URL, uuidString)), StandardCharsets.UTF_8
		).openBufferedStream()) {
			final JsonReader json = new JsonReader(reader);
			json.beginArray();
			
			String name = null;
			
			while (json.hasNext()) {
				json.beginObject();
				
				String nameEntry = null;
				long changedAtEntry = 0;
				
				while (json.hasNext()) {
					final String key = json.nextName();
					
					switch (key) {
						case "name":
							nameEntry = json.nextString();
							break;
						case "changedToAt":
							changedAtEntry = json.nextLong();
							break;
						default:
							json.skipValue();
							break;
					}
				}
				
				json.endObject();
				
				if (nameEntry != null && changedAtEntry >= 0) {
					name = nameEntry;
				}
			}
			
			json.endArray();
			
			return name;
		}
	}
}