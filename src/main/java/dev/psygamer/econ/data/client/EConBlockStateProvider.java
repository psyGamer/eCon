package dev.psygamer.econ.data.client;

import dev.psygamer.econ.ECon;
import dev.psygamer.econ.setup.BlockRegistry;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.common.data.ExistingFileHelper;

public class EConBlockStateProvider extends BlockStateProvider {
	
	public EConBlockStateProvider(final DataGenerator gen, final ExistingFileHelper exFileHelper) {
		super(gen, ECon.MODID, exFileHelper);
	}
	
	@Override
	protected void registerStatesAndModels() {
		horizontalBlock(BlockRegistry.STORE_BLOCK.get(), models().getExistingFile(modLoc("block/store_block")));
	}
}
