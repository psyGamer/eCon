package dev.psygamer.econwf.block

import net.minecraft.block.SoundType
import net.minecraftforge.common.ToolType
import dev.psygamer.econwf.ECon
import dev.psygamer.wireframe.api.block.*
import dev.psygamer.wireframe.api.block.attributes.Material
import dev.psygamer.wireframe.api.item.ItemAttributes
import dev.psygamer.wireframe.util.Identifier

val FACING = DirectionBlockProperty("facing")

// @Register("store_block") TODO: More convinient way to register blocks
object StoreBlock : Block(
	Identifier(ECon.modID, "store_block"),
	BlockAttributes(Material.METAL)
		.hardness(1.2f)
		// TODO: Version-independent SoundType and ToolType
		.sound(SoundType.WOOD)
		.correctTool(ToolType.AXE),
	ItemAttributes(),

	FACING
) {

}