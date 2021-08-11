package dev.psygamer.econ;

import com.mojang.brigadier.CommandDispatcher;
import dev.psygamer.econ.banking.*;
import dev.psygamer.econ.client.screen.store.StoreCustomerScreen;
import dev.psygamer.econ.client.screen.store.StoreOwnerScreen;
import dev.psygamer.econ.client.renderer.tileentity.StoreRenderer;
import dev.psygamer.econ.client.screen.store.StoreStorageScreen;
import dev.psygamer.econ.client.KeybindingsRegistry;
import dev.psygamer.econ.commands.GetBalanceCommand;
import dev.psygamer.econ.commands.PayCommand;
import dev.psygamer.econ.commands.SetBalanceCommand;
import dev.psygamer.econ.network.client.BankAccountMessage;
import dev.psygamer.econ.network.EConPacketHandler;
import dev.psygamer.econ.network.client.RegisteredBankAccountsMessage;
import dev.psygamer.econ.network.client.TransactionHistoryMessage;
import dev.psygamer.econ.proxy.ClientProxy;
import dev.psygamer.econ.proxy.IProxy;
import dev.psygamer.econ.proxy.ServerProxy;
import dev.psygamer.econ.setup.BlockRegistry;
import dev.psygamer.econ.setup.ContainerRegistry;
import dev.psygamer.econ.setup.ItemRegistry;
import dev.psygamer.econ.setup.TileEntityTypeRegistry;
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.command.CommandSource;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.network.PacketDistributor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.UUID;

@Mod(ECon.MODID)
public class ECon {
	
	public static final String MODID = "econ";
	
	public static final ITextComponent MONEY_SYMBOL = new
			TranslationTextComponent("econ.moneySymbol");
	
	public static final ITextComponent COMMAND_DISABLED_MESSAGE = new
			TranslationTextComponent("econ.disabled")
			.withStyle(TextFormatting.RED);
	
	
	private static IProxy proxy;
	
	public static IProxy getProxy() {
		return proxy;
	}
	
	public ECon() {
		final IEventBus forgeEventBus = MinecraftForge.EVENT_BUS;
		final IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
		
		modEventBus.addListener(this::onCommonSetup);
		modEventBus.addListener(this::onClientSetup);
		
		forgeEventBus.addListener(this::onPlayerJoin);
		forgeEventBus.addListener(this::onWorldLoad);
		forgeEventBus.addListener(this::onWorldSaved);
		forgeEventBus.addListener(this::onCommandRegister);
		
		proxy = DistExecutor.safeRunForDist(() -> ClientProxy::new, () -> ServerProxy::new);
		
		BlockRegistry.register();
		ItemRegistry.register();
		TileEntityTypeRegistry.register();
		ContainerRegistry.register();
	}
	
	private void onCommonSetup(final FMLCommonSetupEvent event) {
		EConPacketHandler.register();
	}
	
	private void onClientSetup(final FMLClientSetupEvent event) {
		KeybindingsRegistry.load();
		ScreenManager.register(ContainerRegistry.STORE_BLOCK_CONTAINER.get(), StoreOwnerScreen::new);
		ScreenManager.register(ContainerRegistry.STORE_STORAGE_CONTAINER.get(), StoreStorageScreen::new);
		ScreenManager.register(ContainerRegistry.STORE_CUSTOMER_CONTAINER.get(), StoreCustomerScreen::new);
		
		RenderTypeLookup.setRenderLayer(BlockRegistry.STORE_BLOCK.get(), RenderType.cutout());
		
		ClientRegistry.bindTileEntityRenderer(TileEntityTypeRegistry.STORE_BLOCK_TYPE.get(), StoreRenderer::new);
	}
	
	private void onPlayerJoin(final PlayerEvent.PlayerLoggedInEvent event) {
		final ServerPlayerEntity player = (ServerPlayerEntity) event.getPlayer();
		
		final UUID playerUUID = player.getUUID();
		final String playerName = player.getName().getString();
		
		final BankAccount account = BankAccountHandler.getBankAccount(playerUUID);
		
		if (account == null) {
			BankAccountHandler.registerBankAccount(playerUUID, playerName, 0);
		} else if (account.getOwnerName() == null || !account.getOwnerName().equals(playerName)) {
			BankAccountHandler.getBankAccount(playerUUID).setOwnerName(playerName);
		}
		
		EConPacketHandler.INSTANCE.send(
				PacketDistributor.PLAYER.with(() -> player),
				new BankAccountMessage(BankAccountHandler.getBankAccount(playerUUID))
		);
		
		EConPacketHandler.INSTANCE.send(
				PacketDistributor.ALL.noArg(),
				new RegisteredBankAccountsMessage()
		);
		
		final int maxEntriesPerSite = 16;
		final List<Transaction> transactions = TransactionHandler.getTransactions(playerUUID, 0, maxEntriesPerSite * 2);
		
		if (transactions != null && !transactions.isEmpty()) {
			EConPacketHandler.INSTANCE.send(
					PacketDistributor.PLAYER.with(() -> player),
					new TransactionHistoryMessage(transactions, 0, transactions.size())
			);
		}
	}
	
	private void onWorldLoad(final WorldEvent.Load event) {
		if (!event.getWorld().isClientSide() && event.getWorld() instanceof ServerWorld) {
			if (((ServerWorld) event.getWorld()).getServer().isSingleplayer()) {
				return;
			}
		}
	}
	
	private void onWorldSaved(final WorldEvent.Save event) {
		if (!event.getWorld().isClientSide() && event.getWorld() instanceof ServerWorld) {
			if (((ServerWorld) event.getWorld()).getServer().isSingleplayer()) {
				return;
			}
			
			if (BankAccountHandler.isDirty()) {
				BankAccountWorldSavedData.get((ServerWorld) event.getWorld()).setDirty();
			}
			if (TransactionHandler.isDirty()) {
				TransactionWorldSavedData.get((ServerWorld) event.getWorld()).setDirty();
			}
		}
	}
	
	private void onCommandRegister(final RegisterCommandsEvent event) {
		final CommandDispatcher<CommandSource> dispatcher = event.getDispatcher();
		
		GetBalanceCommand.register(dispatcher);
		SetBalanceCommand.register(dispatcher);
		PayCommand.register(dispatcher);
	}
}
