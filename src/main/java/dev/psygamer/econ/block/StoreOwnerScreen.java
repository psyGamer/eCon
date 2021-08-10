package dev.psygamer.econ.block;

import dev.psygamer.econ.ECon;

import dev.psygamer.econ.gui.widgets.ImageButton;
import dev.psygamer.econ.gui.widgets.TextField;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;

import dev.psygamer.econ.network.EConPacketHandler;
import dev.psygamer.econ.network.server.StoreOwnerMessage;
import dev.psygamer.econ.network.server.StoreStorageContainerMessage;
import net.minecraft.inventory.container.*;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.*;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.lwjgl.glfw.GLFW;

import javax.annotation.Nullable;

@OnlyIn(Dist.CLIENT)
public class StoreOwnerScreen extends ContainerScreen<StoreContainer> {
	
	private static final ResourceLocation STORE_LOCATION = new ResourceLocation(ECon.MODID, "textures/gui/store_gui.png");
	
	private int tick;
	
	private Button storageButton;
	
	private TextField nameField;
	private TextField priceField;
	private TextField quantityField;
	private ImageButton increaseQuantityButton;
	private ImageButton decreaseQuantityButton;
	
	private final StoreTileEntity tileEntity;
	
	public StoreOwnerScreen(final StoreContainer container, final PlayerInventory playerInventory, final ITextComponent title) {
		super(container, playerInventory, title);
		
		this.menu.setOwnerScreen(this);
		
		this.inventoryLabelY = 98;
		
		this.leftPos = 0;
		this.topPos = 0;
		this.imageWidth = 176;
		this.imageHeight = 192;
		
		this.tileEntity = container.getTileEntity();
	}
	
	@Override
	protected void init() {
		final int xPos = this.width / 2 - this.imageWidth / 2;
		final int yPos = this.height / 2 - this.imageHeight / 2;
		final float ntBtnScale = 1.0f;
		
		this.storageButton = new net.minecraft.client.gui.widget.button.ImageButton(
				xPos + this.imageWidth - 27, yPos + 5,
				(int) (22 * ntBtnScale), (int) (22 * ntBtnScale),
				210, 0, (int) (22 * ntBtnScale),
				
				STORE_LOCATION, (int) (256 * ntBtnScale), (int) (256 * ntBtnScale),
				
				onPress -> EConPacketHandler.INSTANCE.sendToServer(new StoreStorageContainerMessage(this.tileEntity.getBlockPos()))
		);
		
		this.nameField = new TextField(this.font, xPos + 22, yPos + 37, 134, 15, 5, 3);
		this.priceField = new TextField(this.font, xPos + 27, yPos + 79, 45, 15, 5, 3);
		this.quantityField = new TextField(this.font, xPos + 94, yPos + 80, 15, 10, 1.5f, 1.5f);
		
		this.nameField.setMaxLength(32);
		this.priceField.setMaxLength(5);
		this.quantityField.setMaxLength(2);
		
		this.nameField.setValue(this.tileEntity.getName());
		if (this.tileEntity.getPrice() > 0)
			this.priceField.setValue(String.valueOf(this.tileEntity.getPrice()));
		if (this.tileEntity.getOfferedItem().getCount() > 0)
			this.quantityField.setValue(String.valueOf(this.tileEntity.getOfferedItem().getCount()));
		
		this.nameField.setValid(true);
		this.priceField.setValid(true);
		this.quantityField.setValid(true);
		
		this.increaseQuantityButton = new ImageButton(
				xPos + 93, yPos + 65, 17, 10, 176, 0,
				256, 256, 10, 20, STORE_LOCATION,
				
				button -> {
					this.quantityField.setValue(String.valueOf(parseInt(this.quantityField.getValue()) + 1));
					updateItemStack();
				});
		this.decreaseQuantityButton = new ImageButton(
				xPos + 93, yPos + 95, 17, 10, 193, 0,
				256, 256, 10, 20, STORE_LOCATION,
				
				button -> {
					this.quantityField.setValue(String.valueOf(parseInt(this.quantityField.getValue()) - 1));
					updateItemStack();
				});
		
		super.init();
	}
	
	@Override
	public void tick() {
		this.nameField.tick();
		this.priceField.tick();
		this.quantityField.tick();
		
		this.tileEntity.setName(this.nameField.getValue());
		this.tileEntity.setPrice(parseInt(this.priceField.getValue()));
		this.tileEntity.getOfferedItem().setCount(
				MathHelper.clamp(parseInt(this.quantityField.getValue()), 1, 64)
		);
		
		this.increaseQuantityButton.active = parseInt(this.quantityField.getValue()) < this.tileEntity.getOfferedItem().getMaxStackSize() && parseInt(this.quantityField.getValue()) > 0;
		this.decreaseQuantityButton.active = parseInt(this.quantityField.getValue()) > 1;
		
		this.tick = ++this.tick % 20;
		
		if (this.tick == 0) {
			EConPacketHandler.INSTANCE.sendToServer(
					new StoreOwnerMessage(this.menu.getTileEntity())
			);
		}
		
		super.tick();
	}
	
	@Override
	public void onClose() {
		EConPacketHandler.INSTANCE.sendToServer(
				new StoreOwnerMessage(
						parseInt(this.priceField.getValue()),
						this.nameField.getValue(),
						this.menu.getFakeSlot().getItem(),
						this.menu.getTileEntity().getBlockPos()
				)
		);
		
		super.onClose();
	}
	
	public void onItemUpdate(final ItemStack itemStack) {
		this.quantityField.setValue(String.valueOf(itemStack.getCount()));
	}
	
	@Override
	public void render(final MatrixStack matrix, final int mouseX, final int mouseY, final float partialTicks) {
		renderBackground(matrix);
//		renderFakeSlot(matrix);

//		final int slotX = this.width / 2 - this.imageWidth / 2 + this.menu.getFakeSlot().x;
//		final int slotY = this.height / 2 - this.imageHeight / 2 + this.menu.getFakeSlot().y;

//		if (mouseX >= slotX && mouseX <= slotX + 32 && mouseY >= slotY && mouseY <= slotY + 32) {
//			AbstractGui.fill(matrix, slotX + 16, slotY, slotX + 32, slotY + 16, 0x80FFFFFF);
//			AbstractGui.fill(matrix, slotX, slotY + 16, slotX + 32, slotY + 32, 0x80FFFFFF);
//			if (!(mouseX >= slotX && mouseX <= slotX + 16 && mouseY >= slotY && mouseY <= slotY + 16)) {
//				AbstractGui.fill(matrix, slotX, slotY, slotX + 16, slotY + 16, 0x80FFFFFF);
//
//				renderTooltip(matrix, this.menu.getFakeSlot().getItem(), mouseX, mouseY);
//			}
//		}
		
		renderSlots(matrix, mouseX, mouseY, partialTicks);
//		super.render(matrix, mouseX, mouseY, partialTicks);
		renderTooltip(matrix, mouseX, mouseY);
		
		this.storageButton.render(matrix, mouseX, mouseY, partialTicks);
		
		this.font.draw(matrix, "Store",
				this.width / 2f - (this.font.width("Store") / 2f), this.height / 2f - this.imageHeight / 2f + 7,
				
				0x404040
		);
		
		this.font.draw(matrix, new TranslationTextComponent("econ.store.nameField"),
				this.nameField.x + 3, this.nameField.y - 13,
				
				Color.fromLegacyFormat(TextFormatting.DARK_GRAY).getValue()
		);
		this.font.draw(matrix, new TranslationTextComponent("econ.store.priceField"),
				this.priceField.x + 3, this.priceField.y - 13,
				
				Color.fromLegacyFormat(TextFormatting.DARK_GRAY).getValue()
		);
		
		this.increaseQuantityButton.render(matrix, mouseX, mouseY, partialTicks);
		this.decreaseQuantityButton.render(matrix, mouseX, mouseY, partialTicks);
		
		this.nameField.render(matrix, mouseX, mouseY, partialTicks);
		this.priceField.render(matrix, mouseX, mouseY, partialTicks);
		this.quantityField.render(matrix, mouseX, mouseY, partialTicks);
	}
	
	@Override
	protected void renderLabels(final MatrixStack matrix, final int mouseX, final int mouseY) {
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
			if (slot instanceof StoreStorageSlot) continue;
			
			final float scale = slot instanceof StoreFakeSlot ? 2.0f : 1.0f;
			
			RenderSystem.pushMatrix();
			
			if (scale == 2.0f) {
				RenderSystem.scalef(scale, scale, scale);
				RenderSystem.translatef(slot.x / -scale, slot.y / -scale, -101); // idk why 101 but i works lol
//				RenderSystem.tra
			}
			
			if (slot.isActive()) {
				this.renderSlot(matrix, slot);
			}
			
			if (this.isHovering(slot.x, slot.y, (int) (16 * scale), (int) (16 * scale), mouseX, mouseY) && slot.isActive()) {
				this.hoveredSlot = slot;
				
				RenderSystem.disableDepthTest();
				RenderSystem.colorMask(true, true, true, false);
				
				this.fillGradient(matrix, slot.x, slot.y, slot.x + 16, slot.y + 16, this.slotColor, this.slotColor);
				
				RenderSystem.colorMask(true, true, true, true);
				RenderSystem.enableDepthTest();
			}
			
			RenderSystem.popMatrix();
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

//	@Override
//	public void renderSlot(final MatrixStack matrix, final Slot slot) {
//		if (slot instanceof StoreFakeSlot) {
//			return;
//		}
//
//		super.renderSlot(matrix, slot);

//		final ItemStack carriedItem = this.minecraft.player.inventory.getCarried();
//		ItemStack slotItem = slot.getItem();
//
//		boolean flag = false;
//		boolean flag1 = slot == this.clickedSlot && !this.draggingItem.isEmpty() && !this.isSplittingStack;
//		String s = null;
//
//		if (slot == this.clickedSlot && !this.draggingItem.isEmpty() && this.isSplittingStack && !slotItem.isEmpty()) {
//			slotItem = slotItem.copy();
//			slotItem.setCount(slotItem.getCount() / 2);
//		} else if (this.isQuickCrafting && this.quickCraftSlots.contains(slot) && !carriedItem.isEmpty()) {
//			if (this.quickCraftSlots.size() == 1) {
//				return;
//			}
//
//			if (Container.canItemQuickReplace(slot, carriedItem, true) && this.menu.canDragTo(slot)) {
//				slotItem = carriedItem.copy();
//				flag = true;
//				Container.getQuickCraftSlotCount(this.quickCraftSlots, this.quickCraftingType, slotItem, slot.getItem().isEmpty() ? 0 : slot.getItem().getCount());
//				final int k = Math.min(slotItem.getMaxStackSize(), slot.getMaxStackSize(slotItem));
//				if (slotItem.getCount() > k) {
//					s = TextFormatting.YELLOW.toString() + k;
//					slotItem.setCount(k);
//				}
//			} else {
//				this.quickCraftSlots.remove(slot);
//				this.recalculateQuickCraftRemaining();
//			}
//		}
//
//		this.setBlitOffset(100);
//		this.itemRenderer.blitOffset = 100.0F;
//		if (slotItem.isEmpty() && slot.isActive()) {
//			final Pair<ResourceLocation, ResourceLocation> pair = slot.getNoItemIcon();
//			if (pair != null) {
//				final TextureAtlasSprite textureatlassprite = this.minecraft.getTextureAtlas(pair.getFirst()).apply(pair.getSecond());
//				this.minecraft.getTextureManager().bind(textureatlassprite.atlas().location());
//				blit(matrix, slot.x, slot.y, this.getBlitOffset(), 16, 16, textureatlassprite);
//				flag1 = true;
//			}
//		}
//
//		if (!flag1) {
//			if (flag) {
//				AbstractGui.fill(matrix, slot.x, slot.y, slot.x + 16, slot.y + 16, 0x80FFFFFF);
//			}
//
////			RenderSystem.enableDepthTest();
//			RenderSystem.pushMatrix();
//
//			if (slot instanceof StoreFakeSlot) {
//				RenderSystem.scalef(2.0f, 2.0f, 2.0f);
//				RenderSystem.translatef(slot.x / -2f, slot.y / -2f, 0);
//			}
//
//			this.itemRenderer.renderAndDecorateItem(this.minecraft.player, slotItem, slot.x, slot.y);
//			this.itemRenderer.renderGuiItemDecorations(this.font, slotItem, slot.x, slot.y, s);
//
//			RenderSystem.popMatrix();
//		}
//
//		this.itemRenderer.blitOffset = 0.0F;
//		this.setBlitOffset(0);
//	}
	
	@Deprecated
	private void renderFakeSlot(final MatrixStack matrixStack) {
		final StoreFakeSlot slot = this.menu.getFakeSlot();
		final ItemStack slotItem = slot.getItem();
		final String quantityString = slotItem.getCount() == 0 ? "" : String.valueOf(slotItem.getCount());
		
		final int xPos = this.width / 2 - this.imageWidth / 2 + slot.x;
		final int yPos = this.height / 2 - this.imageHeight / 2 + slot.y;
		
		RenderSystem.pushMatrix();
		RenderSystem.scalef(2.0f, 2.0f, 2.0f);
		RenderSystem.translatef(xPos / -2f, yPos / -2f, 0);
		
		this.itemRenderer.renderAndDecorateItem(this.minecraft.player, slotItem, xPos, yPos);
		this.itemRenderer.renderGuiItemDecorations(this.font, slotItem, xPos, yPos, quantityString);
		
		RenderSystem.popMatrix();
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
	
	@Override
	public void mouseMoved(final double mouseX, final double mouseY) {
		this.increaseQuantityButton.mouseMoved(mouseX, mouseY);
		this.decreaseQuantityButton.mouseMoved(mouseX, mouseY);
		
		this.storageButton.mouseMoved(mouseX, mouseY);
	}
	
	@Override
	public boolean mouseClicked(final double mouseX, final double mouseY, final int buttonId) {
		this.nameField.mouseClicked(mouseX, mouseY, buttonId);
		this.priceField.mouseClicked(mouseX, mouseY, buttonId);
		this.quantityField.mouseClicked(mouseX, mouseY, buttonId);
		
		this.increaseQuantityButton.mouseClicked(mouseX, mouseY, buttonId);
		this.decreaseQuantityButton.mouseClicked(mouseX, mouseY, buttonId);
		
		this.storageButton.mouseClicked(mouseX, mouseY, buttonId);
		
		final int slotX = this.width / 2 - this.imageWidth / 2 + this.menu.getFakeSlot().x;
		final int slotY = this.height / 2 - this.imageHeight / 2 + this.menu.getFakeSlot().y;
		
		if (mouseX >= slotX && mouseX <= slotX + 32 && mouseY >= slotY && mouseY <= slotY + 32) {
			if (!(mouseX >= slotX && mouseX <= slotX + 16 && mouseY >= slotY && mouseY <= slotY + 16)) {
				slotClicked(this.menu.getFakeSlot(), 36, buttonId, ClickType.PICKUP);
			}
		}
		
		return super.mouseClicked(mouseX, mouseY, buttonId);
	}
	
	@Override
	public boolean keyPressed(final int keycode, final int p_231046_2_, final int p_231046_3_) {
		if (keycode == GLFW.GLFW_KEY_ESCAPE) {
			return super.keyPressed(keycode, p_231046_2_, p_231046_3_);
		}
		
		if (this.nameField.isFocused() || this.priceField.isFocused() || this.quantityField.isFocused()) {
			this.nameField.keyPressed(keycode, p_231046_2_, p_231046_3_);
			this.priceField.keyPressed(keycode, p_231046_2_, p_231046_3_);
			this.quantityField.keyPressed(keycode, p_231046_2_, p_231046_3_);
			
			return true;
		}
		
		return super.keyPressed(keycode, p_231046_2_, p_231046_3_);
	}
	
	@Override
	public boolean charTyped(final char typedChar, final int keyCode) {
		if (isNumericLetter(typedChar)) {
			this.priceField.charTyped(typedChar, keyCode);
			
			if (parseInt(this.quantityField.getValue() + typedChar) >= 1 &&
					parseInt(this.quantityField.getValue() + typedChar) <= this.tileEntity.getOfferedItem().getMaxStackSize()
			) {
				this.quantityField.charTyped(typedChar, keyCode);
				updateItemStack();
			}
		}
		
		if (this.font.width(this.nameField.getValue() + typedChar) <= 124)
			this.nameField.charTyped(typedChar, keyCode);
		
		this.menu.setChanged();
		
		EConPacketHandler.INSTANCE.sendToServer(
				new StoreOwnerMessage(this.menu.getTileEntity())
		);
		
		return true;
	}
	
	@Nullable
	@Override
	public Slot findSlot(final double mouseX, final double mouseY) {
		for (int i = 0 ; i < this.menu.slots.size() ; ++i) {
			final Slot slot = this.menu.slots.get(i);
			
			if (slot instanceof StoreStorageSlot) continue;
			
			if (slot instanceof StoreFakeSlot &&
					slot.isActive() &&
					this.isHovering(slot.x, slot.y, 32, 32, mouseX, mouseY))
				return slot;
			else if (this.isHovering(slot.x, slot.y, 16, 16, mouseX, mouseY) && slot.isActive())
				return slot;
		}
		
		return null;
	}
	
	private void updateItemStack() {
		final ItemStack itemStack = this.menu.getFakeSlot().getItem().copy();
		final int quantity = parseInt(this.quantityField.getValue());
		
		if (quantity < 0) return;
		
		itemStack.setCount(quantity);
		
		this.menu.getFakeSlot().set(itemStack);
		
		EConPacketHandler.INSTANCE.sendToServer(
				new StoreOwnerMessage(this.menu.getTileEntity())
		);
	}
	
	private int parseInt(final String numberString) {
		try {
			return Integer.parseInt(numberString);
		} catch (final NumberFormatException ex) {
			return -1;
		}
	}
	
	private boolean isLatinLetter(final char typedChar) {
		return (typedChar >= 'a' && typedChar <= 'z') || (typedChar >= 'A' && typedChar <= 'Z') || typedChar == '_';
	}
	
	private boolean isNumericLetter(final char typedChar) {
		return (typedChar >= '0' && typedChar <= '9');
	}
}
