package dev.psygamer.econ.gui.widgets;

import dev.psygamer.econ.gui.BankAccountScreen;

import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.util.math.vector.Vector2f;
import net.minecraft.util.text.StringTextComponent;

public class ScrollBar extends Widget {
	
	public final int initialY;
	public final int maxScroll;
	
	private static final int guiWidth = 9;
	private static final int guiHeight = 41;
	
	private float percentageScrolled;
	
	public boolean isDisabled() {
		return this.disabled;
	}
	
	public void setDisabled(final boolean disabled) {
		this.disabled = disabled;
	}
	
	private boolean disabled;
	
	private Vector2f lastMousePosition;
	private Vector2f previousMousePosition;
	
	public ScrollBar(final int x, final int y, final int maxScroll) {
		super(x, y, 9, 41, StringTextComponent.EMPTY);
		
		this.disabled = false;
		this.initialY = y;
		this.maxScroll = y + maxScroll - guiHeight;
	}
	
	@Override
	public void render(final MatrixStack matrix, final int mouseX, final int mouseY, final float partialTicks) {
		super.render(matrix, mouseX, mouseY, partialTicks);
	}
	
	
	@Override
	public boolean mouseScrolled(final double a, final double b, final double c) {
		return super.mouseScrolled(a, b, c);
	}
	
	@Override
	protected void renderBg(final MatrixStack matrix, final Minecraft minecraft, final int mouseX, final int mouseY) {
		super.renderBg(matrix, minecraft, mouseX, mouseY);
		
		this.lastMousePosition = new Vector2f(mouseX, mouseY);
		
		minecraft.getTextureManager().bind(BankAccountScreen.BANK_ACCOUNT_LOCATION);
		
		if (this.disabled) {
			blit(matrix, this.x, this.y, 166, 0, guiWidth, guiHeight, 512, 256);
		} else {
			blit(matrix, this.x, this.y, 175, 0, guiWidth, guiHeight, 512, 256);
		}
	}
	
	public void tick() {
		this.previousMousePosition = this.lastMousePosition;
	}
	
	public boolean isClickInside(final double mouseX, final double mouseY) {
		return mouseX >= this.x && mouseX <= this.x + 9 &&
				mouseY >= this.y && mouseY <= this.y + 41;
	}
	
	public double getScrollPercentage() {
		return (this.y - this.initialY) / (float) (this.maxScroll - this.initialY);
	}
}
