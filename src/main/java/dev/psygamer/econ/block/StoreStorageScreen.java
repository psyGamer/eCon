package dev.psygamer.econ.block;


import dev.psygamer.econ.ECon;


import dev.psygamer.econ.network.EConPacketHandler;
import dev.psygamer.econ.network.server.StoreOwnerMessage;
import dev.psygamer.econ.network.server.StoreSetupContainerMessage;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.gui.widget.button.ImageButton;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;

public class StoreStorageScreen extends ContainerScreen<StoreStorageContainer> {
	
	private static final ResourceLocation STORE_STORAGE_LOCATION = new ResourceLocation(ECon.MODID, "textures/gui/store_storage_gui.png");
	
	private Button storeButton;
	
	public StoreStorageScreen(final StoreStorageContainer container, final PlayerInventory playerInventory, final ITextComponent title) {
		super(container, playerInventory, title);
		
		this.inventoryLabelY = 98;
		
		this.leftPos = 0;
		this.topPos = 0;
		this.imageWidth = 176;
		this.imageHeight = 192;
	}
	
	@Override
	protected void init() {
		final int xPos = (this.width / 2) - (this.imageWidth / 2);
		final int yPos = (this.height / 2) - (this.imageHeight / 2);
		final float ntBtnScale = 1.0f;
		
		this.storeButton = new ImageButton(
				xPos + this.imageWidth - 27, yPos + 5,
				(int) (22 * ntBtnScale), (int) (22 * ntBtnScale),
				176, 0, (int) (22 * ntBtnScale),
				
				STORE_STORAGE_LOCATION, (int) (256 * ntBtnScale), (int) (256 * ntBtnScale),
				
				onPress -> EConPacketHandler.INSTANCE.sendToServer(new StoreSetupContainerMessage(this.menu.getTileEntity().getBlockPos()))
		);
		
		super.init();
	}
	
	@Override
	public void onClose() {
		EConPacketHandler.INSTANCE.sendToServer(
				new StoreOwnerMessage(this.menu.getTileEntity())
		);
		
		super.onClose();
	}
	
	@Override
	public void render(final MatrixStack matrix, final int mouseX, final int mouseY, final float partialTicks) {
		renderBackground(matrix);
		super.render(matrix, mouseX, mouseY, partialTicks);
		renderTooltip(matrix, mouseX, mouseY);
		
		this.font.draw(matrix, "Storage",
				this.width / 2f - (this.font.width("Storage") / 2f), this.height / 2f - this.imageHeight / 2f + 7,
				
				0x404040
		);
		
		this.storeButton.render(matrix, mouseX, mouseY, partialTicks);
	}
	
	@Override
	@SuppressWarnings("deprecation")
	protected void renderBg(final MatrixStack matrix, final float mouseX, final int mouseY, final int partialTicks) {
		RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
		
		final int xPos = this.width / 2 - this.imageWidth / 2;
		final int yPos = this.height / 2 - this.imageHeight / 2;
		
		this.getMinecraft().getTextureManager().bind(STORE_STORAGE_LOCATION);
		this.blit(matrix, xPos, yPos, 0, 0, this.imageWidth, this.imageHeight);
	}
	
	@Override
	protected void renderLabels(final MatrixStack matrix, final int mouseX, final int mouseY) {
		super.renderLabels(matrix, mouseX, mouseY);
		
		this.font.draw(matrix, this.inventory.getDisplayName(),
				this.inventoryLabelX, this.inventoryLabelY,
				
				0x404040
		);
	}
	
	@Override
	public void mouseMoved(final double p_212927_1_, final double p_212927_3_) {
		this.storeButton.mouseMoved(p_212927_1_, p_212927_3_);
		super.mouseMoved(p_212927_1_, p_212927_3_);
	}
	
	@Override
	public boolean mouseClicked(final double p_231044_1_, final double p_231044_3_, final int p_231044_5_) {
		this.storeButton.mouseClicked(p_231044_1_, p_231044_3_, p_231044_5_);
		return super.mouseClicked(p_231044_1_, p_231044_3_, p_231044_5_);
	}
}
