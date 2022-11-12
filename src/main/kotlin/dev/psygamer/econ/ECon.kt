package dev.psygamer.econ

import net.minecraftforge.fml.common.Mod
import dev.psygamer.econ.block.StoreBlock
import dev.psygamer.wireframe.Wireframe

@Mod("econ") // TODO: Let Wireframe handle this
object ECon : Wireframe.Mod("econ", "eConWF", "1.0") {

	init {
		StoreBlock
	}
}
