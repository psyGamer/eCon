package dev.psygamer.econ.gui;

import dev.psygamer.econ.ECon;
import dev.psygamer.econ.gui.widgets.NewTransactionButton;
import dev.psygamer.econ.gui.widgets.PageButtons;
import dev.psygamer.econ.gui.widgets.ScrollBar;
import dev.psygamer.econ.gui.widgets.TransactionEntry;
import dev.psygamer.econ.client.KeybindingsRegistry;
import dev.psygamer.econ.banking.BankAccountHandler;
import dev.psygamer.econ.banking.Transaction;
import dev.psygamer.econ.banking.TransactionHandler;

import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.gui.widget.button.ImageButton;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.Color;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.math.MathHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.entity.player.ClientPlayerEntity;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.LinkedList;

@OnlyIn(Dist.CLIENT)
@Mod.EventBusSubscriber(Dist.CLIENT)
public class BankAccountScreen extends Screen {
	
	public static final ResourceLocation BANK_ACCOUNT_LOCATION = new ResourceLocation("econ", "textures/gui/bank_account.png");
	public static final ResourceLocation TRANSACTION_BUTTON_LOCATION = new ResourceLocation("econ", "textures/gui/transaction_button.png");
	
	public static final int MAX_ENTRIES_PER_SITE = 16;
	
	private static final int guiWidth = 166;
	private static final int guiHeight = 230;
	
	private final ClientPlayerEntity clientPlayer;

//	private Field w, h, tw, th;
	
	private Field textureWidth;
	private Field textureHeight;
	
	private PageButtons pageButtons;
	private ScrollBar scrollBar;
	private double scrollBarClickOffset;
	
	private int page = 0;
	
	private final LinkedList<TransactionEntry> transactions = new LinkedList<>();
	private final TransactionEntry[] visibleTransactions = new TransactionEntry[5];
	
	@SubscribeEvent
	public static void onKeyPressed(final TickEvent.ClientTickEvent event) {
		if (KeybindingsRegistry.BANK_ACCOUNT_KEYBINDING.isDown()) {
			
			if (Minecraft.getInstance().isLocalServer()) {
				Minecraft.getInstance().player.sendMessage(ECon.COMMAND_DISABLED_MESSAGE, Minecraft.getInstance().player.getUUID());
				
				return;
			}
			Minecraft.getInstance().setScreen(new BankAccountScreen());
		}
	}
	
	protected BankAccountScreen() {
		super(new StringTextComponent("Bank Account"));
		
		this.clientPlayer = Minecraft.getInstance().player;
	}
	
	@Override
	protected void init() {
		super.init();
		
		final int xPos = (this.width / 2) - (guiWidth / 2);
		final int yPos = (this.height / 2) - (guiHeight / 2);
		
		final float ntBtnScale = 1.0f;
		
		final Button newTransactionButton = new ImageButton(
				xPos + 5, yPos + 5,
				(int) (21 * ntBtnScale), (int) (20 * ntBtnScale),
				0, 0, (int) (20 * ntBtnScale),
				
				TRANSACTION_BUTTON_LOCATION, (int) (256 * ntBtnScale), (int) (256 * ntBtnScale),
				
				onPress -> this.minecraft.setScreen(new NewTransactionScreen())
		);
		
		addButton(newTransactionButton);

//		this.w = ObfuscationReflectionHelper.findField(Widget.class, "field_230688_j_");
//		this.h = ObfuscationReflectionHelper.findField(Widget.class, "field_230689_k_");
//		this.tw = ObfuscationReflectionHelper.findField(ImageButton.class, "field_212935_e");
//		this.th = ObfuscationReflectionHelper.findField(ImageButton.class, "field_212936_f");
//
//		this.w.setAccessible(true);
//		this.h.setAccessible(true);
//		this.tw.setAccessible(true);
//		this.th.setAccessible(true);

//		this.newTransactionButton = new NewTransactionButton(50, 50, 20, 20, 1.5f, new StringTextComponent("$"), onPress -> {
//			this.minecraft.setScreen(new NewTransactionScreen());
//		});

//		this.textureWidth = ObfuscationReflectionHelper.findField(ImageButton.class, "field_212935_e");
//		this.textureHeight = ObfuscationReflectionHelper.findField(ImageButton.class, "field_212936_f");
//
//		this.textureWidth.setAccessible(true);
//		this.textureHeight.setAccessible(true);
		
		this.transactions.clear();
		
		for (final Transaction transaction : TransactionHandler.clientTransactionHistory) {
			this.transactions.addFirst(new TransactionEntry(this.width / 2 - TransactionEntry.guiWidth / 2 - 5, 0, transaction, this.clientPlayer, this));
		}

//		for (int i = 0 ; i < TransactionHandler.getTransactionHistory().size() ; i++) {
//			this.transactions.add(new TransactionEntry(this.width / 2 - 63, 0, i, null));
//		}
		
		this.pageButtons = new PageButtons(this.width / 2 - PageButtons.guiWidth / 2 - 5, this.height / 2 + 108, this);
		this.scrollBar = new ScrollBar(xPos + 141, yPos + 49, 169);
	}
	
	@Override
	public void resize(final Minecraft minecraft, final int width, final int height) {
		super.resize(minecraft, width, height);
		
		this.init();
	}
	
	@Override
	public void tick() {
		this.scrollBar.tick();
		
		final int scrollIndex = (int) (this.scrollBar.getScrollPercentage() * Math.min(this.transactions.size() - 5, MAX_ENTRIES_PER_SITE - 5));
		
		Arrays.fill(this.visibleTransactions, null);
		
		int j = 0;
		
		try {
			for (int i = scrollIndex + MAX_ENTRIES_PER_SITE * this.page ; i < Math.min(scrollIndex + 5 + MAX_ENTRIES_PER_SITE * this.page, this.transactions.size()) ; i++) {
				this.visibleTransactions[j] = this.transactions.get(this.transactions.size() - i - 1);
				this.visibleTransactions[j++].y = j * 33 + this.height / 2 - 96;
				this.visibleTransactions[j - 1].tick();
			}
		} catch (final IndexOutOfBoundsException ignored) {
		}
		
		this.scrollBar.setDisabled(canScroll());
//		try {
//			for (int i = scrollIndex + maxEntriesPerSite * this.page ; i < Math.min(scrollIndex + 5 + maxEntriesPerSite * this.page, this.transactions.size()) ; i++) {
//				if (i == maxEntriesPerSite - 1 && this.transactions.size() > maxEntriesPerSite) {
//					this.visibleTransactions[j++] = new PageButtons(this.width / 2 - 63, j * 33 + this.height / 2 - 90, this);
//
//					break;
//				}
//
//				if (this.page > 0) {
//					if (j == 0) {
//						this.visibleTransactions[j++] = new PageButtons(this.width / 2 - 63, j * 33 + this.height / 2 - 90, this);
//						this.visibleTransactions[j] = this.transactions.get(i);
//						this.visibleTransactions[j++].y = j * 33 + this.height / 2 - 95;
//					} else {
//						this.visibleTransactions[j] = this.transactions.get(i);
//						this.visibleTransactions[j++].y = j * 33 + this.height / 2 - 95;
//					}
//				} else {
//					this.visibleTransactions[j] = this.transactions.get(i + 1);
//					this.visibleTransactions[j++].y = j * 33 + this.height / 2 - 95;
//				}
//			}
//		} catch (final IndexOutOfBoundsException ignored) {
//		}
	}
	
	@Override
	public void render(final MatrixStack matrix, final int mouseX, final int mouseY, final float partialTicks) {
		renderBackground(matrix);
//		drawCenteredString(matrix, this.font, this.accountBalanceString, mouseX, mouseY, Color.parseColor("#ffffff").getValue());
		this.scrollBar.render(matrix, mouseX, mouseY, partialTicks);
		this.pageButtons.render(matrix, mouseX, mouseY, partialTicks);

//		final float scale = 1.2F;

//		this.newTransactionButton = new ImageButton(
//				50, 50,
//				(int) (20 * scale), (int) (20 * scale),
//				0, 0, 30,
//				TRANSACTION_BUTTON_LOCATION, (int) (256 * scale), (int) (256 * scale), onPress -> this.minecraft.setScreen(new NewTransactionScreen())
//		);
//
//		try {
//			this.w.set(this.newTransactionButton, (int) (21 * scale));
//			this.h.set(this.newTransactionButton, (int) (20 * scale));
//			this.tw.set(this.newTransactionButton, (int) (256 * scale));
//			this.th.set(this.newTransactionButton, (int) (256 * scale));
//		} catch (final IllegalAccessException e) {
//			e.printStackTrace();
//		}

//		try {
//			this.textureWidth.setInt(this.newTransactionButton, mouseX);
//			this.textureHeight.setInt(this.newTransactionButton, mouseY);
//		} catch (final IllegalAccessException e) {
//			e.printStackTrace();
//		}
		
		for (final Widget visibleTransaction : this.visibleTransactions) {
			if (visibleTransaction == null) {
				continue;
			}
			
			visibleTransaction.render(matrix, mouseX, mouseY, partialTicks);
		}
		
		this.font.draw(matrix, "Bank Account", this.width / 2f - (this.font.width("Bank Account") / 2f), this.height / 2f - guiHeight / 2f + 7, Color.fromLegacyFormat(TextFormatting.DARK_GRAY).getValue());
		
		matrix.pushPose();
		matrix.scale(2.0f, 2.0f, 2.0f);
		
		drawCenteredString(matrix, this.font, TextFormatting.BOLD + (BankAccountHandler.clientBankAccount.getBalance() + "\u20AC"), this.width / 4, this.height / 4 - guiHeight / 4 + 12, Color.fromLegacyFormat(TextFormatting.GOLD).getValue());

//		drawCenteredString(matrix, this.font, mouseX + ", " + mouseY, mouseX / 2, mouseY / 2, Color.parseColor("#ffffff").getValue());
//		drawCenteredString(matrix, this.font, (this.width / 2) + ", " + (this.height / 2 - guiHeight / 2 + 12), mouseX / 2, mouseY / 2 + 10, Color.parseColor("#ffffff").getValue());
		
		matrix.popPose();

//		drawCenteredString(matrix, this.font, String.valueOf(scaleFactor), mouseX, mouseY, Color.parseColor("#ffffff").getValue());
//		drawCenteredString(matrix, this.font, mouseX + ", " + mouseY, mouseX, mouseY, Color.parseColor("#ffffff").getValue());
//		drawCenteredString(matrix, this.font, this.width + ", " + this.height, mouseX, mouseY + 10, Co	lor.parseColor("#ffffff").getValue());

//		drawCenteredString(matrix, this.font, String.valueOf(this.scrollBarClickOffset), mouseX, mouseY, Color.parseColor("#ffffff").getValue());
//		drawCenteredString(matrix, this.font, String.valueOf(this.scrollBar.getScrollPercentage()), mouseX, mouseY, Color.parseColor("#ffffff").getValue());
//		drawCenteredString(matrix, this.font, String.valueOf(this.scrollBar.y - this.scrollBar.initialY), mouseX, mouseY + 10, Color.parseColor("#ffffff").getValue());
//		drawCenteredString(matrix, this.font, String.valueOf(this.scrollBar.maxScroll - this.scrollBar.initialY), mouseX, mouseY + 20, Color.parseColor("#ffffff").getValue());

//		drawCenteredString(matrix, this.font, String.valueOf(this.page), mouseX, mouseY, Color.parseColor("#ffffff").getValue());
//		drawCenteredString(matrix, this.font, String.valueOf(this.getMaxPages()), mouseX, mouseY + 10, Color.parseColor("#ffffff").getValue());

//		drawCenteredString(matrix, this.minecraft.font, String.valueOf(this.p1), mouseX, mouseY, Color.parseColor("#ffffff").getValue());
//		drawCenteredString(matrix, this.minecraft.font, String.valueOf(this.p2), mouseX, mouseY + 10, Color.parseColor("#ffffff").getValue());
//		drawCenteredString(matrix, this.minecraft.font, String.valueOf(this.p3), mouseX, mouseY + 20, Color.parseColor("#ffffff").getValue());
//		drawCenteredString(matrix, this.minecraft.font, String.valueOf(this.p4), mouseX, mouseY + 30, Color.parseColor("#ffffff").getValue());
//		drawCenteredString(matrix, this.minecraft.font, String.valueOf(this.p5), mouseX, mouseY + 40, Color.parseColor("#ffffff").getValue());
		
		
		super.render(matrix, mouseX, mouseY, partialTicks);
	}
	
	@Override
	public boolean mouseClicked(final double mouseX, final double mouseY, final int buttonID) {
		if (this.scrollBar.isClickInside(mouseX, mouseY)) {
			this.scrollBarClickOffset = mouseY - this.scrollBar.y;
		} else {
			this.scrollBarClickOffset = -1;
		}
		
		this.pageButtons.mouseClicked(mouseX, mouseY, buttonID);
		
		return super.mouseClicked(mouseX, mouseY, buttonID);
	}
	
	@Override
	public boolean mouseDragged(final double mouseX, final double mouseY, final int c, final double d, final double e) {
		if (this.scrollBarClickOffset != -1 && canScroll()) {
			this.scrollBar.y = MathHelper.clamp((int) (mouseY - this.scrollBarClickOffset), this.scrollBar.initialY, this.scrollBar.maxScroll);
		}
		
		return super.mouseDragged(mouseX, mouseY, c, d, e);
	}
	
	@Override
	public void renderBackground(final MatrixStack matrix) {
		super.renderBackground(matrix);
		
		final int xPos = (this.width / 2) - (guiWidth / 2);
		final int yPos = (this.height / 2) - (guiHeight / 2);
		
		this.getMinecraft().getTextureManager().bind(BANK_ACCOUNT_LOCATION);
		
		blit(matrix, xPos, yPos, 0, 0, guiWidth, guiHeight + 26, 512, 256);
	}
	
	/* Use in render method for "good" results ;) ;)
		final float scaleFactor = mouseX / 100f;
		
		final int guiWidth = (int) (119 * scaleFactor);
		final int guiHeight = (int) (165 * scaleFactor);
		final int xPos = (this.width / 2) - (guiWidth / 2);
		final int yPos = (this.height / 2) - (guiHeight / 2);
		
		
		this.getMinecraft().getTextureManager().bind(BANK_ACCOUNT_LOCATION);
		blit(matrix, xPos, yPos, 0, 0, guiWidth, guiHeight, (int) (256 * scaleFactor), (int) (256 * scaleFactor));
	 */
	
	public boolean canScroll() {
		if (getMaxPages() > this.page) {
			return true;
		}
		
		return (this.transactions.size() - 1) % MAX_ENTRIES_PER_SITE > 4;
	}
	
	public void setCurrentPage(final int page) {
		this.page = page;
		
		this.scrollBar.y = this.scrollBar.initialY;
	}
	
	public int getCurrentPage() {
		return this.page;
	}
	
	public int getMaxPages() {
		return (int) Math.floor(this.transactions.size() / (MAX_ENTRIES_PER_SITE + 1f));
	}
}
