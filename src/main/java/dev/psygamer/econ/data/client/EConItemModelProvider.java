package dev.psygamer.econ.data.client;

import dev.psygamer.econ.ECon;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.common.data.ExistingFileHelper;

public class EConItemModelProvider extends ItemModelProvider {
	
	public EConItemModelProvider(final DataGenerator generator, final ExistingFileHelper existingFileHelper) {
		super(generator, ECon.MODID, existingFileHelper);
	}
	
	@Override
	protected void registerModels() {
		final ModelFile itemGenerated = getExistingFile(mcLoc("item/generated"));
		
		builder(itemGenerated, "one_euro");
		builder(itemGenerated, "five_euros");
		builder(itemGenerated, "ten_euros");
		builder(itemGenerated, "twenty_euros");
		builder(itemGenerated, "fifty_euros");
		builder(itemGenerated, "one_hundert_euros");
		builder(itemGenerated, "two_hundert_euros");
		builder(itemGenerated, "five_hundert_euros");
	}
	
	private void builder(final ModelFile itemGenerated, final String name) {
		getBuilder(name).parent(itemGenerated).texture("layer0", "item/" + name);
	}
}
