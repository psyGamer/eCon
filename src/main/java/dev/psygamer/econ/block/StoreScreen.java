package dev.psygamer.econ.block;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import dev.psygamer.econ.ECon;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.Color;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class StoreScreen extends ContainerScreen<StoreContainer> {
	
	private static final ResourceLocation STORE_LOCATION = new ResourceLocation(ECon.MODID, "textures/gui/store_gui.png");
	
	public StoreScreen(final StoreContainer container, final PlayerInventory playerInventory, final ITextComponent title) {
		super(container, playerInventory, title);
		
		this.leftPos = 0;
		this.topPos = 0;
		this.imageWidth = 175;
		this.imageHeight = 201;
	}
	
	@Override
	public void render(final MatrixStack matrix, final int mouseX, final int mouseY, final float partialTicks) {
		renderBackground(matrix);
		renderTooltip(matrix, mouseX, mouseY);
		
		super.render(matrix, mouseX, mouseY, partialTicks);
	}
	
	@Override
	protected void renderLabels(final MatrixStack matrix, final int mouseX, final int mouseY) {
		this.font.draw(matrix, this.inventory.getDisplayName(),
				this.titleLabelX, this.titleLabelY,
				
				Color.fromLegacyFormat(TextFormatting.GRAY).getValue()
		);
	}
	
	@Override
	@SuppressWarnings("deprecation")
	protected void renderBg(final MatrixStack matrix, final float partialTicks, final int mouseX, final int mouseY) {
		RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
		
		final int xPos = this.width / 2 - this.imageWidth / 2;
		final int yPos = this.height / 2 - this.imageHeight / 2;
		
		this.getMinecraft().getTextureManager().bind(STORE_LOCATION);
		this.blit(matrix, xPos, yPos, 0, 0, this.imageWidth, this.imageHeight);
	}
}
