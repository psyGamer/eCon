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
	private final int yDisabledDiff;
	
	private final ResourceLocation image;
	
	public ImageButton(final int x, final int y,
					   final int width, final int height,
					   final int texX, final int texY,
					   final int texWidth, final int texHeight,
					   final int yHoverDiff,
					   final ResourceLocation image, final Button.IPressable onPress
	) {
		this(x, y, width, height, texX, texY, texWidth, texHeight, yHoverDiff, image, onPress, NO_TOOLTIP);
	}
	
	public ImageButton(final int x, final int y,
					   final int width, final int height,
					   final int texX, final int texY,
					   final int texWidth, final int texHeight,
					   final int yHoverDiff, final int yDisabledDiff,
					   final ResourceLocation image, final Button.IPressable onPress
	) {
		this(x, y, width, height, texX, texY, texWidth, texHeight, yHoverDiff, yDisabledDiff, image, onPress, NO_TOOLTIP);
	}
	
	public ImageButton(final int x, final int y,
					   final int width, final int height,
					   final int texX, final int texY,
					   final int texWidth, final int texHeight,
					   final int yHoverDiff,
					   final ResourceLocation image, final Button.IPressable onPress, final Button.ITooltip onTooltip
	) {
		this(x, y, width, height, texX, texY, texWidth, texHeight, yHoverDiff, 0, image, onPress, onTooltip);
	}
	
	public ImageButton(final int x, final int y,
					   final int width, final int height,
					   final int texX, final int texY,
					   final int texWidth, final int texHeight,
					   final int yHoverDiff, final int yDisabledDiff,
					   final ResourceLocation image, final Button.IPressable onPress, final Button.ITooltip onTooltip
	) {
		super(x, y, width, height, StringTextComponent.EMPTY, onPress, onTooltip);
		
		this.texX = texX;
		this.texY = texY;
		this.texWidth = texWidth;
		this.texHeight = texHeight;
		
		this.yHoverDiff = yHoverDiff;
		this.yDisabledDiff = yDisabledDiff;
		
		this.image = image;
	}
	
	@Override
	public void render(final MatrixStack matrix, final int mouseX, final int mouseY, final float partialTicks) {
		super.render(matrix, mouseX, mouseY, partialTicks);
		
		final int offset;
		
		if (!this.active) {
			offset = this.yDisabledDiff;
		} else if (isHovered()) {
			offset = this.yHoverDiff;
		} else {
			offset = 0;
		}
		
		Minecraft.getInstance().getTextureManager().bind(this.image);
		blit(matrix,
				this.x, this.y,
				this.texX, this.texY + offset,
				this.width, this.height, this.texWidth, this.texHeight
		);
	}
}
