package dev.psygamer.econ;

import dev.psygamer.econ.setup.ItemRegistry;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(ECon.MODID)
public class ECon {
	
	public static final String MODID = "econ";
	public static final Logger LOGGER = LogManager.getLogger("eCon");
	
	public ECon() {
		IEventBus forgeEventBus = MinecraftForge.EVENT_BUS;
		IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
		
		modEventBus.addListener(this::setup);
		
		ItemRegistry.register();
	}
	
	private void setup(final FMLCommonSetupEvent event) {
	
	}
}
