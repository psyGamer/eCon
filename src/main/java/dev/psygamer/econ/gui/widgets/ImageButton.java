package dev.psygamer.econ.gui.widgets;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.StringTextComponent;

public class ImageButton extends Button {
	
	private final int texX;
	private final int texY;
	private final int texWidth;
	private final int texHeight;
	private final int yHoverDiff;
	
	private final float imageScale;
	private final ResourceLocation image;
	
	public ImageButton(final int x, final int y, final int width, final int height, final int texX, final int texY, final int yHoverDiff, final int texWidth, final int texHeight, final float imageScale, final ResourceLocation image, final Button.IPressable press) {
		this(x, y, width, height, texX, texY, yHoverDiff, texWidth, texHeight, imageScale, image, press, NO_TOOLTIP);
	}
	
	public ImageButton(final int x, final int y, final int width, final int height, final int texX, final int texY, final int yHoverDiff, final int texWidth, final int texHeight, final float imageScale, final ResourceLocation image, final Button.IPressable press, final Button.ITooltip onTooltip) {
		super(x, y, width, height, StringTextComponent.EMPTY, press, onTooltip);
		
		this.texX = texX;
		this.texY = texY;
		this.texWidth = texWidth;
		this.texHeight = texHeight;
		this.yHoverDiff = yHoverDiff;
		
		this.imageScale = imageScale;
		this.image = image;
	}
	
	@Override
	public void render(final MatrixStack matrix, final int mouseX, final int mouseY, final float partialTicks) {
		super.render(matrix, mouseX, mouseY, partialTicks);
		
		Minecraft.getInstance().getTextureManager().bind(this.image);
		blit(matrix, this.x + (int) ((1 - this.imageScale) / this.width * 200), this.y + (int) ((1 - this.imageScale) / this.height * 200), this.texX, this.texY + (isHovered() ? this.yHoverDiff * this.imageScale : 0), (int) (this.width * this.imageScale), (int) (this.height * this.imageScale), (int) (this.texWidth * this.imageScale), (int) (this.texHeight * this.imageScale));
	}
}
