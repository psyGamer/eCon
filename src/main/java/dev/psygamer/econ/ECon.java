package dev.psygamer.econ;

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
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
	}
	
	private void setup(final FMLCommonSetupEvent event) {
		// Setup code //
	}
}
