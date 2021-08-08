package dev.psygamer.econ.block;

import com.mojang.blaze3d.matrix.MatrixStack;

import com.mojang.blaze3d.systems.RenderSystem;
import dev.psygamer.econ.ECon;
import dev.psygamer.econ.gui.BankAccountScreen;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.gui.widget.button.ImageButton;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;

import javax.annotation.Nullable;

public class StoreStorageScreen extends ContainerScreen<StoreContainer> {
	
	private static final ResourceLocation STORE_STORAGE_LOCATION = new ResourceLocation(ECon.MODID, "textures/gui/store_storage_gui.png");
	
	private Button storeButton;
	
	protected StoreStorageScreen(final StoreContainer container, final PlayerInventory playerInventory, final ITextComponent title) {
		super(container, playerInventory, title);
		
		this.menu.setOwnerScreen(this);
		
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
				
				onPress -> this.minecraft.setScreen(new StoreOwnerScreen(this.menu, this.menu.getPlayerInventory(), StringTextComponent.EMPTY))
		);
		
		super.init();
	}
	
	@Override
	public void render(final MatrixStack matrix, final int mouseX, final int mouseY, final float partialTicks) {
		renderBackground(matrix);
		renderSlots(matrix, mouseX, mouseY, partialTicks);
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
	
	@SuppressWarnings("deprecation")
	private void renderSlots(final MatrixStack matrix, final int mouseX, final int mouseY, final float partialTicks) {
		this.renderBg(matrix, partialTicks, mouseX, mouseY);
		
		RenderSystem.disableRescaleNormal();
		RenderSystem.disableDepthTest();
		RenderSystem.pushMatrix();
		RenderSystem.translatef((float) this.leftPos, (float) this.topPos, 0.0F);
		RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		RenderSystem.enableRescaleNormal();
		
		this.hoveredSlot = null;
		
		RenderSystem.glMultiTexCoord2f(33986, 240.0F, 240.0F);
		RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		
		for (final Slot slot : this.menu.slots) {
			if (slot instanceof StoreFakeSlot) continue;
			
			if (slot.isActive()) {
				this.renderSlot(matrix, slot);
			}
			
			if (this.isHovering(slot.x, slot.y, 16, 16, mouseX, mouseY) && slot.isActive()) {
				this.hoveredSlot = slot;
				
				RenderSystem.disableDepthTest();
				RenderSystem.colorMask(true, true, true, false);
				
				this.fillGradient(matrix, slot.x, slot.y, slot.x + 16, slot.y + 16, this.slotColor, this.slotColor);
				
				RenderSystem.colorMask(true, true, true, true);
				RenderSystem.enableDepthTest();
			}
		}
		
		final PlayerInventory playerinventory = this.minecraft.player.inventory;
		ItemStack itemstack = this.draggingItem.isEmpty() ? playerinventory.getCarried() : this.draggingItem;
		
		this.renderLabels(matrix, mouseX, mouseY);
		if (!itemstack.isEmpty()) {
			final int itemXOffset = this.draggingItem.isEmpty() ? 8 : 16;
			
			String quantityString = "";
			
			if (!this.draggingItem.isEmpty() && this.isSplittingStack) {
				itemstack = itemstack.copy();
				itemstack.setCount(MathHelper.ceil((float) itemstack.getCount() / 2.0F));
			} else if (this.isQuickCrafting && this.quickCraftSlots.size() > 1) {
				itemstack = itemstack.copy();
				itemstack.setCount(this.quickCraftingRemainder);
				
				if (itemstack.isEmpty()) {
					quantityString = "" + TextFormatting.YELLOW + "0";
				}
			}
			
			this.renderFloatingItem(itemstack, mouseX - this.leftPos - 8, mouseY - this.topPos - itemXOffset, quantityString);
		}
		
		if (!this.snapbackItem.isEmpty()) {
			float f = (float) (Util.getMillis() - this.snapbackTime) / 100.0F;
			if (f >= 1.0F) {
				f = 1.0F;
				this.snapbackItem = ItemStack.EMPTY;
			}
			
			final int l2 = this.snapbackEnd.x - this.snapbackStartX;
			final int i3 = this.snapbackEnd.y - this.snapbackStartY;
			final int l1 = this.snapbackStartX + (int) ((float) l2 * f);
			final int i2 = this.snapbackStartY + (int) ((float) i3 * f);
			
			this.renderFloatingItem(this.snapbackItem, l1, i2, (String) null);
		}
		
		RenderSystem.popMatrix();
		RenderSystem.enableDepthTest();
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
	
	@Nullable
	@Override
	public Slot findSlot(final double mouseX, final double mouseY) {
		for (int i = 0 ; i < this.menu.slots.size() ; ++i) {
			final Slot slot = this.menu.slots.get(i);
			
			if (slot instanceof StoreFakeSlot) continue;
			
			if (this.isHovering(slot.x, slot.y, 16, 16, mouseX, mouseY) && slot.isActive())
				return slot;
		}
		
		return null;
	}
}
