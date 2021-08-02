package dev.psygamer.econ.gui;

import com.mojang.blaze3d.matrix.MatrixStack;

import dev.psygamer.econ.banking.BankAccountHandler;
import dev.psygamer.econ.gui.widgets.PlayerHead;
import dev.psygamer.econ.gui.widgets.TextField;
import dev.psygamer.econ.network.EConPacketHandler;
import dev.psygamer.econ.network.server.TransactionMessage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.gui.widget.button.ImageButton;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.Color;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.lwjgl.glfw.GLFW;

import java.util.UUID;

@Mod.EventBusSubscriber(Dist.CLIENT)
public class NewTransactionScreen extends Screen {
	
	@SubscribeEvent
	public static void onKeyPressed(final InputEvent.KeyInputEvent event) {
		if (Minecraft.getInstance().screen instanceof NewTransactionScreen &&
				event.getKey() == GLFW.GLFW_KEY_ENTER ||
				event.getKey() == GLFW.GLFW_KEY_KP_ENTER
		) {
			((NewTransactionScreen) Minecraft.getInstance().screen).submitButton.onPress();
		}
	}
	
	public static final ResourceLocation NEW_TRANSACTION_LOCATION = new ResourceLocation("econ", "textures/gui/new_transaction.png");
	public static final ResourceLocation BANK_ACCOUNT_BUTTON_LOCATION = new ResourceLocation("econ", "textures/gui/bank_account_button.png");
	
	private static final int guiWidth = 166;
	private static final int guiHeight = 230;
	
	private PlayerHead sendingPlayerHead;
	private PlayerHead receivingPlayerHead;
	
	private TextField playerNameInput;
	private TextField amountInput;
	private Button submitButton;
	
	protected NewTransactionScreen() {
		super(new StringTextComponent("New Transaction"));
	}
	
	@Override
	protected void init() {
		final int w = 134;
		
		final int xPos = (this.width / 2) - (guiWidth / 2);
		final int yPos = (this.height / 2) - (guiHeight / 2);
		final float ntBtnScale = 1.0f;
		
		
		final Button newTransactionButton = new ImageButton(
				xPos + 5, yPos + 5,
				(int) (21 * ntBtnScale), (int) (20 * ntBtnScale),
				0, 0, (int) (20 * ntBtnScale),
				
				BANK_ACCOUNT_BUTTON_LOCATION, (int) (256 * ntBtnScale), (int) (256 * ntBtnScale),
				
				onPress -> this.minecraft.setScreen(new BankAccountScreen())
		);
		
		addButton(newTransactionButton);
		
		this.playerNameInput = new TextField(this.font, this.width / 2 - 67, this.height / 2 - 57, 134, 20);
		this.amountInput = new TextField(this.font, this.width / 2 - 67, this.height / 2 - 26, 134, 20);
		this.submitButton = new Button(this.width / 2 - 67, this.height / 2 + 60, 134, 20,
				new StringTextComponent("Process Transaction"), onPress -> {
			
			final UUID receivingUUID = getPlayerUUID();
			final long amount = getAmount();
			
			EConPacketHandler.INSTANCE.sendToServer(new TransactionMessage(receivingUUID, amount));
			Minecraft.getInstance().player.closeContainer();
		});
		
		this.sendingPlayerHead = new PlayerHead(
				this.width / 2 - 60, this.height / 2 + 14,
				23, 23,
				
				Minecraft.getInstance().player.getUUID(), this
		);
		this.receivingPlayerHead = new PlayerHead(
				this.width / 2 + 37, this.height / 2 + 14,
				23, 23,
				
				null, this
		);
		
		this.submitButton.active = false;
		
		this.playerNameInput.setFocus(true);
		this.playerNameInput.setMaxLength(16);
		this.amountInput.setMaxLength(6);
		
		addButton(this.playerNameInput);
		addButton(this.amountInput);
		addButton(this.submitButton);
	}
	
	@Override
	public void tick() {
		this.playerNameInput.tick();
		this.amountInput.tick();
		
		if (this.playerNameInput.getValue().isEmpty()) {
			this.playerNameInput.setSuggestion(null);
		} else {
			this.playerNameInput.setSuggestion(BankAccountHandler.bankAccountPlayerNames.values().stream()
					.filter(name -> name.toLowerCase().startsWith(this.playerNameInput.getValue().toLowerCase()))
					.findFirst()
					.orElse(null)
			);
		}
		
		this.playerNameInput.setValid(
				getPlayerUUID() != null
		);
		this.amountInput.setValid(
				getAmount() > 0 && getAmount() <= BankAccountHandler.clientBankAccount.getBalance()
		);
		
		this.submitButton.active = this.playerNameInput.isValid() && this.amountInput.isValid();
		
		this.receivingPlayerHead.setPlayerUUID(getPlayerUUID());
	}
	
	@Override
	public void render(final MatrixStack matrix, final int mouseX, final int mouseY, final float partialTicks) {
		renderBackground(matrix);
		
		this.sendingPlayerHead.render(matrix, mouseX, mouseY, partialTicks);
		this.receivingPlayerHead.render(matrix, mouseX, mouseY, partialTicks);
		
		final long accountBalance = BankAccountHandler.clientBankAccount.getBalance();
		final long transferAmount = MathHelper.clamp(getAmount(), 0, accountBalance);
		
		drawString(matrix, this.font, TextFormatting.BOLD + "\u00BB",
				
				this.width / 2 - 27,
				this.height / 2 + 22,
				
				Color.fromLegacyFormat(TextFormatting.YELLOW).getValue()
		);
		drawString(matrix, this.font, transferAmount + "\u20AC",
				
				this.width / 2 - 27 + this.font.width("\u00BB "),
				this.height / 2 + 22,
				
				Color.fromLegacyFormat(TextFormatting.YELLOW).getValue()
		);

//		final int off = 10;
//
//		drawString(matrix, this.font, accountBalance + "\u20AC",
//
//				this.width / 2 - this.font.width(String.valueOf(accountBalance)) / 2,
//				this.height / 2 + 45 + off,
//
//				Color.fromLegacyFormat(TextFormatting.DARK_GREEN).getValue()
//		);
//		drawString(matrix, this.font, "-",
//
//				this.width / 2 - this.font.width("-") - this.font.width(String.valueOf(accountBalance)) / 2 - 1,
//				this.height / 2 + 55 + off,
//
//				Color.fromLegacyFormat(TextFormatting.DARK_RED).getValue()
//		);
//		drawString(matrix, this.font, transferAmount + "\u20AC",
//
//				this.width / 2 + this.font.width(String.valueOf(accountBalance)) / 2 - this.font.width(String.valueOf(transferAmount)),
//				this.height / 2 + 55 + off,
//
//				Color.fromLegacyFormat(TextFormatting.DARK_RED).getValue()
//		);
//
//		AbstractGui.fill(matrix,
//				this.width / 2 - this.font.width(String.valueOf(accountBalance)) / 2, this.height / 2 + 66 + off,
//				this.width / 2 + this.font.width(String.valueOf(accountBalance)) / 2, this.height / 2 + 67 + off,
//
//				Color.fromLegacyFormat(TextFormatting.GOLD).getValue() + 0xFF000000
//		);
//		drawString(matrix, this.font, (accountBalance - transferAmount) + "\u20AC",
//
//				this.width / 2 + this.font.width(String.valueOf(accountBalance)) / 2 - this.font.width(String.valueOf(accountBalance - transferAmount)),
//				this.height / 2 + 70 + off,
//
//				Color.fromLegacyFormat(TextFormatting.GOLD).getValue()
//		);
		
		this.font.draw(matrix, "Make Transaction",
				this.width / 2f - (this.font.width("Make Transaction") / 2f), this.height / 2f - guiHeight / 2f + 7,
				
				Color.fromLegacyFormat(TextFormatting.DARK_GRAY).getValue()
		);
		
		this.font.draw(matrix, "Player Name",
				this.width / 2f - 62, this.height / 2f - 65,
				
				Color.fromLegacyFormat(TextFormatting.DARK_GRAY).getValue()
		);
		this.font.draw(matrix, "Transfer Amount",
				this.width / 2f - 62, this.height / 2f - 34,
				
				Color.fromLegacyFormat(TextFormatting.DARK_GRAY).getValue()
		);
		
		matrix.pushPose();
		matrix.scale(2.0f, 2.0f, 2.0f);
		
		drawCenteredString(matrix, this.font, TextFormatting.BOLD + (BankAccountHandler.clientBankAccount.getBalance() + "\u20AC"), this.width / 4, this.height / 4 - guiHeight / 4 + 12, Color.fromLegacyFormat(TextFormatting.GOLD).getValue());
		
		matrix.popPose();
		
		super.render(matrix, mouseX, mouseY, partialTicks);
	}
	
	@Override
	public void renderBackground(final MatrixStack matrix) {
		super.renderBackground(matrix); /* If you render the default background last, you get a dark mode */
		
		final int xPos = (this.width / 2) - (guiWidth / 2);
		final int yPos = (this.height / 2) - (guiHeight / 2);
		
		this.getMinecraft().getTextureManager().bind(NEW_TRANSACTION_LOCATION);
		
		AbstractGui.blit(matrix, xPos, yPos,
				0, 0,
				guiWidth, guiHeight + 26,
				512, 256
		);
		
		final long accountBalance = BankAccountHandler.clientBankAccount.getBalance();
		final long transferAmount = MathHelper.clamp(getAmount(), 0, accountBalance);
	}
	
	@Deprecated
	private void drawBackground(final MatrixStack matrix, int topLeftX, int topLeftY, int bottomRightX, int bottomRightY, final int margin) {
		topLeftX -= margin;
		topLeftY -= margin;
		bottomRightX += margin;
		bottomRightY += margin;
		
		AbstractGui.fill(matrix,
				topLeftX - 1, topLeftY - 1,
				bottomRightX + 1, bottomRightY + 1,
				
				0xFF8B8B8B
		);
		
		AbstractGui.fill(matrix,
				topLeftX - 1, topLeftY - 1,
				bottomRightX, topLeftY,
				
				0xFF373737
		);
		AbstractGui.fill(matrix,
				topLeftX - 1, topLeftY,
				topLeftX, bottomRightY,
				
				0xFF373737
		);
		
		AbstractGui.fill(matrix,
				topLeftX, bottomRightY,
				bottomRightX + 1, bottomRightY + 1,
				
				0xFFFFFFFF
		);
		AbstractGui.fill(matrix,
				bottomRightX + 1, topLeftY,
				bottomRightX, bottomRightY,
				
				0xFFFFFFFF
		);
	}
	
	@Override
	public boolean mouseClicked(final double mouseX, final double mouseY, final int buttonID) {
		
		this.playerNameInput.setFocus(false);
		this.playerNameInput.setHighlightPos(this.playerNameInput.getCursorPosition());
		this.amountInput.setFocus(false);
		this.amountInput.setHighlightPos(this.amountInput.getCursorPosition());
		
		if (mouseX >= this.playerNameInput.x && mouseX <= this.playerNameInput.x + this.playerNameInput.getWidth() &&
				mouseY >= this.playerNameInput.y && mouseY <= this.playerNameInput.y + this.playerNameInput.getHeight()
		) {
			this.playerNameInput.setFocus(true);
		}
		if (mouseX >= this.amountInput.x && mouseX <= this.amountInput.x + this.amountInput.getWidth() &&
				mouseY >= this.amountInput.y && mouseY <= this.amountInput.y + this.amountInput.getHeight()
		) {
			this.amountInput.setFocus(true);
		}
		
		return super.mouseClicked(mouseX, mouseY, buttonID);
	}
	
	@Override
	public boolean charTyped(final char typedChar, final int keyCode) {
		if (isNumericLetter(typedChar)) {
			this.playerNameInput.charTyped(typedChar, keyCode);
			this.amountInput.charTyped(typedChar, keyCode);
		} else if (isLatinLetter(typedChar)) {
			this.playerNameInput.charTyped(typedChar, keyCode);
		}
		
		return true;
	}
	
	private boolean isLatinLetter(final char typedChar) {
		return (typedChar >= 'a' && typedChar <= 'z') || (typedChar >= 'A' && typedChar <= 'Z') || typedChar == '_';
	}
	
	private boolean isNumericLetter(final char typedChar) {
		return (typedChar >= '0' && typedChar <= '9');
	}
	
	private UUID getPlayerUUID() {
		for (final UUID accountUUID : BankAccountHandler.bankAccountPlayerNames.keySet()) {
			final String accountName = BankAccountHandler.bankAccountPlayerNames.get(accountUUID);
			
			if (accountName.equalsIgnoreCase(this.playerNameInput.getValue())) {
				return accountUUID;
			}
		}
		
		return null;
	}
	
	private long getAmount() {
		try {
			return Long.parseLong(this.amountInput.getValue());
		} catch (final NumberFormatException ex) {
			return -1L;
		}
	}
}