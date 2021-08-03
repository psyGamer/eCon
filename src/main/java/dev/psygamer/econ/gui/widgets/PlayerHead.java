package dev.psygamer.econ.gui.widgets;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.mojang.authlib.properties.Property;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.network.play.NetworkPlayerInfo;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.client.gui.widget.Widget;

import org.json.simple.JSONObject;
import org.shanerx.mojang.Mojang;
import org.shanerx.mojang.PlayerProfile;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

public class PlayerHead extends Widget {
	
	private static final Mojang mojangAPI = new Mojang();
	
	private static final Map<UUID, CompletableFuture<String>> nameCache = new HashMap<>();
	private static final Map<UUID, CompletableFuture<ResourceLocation>> skinCache = new HashMap<>();
	
	private static final String fallbackName = "Steve";
	private static final ResourceLocation fallbackSkinLocation = new ResourceLocation("textures/entity/steve.png");
	
	private UUID playerUUID;
	
	private CompletableFuture<String> nameFuture;
	private CompletableFuture<ResourceLocation> skinFuture;
	
	private final Screen parentScreen;
	
	public PlayerHead(final int x, final int y, final int width, final int height, final UUID playerUUID, final Screen parentScreen) {
		super(x, y, width, height, StringTextComponent.EMPTY);
		
		this.playerUUID = playerUUID;
		
		this.nameFuture = getPlayerName(playerUUID);
		this.skinFuture = getSkinTexture(playerUUID);
		
		this.parentScreen = parentScreen;
	}
	
	public UUID getPlayerUUID() {
		return this.playerUUID;
	}
	
	public void setPlayerUUID(final UUID playerUUID) {
		if (this.playerUUID == playerUUID) {
			return;
		}
		
		this.playerUUID = playerUUID;
		
		this.nameFuture = getPlayerName(playerUUID);
		this.skinFuture = getSkinTexture(playerUUID);
	}
	
	@Override
	public void render(final MatrixStack matrix, final int mouseX, final int mouseY, final float partialTicks) {
		Minecraft.getInstance().getTextureManager().bind(this.skinFuture.getNow(fallbackSkinLocation));
		
		AbstractGui.blit(matrix, this.x, this.y, this.width, this.height, 8.0F, 8.0F, 8, 8, 64, 64);
		RenderSystem.enableBlend();
		AbstractGui.blit(matrix, this.x, this.y, this.width, this.height, 40.0F, 8.0F, 8, 8, 64, 64);
		RenderSystem.disableBlend();
		
		if (isMouseOver(mouseX, mouseY)) {
			this.parentScreen.renderTooltip(matrix, new StringTextComponent(this.nameFuture.getNow(fallbackName)), mouseX, mouseY);
		}
	}
	
	public static void preparePlayerData(final Set<UUID> playerUUIDs) {
		nameCache.clear();
		skinCache.clear();
		
		for (final UUID playerUUID : playerUUIDs) {
			nameCache.put(playerUUID, getPlayerName(playerUUID));
			skinCache.put(playerUUID, getSkinTexture(playerUUID));
		}
	}
	
	@SuppressWarnings("unchecked")
	private static CompletableFuture<ResourceLocation> getSkinTexture(final UUID playerUUID) {
		if (playerUUID == null) {
			return CompletableFuture.completedFuture(fallbackSkinLocation);
		}
		if (skinCache != null && playerUUID != null && skinCache.containsKey(playerUUID)) {
			return skinCache.get(playerUUID);
		}
		
		return CompletableFuture.supplyAsync(() -> {
			final NetworkPlayerInfo playerInfo = Minecraft.getInstance().player.connection.getPlayerInfo(playerUUID);
			
			if (playerInfo != null) {
				return playerInfo.getSkinLocation();
			}
			
			try {
				if (mojangAPI.getStatus(Mojang.ServiceType.API_MOJANG_COM) == null) {
					mojangAPI.connect();
				}
				
				final String playerName = getPlayerName(playerUUID).get();
				
				final GameProfile profile = new GameProfile(playerUUID, playerName);
				final PlayerProfile playerProfile = mojangAPI.getPlayerProfile(playerUUID.toString());
				
				final JSONObject json = new JSONObject();
				final JSONObject textureJson = new JSONObject();
				
				json.put("profileId", playerProfile.getUUID());
				json.put("profileName", playerProfile.getUsername());
				
				if (playerProfile.getTextures().isPresent()) {
					final PlayerProfile.TexturesProperty texturesProperty = playerProfile.getTextures().get();
					
					json.put("timestamp", texturesProperty.getTimestamp());
					
					if (texturesProperty.getSkin().isPresent()) {
						final JSONObject skinJson = new JSONObject();
						
						skinJson.put("url", texturesProperty.getSkin().get().toString());
						textureJson.put("SKIN", skinJson);
					}
					if (texturesProperty.getCape().isPresent()) {
						final JSONObject capeJson = new JSONObject();
						
						capeJson.put("url", texturesProperty.getCape().get().toString());
						textureJson.put("CAPE", capeJson);
					}
					
					json.put("textures", textureJson);
				}
				
				final String textureString = new String(
						Base64.getEncoder().encode(json.toJSONString().getBytes())
				);
				
				profile.getProperties().put("textures",
						new Property("textures", textureString, null)
				);
				
				final AtomicReference<ResourceLocation> skinLocationReference = new AtomicReference<>(
						new ResourceLocation("textures/entity/steve.png")
				);
				final CountDownLatch latch = new CountDownLatch(1);
				
				Minecraft.getInstance().getSkinManager().registerSkins(profile, (type, textureLocation, profileTexture) -> {
					if (type == MinecraftProfileTexture.Type.SKIN) {
						skinLocationReference.set(textureLocation);
						latch.countDown();
					}
				}, false);
				
				if (!latch.await(10, TimeUnit.SECONDS)) {
					return fallbackSkinLocation;
				}
				
				return skinLocationReference.get();
				
			} catch (final RuntimeException | InterruptedException | ExecutionException ex) {
				return fallbackSkinLocation;
			}
		});
	}
	
	private static CompletableFuture<String> getPlayerName(final UUID playerUUID) {
		if (playerUUID == null) {
			return CompletableFuture.completedFuture(fallbackName);
		}
		
		if (nameCache != null && playerUUID != null && nameCache.containsKey(playerUUID)) {
			return nameCache.get(playerUUID);
		}
		
		return CompletableFuture.supplyAsync(() -> {
			final NetworkPlayerInfo playerInfo = Minecraft.getInstance().player.connection.getPlayerInfo(playerUUID);
			
			if (playerInfo != null && playerInfo.getProfile() != null) {
				return playerInfo.getProfile().getName();
			}
			
			try {
				if (mojangAPI.getStatus(Mojang.ServiceType.API_MOJANG_COM) == null) {
					mojangAPI.connect();
				}
				
				ECon.LOGGER.debug("Requesting name history of " + playerUUID);
				
				return mojangAPI.getNameHistoryOfPlayer(playerUUID.toString()).entrySet().stream()
						.min((entryA, entryB) -> (int) (entryB.getValue() - entryA.getValue()))
						.orElseThrow(RuntimeException::new)
						.getKey();
				
			} catch (final RuntimeException ex) {
				return fallbackName;
			}
		});
	}
}
