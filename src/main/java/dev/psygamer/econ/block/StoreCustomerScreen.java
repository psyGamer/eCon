package dev.psygamer.econ.block;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import dev.psygamer.econ.ECon;
import dev.psygamer.econ.gui.widgets.ImageButton;
import dev.psygamer.econ.gui.widgets.TextField;
import dev.psygamer.econ.network.EConPacketHandler;
import dev.psygamer.econ.network.server.StoreOwnerMessage;
import dev.psygamer.econ.network.server.StoreTransactionMessage;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.ClickType;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.*;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.lwjgl.glfw.GLFW;

import java.util.Locale;

@OnlyIn(Dist.CLIENT)
public class StoreCustomerScreen extends ContainerScreen<StoreCustomerContainer> {
	
	private static final ResourceLocation STORE_CUSTOMER_LOCATION = new ResourceLocation(ECon.MODID, "textures/gui/store_customer_gui.png");
	
	private TextField quantityField;
	private ImageButton increaseQuantityButton;
	private ImageButton decreaseQuantityButton;
	
	private Button orderButton;
	
	private final StoreTileEntity tileEntity;
	
	public StoreCustomerScreen(final StoreCustomerContainer container, final PlayerInventory playerInventory, final ITextComponent title) {
		super(container, playerInventory, title);
		
		this.inventoryLabelY = 98;
		
		this.leftPos = 0;
		this.topPos = 0;
		this.imageWidth = 155;
		this.imageHeight = 98;
		
		this.tileEntity = container.getTileEntity();
	}
	
	@Override
	protected void init() {
		final int xPos = this.width / 2 - this.imageWidth / 2;
		final int yPos = this.height / 2 - this.imageHeight / 2;
		
		this.quantityField = new TextField(this.font, xPos + 70, yPos + 33, 15, 10, 1.5f, 1.5f);
		this.quantityField.setMaxLength(2);
		this.quantityField.setValue("1");
		
		this.quantityField.setValid(true);
		
		this.increaseQuantityButton = new ImageButton(
				xPos + 69, yPos + 19, 17, 10, 155, 0,
				256, 256, 10, 20, STORE_CUSTOMER_LOCATION,
				
				button -> this.quantityField.setValue(String.valueOf(parseInt(this.quantityField.getValue()) + 1))
		);
		this.decreaseQuantityButton = new ImageButton(
				xPos + 69, yPos + 47, 17, 10, 172, 0,
				256, 256, 10, 20, STORE_CUSTOMER_LOCATION,
				
				button -> this.quantityField.setValue(String.valueOf(parseInt(this.quantityField.getValue()) - 1))
		);
		
		this.orderButton = new Button(this.width / 2 - 34, yPos + this.imageHeight - 30, 69, 20, new TranslationTextComponent("econ.store.buy").withStyle(TextFormatting.BOLD), button -> {
			EConPacketHandler.INSTANCE.sendToServer(new StoreTransactionMessage(this.tileEntity.getBlockPos(), parseInt(this.quantityField.getValue())));
			
			this.onClose();
		});
		
		super.init();
	}
	
	@Override
	public void tick() {
		final int quantity = parseInt(this.quantityField.getValue());
		final int totalQuantity = quantity * this.tileEntity.getOfferedItem().getCount();
		
		this.increaseQuantityButton.active = totalQuantity < this.tileEntity.getLeftStock();
		this.decreaseQuantityButton.active = quantity > 1;
		
		this.orderButton.active = totalQuantity <= this.tileEntity.getLeftStock() && quantity > 0;
		
		super.tick();
	}
	
	@Override
	public void render(final MatrixStack matrix, final int mouseX, final int mouseY, final float partialTicks) {
		renderBackground(matrix);
		renderSlots(matrix, mouseX, mouseY, partialTicks);
		renderTooltip(matrix, mouseX, mouseY);
		
		final int xPos = this.width / 2 - this.imageWidth / 2;
		final int yPos = this.height / 2 - this.imageHeight / 2;
		
		final boolean isFree = this.tileEntity.getPrice() <= 0;
		final String priceString = isFree ? "FREE" : this.tileEntity.getPrice() * parseInt(this.quantityField.getValue()) + ECon.MONEY_SYMBOL.getString();
		
		this.font.draw(matrix, "Total Price",
				xPos + 6, yPos + 17,
				
				Color.fromLegacyFormat(TextFormatting.DARK_GRAY).getValue()
		);
		
		this.font.drawShadow(matrix, TextFormatting.BOLD + priceString,
				xPos + 9, yPos + 27,
				
				Color.fromLegacyFormat(isFree ? TextFormatting.GREEN : TextFormatting.GOLD).getValue()
		);
		
		final int instancesLeftInStock = (int) Math.floor(
				this.tileEntity.getLeftStock() / (float) this.tileEntity.getOfferedItem().getCount()
		);
		final Color leftInStockColor;
		
		if (instancesLeftInStock <= 3)
			leftInStockColor = Color.fromLegacyFormat(TextFormatting.RED);
		else if (instancesLeftInStock <= 5)
			leftInStockColor = Color.fromLegacyFormat(TextFormatting.YELLOW);
		else
			leftInStockColor = Color.fromLegacyFormat(TextFormatting.GREEN);
		
		this.font.draw(matrix, "Stock left",
				xPos + 6, yPos + 42,
				
				Color.fromLegacyFormat(TextFormatting.DARK_GRAY).getValue()
		);
		
		this.font.drawShadow(matrix, TextFormatting.BOLD + String.valueOf(instancesLeftInStock <= 0 ? "SOLD OUT" : instancesLeftInStock),
				xPos + 9, yPos + 52,
				
				leftInStockColor.getValue()
		);
		
		final ITextComponent storeName = this.tileEntity.getName().isEmpty() ? new TranslationTextComponent("econ.store.title") : new StringTextComponent(this.tileEntity.getName());
		
		this.font.draw(matrix, storeName,
				this.width / 2f - (this.font.width(storeName) / 2f), this.height / 2f - this.imageHeight / 2f + 7,
				
				0x404040
		);
		
		this.increaseQuantityButton.render(matrix, mouseX, mouseY, partialTicks);
		this.decreaseQuantityButton.render(matrix, mouseX, mouseY, partialTicks);
		this.quantityField.render(matrix, mouseX, mouseY, partialTicks);
		
		this.orderButton.render(matrix, mouseX, mouseY, partialTicks);
	}
	
	@Override
	protected void renderBg(final MatrixStack matrix, final float partialTicks, final int mouseX, final int mouseY) {
		final int xPos = this.width / 2 - this.imageWidth / 2;
		final int yPos = this.height / 2 - this.imageHeight / 2;
		
		this.getMinecraft().getTextureManager().bind(STORE_CUSTOMER_LOCATION);
		this.blit(matrix, xPos, yPos, 0, 0, this.imageWidth, this.imageHeight);
	}
	
	@Override
	protected void renderLabels(final MatrixStack matrix, final int mouseX, final int mouseY) {
	}
	
	@Override
	public void mouseMoved(final double mouseX, final double mouseY) {
		this.increaseQuantityButton.mouseMoved(mouseX, mouseY);
		this.decreaseQuantityButton.mouseMoved(mouseX, mouseY);
		
		this.orderButton.mouseMoved(mouseX, mouseY);
	}
	
	@Override
	public boolean mouseClicked(final double mouseX, final double mouseY, final int buttonId) {
		this.quantityField.mouseClicked(mouseX, mouseY, buttonId);
		
		this.increaseQuantityButton.mouseClicked(mouseX, mouseY, buttonId);
		this.decreaseQuantityButton.mouseClicked(mouseX, mouseY, buttonId);
		
		this.orderButton.mouseClicked(mouseX, mouseY, buttonId);
		
		return super.mouseClicked(mouseX, mouseY, buttonId);
	}
	
	@Override
	public boolean keyPressed(final int keycode, final int p_231046_2_, final int p_231046_3_) {
		if (keycode == GLFW.GLFW_KEY_ESCAPE) {
			return super.keyPressed(keycode, p_231046_2_, p_231046_3_);
		}
		
		if (this.quantityField.isFocused()) {
			return this.quantityField.keyPressed(keycode, p_231046_2_, p_231046_3_);
		}
		
		return super.keyPressed(keycode, p_231046_2_, p_231046_3_);
	}
	
	@Override
	public boolean charTyped(final char typedChar, final int keyCode) {
		if (isNumericLetter(typedChar) &&
				parseInt(this.quantityField.getValue() + typedChar) >= 1 &&
				parseInt(this.quantityField.getValue() + typedChar) <= 99
		) {
			this.quantityField.charTyped(typedChar, keyCode);
		}
		
		return super.charTyped(typedChar, keyCode);
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
			final float scale = slot instanceof StorePreviewSlot ? 2.0f : 1.0f;
			
			RenderSystem.pushMatrix();
			
			if (scale == 2.0f) {
				RenderSystem.scalef(scale, scale, scale);
				RenderSystem.translatef(slot.x / -scale, slot.y / -scale, (this.itemRenderer.blitOffset + 300) / -2f); // idk why 101 but i works lol
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
			
			String quantityString = null;
			
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
	
	private int parseInt(final String numberString) {
		try {
			return Integer.parseInt(numberString);
		} catch (final NumberFormatException ex) {
			return 1;
		}
	}
	
	private boolean isNumericLetter(final char typedChar) {
		return (typedChar >= '0' && typedChar <= '9');
	}
}
