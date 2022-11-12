package dev.psygamer.econ.data;

import dev.psygamer.econ.ECon;

import dev.psygamer.econ.data.client.EConBlockStateProvider;
import dev.psygamer.econ.data.client.EConItemModelProvider;

import net.minecraft.data.DataGenerator;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.GatherDataEvent;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.eventbus.api.SubscribeEvent;

@Mod.EventBusSubscriber(modid = ECon.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class DataGenerators {
	
	@SubscribeEvent
	public static void gatherData(final GatherDataEvent event) {
		final DataGenerator generator = event.getGenerator();
		final ExistingFileHelper existingFileHelper = event.getExistingFileHelper();
		
		generator.addProvider(new EConBlockStateProvider(generator, existingFileHelper));
		generator.addProvider(new EConItemModelProvider(generator, existingFileHelper));
	}
}
