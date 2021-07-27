package dev.psygamer.econ;

import com.mojang.brigadier.CommandDispatcher;
import dev.psygamer.econ.banking.BankAccountHandler;
import dev.psygamer.econ.banking.BankAccountWorldSavedData;
import dev.psygamer.econ.banking.TransactionHandler;
import dev.psygamer.econ.banking.TransactionWorldSavedData;
import dev.psygamer.econ.client.KeybindingsRegistry;
import dev.psygamer.econ.commands.PayCommand;
import dev.psygamer.econ.commands.SetBalanceCommand;
import dev.psygamer.econ.commands.GetBalanceCommand;
import dev.psygamer.econ.setup.ItemRegistry;

import net.minecraft.command.CommandSource;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(ECon.MODID)
public class ECon {
	
	public static final String MODID = "econ";
	public static final Logger LOGGER = LogManager.getLogger("eCon");
	
	public ECon() {
		final IEventBus forgeEventBus = MinecraftForge.EVENT_BUS;
		final IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
		
		forgeEventBus.addListener(this::onPlayerJoin);
		forgeEventBus.addListener(this::onWorldLoad);
		forgeEventBus.addListener(this::onWorldSaved);
		forgeEventBus.addListener(this::onCommandRegister);
		
		ItemRegistry.register();
	}
	
	private void onPlayerJoin(final PlayerEvent.PlayerLoggedInEvent event) {
		if (BankAccountHandler.getBankAccount(event.getPlayer().getUUID()) == null) {
			BankAccountHandler.registerBankAccount(event.getPlayer().getUUID(), 0);
		}
	}
	
	private void onWorldLoad(final WorldEvent.Load event) {
		if (!event.getWorld().isClientSide() && event.getWorld() instanceof ServerWorld) {
			BankAccountWorldSavedData.get((ServerWorld) event.getWorld());
			TransactionWorldSavedData.get((ServerWorld) event.getWorld());
		}
	}
	
	private void onWorldSaved(final WorldEvent.Save event) {
		if (!event.getWorld().isClientSide() && event.getWorld() instanceof ServerWorld) {
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
