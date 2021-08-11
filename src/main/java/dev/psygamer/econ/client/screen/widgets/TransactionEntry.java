package dev.psygamer.econ.client.screen.widgets;

import dev.psygamer.econ.ECon;
import dev.psygamer.econ.banking.Transaction;
import dev.psygamer.econ.client.screen.BankAccountScreen;

import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraft.util.text.Color;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.entity.player.ClientPlayerEntity;

import net.minecraftforge.client.MinecraftForgeClient;

import java.util.*;
import java.time.Instant;
import java.text.DateFormat;

public class TransactionEntry extends Widget {
	
	public static final int guiWidth = 118;
	public static final int guiHeight = 31;
	
	private final FontRenderer fontRenderer = Minecraft.getInstance().font;
	private final Transaction transaction;
	private final PlayerHead playerHead;
	
	private final String dateTimeString;
	
	private final boolean isReceivingMoney;
	
	public TransactionEntry(final int x, final int y, final Transaction transaction, final ClientPlayerEntity clientPlayer, final Screen parentScreen) {
		super(x, y, 0, 0, StringTextComponent.EMPTY);
		
		this.transaction = transaction;
		
		final Instant timestamp = Instant.ofEpochSecond(transaction.getUnixTimestamp());
		final Date date = Date.from(timestamp);
		
		final Locale clientLocale = MinecraftForgeClient.getLocale();
		
		final DateFormat dateTimeFormatter = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT, clientLocale);
		
		this.dateTimeString = dateTimeFormatter.format(date);
		this.isReceivingMoney = clientPlayer.getUUID().equals(this.transaction.getReceivingPlayer());
		this.playerHead = new PlayerHead(x + 4, y + 4, 23, 23,
				this.isReceivingMoney ? transaction.getSendingPlayer() : transaction.getReceivingPlayer(), parentScreen
		);
	}
	
	public void tick() {
		this.playerHead.x = this.x + 4;
		this.playerHead.y = this.y + 4;
	}
	
	@Override
	public void render(final MatrixStack matrix, final int mouseX, final int mouseY, final float partialTicks) {
		super.render(matrix, mouseX, mouseY, partialTicks);
		
		this.playerHead.render(matrix, mouseX, mouseY, partialTicks);
		
		drawString(matrix, this.fontRenderer, TextFormatting.BOLD + (this.isReceivingMoney ? "\u00BB" : "\u00AB"),
				
				this.x + 33,
				this.y + guiHeight / 2 - 8,
				
				Color.fromLegacyFormat(this.isReceivingMoney ? TextFormatting.GREEN : TextFormatting.RED).getValue()
		);
		
		drawString(matrix, this.fontRenderer, this.transaction.getTransferAmount() + ECon.MONEY_SYMBOL.getString(),
				
				this.x + 33 + this.fontRenderer.width("\u00BB "),
				this.y + guiHeight / 2 - 8,
				
				Color.fromLegacyFormat(this.isReceivingMoney ? TextFormatting.GREEN : TextFormatting.RED).getValue()
		);
		
		drawCenteredString(matrix, this.fontRenderer, this.dateTimeString,
				this.x + 73,
				this.y + guiHeight - this.fontRenderer.lineHeight - 4,
				
				8947848 // #888888
		);
	}
	
	@Override
	protected void renderBg(final MatrixStack matrix, final Minecraft minecraft, final int mouseX, final int mouseY) {
		super.renderBg(matrix, minecraft, mouseX, mouseY);
		
		minecraft.getTextureManager().bind(BankAccountScreen.BANK_ACCOUNT_LOCATION);
		blit(matrix, this.x, this.y, 184, 0, guiWidth, guiHeight, 512, 256);
	}
}
