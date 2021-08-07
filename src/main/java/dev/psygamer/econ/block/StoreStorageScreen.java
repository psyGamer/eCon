package dev.psygamer.econ.block;

import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraft.client.gui.screen.inventory.ChestScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.ChestContainer;
import net.minecraft.util.text.ITextComponent;

public class StoreStorageScreen extends ChestScreen {
	
	protected StoreStorageScreen(final ChestContainer container, final PlayerInventory playerInventory, final ITextComponent title) {
		super(container, playerInventory, title);
	}
	
	@Override
	public void render(final MatrixStack matrix, final int mouseX, final int mouseY, final float partialTicks) {
		super.render(matrix, mouseX, mouseY, partialTicks);
		
		
	}
}
